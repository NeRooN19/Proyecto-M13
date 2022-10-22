/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import data.User;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;

/**
 *
 * @author NeRooN
 */
public class DatabaseHelper {

    private static final String CONFIG = "config.properties";
    private EntityManager em;
    private DataInputStream dis;
    private DataOutputStream dos;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public DatabaseHelper(EntityManager em, DataInputStream dis, DataOutputStream dos, ObjectInputStream ois, ObjectOutputStream oos) {
        this.em = em;
        this.dis = dis;
        this.dos = dos;
        this.ois = ois;
        this.oos = oos;

    }

    public static Map<String, Object> getEntityManager() {
        Map<String, Object> persistenceConfig = new HashMap<>();
        Properties properties = getConfig();
        persistenceConfig.put("javax.persistence.jdbc.driver", properties.get("DRIVER"));
        persistenceConfig.put("javax.persistence.jdbc.password", properties.get("PASS"));
        persistenceConfig.put("javax.persistence.schema-generation.database.action", properties.get("GENERATE"));
        persistenceConfig.put("javax.persistence.jdbc.url", properties.get("JDBC"));
        persistenceConfig.put("javax.persistence.jdbc.user", properties.get("USER"));

        return persistenceConfig;
    }

    public static Properties getConfig() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(CONFIG)));
            return properties;
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
        } catch (IOException e) {
            //  e.printStackTrace();
        }
        return null;
    }

    public User checkLogin(String username, String password) {
        User user = em.find(User.class, username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public void doLogin() {
        try {
            String username = dis.readUTF();
            String password = dis.readUTF();

            User user = checkLogin(username, password);

            if (user == null) {
                dos.writeBoolean(false);
            } else {
                dos.writeBoolean(true);
                oos.writeObject(user);
            }
        } catch (IOException ex) {
            // Logger.getLogger(ServerConnexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int tryRegister(User user) {
        try {
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

}
