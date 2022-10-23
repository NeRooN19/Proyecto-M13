/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package views;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import connexions.ServerThread;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author NeRooN
 */
public class ServerView extends javax.swing.JFrame {

    private static boolean statusOn = false;
    private static ServerThread serverThread;
    private final int DEFAULT_PORT = 5000;

    /**
     * Creates new form ServerView
     */
    public ServerView() {
        initComponents();
        this.setVisible(true);
        disconnectBtn.setEnabled(false);
        setResizable(false);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        startButton = new javax.swing.JButton();
        statusText = new javax.swing.JLabel();
        portText = new javax.swing.JLabel();
        portNumber = new javax.swing.JTextField();
        updateList = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        socketList = new javax.swing.JList<>();
        disconnectBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        startButton.setText("Start");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        statusText.setText("Server offline");

        portText.setText("Port number");

        updateList.setText("Update");
        updateList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateListActionPerformed(evt);
            }
        });

        jScrollPane2.setViewportView(socketList);

        disconnectBtn.setText("Disconnect");
        disconnectBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disconnectBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(portText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(statusText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(portNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(62, 62, 62)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(215, 215, 215)
                        .addComponent(disconnectBtn)
                        .addGap(18, 18, 18)
                        .addComponent(updateList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(portText)
                        .addComponent(portNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startButton)
                    .addComponent(statusText)
                    .addComponent(updateList)
                    .addComponent(disconnectBtn))
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Method to close or start the server
     * @param evt 
     */
    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        // TODO add your handling code here:
        if (!statusOn) {
            serverThread = new ServerThread(getPort(), this);
            serverThread.start();
            statusText.setText("Server Online");
            startButton.setText("Stop");
            statusOn = true;
            portNumber.setEditable(false);
        } else if (statusOn) {
            try {
                statusText.setText("Server Offline");
                startButton.setText("Start");
                statusOn = false;
                portNumber.setEditable(true);

                serverThread.getSockets().forEach(s -> {
                    try {
                        s.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ServerView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                serverThread.getServer().close();
                serverThread.interrupt();
                serverThread = null;
            } catch (IOException ex) {
                //Logger.getLogger(ServerView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_startButtonActionPerformed

    /**
     * method to update the ip list on click
     * @param evt 
     */
    private void updateListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateListActionPerformed
        // TODO add your handling code here:
        updateList();
    }//GEN-LAST:event_updateListActionPerformed

    /**
     * Method to disconnect the selected socket
     * @param evt 
     */
    private void disconnectBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disconnectBtnActionPerformed
        // TODO add your handling code here:
        int socket = socketList.getSelectedIndex();
        if (socket >= 0) {
            try {
                serverThread.getSockets().get(socket).close();
            } catch (IOException ex) {
                Logger.getLogger(ServerView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_disconnectBtnActionPerformed

    /**
     * Method to update the ip list connexions to display in the ListModel
     */
    public void updateList() {

        if (serverThread == null) {
            return;
        }

        DefaultListModel<String> model = new DefaultListModel<>();
        socketList.setModel(model);
        String[] socketIPs = serverThread.getSockets().stream().map(s -> s.getInetAddress().toString()).toArray(size -> new String[size]);
        if (socketIPs == null || socketIPs.length == 0) {
            disconnectBtn.setEnabled(false);
            return;
        }
        disconnectBtn.setEnabled(true);
        for (int i = 0; i < socketIPs.length; i++) {
            model.addElement(socketIPs[i]);
        }
    }

    /**
     *
     * @return the port from the text input or the default port in case nothing is written
     */
    public int getPort() {
        try {
            return Integer.parseInt(this.portNumber.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error parsing port number. Server started with port " + DEFAULT_PORT);
            portNumber.setText(DEFAULT_PORT + "");
            return DEFAULT_PORT;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ServerView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ServerView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ServerView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ServerView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ServerView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton disconnectBtn;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField portNumber;
    private javax.swing.JLabel portText;
    private javax.swing.JList<String> socketList;
    private javax.swing.JButton startButton;
    private javax.swing.JLabel statusText;
    private javax.swing.JButton updateList;
    // End of variables declaration//GEN-END:variables
}
