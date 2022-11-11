/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package connexions;

import db.DatabaseHelper;
import data.User;
import db.VideogameQuery;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import views.ServerView;

/**
 *
 * @author NeRooN
 */
public class ServerConnexion extends Thread {

    private ArrayList<Socket> sockets;
    private DatabaseHelper dbHelp;
    private static Socket socket;
    private static DataInputStream dis;
    private DataOutputStream dos;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private EntityManagerFactory emf;
    private EntityManager em;
    private ServerView serverView;

    /**
     * Constructor of the class
     *
     * @param socket connection from the client
     * @param sockets list that contains all socket ip's
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
                    case LOGIN ->
                        dbHelp.doLogin();
                    case REGISTER -> {
                        User user = (User) ois.readObject();
                        byte registerComplete = (byte) dbHelp.tryRegister(user);
                        dos.writeByte(registerComplete);
                    }
                    case VIDEOGAME -> {

                    }
                    case VIDEOGAMES_PAGINATION -> {

                    }
                    case VIDEOGAME_TOP -> {
                        oos.writeObject(VideogameQuery.getGamesTop5());
                    }
                    case EDIT_USER -> {

                    }
                    default ->
                        System.out.println("");
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
     * Update the list of ip's
     */
    private void updateList() {
        if (serverView != null) {
            serverView.updateList();
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
