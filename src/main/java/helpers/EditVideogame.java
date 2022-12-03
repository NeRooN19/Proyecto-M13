package helpers;

import java.io.Serializable;
import java.util.Date;

public class EditVideogame implements Serializable {
    private static final long serialVersionUID = 9;

    private final String currentName;
    private String newName;
    private String description;
    private Date releaseDate;
    private String developer;
    private String publisher;

    public EditVideogame(String currentName) {
        this.currentName = currentName;
    }

    public EditVideogame(String currentName, String newName, String description, Date releaseDate, String developer, String publisher) {
        this.currentName = currentName;
        this.newName = newName;
        this.description = description;
        this.releaseDate = releaseDate;
        this.developer = developer;
        this.publisher = publisher;
    }

    public String getCurrentName() {
        return currentName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

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
}
