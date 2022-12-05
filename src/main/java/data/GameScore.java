/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;

/**
 * @author NeRooN
 */
@Entity
public class GameScore implements Serializable {

    private static final long serialVersionUID = 4;
    private int id;
    private double score;
    private String username;
    private String videogame;

    public GameScore(double score) {
        this.score = score;
    }

    public GameScore(int id, double score) {
        this.id = id;
        this.score = score;
    }

    public GameScore(double score, String username, String videogame) {
        this.score = score;
        this.username = username;
        this.videogame = videogame;
    }

    public GameScore(int id, double score, String username, String videogame) {
        this.id = id;
        this.score = score;
        this.username = username;
        this.videogame = videogame;
    }

    public GameScore() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVideogame() {
        return videogame;
    }

    public void setVideogame(String videogame) {
        this.videogame = videogame;
    }

}
