/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package connexions;

import data.*;
import db.DatabaseHelper;
import db.VideogameQuery;
import views.ServerView;

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

    private ArrayList<Socket> sockets;
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
    public ServerConnexion(Socket socket, ArrayList<Socket> sockets, ServerView serverView) {
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
                        byte registerComplete = (byte) dbHelp.tryRegister(user);
                        dos.writeByte(registerComplete);
                        oos.writeObject(user);
                    }
                    case LOGIN -> dbHelp.doLogin();
                    case VIDEOGAMES_PAGINATION -> {
                        QueryFilter query = (QueryFilter) ois.readObject();
                        dos.write(VideogameQuery.getGamesCount(query));
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
                    case EDIT_USER -> DatabaseHelper.updateUser((EditUser) ois.readObject());

                    case EDIT_GAME -> {

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

                    }
                    case NEW_SCORE -> {

                    }
                    default -> System.out.println("");
                }
            }
        } catch (IOException ex) {

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
