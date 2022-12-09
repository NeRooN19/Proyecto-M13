package tests;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import connexions.ServerThread;
import data.Category;
import data.Platforms;
import data.User;
import data.Videogame;
import db.DatabaseHelper;
import db.VideogameQuery;
import encrypt.Encrypter;
import helpers.EditUser;
import helpers.QueryFilter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
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
            dos.writeUTF(Encrypter.getEncodedString("123456"));
            assertTrue(dis.readBoolean());
            user = (User) ois.readObject();

            assertEquals("Ludox", user.getName());
            assertEquals("ioc@test.com", user.getMail());
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
        Mockito.when(DatabaseHelper.tryRegister(userRegOk)).thenReturn(0);
        assertEquals(0, DatabaseHelper.tryRegister(userRegOk));
    }

    /*
     * Mitjançant Mockito, simularem un registre dbHelper la base de dades.
     * Si envien un User userRegNoOk, ens retornará 1 indicant que l'username ja existeix.
     */
    @Test
    @DisplayName("Register username already exist")
    public void registerNoOkUser() {
        Mockito.when(DatabaseHelper.tryRegister(userRegNoOk)).thenReturn(1);
        assertEquals(1, DatabaseHelper.tryRegister(userRegNoOk));
    }

    /*
     * Mitjançant Mockito, simularem un registre dbHelper la base de dades.
     * Si envien un User userRegNoOk, ens retornará 2 indicant que el mail ja existeix.
     */
    @Test
    @DisplayName("Register mail already exist")
    public void registerNoOkMail() {
        Mockito.when(DatabaseHelper.tryRegister(userRegNoMailOk)).thenReturn(2);
        assertEquals(2, DatabaseHelper.tryRegister(userRegNoMailOk));
    }

    /*
     * Mitjançant Mockito, simularem una busqueda dbHelper la base de dades sobre un usuari.
     * Si envien els credencials d'un usuari existent, ens retornará un usuari existent.
     */
    @Test
    @DisplayName("Check existing user")
    public void checkLoginOk() {
        try (MockedStatic<DatabaseHelper> mocked = Mockito.mockStatic(DatabaseHelper.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(() -> DatabaseHelper.checkLogin("admin", "admin")).thenReturn(user);
            User user = DatabaseHelper.checkLogin("admin", "admin");
            assertEquals(ServerTest.user, user);
        }
    }

    /*
     * Mitjançant Mockito, simularem una busqueda dbHelper la base de dades sobre un usuari.
     * Si envien els credencials d'un usuari NO existent, ens retornará un usuari null.
     */
    @Test
    @DisplayName("Check non existing user")
    public void checkLoginNoOk() {
        try (MockedStatic<DatabaseHelper> mocked = Mockito.mockStatic(DatabaseHelper.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(() -> DatabaseHelper.checkLogin("admin5", "admin")).thenReturn(null);
            assertNull(DatabaseHelper.checkLogin("admin5", "admin"));
        }
    }

    /* TEA 3 */
    /* DatabaseHelper */

    /*
     * Indicant un nom d'usuari per String, ens retorna l'objecte usuari amb les seves dades
     */
    @Test
    @DisplayName("Get user by name")
    public void getUserTest() {
        User user = DatabaseHelper.getUser("admin");
        assertEquals("Ludox", user.getName());
    }

    /*
     * Retorna tots els usuaris registrats a la base de dades
     */
    @Test
    @DisplayName("Get all users")
    public void getUsersTest() {
        List<User> users = DatabaseHelper.getUsers();
        assertEquals(4, users.size());
    }

    /*
     * Métode que actualitza les dades de l'usuari segons quines s'indtrodueixin a l'objecte EditUser.
     * En aquest cas, s'actualitza l'email
     */
    @Test
    @DisplayName("Update user mail")
    public void updateUserTest() {
        EditUser editUser = new EditUser();
        editUser.setUsername("admin");
        editUser.setMail("test@test.com");
        DatabaseHelper.updateUser(editUser);
        User user = DatabaseHelper.getUser("admin");
        assertEquals(editUser.getMail(), user.getMail());
    }

    /*
     * Indicant un nom d'usuari per String i un valor true o false, modificarem els permisos d'administrador d'aquest usuari.
     */
    @Test
    @DisplayName("Make admin True")
    public void makeAdminTrueTest() {
        DatabaseHelper.makeAdmin("admin", true);
        User user = DatabaseHelper.getUser("admin");
        assertTrue(user.isIsAdmin());
    }

    /*
     * Crearem un nou joc a la base de dades. El return ha de ser 0 si ha estat exitòs.
     */
    @Test
    @DisplayName("Create new game")
    public void saveNewGameTest() {
        //String description, String developer, String name, String publisher, Date releaseDate, byte[] gameImage
        Videogame videogame = new Videogame("description", "developer", "name", "publisher", new Date(), null);
        int result = DatabaseHelper.saveNewGame(videogame);
        assertEquals(0, result);
    }

    /*
     * Crearem un nou joc a la base de dades. El return ha de ser 1 si ja existeix aquest nom a la base de dades.
     */
    @Test
    @DisplayName("Create repeated game")
    public void saveExistingGameTest() {
        //String description, String developer, String name, String publisher, Date releaseDate, byte[] gameImage
        Videogame videogame = new Videogame("description", "developer", "name", "publisher", new Date(), null);
        int result = DatabaseHelper.saveNewGame(videogame);
        assertEquals(1, result);
    }

    /*
     * Comprovem que les diferents plataformes no son a la base de dades. Retorna una llista buida.
     */
    @Test
    @DisplayName("Check not existing platform")
    public void checkGameNoExistPlatformsTest() {
        List<Platforms> platformsList = new ArrayList<>();
        platformsList.add(new Platforms("Plataforma Test"));
        List<Platforms> listResult = VideogameQuery.getExistingGamePlatforms(platformsList);
        assertEquals(0, listResult.size());
    }

    /*
     * Comprovem que les diferents plataformes si son a la base de dades. Retorna una llista amb els valor existents.
     */
    @Test
    @DisplayName("Check existing platform")
    public void checkGameExistPlatformsTest() {
        List<Platforms> platformsList = new ArrayList<>();
        platformsList.add(new Platforms("Switch"));
        List<Platforms> listResult = VideogameQuery.getExistingGamePlatforms(platformsList);
        assertEquals(1, listResult.size());
    }

    /*
     * Comprovem que les diferents categories no son a la base de dades. Retorna una llista buida.
     */
    @Test
    @DisplayName("Check not existing category")
    public void checkGameNoExistCategoriesTest() {
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Category Test"));
        List<Category> listResult = VideogameQuery.getExistingGameCategories(categoryList);
        assertEquals(0, listResult.size());
    }

    /*
     * Comprovem que les diferents categories si son a la base de dades. Retorna una llista amb els valor existents.
     */
    @Test
    @DisplayName("Check existing category")
    public void checkGameExistCategoriesTest() {
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Plataforma"));
        List<Category> listResult = VideogameQuery.getExistingGameCategories(categoryList);
        assertEquals(1, listResult.size());
    }

    /*
     * Comprovem el resultat d'executar el query de paginació. Indicant un filtre buit i la pàgina 1, ens retorna els 10 primers videojocs.
     */
    @Test
    @DisplayName("Query paginated")
    public void getQueryPaginatedTest() {
        QueryFilter q = new QueryFilter();
        List<Videogame> a = VideogameQuery.getGamesPaginated(1, q);
        assertEquals(10, a.size());
    }

    /*
     * Crearem un videojoc amb una imatge. Es crearà aquesta mateixa imatge a la carpeta img i s'indicarà el path a la base de dades.
     */
    @Test
    @DisplayName("Save image method test")
    public void saveImageTest() {
        try {
            String filePath = "imagetest\\test-pic.png";
            BufferedImage b = null;
            byte[] img;
            if (filePath != null && !filePath.trim().isEmpty()) {
                b = ImageIO.read(new File(filePath));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(b, "jpeg", bos);
                img = bos.toByteArray();
            } else {
                img = null;
            }
            List<Platforms> platforms = new ArrayList<>();
            platforms.add(new Platforms("Switch"));

            Videogame videogame = new Videogame("test", "test", "test: game 1", "test", new Date(), img);
            videogame.setPlatforms(platforms);
            int result = DatabaseHelper.saveNewGame(videogame);
            assertEquals(0, result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* VideogameQuery */


    /*
     * Cerca de categoria per nom
     */
    @Test
    @DisplayName("Get category by name")
    public void getCategoryTest() {
        Category category = VideogameQuery.getCategory("Plataforma");
        assertEquals("Plataforma", category.getCategory());
    }

    /*
     * Creació de categoria per nom
     */
    @Test
    @DisplayName("Create category by name")
    public void createCategoryTest() {
        VideogameQuery.createCategory("Test");
        Category category = VideogameQuery.getCategory("Test");
        assertEquals("Test", category.getCategory());
    }

    /*
     * Cerca de totes les categories existents
     */
    @Test
    @DisplayName("Get all categories")
    public void getCategoriesTest() {
        List<Category> cat = VideogameQuery.getAllCategories();
        assertEquals("Plataforma", cat.get(0).getCategory());
    }

    /*
     * Creació de categories a través d'una llista
     */
    @Test
    @DisplayName("Create multiple categories")
    public void createMultipleCategoriesTest() {
        List<String> categoryList = new ArrayList<>();
        categoryList.add("Test 01");
        categoryList.add("Test 02");
        categoryList.add("Test 03");
        VideogameQuery.createMultipleCategories(categoryList);

        List<Category> categories = VideogameQuery.getAllCategories();
        assertEquals("Test 01", categories.get(1).getCategory());
    }

    /*
     * Cerca de plataforma per nom
     */
    @Test
    @DisplayName("Get platform by name")
    public void getPlatformTest() {
        Platforms platforms = VideogameQuery.getPlatform("Switch");
        assertEquals("Switch", platforms.getName());
    }

    /*
     * Creació de plataforma per nom
     */
    @Test
    @DisplayName("Create platform by name")
    public void createPlatformTest() {
        VideogameQuery.createPlatform("Platform test");
        Platforms platforms = VideogameQuery.getPlatform("Platform test");
        assertEquals("Platform test", platforms.getName());
    }

    /*
     * Cerca de totes les plataformes existents
     */
    @Test
    @DisplayName("Get all platforms")
    public void getPlatformsTest() {
        List<Platforms> plat = VideogameQuery.getAllPlatforms();
        assertEquals("Switch", plat.get(0).getName());
    }

    static QueryFilter queryFilter = new QueryFilter();

    /*
     * Mostra d'obtenció de videojocs paginats
     */
    @Test
    @DisplayName("Get videogames paginated")
    public void getGamesPaginatedTest() {
        int page = 1;

        List<Videogame> videogameList = VideogameQuery.getGamesPaginated(page, queryFilter);
        assertEquals(10, videogameList.size());

        List<Videogame> videogameList2 = VideogameQuery.getGamesPaginated(2, queryFilter);
        assertEquals(4, videogameList2.size());
    }

    /*
     * Mostra d'obtenció del total de pàgines que ocupen els videojocs agrupats de 10 en 10
     */
    @Test
    @DisplayName("Get total videogame pages")
    public void getGamesTotalPageCountTest() {
        int count = VideogameQuery.getGamesTotalPageCount(queryFilter);
        assertEquals(2, count);
    }

    /*
     * Obtenció del top 5 videojocs per nota mitja
     */
    @Test
    @DisplayName("Get top 5 videogames")
    public void getGamesTop5Test() {
        List<Videogame> top5 = VideogameQuery.getGamesTop5();
        assertEquals(5, top5.size());
    }

    /*
     * Cerca de videojoc per nom
     */
    @Test
    @DisplayName("Get videogame by name")
    public void getVideogameByNameTest() {
        Videogame videogame = VideogameQuery.getVideogameByName("Zelda");
        assertEquals("Zelda", videogame.getName());
    }

}
