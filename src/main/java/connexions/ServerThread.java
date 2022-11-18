/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package connexions;

import views.ServerView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author NeRooN
 */
public class ServerThread extends Thread {

    private ServerSocket server;
    private ArrayList<Socket> socketList = new ArrayList<>();
    private final int portNumber;
    private Socket socket;
    private ServerView serverView;

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
            server = new ServerSocket(portNumber);
            while (true) {
                socket = server.accept();
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
    public ArrayList<Socket> getSockets() {
        return socketList;
    }

}
