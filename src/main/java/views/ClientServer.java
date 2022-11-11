/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views;

import data.User;
import data.Videogame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author NeRooN
 */
public class ClientServer {

    private static Socket socket;
    private static DataInputStream dis;
    private static DataOutputStream dos;
    private static ObjectInputStream ois;
    private static ObjectOutputStream oos;

    public static void main(String[] args) throws IOException {
        try {
            //Server conexion to test real database acces
            //client conexion to send petitions to the server
            socket = new Socket("localhost", 5000);

            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        getTop5();
        //tryReg();
    }

    public static void getTop5() {
        try {
            dos.write((byte) 4);
            List<Videogame> vi = (List<Videogame>) ois.readObject();

            vi.forEach(v -> System.out.println(v.getName()));

        } catch (IOException ex) {
            Logger.getLogger(ClientServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void tryReg() throws IOException {
        User user = new User();
        user.setPassword("123456");
        user.setUsername("admin6");
        user.setIsAdmin(true);

        dos.writeByte((byte)0);
        oos.writeObject(user);
        byte a = dis.readByte();
    }
}
