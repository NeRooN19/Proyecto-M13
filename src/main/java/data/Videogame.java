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
    private transient List<Rental> rentals;
    private transient List<GameScore> scores;
    private transient List<Platforms> platforms;
    private transient List<Category> categories;
    private byte[] gameImage;

    public Videogame() {
    }

    public Videogame(String description, String developer, String name, String publisher, Date releaseDate, byte[] gameImage, List<Platforms> platforms, List<Category> categories) {
        this.description = description;
        this.developer = developer;
        this.name = name;
        this.publisher = publisher;
        this.releaseDate = releaseDate;
        this.gameImage = gameImage;
        this.scores = new ArrayList<>();
        this.rentals = new ArrayList<>();
        this.platforms = platforms;
        this.categories = categories;
    }

    //Constructor de EclipseLink - Base Datos
    public Videogame(List<Category> categories, String description, String developer, double finalScore, int ID, String imagePath, String name, String publisher, Date releaseDate, List<Platforms> platforms) {
        this.categories = categories;
        this.description = description;
        this.developer = developer;
        this.finalScore = finalScore;
        this.ID = ID;
        this.imagePath = imagePath;
        this.name = name;
        this.publisher = publisher;
        this.releaseDate = releaseDate;
        this.platforms = platforms;
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

    @OneToMany(cascade = CascadeType.ALL)
    public List<Rental> getRentals() {
        return rentals;
    }

    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }

    @OneToMany(cascade = CascadeType.ALL)
    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
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

    @OneToMany(cascade = CascadeType.ALL)
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

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (this.categories != null && this.categories.size() > 0) {
            out.writeInt(this.categories.size());
            for (Category cat : this.getCategories()) {
                out.writeObject(cat);
            }
        } else {
            out.writeInt(0);
        }

        if (this.platforms != null && this.platforms.size() > 0) {
            out.writeInt(this.platforms.size());
            for (Platforms pl : this.getPlatforms()) {
                out.writeObject(pl);
            }
        } else {
            out.writeInt(0);
        }

        if (this.scores != null && this.scores.size() > 0) {
            out.writeInt(this.scores.size());
            for (GameScore gs : this.getScores()) {
                out.writeObject(gs);
            }
        } else {
            out.writeInt(0);
        }

        if (this.rentals != null && this.rentals.size() > 0) {
            out.writeInt(this.rentals.size());
            for (Rental ren : this.getRentals()) {
                out.writeObject(ren);
            }
        } else {
            out.writeInt(0);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        if (this.categories == null) {
            this.categories = new ArrayList<>();
        }
        int catNum = in.readInt();
        if (catNum > 0) {
            for (int i = 0; i < catNum; i++) {
                this.categories.add((Category) in.readObject());
            }
        }

        if (this.platforms == null) {
            this.platforms = new ArrayList<>();
        }

        int platNum = in.readInt();
        if (platNum > 0) {
            for (int i = 0; i < platNum; i++) {
                this.platforms.add((Platforms) in.readObject());
            }
        }

        if (this.scores == null) {
            this.scores = new ArrayList<>();
        }

        int scoresNum = in.readInt();
        if (scoresNum > 0) {
            for (int i = 0; i < scoresNum; i++) {
                this.scores.add((GameScore) in.readObject());
            }
        }

        if (this.rentals == null) {
            this.rentals = new ArrayList<>();
        }

        int rentalsNum = in.readInt();
        if (rentalsNum > 0) {
            for (int i = 0; i < rentalsNum; i++) {
                this.rentals.add((Rental) in.readObject());
            }
        }
    }
}
