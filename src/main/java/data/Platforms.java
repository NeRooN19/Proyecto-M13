/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import jakarta.persistence.*;

import java.io.Serializable;

/**
 *
 * @author NeRooN
 */
@Entity
public class Platforms implements Serializable {
    private static final long serialVersionUID = 5;
    private int ID;
    private String name;

    public Platforms() {
    }

    public Platforms(String name) {
        this.name = name;
    }

    public Platforms(int ID, String name) {
        this.ID = ID;
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    @Column(unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String capitalize(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
}
