/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author NeRooN
 */
@Entity
public class Videogame implements Serializable {

    private static final long serialVersionUID = 2;

    private String description;
    private String developer;
    private double finalScore;
    private int ID;
    private String imagePath;
    private String name;
    private String publisher;
    private Date releaseDate;
    private int stock;
    private List<Rental> rentals;
    private List<GameScore> scores;
    private List<Platforms> platforms;
    private List<Category> categories;
    private byte[] gameImage;

    public Videogame() {
    }

    public Videogame(String description, String developer, String name, String publisher, Date releaseDate, List<Platforms> platforms, List<Category> categories, byte[] gameImage) {
        this.description = description;
        this.developer = developer;
        this.name = name;
        this.publisher = publisher;
        this.releaseDate = releaseDate;
        this.platforms = platforms;
        this.categories = categories;
        this.gameImage = gameImage;
        this.scores = new ArrayList<>();
        this.rentals = new ArrayList<>();
        if (platforms == null) {
            this.platforms = new ArrayList<>();
        }
        if (categories == null) {
            this.categories = new ArrayList<>();
        }
    }

    //Constructor de EclipseLink - Base Datos
    public Videogame(List<Category> categories, String description, String developer, double finalScore, int ID, String imagePath, String name, String publisher, Date releaseDate, List<Rental> rentals, List<GameScore> scores, int stock, List<Platforms> platforms) {
        this.categories = categories;
        this.description = description;
        this.developer = developer;
        this.finalScore = finalScore;
        this.ID = ID;
        this.imagePath = imagePath;
        this.name = name;
        this.publisher = publisher;
        this.releaseDate = releaseDate;
        this.rentals = rentals;
        this.scores = scores;
        this.stock = stock;
        this.platforms = platforms;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getID() {
        return ID;
    }

    @Column(unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(length = 500)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    @OneToMany(mappedBy = "videogame", cascade = CascadeType.ALL)
    public List<Rental> getRentals() {
        return rentals;
    }

    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    @OneToMany(cascade = CascadeType.ALL)
    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @OneToMany(mappedBy = "videogame", cascade = CascadeType.ALL)
    public List<GameScore> getScores() {
        return scores;
    }

    public void setScores(List<GameScore> scores) {
        this.scores = scores;
    }

    @OneToMany(cascade = CascadeType.ALL)
    public List<Platforms> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<Platforms> platforms) {
        this.platforms = platforms;
    }

    @Transient
    public byte[] getGameImage() {
        return this.gameImage;
    }

    public void setGameImage(byte[] gameImage) {
        this.gameImage = gameImage;
    }

    public void addScore(User user, double score) {
        if (this.scores == null) {
            this.scores = new ArrayList<>();
        }
        this.scores.add(new GameScore(score, user, this));
        updateScore();
    }

    public void updateScore() {
        double finalScore = 0;
        if (this.scores == null) {
            this.scores = new ArrayList<>();
            return;
        }

        for (GameScore gameScore : scores) {
            finalScore += gameScore.getScore();
        }
        finalScore /= scores.size();
        finalScore = Math.round(finalScore * 100.0) / 100.0;
        setFinalScore(finalScore);
    }

}
