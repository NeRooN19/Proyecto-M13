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
    private ArrayList<Socket> sockets = new ArrayList<>();
    private final int portNumber;
    private Socket socket;
    private ServerView serverView;

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
                sockets.add(socket);
                updateList();

                new ServerConnexion(socket, sockets, serverView).start();
            }
        } catch (IOException ex) {
            try {
                sockets.remove(socket);
                updateList();
                socket.close();
            } catch (IOException ex1) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    public void updateList() {
        if (serverView != null) {
            serverView.updateList();
        }
    }

    public ServerSocket getServer() {
        return server;
    }

    public ArrayList<Socket> getSockets() {
        return sockets;
    }

}
