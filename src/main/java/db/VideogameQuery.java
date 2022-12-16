/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import data.*;
import helpers.DateHelper;
import helpers.EditVideogame;
import helpers.QueryFilter;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
            return (Category) DatabaseHelper.getEm().createQuery("SELECT s FROM Category s WHERE LOWER(s.category) = LOWER('" + category + "')").getSingleResult();
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
            DatabaseHelper.getEm().getTransaction().begin();
            DatabaseHelper.getEm().persist(c);
            DatabaseHelper.getEm().getTransaction().commit();
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

        List<Category> finalCats = checkExistsGameCategories(cats);
        DatabaseHelper.getEm().getTransaction().begin();
        finalCats.forEach(s -> {
            DatabaseHelper.getEm().persist(s);
        });
        DatabaseHelper.getEm().getTransaction().commit();
    }

    /**
     * Method to check if a videogame has categories stored that don't exist in the database and remove them
     *
     * @param categories
     * @return List of categories
     */
    public static List<Category> getExistingGameCategories(List<Category> categories) {
        List<Category> cats = new ArrayList<>();
        if (categories.size() != 0) {
            categories.forEach(c -> {
                Category cat = VideogameQuery.getCategory(c.getCategory());
                if (cat != null) {
                    cats.add(cat);
                }
            });
        }
        return cats;
    }

    public static List<Category> checkExistsGameCategories(List<Category> categories) {
        List<Category> cats = new ArrayList<>();
        if (categories.size() != 0) {
            categories.forEach(c -> {
                Category cat = VideogameQuery.getCategory(c.getCategory());
                if (cat == null) {
                    cats.add(c);
                }
            });
        }
        return cats;
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

            DatabaseHelper.getEm().getTransaction().begin();
            DatabaseHelper.getEm().persist(p);
            DatabaseHelper.getEm().getTransaction().commit();
            return 0;
        } catch (Exception ex) {
            return 1;
        }
    }

    /**
     * Method to get the platform from the String
     *
     * @param platform
     * @return Platform fetched from the database
     */
    public static Platforms getPlatform(String platform) {
        try {
            return (Platforms) DatabaseHelper.getEm().createQuery("SELECT p FROM Platforms p WHERE LOWER(p.name) = LOWER('" + platform + "')").getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * Method to create multiple categories from a given List of Strings
     *
     * @param platforms
     */
    public static void createMultiplePlatforms(List<String> platforms) {
        List<Platforms> plats = new ArrayList<>();

        platforms.forEach(s -> plats.add(new Platforms(s)));

        List<Platforms> finalCats = checkExistPlatforms(plats);
        DatabaseHelper.getEm().getTransaction().begin();
        finalCats.forEach(s -> {
            DatabaseHelper.getEm().persist(s);
        });
        DatabaseHelper.getEm().getTransaction().commit();
    }

    /**
     * Method to check if a videogame has platforms stored that don't exist in the database and remove them
     *
     * @param platforms
     * @return List of platforms
     */
    public static List<Platforms> getExistingGamePlatforms(List<Platforms> platforms) {
        List<Platforms> plats = new ArrayList<>();
        if (platforms.size() != 0) {
            platforms.forEach(p -> {
                Platforms pl = VideogameQuery.getPlatform(p.getName());
                if (pl != null) {
                    plats.add(pl);
                }
            });
        }
        return plats;
    }

    public static List<Platforms> checkExistPlatforms(List<Platforms> platforms) {
        List<Platforms> plats = new ArrayList<>();
        if (platforms.size() != 0) {
            platforms.forEach(p -> {
                Platforms pl = VideogameQuery.getPlatform(p.getName());
                if (pl == null) {
                    plats.add(p);
                }
            });
        }
        return plats;
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
        if (page <= 0) {
            page = 1;
        }
        queryBuilder.append(" ORDER BY Videogame.id OFFSET " + ((page - 1) * OFFSET) + " FETCH NEXT " + OFFSET + " ROWS ONLY");

        System.out.println(queryBuilder);

        Query query = DatabaseHelper.getEm().createNativeQuery(queryBuilder.toString(), Videogame.class);
        return (List<Videogame>) getGamesWithImage(query.getResultList());
    }

    /**
     * Method to get the total count of videogames in a page
     *
     * @param filter
     * @return Total count of pages based on the total result and the offset
     */
    public static int getGamesTotalPageCount(QueryFilter filter) {
        return (int) Math.ceil((double) DatabaseHelper.getEm().createNativeQuery(getGamesWithoutPagination(filter).toString(), Videogame.class).getResultList().size() / OFFSET);
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
            queryBuilder.append(" LEFT JOIN videogame_category on videogame_category.videogame_id = Videogame.id LEFT JOIN category on videogame_category.categories_id = category.id");
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

        if (filter.getScore() > -1) {
            queryBuilder.append(" AND videogame.finalscore >= " + filter.getScore());
        }

        if (filter.getDate() != null) {
            queryBuilder.append(" AND videogame.releasedate >= '" + filter.getDate() + "'");
        }

        return queryBuilder;
    }

    /**
     * Method to get the top 5 videogames
     *
     * @return List of the top 5 videogames
     */
    public static List<Videogame> getGamesTop5() {
        Query query = DatabaseHelper.getEm().createNativeQuery("SELECT * FROM Videogame order by Videogame.finalscore DESC limit 5", Videogame.class);
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
        return DatabaseHelper.getEm().createQuery("SELECT c FROM Category c").getResultList();
    }

    /**
     * Method to get the list of platforms
     *
     * @return List of all platforms on the database
     */
    public static List<Platforms> getAllPlatforms() {
        return DatabaseHelper.getEm().createQuery("SELECT p FROM Platforms p").getResultList();
    }

    /**
     * Method to get the game from the String
     *
     * @param name
     * @return Videogame fetched from the database
     */
    public static Videogame getVideogameByName(String name) {
        try {
            return (Videogame) DatabaseHelper.getEm().createQuery("SELECT v FROM Videogame v WHERE LOWER(v.name) = '" + name.toLowerCase() + "'").getSingleResult();
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
    public static List<Videogame> getGamesWithImage(List<Videogame> videogames) {
        videogames.forEach(v -> {
            if (v.getImagePath() != null && !v.getImagePath().equals("")) {
                try {
                    BufferedImage b = ImageIO.read(new File(v.getImagePath()));
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ImageIO.write(b, "jpeg", bos);
                    byte[] img = bos.toByteArray();
                    v.setGameImage(img);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return videogames;
    }

    public static boolean updateGame(EditVideogame editVideogame) {
        try {
            Videogame game = getVideogameByName(editVideogame.getCurrentName());

            if (game != null) {
                StringBuilder query = new StringBuilder("UPDATE Videogame SET");

                if (editVideogame.getNewName() != null) {
                    query.append(" name = '" + editVideogame.getNewName() + "',");
                }

                if (editVideogame.getDescription() != null) {
                    query.append(" description = '" + editVideogame.getDescription() + "',");
                }

                if (editVideogame.getDeveloper() != null) {
                    query.append(" developer = '" + editVideogame.getDeveloper() + "',");
                }

                if (editVideogame.getPublisher() != null) {
                    query.append(" publisher = '" + editVideogame.getPublisher() + "',");
                }

                if (editVideogame.getReleaseDate() != null) {
                    query.append(" releasedate = '" + editVideogame.getReleaseDate() + "'");
                }

                query.append(" WHERE id = " + game.getID());

                String finalQuery = query.toString().replace(", WHERE", " WHERE");
                DatabaseHelper.getEm().getTransaction().begin();
                DatabaseHelper.getEm().createNativeQuery(finalQuery).executeUpdate();
                DatabaseHelper.getEm().getTransaction().commit();
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static int newScore(double score, String usern, String videogamen) {

        User user = DatabaseHelper.getUser(usern);
        if (user == null) {
            return 2;
        }

        Videogame videogame = getVideogameByName(videogamen);
        if (videogame == null) {
            return 3;
        }

        GameScore gameScore = findScore(usern, videogamen);

        try {
            if (gameScore == null) {

                gameScore = new GameScore(score, usern, videogamen);
                user.getScores().add(gameScore);
                videogame.getScores().add(gameScore);

                videogame.setFinalScore(getAverage(videogame));

                DatabaseHelper.getEm().getTransaction().begin();
                DatabaseHelper.getEm().merge(videogame);
                DatabaseHelper.getEm().merge(user);
                DatabaseHelper.getEm().merge(gameScore);
                DatabaseHelper.getEm().getTransaction().commit();
                return 0;
            }

            if (gameScore != null) {
                GameScore g = findScoreInGame(usern, videogame);

                gameScore.setScore(score);

                int index = videogame.getScores().indexOf(g);
                int index2 = user.getScores().indexOf(g);

                videogame.getScores().set(index, gameScore);
                user.getScores().set(index2, gameScore);
                videogame.setFinalScore(getAverage(videogame));
                DatabaseHelper.getEm().getTransaction().begin();
                DatabaseHelper.getEm().merge(videogame);
                DatabaseHelper.getEm().merge(user);
                DatabaseHelper.getEm().merge(gameScore);
                DatabaseHelper.getEm().getTransaction().commit();

                return 1;
            }
        } catch (Exception ex) {
            return 4;
        }
        return -1;
    }

    public static GameScore findScore(String user, String videogame) {
        try {
            GameScore gs = (GameScore) DatabaseHelper.getEm().createNativeQuery("SELECT * FROM GameScore WHERE LOWER(username) = '" + user.toLowerCase() + "' and LOWER(videogame) = '" + videogame.toLowerCase() + "'", GameScore.class).getSingleResult();
            return gs;
        } catch (Exception ex) {
            return null;
        }
    }

    public static GameScore findScoreInGame(String user, Videogame videogame) {
        return videogame.getScores().stream().filter(r -> r.getUsername().equals(user)).findFirst().orElse(null);
    }

    public static GameScore findScoreInUser(User user, String videogame) {
        return user.getScores().stream().filter(r -> r.getUsername().equals(videogame)).findFirst().orElse(null);
    }


    private static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private static double getAverage(Videogame videogame) {
        double[] finalScore = {0};

        videogame.getScores().forEach(s -> {
            finalScore[0] += s.getScore();
        });

        finalScore[0] /= videogame.getScores().size();

        return round(finalScore[0], 2);
    }


    public static int newRental(String username, String videogame, String initialDate, String finalDate) {


        User user = DatabaseHelper.getUser(username);
        if (user == null) {
            return 2;
        }

        Videogame vgame = getVideogameByName(videogame);
        if (vgame == null) {
            return 3;
        }

        if (initialDate == null) {
            return 4;
        }

        if (finalDate == null) {
            return 5;
        }

        Rental rental = getRental(username, videogame);
        try {
            if (rental == null) {
                rental = new Rental(DateHelper.getDate(initialDate), DateHelper.getDate(finalDate));
                rental.setVideogame(videogame);
                rental.setUsername(username);
                user.getRental().add(rental);
                vgame.getRentals().add(rental);
                DatabaseHelper.getEm().getTransaction().begin();
                DatabaseHelper.getEm().merge(vgame);
                DatabaseHelper.getEm().merge(user);
                DatabaseHelper.getEm().merge(rental);
                DatabaseHelper.getEm().getTransaction().commit();
                return 0;
            }

            if (rental != null) {
                rental.setRentalDate(DateHelper.getDate(initialDate));
                rental.setFinalDate(DateHelper.getDate(finalDate));

                DatabaseHelper.getEm().getTransaction().begin();
                DatabaseHelper.getEm().merge(rental);
                DatabaseHelper.getEm().getTransaction().commit();
                return 1;
            }
        } catch (Exception ex) {
            return 6;
        }
        return -1;
    }

    public static Rental getRental(String username, String videogame) {
        try {
            Rental rental = (Rental) DatabaseHelper.getEm().createNativeQuery("SELECT * FROM Rental WHERE LOWER(username) = '" + username.toLowerCase() + "' and LOWER(videogame) = '" + videogame.toLowerCase() + "'", Rental.class).getSingleResult();
            return rental;
        } catch (Exception ex) {
            return null;
        }
    }

}
