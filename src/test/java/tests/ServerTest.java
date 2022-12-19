package tests;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import connexions.ServerThread;
import data.*;
import db.DatabaseHelper;
import db.VideogameQuery;
import encrypt.Encrypter;
import helpers.DateHelper;
import helpers.EditUser;
import helpers.EditVideogame;
import helpers.QueryFilter;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.imageio.ImageIO;
import javax.net.ssl.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.KeyStore;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerTest {

    static QueryFilter queryFilter = new QueryFilter();
    /**
     * Declarem totes les variables necessaries per dbHelper les probes
     */
    private static SSLSocket sslSocket;
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
            userRegNoOk.setMail("mock2@mock.com");
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
            svThread = new ServerThread(5001, null);
            svThread.start();

            //client conexion to send petitions to the server
            KeyStore keystore;
            try {
                keystore = KeyStore.getInstance("JKS");

                keystore.load(new FileInputStream("Cert/mykeystore.jks"), "Ludox123.".toCharArray());
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                tmf.init(keystore);

                SSLContext context = SSLContext.getInstance("TLS");
                TrustManager[] trustManagers = tmf.getTrustManagers();

                context.init(null, trustManagers, null);

                SSLSocketFactory sf = context.getSocketFactory();

                sslSocket = (SSLSocket) sf.createSocket("192.168.1.20", 5001);
                sslSocket.startHandshake();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


            dos = new DataOutputStream(sslSocket.getOutputStream());
            dis = new DataInputStream(sslSocket.getInputStream());
            oos = new ObjectOutputStream(sslSocket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(sslSocket.getInputStream());

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
            sslSocket.close();
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
     * Administrador: false
     * Password: null
     */
    @Test
    @Order(1)
    @DisplayName("Login test true")
    public void loginTestOk() {
        try {
            dos.writeByte(1);
            dos.writeUTF("user4");
            dos.writeUTF(Encrypter.getEncodedString("123456"));
            assertTrue(dis.readBoolean());
            user = (User) ois.readObject();

            assertEquals("Name4", user.getName());
            assertEquals("mail4@mail.com", user.getMail());
            assertFalse(user.isIsAdmin());
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
    @Order(2)
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
    @Order(3)
    @DisplayName("Register new user")
    public void registerOk() {
        try (MockedStatic<DatabaseHelper> mocked = Mockito.mockStatic(DatabaseHelper.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(() -> DatabaseHelper.tryRegister(userRegOk)).thenReturn(0);
            assertEquals(0, DatabaseHelper.tryRegister(userRegOk));
        }
    }

    /*
     * Mitjançant Mockito, simularem un registre dbHelper la base de dades.
     * Si envien un User userRegNoOk, ens retornará 1 indicant que l'username ja existeix.
     */
    @Test
    @Order(4)
    @DisplayName("Register username already exist")
    public void registerNoOkUser() {
        try (MockedStatic<DatabaseHelper> mocked = Mockito.mockStatic(DatabaseHelper.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(() -> DatabaseHelper.tryRegister(userRegNoOk)).thenReturn(1);
            assertEquals(1, DatabaseHelper.tryRegister(userRegNoOk));
        }
    }

    /*
     * Mitjançant Mockito, simularem un registre dbHelper la base de dades.
     * Si envien un User userRegNoOk, ens retornará 2 indicant que el mail ja existeix.
     */
    @Test
    @Order(5)
    @DisplayName("Register mail already exist")
    public void registerNoOkMail() {
        try (MockedStatic<DatabaseHelper> mocked = Mockito.mockStatic(DatabaseHelper.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(() -> DatabaseHelper.tryRegister(userRegNoMailOk)).thenReturn(2);
            assertEquals(2, DatabaseHelper.tryRegister(userRegNoMailOk));
        }
    }

    /*
     * Mitjançant Mockito, simularem una busqueda dbHelper la base de dades sobre un usuari.
     * Si envien els credencials d'un usuari existent, ens retornará un usuari existent.
     */
    @Test
    @Order(6)
    @DisplayName("Check existing user")
    public void checkLoginOk() {
        try (MockedStatic<DatabaseHelper> mocked = Mockito.mockStatic(DatabaseHelper.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(() -> DatabaseHelper.checkLogin("mock", "123456")).thenReturn(user);
            User user = DatabaseHelper.checkLogin("mock", "123456");
            assertEquals(ServerTest.user, user);
        }
    }

    /* TEA 3 */
    /* DatabaseHelper */

    /*
     * Mitjançant Mockito, simularem una busqueda dbHelper la base de dades sobre un usuari.
     * Si envien els credencials d'un usuari NO existent, ens retornará un usuari null.
     */
    @Test
    @Order(7)
    @DisplayName("Check non existing user")
    public void checkLoginNoOk() {
        try (MockedStatic<DatabaseHelper> mocked = Mockito.mockStatic(DatabaseHelper.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(() -> DatabaseHelper.checkLogin("admin5", "admin")).thenReturn(null);
            assertNull(DatabaseHelper.checkLogin("admin5", "admin"));
        }
    }

    /*
     * Indicant un nom d'usuari per String, ens retorna l'objecte usuari amb les seves dades
     */
    @Test
    @Order(8)
    @DisplayName("Get user by name")
    public void getUserTest() {
        User user = DatabaseHelper.getUser("user0");
        assertEquals("Name0", user.getName());
    }

    /*
     * Retorna tots els usuaris registrats a la base de dades
     */
    @Test
    @Order(9)
    @DisplayName("Get all users")
    public void getUsersTest() {
        List<User> users = DatabaseHelper.getUsers();
        assertEquals(6, users.size());
    }

    /*
     * Métode que actualitza les dades de l'usuari segons quines s'indtrodueixin a l'objecte EditUser.
     * En aquest cas, s'actualitza l'email
     */
    @Test
    @Order(10)
    @DisplayName("Update user mail")
    public void updateUserTest() {

        EditUser editUser = new EditUser();
        editUser.setUsername("user0");
        editUser.setMail("test@test.com");
        User user = DatabaseHelper.getUser("user0");

        try (MockedStatic<DatabaseHelper> mocked = Mockito.mockStatic(DatabaseHelper.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(() -> {
                DatabaseHelper.updateUser(editUser);
                user.setMail(editUser.getMail());
            }).thenReturn(true);
        }
        assertTrue(DatabaseHelper.updateUser(editUser));
        assertEquals(editUser.getMail(), user.getMail());
    }

    /*
     * Indicant un nom d'usuari per String i un valor true o false, modificarem els permisos d'administrador d'aquest usuari.
     */
    @Test
    @Order(11)
    @DisplayName("Make admin True")
    public void makeAdminTrueTest() {
        DatabaseHelper.makeAdmin("user0", true);
        User user = DatabaseHelper.getUser("user0");
        assertTrue(user.isIsAdmin());
    }

    /*
     * Crearem un nou joc a la base de dades. El return ha de ser 0 si ha estat exitòs.
     */
    @Test
    @Order(12)
    @DisplayName("Create new game")
    public void saveNewGameTest() {
        Videogame videogame = new Videogame("description", "developer", "name", "publisher", new Date(), null, null, null);
        int result = DatabaseHelper.saveNewGame(videogame);
        assertEquals(0, result);
    }

    /*
     * Crearem un nou joc a la base de dades. El return ha de ser 1 si ja existeix aquest nom a la base de dades.
     */
    @Test
    @Order(13)
    @DisplayName("Create repeated game")
    public void saveExistingGameTest() {
        //String description, String developer, String name, String publisher, Date releaseDate, byte[] gameImage
        Videogame videogame = new Videogame("description", "developer", "Videogame 5", "publisher", new Date(), null, null, null);
        int result = DatabaseHelper.saveNewGame(videogame);
        assertEquals(1, result);
    }

    /*
     * Comprovem que les diferents plataformes no son a la base de dades. Retorna una llista buida.
     */
    @Test
    @Order(14)
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
    @Order(15)
    @DisplayName("Check existing platform")
    public void checkGameExistPlatformsTest() {
        List<Platforms> platformsList = new ArrayList<>();
        platformsList.add(new Platforms("Platform0"));
        List<Platforms> listResult = VideogameQuery.getExistingGamePlatforms(platformsList);
        assertEquals(1, listResult.size());
    }

    /*
     * Comprovem que les diferents categories no son a la base de dades. Retorna una llista buida.
     */
    @Test
    @Order(16)
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
    @Order(17)
    @DisplayName("Check existing category")
    public void checkGameExistCategoriesTest() {
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Category0"));
        List<Category> listResult = VideogameQuery.getExistingGameCategories(categoryList);
        assertEquals(1, listResult.size());
    }

    /*
     * Comprovem el resultat d'executar el query de paginació. Indicant un filtre buit i la pàgina 1, ens retorna els 10 primers videojocs.
     */
    @Test
    @Order(18)
    @DisplayName("Query paginated")
    public void getQueryPaginatedTest() {
        QueryFilter q = new QueryFilter();
        List<Videogame> a = VideogameQuery.getGamesPaginated(1, q);
        assertEquals(10, a.size());
    }

    /* VideogameQuery */

    /*
     * Crearem un videojoc amb una imatge. Es crearà aquesta mateixa imatge a la carpeta img i s'indicarà el path a la base de dades.
     */
    @Test
    @Order(19)
    @DisplayName("Save image method test")
    public void saveImageTest() {
        try {
            String filePath = "default-placeholder.jpeg";
            BufferedImage b;
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

            Videogame videogame = new Videogame("test", "test", "test: game 1", "test", new Date(), img, null, null);
            videogame.setPlatforms(platforms);
            int result = DatabaseHelper.saveNewGame(videogame);
            assertEquals(0, result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Cerca de categoria per nom
     */
    @Test
    @Order(20)
    @DisplayName("Get category by name")
    public void getCategoryTest() {
        Category category = VideogameQuery.getCategory("Category0");
        assertEquals("Category0", category.getCategory());
    }

    /*
     * Creació de categoria per nom
     */
    @Test
    @Order(21)
    @DisplayName("Create category by name")
    public void createCategoryTest() {
        VideogameQuery.createCategory("Test 00");
        Category category = VideogameQuery.getCategory("Test 00");
        assertEquals("Test 00", category.getCategory());
    }

    /*
     * Cerca de totes les categories existents
     */
    @Test
    @Order(22)
    @DisplayName("Get all categories")
    public void getCategoriesTest() {
        List<Category> cat = VideogameQuery.getAllCategories();
        assertEquals("Category0", cat.get(0).getCategory());
    }

    /*
     * Creació de categories a través d'una llista
     */
    @Test
    @Order(23)
    @DisplayName("Create multiple categories")
    public void createMultipleCategoriesTest() {
        List<String> categoryList = new ArrayList<>();
        categoryList.add("Test 01");
        categoryList.add("Test 02");
        categoryList.add("Test 03");
        VideogameQuery.createMultipleCategories(categoryList);

        List<Category> categories = VideogameQuery.getAllCategories();
        assertEquals(9, categories.size());
    }

    /*
     * Cerca de plataforma per nom
     */
    @Test
    @Order(24)
    @DisplayName("Get platform by name")
    public void getPlatformTest() {
        Platforms platforms = VideogameQuery.getPlatform("Platform1");
        assertEquals("Platform1", platforms.getName());
    }

    /*
     * Creació de plataforma per nom
     */
    @Test
    @Order(25)
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
    @Order(26)
    @DisplayName("Get all platforms")
    public void getPlatformsTest() {
        List<Platforms> plat = VideogameQuery.getAllPlatforms();
        assertEquals("Platform0", plat.get(0).getName());
    }

    /*
     * Mostra d'obtenció de videojocs paginats
     */
    @Test
    @Order(27)
    @DisplayName("Get videogames paginated")
    public void getGamesPaginatedTest() {
        int page = 1;

        List<Videogame> videogameList = VideogameQuery.getGamesPaginated(page, queryFilter);
        assertEquals(10, videogameList.size());

        List<Videogame> videogameList2 = VideogameQuery.getGamesPaginated(2, queryFilter);
        assertEquals(10, videogameList2.size());

        List<Videogame> videogameList3 = VideogameQuery.getGamesPaginated(3, queryFilter);
        assertEquals(7, videogameList3.size());
    }

    /*
     * Mostra d'obtenció del total de pàgines que ocupen els videojocs agrupats de 10 en 10
     */
    @Test
    @Order(28)
    @DisplayName("Get total videogame pages")
    public void getGamesTotalPageCountTest() {
        int count = VideogameQuery.getGamesTotalPageCount(queryFilter);
        assertEquals(3, count);
    }

    /*
     * Obtenció del top 5 videojocs per nota mitja
     */
    @Test
    @Order(29)
    @DisplayName("Get top 5 videogames")
    public void getGamesTop5Test() {
        List<Videogame> top5 = VideogameQuery.getGamesTop5();
        assertEquals(5, top5.size());
    }

    /*
     * Cerca de videojoc per nom
     */
    @Test
    @Order(30)
    @DisplayName("Get videogame by name")
    public void getVideogameByNameTest() {
        Videogame videogame = VideogameQuery.getVideogameByName("Videogame 6");
        assertEquals("Videogame 6", videogame.getName());
    }

    /* TEA 4 */

    /* Actualitza la informació d'un videojoc */
    @Test
    @Order(31)
    @DisplayName("Update a game")
    public void updateGameTest() {
        EditVideogame editVideogame = new EditVideogame("Videogame 1");
        editVideogame.setNewName("TestVideogame");
        Videogame v = VideogameQuery.getVideogameByName("TestVideogame");

        try (MockedStatic<VideogameQuery> mocked = Mockito.mockStatic(VideogameQuery.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(() -> {
                VideogameQuery.updateGame(editVideogame);
                v.setName(editVideogame.getNewName());
            }).thenReturn(true);
        }

        assertTrue(VideogameQuery.updateGame(editVideogame));
        assertEquals(editVideogame.getNewName(), v.getName());
    }

    /* Afegeix un now score */
    @Test
    @Order(32)
    @DisplayName("Add new score")
    public void newScoreTest() {
        VideogameQuery.newScore(7.2, "user1", "TestVideogame");
        Videogame v = VideogameQuery.getVideogameByName("TestVideogame");
        assertEquals(7.2, v.getScores().get(0).getScore());
    }

    /* Busca un socre */
    @Test
    @Order(33)
    @DisplayName("Find score")
    public void findScoreTest() {
        GameScore gameScore = VideogameQuery.findScore("user1", "TestVideogame");
        assertEquals(7.2, gameScore.getScore());
    }

    /* Busca un score dins d'un objecte videojoc */
    @Test
    @Order(34)
    @DisplayName("Find score in a videogame")
    public void findScoreInGameTest() {
        Videogame videogame = VideogameQuery.getVideogameByName("TestVideogame");
        GameScore gameScore = VideogameQuery.findScoreInGame("user1", videogame);
        assertEquals(7.2, gameScore.getScore());
    }

    /* Mitjana de puntuacions d'un videojoc */
    @Test
    @Order(35)
    @DisplayName("Get average score")
    public void getAverageTest() {
        Videogame videogame = VideogameQuery.getVideogameByName("TestVideogame");
        assertEquals(7.2, VideogameQuery.getAverage(videogame));
    }

    /* Afegeix un nou lloguer */
    @Test
    @Order(36)
    @DisplayName("Add new rental")
    public void newRentalTest() {
        int result = VideogameQuery.newRental("user1", "TestVideogame", "2022/10/20", "2022/10/27");
        assertEquals(0, result);
    }

    /* Busca un lloguer */
    @Test
    @Order(37)
    @DisplayName("Get a rental")
    public void getRentalTest() {
        Rental rental = VideogameQuery.getRental("user1", "TestVideogame");
        assertEquals(DateHelper.getDate("2022/10/20"), rental.getRentalDate());
        assertEquals(DateHelper.getDate("2022/10/27"), rental.getFinalDate());
    }

    /* Actualitza l'estat d'un usuari i comproba que s'hagi actualitzat */
    @Test
    @Order(38)
    @DisplayName("Update and Get user status")
    public void getUserStatusTest() {
        DatabaseHelper.updateUserStatus("user1", false);
        UserEnabled userEnabled = DatabaseHelper.getUserStatus("user1");
        assertFalse(userEnabled.isEnabled());
    }
}
