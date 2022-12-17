/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import data.*;
import encrypt.Encrypter;
import helpers.EditUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        this.dis = dis;
        this.dos = dos;
        this.ois = ois;
        this.oos = oos;
        if (em != null) {
            System.out.println("not null");
        }
    }

    public static void initDatabaseConnection() {
        emf = Persistence.createEntityManagerFactory("ludox", getEntityManager());
        em = emf.createEntityManager();
        createPathFolder();
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
            User user = getUser(username);
            if (user != null && (getUserStatus(username) == null || getUserStatus(username).isEnabled()) && Encrypter.getDecryptedString(user.getPassword()).equals(password)) {
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
    public static int tryRegister(User user) {
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

    /**
     * Nuevo para TEA3
     */
    /**
     * Method to get a user from the database from a given String
     *
     * @param username
     * @return User fetched from the database
     */
    public static User getUser(String username) {
        try {
            return (User) em.createQuery("SELECT u FROM User u WHERE LOWER(u.username) = LOWER('" + username.toLowerCase() + "')").getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Method to get all the users from the database
     *
     * @return
     */
    public static List<User> getUsers() {
        return (List<User>) em.createNativeQuery("select usuarios.id, usuarios.admin, usuarios.mail, usuarios.name, usuarios.password, usuarios.username from usuarios left join userenabled on usuarios.username = userenabled.username where (userenabled.isenabled IS null OR userenabled.isenabled = false)", User.class).getResultList();
    }

    /**
     * Method to update a specific user given a EditUser object, where the data to update is stored
     *
     * @param editData
     */
    public static boolean updateUser(EditUser editData) {
        try {
            User user = getUser(editData.getUsername());

            if (user != null) {
                StringBuilder query = new StringBuilder("UPDATE Usuarios SET");

                if (editData.getName() != null) {
                    query.append(" name = '" + editData.getName() + "',");
                }

                if (editData.getMail() != null) {
                    query.append(" mail = '" + editData.getMail() + "',");
                }

                if (editData.getPassword() != null) {
                    query.append(" password = '" + Encrypter.getEncodedString(editData.getPassword()) + "'");
                }

                query.append(" WHERE username = '" + editData.getUsername() + "'");

                String finalQuery = query.toString().replace(", WHERE", " WHERE");

                em.getTransaction().begin();
                em.createNativeQuery(finalQuery).executeUpdate();
                em.getTransaction().commit();
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Method to update a user to make it admin true/false
     *
     * @param username
     * @param admin
     */
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

    /**
     * Method to create a new videogame if it doesn't exist in the database
     *
     * @param videogame
     * @return Int with the result value. 0 for an ok and 1 for error
     */
    public static int saveNewGame(Videogame videogame) {
        if (VideogameQuery.getVideogameByName(videogame.getName()) == null) {
            if (videogame.getCategories() != null) {
                videogame.setCategories(VideogameQuery.getExistingGameCategories(videogame.getCategories()));
            }
            if (videogame.getPlatforms() != null) {
                videogame.setPlatforms(VideogameQuery.getExistingGamePlatforms(videogame.getPlatforms()));
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

    /**
     * Method to save a videogame image in the image folder
     *
     * @param videogame
     */
    private static void saveImage(Videogame videogame) {

        try (FileOutputStream stream = new FileOutputStream(videogame.getImagePath())) {
            stream.write(videogame.getGameImage());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to generate a name for the image file from the videogame name without symbols
     *
     * @param gameName
     * @return String with the new path game for the videogame
     */
    private static String pathName(String gameName) {
        return gameName.replaceAll("[^a-zA-Z0-9 ]", "").replaceAll(" ", "-").toLowerCase() + ".jpeg";
    }

    public static EntityManager getEm() {
        return em;
    }

    public static void createPathFolder() {
        try {
            if (!new File(PATH).exists()) {
                Files.createDirectories(Paths.get(PATH));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateUserStatus(String username, boolean enabled) {
        User user = getUser(username);
        UserEnabled userEnabled = getUserStatus(username);

        if (userEnabled == null) {
            if (user != null && !user.isIsAdmin()) {
                userEnabled = new UserEnabled();
                userEnabled.setUsername(username);
            }
        }

        userEnabled.setEnabled(enabled);

        em.getTransaction().begin();
        em.merge(userEnabled);
        em.getTransaction().commit();
    }

    public static UserEnabled getUserStatus(String user) {
        try {
            return (UserEnabled) em.createQuery("SELECT ue FROM UserEnabled ue WHERE LOWER(ue.username) = '" + user.toLowerCase() + "'").getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    public static void generateData() {
        Random r = new Random();
        List<String> platforms = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            categories.add("Category" + i);
            platforms.add("Platform" + i);
            VideogameQuery.createMultipleCategories(categories);
            VideogameQuery.createMultiplePlatforms(platforms);
        }

        List<Videogame> v = new ArrayList<>();
        List<Platforms> platforms2 = new ArrayList<>();
        List<Category> categories2 = new ArrayList<>();
        for (int i = 1; i < 26; i++) {
            int random = r.nextInt(5);
            if (i == 1) {
                platforms2.add(new Platforms(platforms.get(random)));
                categories2.add(new Category(categories.get(random)));
            }
            Videogame video = new Videogame("Description " + i, "Developer" + i, "Videogame " + i, "Publisher" + i, new Date(), null, platforms2, categories2);
            video.setImagePath("default-placeholder.jpeg");
            v.add(video);
        }
        VideogameQuery.getGamesWithImage(v);
        v.forEach(x -> DatabaseHelper.saveNewGame(x));

        for (int i = 0; i < 5; i++) {
            User user = new User(Encrypter.getEncodedString("123456"), "user" + i, "Name" + i, "mail" + i + "@mail.com");
            //user.setIsAdmin(r.nextInt(100) < 80 ? false : true);
            user.setIsAdmin(false);
            DatabaseHelper.tryRegister(user);
        }
    }

}
