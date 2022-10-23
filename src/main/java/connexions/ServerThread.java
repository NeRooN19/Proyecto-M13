/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package connexions;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import views.ServerView;

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
     * Constructor
     *
     * @param portNumber
     * @param serverView
     */
    public ServerThread(int portNumber, ServerView serverView) {
        this.portNumber = portNumber;
        this.serverView = serverView;
    }

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

    private void updateList() {
        if (serverView != null) {
            serverView.updateList();
        }
    }

    /**
     * Getter
     *
     * @return server sicket
     */
    public ServerSocket getServer() {
        return server;
    }

    /**
     * Getter
     *
     * @return socket list
     */
    public ArrayList<Socket> getSockets() {
        return socketList;
    }

}
