/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

/**
 *
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
    private List<Rental> rental;
    private List<GameScore> scores;

    public User() {
    }
    
    public User(String password, String username, String name, String mail, boolean isAdmin) {
        this.password = password;
        this.username = username;
        this.name = name;
        this.mail = mail;
        this.isAdmin = isAdmin;
        rental = null;
    }

    public User(int id, String password, String username, String name, String mail, boolean isAdmin, List<Rental> rental, List<GameScore> scores) {
        this.id = id;
        this.password = password;
        this.username = username;
        this.name = name;
        this.mail = mail;
        this.isAdmin = isAdmin;
        this.rental = rental;
        this.scores = scores;
    }

    @Column(name = "username", unique = true)
    public String getUsername() {
        return username;
    }

    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    @Column(name = "mail", unique = true)
    public String getMail() {
        return mail;
    }

    @Column(name = "admin")
    public boolean isIsAdmin() {
        return isAdmin;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<Rental> getRental() {
        return rental;
    }

    public void setRental(List<Rental> rental) {
        this.rental = rental;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
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

}
