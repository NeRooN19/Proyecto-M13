/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import connexions.ServerThread;
import data.User;
import db.DatabaseHelper;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 *
 * @author NeRooN
 */
@DisplayName("Database connections testing")
public class ServerTest {

    private static Socket socket;
    private static DataInputStream dis;
    private static DataOutputStream dos;
    private static ObjectInputStream ois;
    private static ObjectOutputStream oos;
    private static User user;
    private static User userRegOk;
    private static User userRegNoOk;
    private static User userRegNoMailOk;
    private static ServerThread svThread;

    @Mock
    DatabaseHelper a = Mockito.mock(DatabaseHelper.class);

    public ServerTest() {

    }

    @BeforeAll
    public static void setUpClass() {
        try {
            //User to test register ok
            userRegOk = new User();
            userRegOk.setIsAdmin(true);
            userRegOk.setMail("mock@mock.com");
            userRegOk.setName("Mock");
            userRegOk.setPassword("mock");
            userRegOk.setUsername("mock");

            //User to test register username already exists
            userRegNoOk = new User();
            userRegNoOk.setIsAdmin(true);
            userRegNoOk.setMail("mock@mock.com");
            userRegNoOk.setName("Mock");
            userRegNoOk.setPassword("mock");
            userRegNoOk.setUsername("mock");

            //User to test register mail already exists
            userRegNoMailOk = new User();
            userRegNoMailOk.setIsAdmin(true);
            userRegNoMailOk.setMail("mock@mock.com");
            userRegNoMailOk.setName("Mock");
            userRegNoMailOk.setPassword("mock");
            userRegNoMailOk.setUsername("mock2");

            svThread = new ServerThread(5000, null);
            svThread.start();
            socket = new Socket("localhost", 5000);

            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
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
    @DisplayName("Login test true")
    public void loginTestOk() {
        try {
            dos.writeByte(1);
            dos.writeUTF("admin");
            dos.writeUTF("admin");
            assertTrue(dis.readBoolean());
            user = (User) ois.readObject();
            assertEquals("Ludox", user.getName());
            assertEquals("admin@admin.com", user.getMail());
            assertTrue(user.isIsAdmin());
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ServerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    @DisplayName("Login test false")
    public void loginTestNoOk() {
        try {
            dos.writeByte(1);
            dos.writeUTF("admin");
            dos.writeUTF("admin2");
            assertFalse(dis.readBoolean());
        } catch (IOException ex) {
            Logger.getLogger(ServerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    @DisplayName("Register new user")
    public void registerOk() {
        Mockito.when(a.tryRegister(userRegOk)).thenReturn(0);
        assertEquals(0, a.tryRegister(userRegOk));
    }

    @Test
    @DisplayName("Register username already exist")
    public void registerNoOkUser() {
        Mockito.when(a.tryRegister(userRegNoOk)).thenReturn(1);
        assertEquals(1, a.tryRegister(userRegNoOk));
    }

    @Test
    @DisplayName("Register mail already exist")
    public void registerNoOkMail() {
        Mockito.when(a.tryRegister(userRegNoMailOk)).thenReturn(2);
        assertEquals(2, a.tryRegister(userRegNoMailOk));
    }

    @Test
    @DisplayName("Check existing user")
    public void checkLoginOk() {
        Mockito.when(a.checkLogin("admin", "admin")).thenReturn(user);
        User user = a.checkLogin("admin", "admin");
        assertEquals(this.user, user);
    }

    @Test
    @DisplayName("Check non existing user")
    public void checkLoginNoOk() {
        Mockito.when(a.checkLogin("admin5", "admin")).thenReturn(null);
        User user = a.checkLogin("admin5", "admin");
        assertNull(user);
    }

}
