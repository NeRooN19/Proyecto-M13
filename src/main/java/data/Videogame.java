/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author NeRooN
 */
@Entity
@Table(name = "videojuegos")
public class Videogame implements Serializable {

    private String name;
    private String company;
    private String description;
    private Date releaseDate;
    private List<Rental> rentals;
    private static final long serialVersionUID = 123456789;

    public Videogame() {
    }

    public Videogame(String name, String company, String description, Date releaseDate, List<Rental> rentals) {
        this.name = name;
        this.company = company;
        this.description = description;
        this.releaseDate = releaseDate;
        this.rentals = rentals;
    }

    @Id
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
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

    @OneToMany(mappedBy = "videogame")
    public List<Rental> getRentals() {
        return rentals;
    }

    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }

}
