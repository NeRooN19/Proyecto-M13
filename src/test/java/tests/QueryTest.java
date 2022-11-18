package tests;

import connexions.ServerThread;
import data.Category;
import data.Platforms;
import data.Videogame;
import db.VideogameQuery;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * @author NeRooN
 */
@DisplayName("Database query testing")
public class QueryTest {

    private static Socket socket;
    private static DataInputStream dis;
    private static DataOutputStream dos;
    private static ObjectInputStream ois;
    private static ObjectOutputStream oos;
    private static ServerThread svThread;

    @BeforeAll
    public static void setUpClass() {
        try {
            svThread = new ServerThread(5000, null);
            svThread.start();
            socket = new Socket("localhost", 5000);

            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(ServerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @AfterAll
    public static void tearDownClass() {
        try {
            dis.close();
            dos.close();
            ois.close();
            oos.close();
            socket.close();
            svThread.getServer().close();
        } catch (IOException ex) {
            Logger.getLogger(ServerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    @DisplayName("Top 5")
    public void getTop5Test() {
        try {
            dos.write((byte) 3);
            List<Videogame> vi = (List<Videogame>) ois.readObject();
            assertEquals(vi.get(0).getName(), "a");

        } catch (IOException ex) {
            Logger.getLogger(QueryTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(QueryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    @DisplayName("Query paginated")
    public void getQueryPaginatedTest() {

    }

    @Test
    @DisplayName("Categories")
    public void getCategoriesTest() {
        List<Category> cat = VideogameQuery.getAllCategories();
        assertEquals("Plataforma", cat.get(0).getCategory());
    }

    @Test
    @DisplayName("Platforms")
    public void getPlatformsTest() {
        List<Platforms> plat = VideogameQuery.getAllPlatforms();
        assertEquals("Switch", plat.get(0).getName());
    }
}
