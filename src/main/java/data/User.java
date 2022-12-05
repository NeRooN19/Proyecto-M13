/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import jakarta.persistence.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author NeRooN
 */
@Entity
@Table(name = "Usuarios")
public class User implements Serializable {

    private static final long serialVersionUID = 1;

    private int id;
    private String password;
    private String username;
    private String name;
    private String mail;
    private boolean isAdmin;
    private transient List<Rental> rental;
    private transient List<GameScore> scores;

    public User() {
    }

    public User(String password, String username, String name, String mail) {
        this.password = password;
        this.username = username;
        this.name = name;
        this.mail = mail;
        this.isAdmin = false;
    }

    public User(int id, String password, String username, String name, String mail, boolean isAdmin) {
        this.id = id;
        this.password = password;
        this.username = username;
        this.name = name;
        this.mail = mail;
        this.isAdmin = isAdmin;
    }

    @Column(name = "username", unique = true)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "mail", unique = true)
    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Column(name = "admin")
    public boolean isIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    @OneToMany(cascade = CascadeType.ALL)
    public List<Rental> getRental() {
        return rental;
    }

    public void setRental(List<Rental> rental) {
        this.rental = rental;
    }

    @OneToMany(cascade = CascadeType.ALL)
    public List<GameScore> getScores() {
        return scores;
    }

    public void setScores(List<GameScore> scores) {
        this.scores = scores;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (this.scores != null && this.scores.size() > 0) {
            out.writeInt(this.scores.size());
            for (GameScore gs : this.getScores()) {
                out.writeObject(gs);
            }
        } else {
            out.writeInt(0);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (this.scores == null) {
            this.scores = new ArrayList<>();
        }

        int scoresNum = in.readInt();
        if (scoresNum > 0) {
            for (int i = 0; i < scoresNum; i++) {
                this.scores.add((GameScore) in.readObject());
            }
        }
    }

}
