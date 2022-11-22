/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import data.Category;
import data.Platforms;
import data.QueryFilter;
import data.Videogame;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author NeRooN
 */
public class VideogameQuery {

    /**
     * Constant to define the offset of the pagination
     */
    private static final int OFFSET = 10;

    /**
     * Method to get the category from a given String
     *
     * @param category
     * @return Category fetched from the database
     */
    public static Category getCategory(String category) {
        try {
            return (Category) DatabaseHelper.em.createQuery("SELECT s FROM Category s WHERE LOWER(s.category) = LOWER('" + category + "')").getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * Method to create a category from a given String
     *
     * @param category
     * @return integer with the result value. 0 for ok and 1 for error
     */
    public static int createCategory(String category) {
        try {
            Category c = new Category(category);
            DatabaseHelper.em.getTransaction().begin();
            DatabaseHelper.em.persist(c);
            DatabaseHelper.em.getTransaction().commit();
            return 0;
        } catch (Exception ex) {
            return 1;
        }
    }

    /**
     * Method to create a platform from a given String
     *
     * @param platform
     * @return integer with the result value. 0 for ok and 1 for error
     */
    public static int createPlatform(String platform) {
        try {
            Platforms p = new Platforms(platform);

            DatabaseHelper.em.getTransaction().begin();
            DatabaseHelper.em.persist(p);
            DatabaseHelper.em.getTransaction().commit();
            return 0;
        } catch (Exception ex) {
            return 1;
        }
    }

    /**
     * Method to create multiple categories from a given List of Strings
     *
     * @param categories
     */
    public static void createMultipleCategories(List<String> categories) {
        List<Category> cats = new ArrayList<>();

        categories.forEach(s -> cats.add(new Category(s)));

        cats.forEach(s -> {
            try {
                DatabaseHelper.em.getTransaction().begin();
                DatabaseHelper.em.persist(s);
                DatabaseHelper.em.getTransaction().commit();
            } catch (Exception ex) {
                System.out.println("Ya existe la categoría " + s.getCategory());
            }
        });
    }

    /**
     * Method to get a List of videogames with a query filter and pagination
     *
     * @param page
     * @param filter
     * @return List of videogames with pagination
     */
    public static List<Videogame> getGamesPaginated(int page, QueryFilter filter) {

        StringBuilder queryBuilder = getGamesWithoutPagination(filter);
        if (page <= 0) page = 1;
        queryBuilder.append(" ORDER BY Videogame.id OFFSET " + ((page - 1) * OFFSET) + " FETCH NEXT " + OFFSET + " ROWS ONLY");

        System.out.println(queryBuilder);

        Query query = DatabaseHelper.em.createNativeQuery(queryBuilder.toString(), Videogame.class);
        return (List<Videogame>) getGamesWithImage(query.getResultList());
    }

    /**
     * Method to get the total count of videogames in a page
     *
     * @param filter
     * @return Total count of pages based on the total result and the offset
     */
    public static int getGamesTotalPageCount(QueryFilter filter) {
        return (int) Math.ceil((double) DatabaseHelper.em.createNativeQuery(getGamesWithoutPagination(filter).toString(), Videogame.class).getResultList().size() / OFFSET);
    }

    /**
     * Method to build a query with filters based on a QueryFilter object
     *
     * @param filter
     * @return StringBuilder with the query needed to fetch videogames without pagination
     */
    public static StringBuilder getGamesWithoutPagination(QueryFilter filter) {
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Videogame");
        if (filter.getPlatformName() != null) {
            queryBuilder.append(" LEFT JOIN videogame_platforms on videogame_platforms.videogame_id = Videogame.id LEFT JOIN platforms on videogame_platforms.platforms_id = platforms.id");
        }

        if (filter.getCategoryName() != null) {
            queryBuilder.append(" LEFT JOIN videogame_category on videogame_category.videogame_id = Videogame.id LEFT JOIN category on videogame_category.category_id = category.id");
        }

        queryBuilder.append(" WHERE 1=1");

        if (filter.getPlatformName() != null) {
            queryBuilder.append(" AND platforms.name = '" + filter.getPlatformName() + "'");
        }

        if (filter.getCategoryName() != null) {
            queryBuilder.append(" AND category.category = '" + filter.getCategoryName() + "'");
        }

        if (filter.getName() != null && !filter.getName().trim().equals("")) {
            queryBuilder.append(" AND LOWER(videogame.name) LIKE '%" + filter.getName().toLowerCase() + "%'");
        }

        if (filter.getScoreSearchParam() == null) filter.setScoreSearchParam("");
        switch (filter.getScoreSearchParam()) {
            case "mayor" -> queryBuilder.append(" AND videogame.finalscore >= " + filter.getMinScore());
            case "menor" -> queryBuilder.append(" AND videogame.finalscore <= " + filter.getMaxScore());
            case "igual" -> queryBuilder.append(" AND videogame.finalscore = " + filter.getMinScore());
            case "entre" ->
                    queryBuilder.append(" AND videogame.finalscore >= " + filter.getMinScore() + " AND videogame.finalscore <= " + filter.getMaxScore());
            default -> {
                if (filter.getMinScore() > 0) {
                    queryBuilder.append(" AND videogame.finalscore >= " + filter.getMinScore());
                }
            }
        }

        if (filter.getDateSearchParam() == null) filter.setDateSearchParam("");
        switch (filter.getDateSearchParam()) {
            case "menor" -> {
                if (filter.getMaxDate() != null) {
                    queryBuilder.append(" AND videogame.releasedate <= '" + filter.getMaxDate() + "'");
                }
            }
            case "igual" -> {
                if (filter.getMinDate() != null) {
                    queryBuilder.append(" AND videogame.releasedate = '" + filter.getMinDate() + "'");
                }
            }
            case "entre" -> {
                if (filter.getMinDate() != null && filter.getMaxDate() != null) {
                    queryBuilder.append(" AND videogame.releasedate >= '" + filter.getMinDate() + "'" + " AND videogame.releasedate <= '" + filter.getMaxDate() + "'");
                }
            }
            default -> {
                if (filter.getMinDate() != null) {
                    queryBuilder.append(" AND videogame.releasedate >= '" + filter.getMinDate() + "'");
                }
            }
        }
        return queryBuilder;
    }

    /**
     * Method to get the top 5 videogames
     *
     * @return List of the top 5 videogames
     */
    public static List<Videogame> getGamesTop5() {
        Query query = DatabaseHelper.em.createNativeQuery("SELECT * FROM Videogame order by Videogame.finalscore DESC limit 5", Videogame.class);
        List<Videogame> vi = query.getResultList();
        vi.forEach(v -> System.out.println(v.getName()));
        return getGamesWithImage(vi);
    }

    /**
     * Method to get the list of categories
     *
     * @return List of all categories on the database
     */
    public static List<Category> getAllCategories() {
        return DatabaseHelper.em.createQuery("SELECT c FROM Category c").getResultList();
    }

    /**
     * Method to get the list of platforms
     *
     * @return List of all platforms on the database
     */
    public static List<Platforms> getAllPlatforms() {
        return DatabaseHelper.em.createQuery("SELECT p FROM Platforms p").getResultList();
    }

    /**
     * Method to get the platform from the String
     *
     * @param platform
     * @return Platform fetched from the database
     */
    public static Platforms getPlatform(String platform) {
        try {
            return (Platforms) DatabaseHelper.em.createQuery("SELECT p FROM Platforms p WHERE LOWER(p.name) = LOWER('" + platform + "')").getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * Method to get the game from the String
     *
     * @param name
     * @return Videogame fetched from the database
     */
    public static Videogame getVideogameByName(String name) {
        try {
            return (Videogame) DatabaseHelper.em.createQuery("SELECT v FROM Videogame v WHERE LOWER(v.name) = '" + name.toLowerCase() + "'").getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Method to get the image from the img folder and insert it on each game as byte[]
     *
     * @param videogames
     * @return List of videogames with the image set
     */
    private static List<Videogame> getGamesWithImage(List<Videogame> videogames) {
        videogames.forEach(v -> {
            if (v.getImagePath() != null && !v.getImagePath().equals("")) {
                try {
                    BufferedImage b = ImageIO.read(new File(v.getImagePath()));
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ImageIO.write(b, "png", bos);
                    byte[] img = bos.toByteArray();
                    v.setGameImage(img);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return videogames;
    }
}
