/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author NeRooN
 */
@Entity
@Table(name = "alquiler")
public class Rental {

    private int rentalID;
    private Date rentalDate;
    private Date finalDate;
    private Videogame videogame;
    private User user;

    public Rental() {
    }

    public Rental(int rentalID, Date rentalDate, Date finalDate, Videogame videogame, User user) {
        this.rentalID = rentalID;
        this.rentalDate = rentalDate;
        this.finalDate = finalDate;
        this.videogame = videogame;
        this.user = user;
    }

    @Id
    public int getRentalID() {
        return rentalID;
    }

    public void setRentalID(int rentalID) {
        this.rentalID = rentalID;
    }

    public Date getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(Date rentalDate) {
        this.rentalDate = rentalDate;
    }

    public Date getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(Date finalDate) {
        this.finalDate = finalDate;
    }

    @ManyToOne
    public Videogame getVideogame() {
        return videogame;
    }

    public void setVideogame(Videogame videogame) {
        this.videogame = videogame;
    }

    @ManyToOne
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
