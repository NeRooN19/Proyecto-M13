/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author NeRooN
 */
@Entity
@Table(name = "usuarios")
public class User implements Serializable {

    private String password;
    private String username;
    private String name;
    private String mail;
    private boolean isAdmin;
    private List<Rental> rental;
    private static final long serialVersionUID = 123456789;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String password, String username, String name, String mail, boolean isAdmin, List<Rental> rental) {
        this.password = password;
        this.username = username;
        this.name = name;
        this.mail = mail;
        this.isAdmin = isAdmin;
        this.rental = rental;
    }

    @Id
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

    @OneToMany(mappedBy = "user")
    public List<Rental> getRental() {
        return rental;
    }

    public void setRental(List<Rental> rental) {
        this.rental = rental;
    }
}
