/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package connexions;

import data.Category;
import data.Platforms;
import data.User;
import data.Videogame;
import db.DatabaseHelper;
import db.VideogameQuery;
import helpers.EditUser;
import helpers.EditVideogame;
import helpers.QueryFilter;
import views.ServerView;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author NeRooN
 */
public class ServerConnexion extends Thread {

    private ArrayList<SSLSocket> sockets;
    private DatabaseHelper dbHelp;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private ServerView serverView;

    /**
     * Constructor of the class
     *
     * @param socket     connection from the client
     * @param sockets    list that contains all socket ip's
     * @param serverView view to show the ip list
     */
    public ServerConnexion(Socket socket, ArrayList<SSLSocket> sockets, ServerView serverView) {
        try {
            this.socket = socket;
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            dbHelp = new DatabaseHelper(dis, dos, ois, oos);
            this.sockets = sockets;
            this.serverView = serverView;
        } catch (IOException ex) {
            Logger.getLogger(ServerConnexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Overrided run method to handle all petitions from the clients with a switch, dependending on the petition, it will do an action or another
     */
    @Override
    public void run() {
        try {
            while (true) {
                Options option = Options.values()[dis.readByte()];
                switch (option) {
                    case REGISTER -> {
                        User user = (User) ois.readObject();
                        byte registerComplete = (byte) DatabaseHelper.tryRegister(user);
                        dos.writeByte(registerComplete);
                    }

                    case LOGIN -> {
                        dbHelp.doLogin();
                    }

                    case VIDEOGAMES_PAGINATION -> {
                        QueryFilter query = (QueryFilter) ois.readObject();
                        dos.write(VideogameQuery.getGamesTotalPageCount(query));
                        int page = dis.readInt();
                        oos.writeObject(VideogameQuery.getGamesPaginated(page, query));
                    }

                    case INITIALIZATION -> {
                        List<Videogame> vi = VideogameQuery.getGamesTop5();
                        oos.writeObject(vi);
                        List<Category> cat = VideogameQuery.getAllCategories();
                        oos.writeObject(cat);
                        List<Platforms> plat = VideogameQuery.getAllPlatforms();
                        oos.writeObject(plat);
                    }

                    case EDIT_USER -> {
                        boolean succes = DatabaseHelper.updateUser((EditUser) ois.readObject());
                        dos.writeBoolean(succes);
                    }

                    case EDIT_GAME -> {
                        boolean succes = VideogameQuery.updateGame((EditVideogame) ois.readObject());
                        dos.writeBoolean(succes);
                    }

                    case NEW_GAME -> {
                        Videogame videogame = (Videogame) ois.readObject();
                        dos.writeByte(DatabaseHelper.saveNewGame(videogame));
                    }

                    case MAKE_ADMIN -> {
                        String user = dis.readUTF();
                        boolean admin = dis.readBoolean();
                        DatabaseHelper.makeAdmin(user, admin);
                    }

                    case NEW_RENTAL -> {
                        String username = dis.readUTF();
                        String videogame = dis.readUTF();
                        String initialDate = dis.readUTF();
                        String finalDate = dis.readUTF();
                        dos.writeInt(VideogameQuery.newRental(username, videogame, initialDate, finalDate));
                    }

                    case NEW_SCORE -> {
                        String username = dis.readUTF();
                        String videogame = dis.readUTF();
                        double score = dis.readDouble();
                        dos.writeInt(VideogameQuery.newScore(score, username, videogame));
                        oos.writeObject(VideogameQuery.getVideogameByName(videogame));
                    }

                    case GET_USER_LIST -> {
                        oos.writeObject(DatabaseHelper.getUsers());
                    }
                    case UPDATE_USER_STATUS -> {
                        String user = dis.readUTF();
                        boolean isEnabled = dis.readBoolean();
                        DatabaseHelper.updateUserStatus(user, isEnabled);
                    }
                    case GET_VIDEOGAME_LIST -> {
                        oos.writeObject(VideogameQuery.getVideogames());
                    }

                    default -> System.out.println();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerConnexion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerConnexion.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.out.println("Conexió amb " + socket.getInetAddress() + " acabada");
            try {
                end();
                sockets.remove(socket);
                serverView.updateList();
                socket.close();
            } catch (IOException ex1) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    /**
     * Method to close all existing connections
     */
    private void end() {
        try {
            dis.close();
            dos.close();
            ois.close();
            oos.close();
            if (socket != null && socket.isInputShutdown()) {
                socket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
