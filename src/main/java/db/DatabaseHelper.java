/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import data.*;
import encrypt.Encrypter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.*;
import java.util.*;

/**
 * @author NeRooN
 */
public class DatabaseHelper {

    private static final String PATH = "img\\";
    private static final String CONFIG = "config.properties";
    public static EntityManager em;
    private static EntityManagerFactory emf;
    private final DataInputStream dis;
    private final DataOutputStream dos;
    private final ObjectInputStream ois;
    private final ObjectOutputStream oos;

    /**
     * Constructor of the class
     *
     * @param dis data input stream
     * @param dos data output stream
     * @param ois object input stream
     * @param oos object output stream
     */
    public DatabaseHelper(DataInputStream dis, DataOutputStream dos, ObjectInputStream ois, ObjectOutputStream oos) {
        emf = Persistence.createEntityManagerFactory("ludox", getEntityManager());
        em = emf.createEntityManager();
        this.dis = dis;
        this.dos = dos;
        this.ois = ois;
        this.oos = oos;
        if (em != null) {
            System.out.println("not null");
        }
    }

    /**
     * Method to get the entity manager config from a given file
     *
     * @return Map with the entity manager config
     */
    public static Map<String, Object> getEntityManager() {
        Map<String, Object> persistenceConfig = new HashMap<>();
        Properties properties = getConfig();
        persistenceConfig.put("jakarta.persistence.jdbc.driver", properties.get("DRIVER"));
        persistenceConfig.put("jakarta.persistence.jdbc.password", properties.get("PASS"));
        persistenceConfig.put("jakarta.persistence.jdbc.url", properties.get("JDBC"));
        persistenceConfig.put("jakarta.persistence.jdbc.user", properties.get("USER"));
        persistenceConfig.put("jakarta.persistence.schema-generation.database.action", properties.get("GENERATE"));

        return persistenceConfig;
    }

    /**
     * Method to get the file where the entity manager config is
     *
     * @return Properties file
     */
    public static Properties getConfig() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(CONFIG));
            return properties;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method to get the User given a username and password.
     *
     * @param username the username from the user
     * @param password the password from the user
     * @return Null if the user doesn't exist or the user if it exists
     */
    public static User checkLogin(String username, String password) {
        try {
            User user = (User) em.createQuery("SELECT u FROM User u WHERE LOWER(u.username) = LOWER(?1)").setParameter(1, username).getSingleResult();
            if (user != null && Encrypter.getDecryptedString(user.getPassword()).equals(password)) {
                em.detach(user);
                return user;
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    /**
     * Method to manage the petitions from a login It will receive the username and the password as a plain string Then it checks if the user exists Finally it
     * will send a boolean to the client. If it's true, it will also send the user.
     */
    public void doLogin() {
        try {
            String username = dis.readUTF();
            String password = dis.readUTF();

            User user = checkLogin(username, password);

            if (user == null) {
                dos.writeBoolean(false);
            } else {
                dos.writeBoolean(true);
                user.setPassword(null);
                oos.writeObject(user);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method to register a user into the databse.
     *
     * @param user User object to register into the database
     * @return a byte code for the client to be able to check if it succeeded or not
     */
    public int tryRegister(User user) {
        try {
            user.setPassword(Encrypter.getEncodedString(user.getPassword()));
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return 0;
        } catch (Exception ex) {
            if (ex.getMessage().contains("usuarios_pkey")) {
                System.out.println("user");
                return 1;
            }

            if (ex.getMessage().contains("usuarios_mail_key")) {
                System.out.println("mail");
                return 2;
            }
        }
        return 3;
    }

    private static User getUser(String username) {
        User user = (User) em.createQuery("SELECT u FROM User u WHERE LOWER(u.username) = LOWER(?1)").setParameter(1, username).getSingleResult();
        return user;
    }

    public static List<User> getUsers() {
        return (List<User>) em.createQuery("SELECT u FROM User u").getResultList();

    }

    public static void updateUser(EditUser editData) {
        try {
            User user = getUser(editData.getUsername());

            if (user != null) {
                StringBuilder query = new StringBuilder("UPDATE User SET");

                if (editData.getName() != null) {
                    query.append(" name = '" + editData.getName() + "'");
                }

                if (editData.getMail() != null) {
                    if (editData.getName() != null) {
                        query.append(",");
                    }
                    query.append(" mail = '" + editData.getMail() + "'");
                }

                if (editData.getPassword() != null) {
                    if (editData.getName() != null) {
                        query.append(",");
                    } else if (editData.getMail() != null) {
                        query.append(",");
                    }
                    query.append(" password = '" + Encrypter.getEncodedString(editData.getPassword()) + "'");
                }

                query.append(" WHERE username = '" + editData.getUsername() + "'");

                em.getTransaction().begin();
                em.createQuery(query.toString()).executeUpdate();
                em.getTransaction().commit();
            }
        } catch (Exception ex) {

        }
    }

    public static void makeAdmin(String username, boolean admin) {
        try {
            User user = getUser(username);
            if (user != null) {
                String query = "UPDATE User SET isAdmin = " + admin + " WHERE username = '" + username + "'";
                em.getTransaction().begin();
                em.createQuery(query).executeUpdate();
                em.getTransaction().commit();
            }
        } catch (Exception ex) {

        }
    }

    public static int saveNewGame(Videogame videogame) {
        if (VideogameQuery.getVideogameByName(videogame.getName()) == null) {
            if (videogame.getCategories() != null) {
                videogame.setCategories(checkGameCategories(videogame.getCategories()));
            }
            if (videogame.getPlatforms() != null) {
                videogame.setPlatforms(checkGamePlatforms(videogame.getPlatforms()));
            }
            if (videogame.getGameImage() != null) {
                videogame.setImagePath(PATH + pathName(videogame.getName()));
                saveImage(videogame);
            } else {
                videogame.setImagePath("");
            }
            em.getTransaction().begin();
            em.persist(videogame);
            em.getTransaction().commit();
            return 0;
        }
        return 1;
    }

    private static List<Platforms> checkGamePlatforms(List<Platforms> platforms) {
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

    private static List<Category> checkGameCategories(List<Category> categories) {
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

    private static void saveImage(Videogame videogame) {

        try (FileOutputStream stream = new FileOutputStream(videogame.getImagePath())) {
            stream.write(videogame.getGameImage());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String pathName(String gameName) {
        return gameName.replaceAll("[^a-zA-Z0-9 ]", "").replaceAll(" ", "-").toLowerCase() + ".png";
    }
}
