package tests;

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
import org.junit.jupiter.api.AfterAll;
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

    /**
     * Declarem totes les variables necessaries per dbHelper les probes
     */
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

    /*
     * Fem un Mock de la classe DatabaseHelper per dbHelper evitar realitzar consultes reals dbHelper la base de dades actual
     */
    @Mock
    DatabaseHelper dbHelper = Mockito.mock(DatabaseHelper.class);

    /*
     * Iniciem les variables necessaries abans de tots els tests
     */
    @BeforeAll
    public static void setUpClass() {
        try {
            //User to test register ok
            user = new User();
            user.setIsAdmin(true);
            user.setMail("mock@mock.com");
            user.setName("Mock");
            user.setPassword("123456");
            user.setUsername("mock");

            userRegOk = new User();
            userRegOk.setIsAdmin(true);
            userRegOk.setMail("mock@mock.com");
            userRegOk.setName("Mock");
            userRegOk.setPassword("mock");
            userRegOk.setUsername("mock");

            //User to test register username already exists, same as userRegOk
            userRegNoOk = new User();
            userRegNoOk.setIsAdmin(true);
            userRegNoOk.setMail("mock@mock.com");
            userRegNoOk.setName("Mock");
            userRegNoOk.setPassword("mock");
            userRegNoOk.setUsername("mock");

            //User to test register mail already exists, same as UserRegOk
            userRegNoMailOk = new User();
            userRegNoMailOk.setIsAdmin(true);
            userRegNoMailOk.setMail("mock@mock.com");
            userRegNoMailOk.setName("Mock");
            userRegNoMailOk.setPassword("mock");
            userRegNoMailOk.setUsername("mock2");

            //Server conexion to test real database acces
            svThread = new ServerThread(5000, null);
            svThread.start();

            //client conexion to send petitions to the server
            socket = new Socket("localhost", 5000);

            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

        } catch (IOException ex) {
            Logger.getLogger(ServerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
     * Tanquem totes les conexions i streams en acabar els tests
     */
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

    /*
     * Test en el que comprobarem, mitjançant una conexió i una petició real, si l'usuari existeix.
     * Enviant l'usuari admin amb contasenya admin rebrem un objecte de tipus
     * User amb les dades: 
     * User: admin 
     * Mail: admin
     * Administrador: true
     * Password: null
     */
    @Test
    @DisplayName("Login test true")
    public void loginTestOk() {
        try {
            dos.writeByte(1);
            dos.writeUTF("admin");
            dos.writeUTF("123456");
            assertTrue(dis.readBoolean());
            user = (User) ois.readObject();

            assertEquals("admin", user.getName());
            assertEquals("admin", user.getMail());
            assertTrue(user.isIsAdmin());
            assertNull(user.getPassword());
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ServerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
     * Test en el que comprobarem, mitjançant una conexió i una petició real, si l'usuari NO existeix.
     * Enviant l'usuari admin2 amb contasenya admin rebrem un boolean false
     * indicant que no es troba dbHelper la base de dades
     */
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

    /*
     * Mitjançant Mockito, simularem un registre dbHelper la base de dades. 
     * Si envien un User userRegOk, ens retornará 0 indicant que s'ha registrat sense problemes.
     */
    @Test
    @DisplayName("Register new user")
    public void registerOk() {
        Mockito.when(dbHelper.tryRegister(userRegOk)).thenReturn(0);
        assertEquals(0, dbHelper.tryRegister(userRegOk));
    }

    /*
     * Mitjançant Mockito, simularem un registre dbHelper la base de dades.
     * Si envien un User userRegNoOk, ens retornará 1 indicant que l'username ja existeix.
     */
    @Test
    @DisplayName("Register username already exist")
    public void registerNoOkUser() {
        Mockito.when(dbHelper.tryRegister(userRegNoOk)).thenReturn(1);
        assertEquals(1, dbHelper.tryRegister(userRegNoOk));
    }

    /*
     * Mitjançant Mockito, simularem un registre dbHelper la base de dades.
     * Si envien un User userRegNoOk, ens retornará 2 indicant que el mail ja existeix.
     */
    @Test
    @DisplayName("Register mail already exist")
    public void registerNoOkMail() {
        Mockito.when(dbHelper.tryRegister(userRegNoMailOk)).thenReturn(2);
        assertEquals(2, dbHelper.tryRegister(userRegNoMailOk));
    }

    /*
     * Mitjançant Mockito, simularem una busqueda dbHelper la base de dades sobre un usuari.
     * Si envien els credencials d'un usuari existent, ens retornará un usuari existent.
     */
    @Test
    @DisplayName("Check existing user")
    public void checkLoginOk() {
        Mockito.when(dbHelper.checkLogin("admin", "admin")).thenReturn(user);
        User user = dbHelper.checkLogin("admin", "admin");
        assertEquals(this.user, user);
    }

    /*
     * Mitjançant Mockito, simularem una busqueda dbHelper la base de dades sobre un usuari.
     * Si envien els credencials d'un usuari NO existent, ens retornará un usuari null.
     */
    @Test
    @DisplayName("Check non existing user")
    public void checkLoginNoOk() {
        Mockito.when(dbHelper.checkLogin("admin5", "admin")).thenReturn(null);
        User user = dbHelper.checkLogin("admin5", "admin");
        assertNull(user);
    }

}
