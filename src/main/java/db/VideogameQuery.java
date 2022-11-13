/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import data.Category;
import data.Platforms;
import data.Videogame;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 * @author NeRooN
 */
public class VideogameQuery {

    private static final int OFFSET = 10;

    public static List<Videogame> getGamesByCategory(List<String> queries) {

        List<String> existCategory = existingCategories(queries);

        Query filter = DatabaseHelper.em.createQuery(categortyFilterBuilder(existCategory));

        int[] i = {1};

        existCategory.forEach(q -> {
            Category cat = getCategory(q);
            filter.setParameter(i[0]++, cat);
        });

        return (List<Videogame>) filter.getResultList();
    }

    public static String categortyFilterBuilder(List<String> categories) {
        int[] i = {2};
        StringBuilder baseQuery = new StringBuilder("SELECT v FROM Videogame v WHERE v.category = ?1");
        String filteredQuery = baseQuery.toString();

        if (categories.size() > 1) {
            categories.forEach(q -> {
                baseQuery.append(" AND v.category = ?" + i[0]++);
            });
            filteredQuery = baseQuery.substring(0, baseQuery.length() - 20);
        }
        return filteredQuery;
    }

    public static List<String> existingCategories(List<String> categories) {

        List<String> existingCategories = new ArrayList<>();

        categories.forEach(q -> {
            try {
                Category cat = getCategory(q);
                existingCategories.add(q);
            } catch (Exception ex) {

            }
        });
        return existingCategories;
    }

    private static Category getCategory(String category) {
        return (Category) DatabaseHelper.em.createQuery("SELECT s FROM Category s WHERE LOWER(s.category) = LOWER('" + category + "')").getSingleResult();
    }

    private static void createCategory(String category) {
        Category c = new Category(category);
        DatabaseHelper.em.getTransaction().begin();
        DatabaseHelper.em.persist(c);
        DatabaseHelper.em.getTransaction().commit();
    }

    private static void createMultipleCategories(List<String> categories) {
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

    public static List<Videogame> getGameByMinScore(float score) {
        String query = "SELECT v FROM Videogame v WHERE v.finalScore >= " + score;
        return DatabaseHelper.em.createQuery(query).getResultList();
    }

    public static List<Videogame> getGameByMaxScore(float score) {
        String query = "SELECT v FROM Videogame v WHERE v.finalScore <= " + score;
        return DatabaseHelper.em.createQuery(query).getResultList();
    }

    public static List<Videogame> getGameByExactScore(float score) {
        String query = "SELECT v FROM Videogame v WHERE v.finalScore = " + score;
        return DatabaseHelper.em.createQuery(query).getResultList();
    }

    public static List<Videogame> getGameByRangeScore(float min, float max) {
        String query = "SELECT v FROM Videogame v WHERE v.finalScore >= " + min + " AND v.finalScore <= " + max;
        return DatabaseHelper.em.createQuery(query).getResultList();
    }

    public static List<Videogame> getGameByScoreAndDate(float score, String date) {
        String query = "SELECT v FROM Videogame v WHERE v.finalScore >= " + score + " AND v.releaseDate >= '" + date + "'";
        return DatabaseHelper.em.createQuery(query).getResultList();
    }

    public static List<Videogame> getGameByPlatform(String platformName) {
        Platforms platform = getPlatform(platformName);
        Query query = DatabaseHelper.em.createQuery("SELECT v FROM Videogame WHERE v.platforms = ?1");
        return query.setParameter(1, platform).getResultList();
    }

    public static List<Videogame> getGamesPaginated(int page, String platform, String category, float score) {
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Videogame");
        if (platform != null) {
            queryBuilder.append(" LEFT JOIN videogame_platforms on videogame_platforms.videogame_id = Videogame.id LEFT JOIN platforms on videogame_platforms.platforms_id = platforms.id");
        }

        if (category != null) {
            queryBuilder.append(" LEFT JOIN videogame_category on videogame_category.videogame_id = Videogame.id LEFT JOIN category on videogame_category.category_id = category.id");
        }



        if (platform != null) {
            queryBuilder.append(" WHERE platforms.name = '" + platform + "'");
        }

        if (platform != null && category != null) {
            queryBuilder.append(" AND category.category = '" + category + "'");
        }

        if (platform == null && category != null) {
            queryBuilder.append(" WHERE category.category = '" + category + "'");
        }



        queryBuilder.append(" ORDER BY Videogame.id OFFSET " + ((page - 1) * OFFSET) + " FETCH NEXT " + OFFSET + " ROWS ONLY");

        System.out.println(queryBuilder.toString());

        Query query = DatabaseHelper.em.createNativeQuery(queryBuilder.toString(), Videogame.class);
        return query.getResultList();
    }

    public static List<Videogame> getGamesTop5() {
        Query query = DatabaseHelper.em.createNativeQuery("SELECT * FROM Videogame order by Videogame.finalscore DESC limit 5", Videogame.class);
        List<Videogame> vi = query.getResultList();
        vi.forEach(v -> System.out.println(v.getName()));
        return query.getResultList();
    }

    public static List<Category> getAllCategories() {
        return DatabaseHelper.em.createQuery("SELECT c FROM Category c").getResultList();
    }

    public static List<Platforms> getAllPlatforms() {
        return DatabaseHelper.em.createQuery("SELECT p FROM Platforms p").getResultList();
    }

    public static Platforms getPlatform(String platform) {
        return (Platforms) DatabaseHelper.em.createQuery("SELECT p FROM Platforms p WHERE LOWER(p.name) = LOWER('" + platform + "')").getSingleResult();
    }
}
