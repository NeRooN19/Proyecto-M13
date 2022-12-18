/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package connexions;

import db.DatabaseHelper;
import views.ServerView;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author NeRooN
 */
public class ServerThread extends Thread {

    private final ArrayList<SSLSocket> socketList = new ArrayList<>();
    private final int portNumber;
    private final ServerView serverView;
    private SSLServerSocket server;
    private SSLServerSocketFactory serverSocketFactory;
    private SSLSocket socket;

    /**
     * Constructor of the class
     *
     * @param portNumber to start the server socket
     * @param serverView view to show the ip list
     */
    public ServerThread(int portNumber, ServerView serverView) {
        this.portNumber = portNumber;
        this.serverView = serverView;
    }

    /**
     * Overrided run method that will accept all client socket connections and execute a new thread to handle petitions
     */
    @Override
    public void run() {
        try {
            DatabaseHelper.initDatabaseConnection();
            System.setProperty("javax.net.ssl.keyStore", "Cert/mykeystore.jks");

            System.setProperty("javax.net.ssl.keyStorePassword", "Ludox123.");

            serverSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

            server = (SSLServerSocket) serverSocketFactory.createServerSocket(portNumber);
            while (true) {
                socket = (SSLSocket) server.accept();
                socket.startHandshake();
                socketList.add(socket);
                updateList();

                new ServerConnexion(socket, socketList, serverView).start();
            }
        } catch (IOException ex) {
            try {
                socketList.remove(socket);
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
     * Getter
     *
     * @return server socket
     */
    public ServerSocket getServer() {
        return server;
    }

    /**
     * Getter
     *
     * @return socket ip list
     */
    public ArrayList<SSLSocket> getSockets() {
        return socketList;
    }

}
