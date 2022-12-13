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
import java.util.Date;

/**
 * @author NeRooN
 */
@Entity
public class Rental implements Serializable {

    private static final long serialVersionUID = 6;
    private int rentalID;
    private Date rentalDate;
    private Date finalDate;
    private String username;
    private String videogame;

    public Rental() {
    }

    public Rental(Date rentalDate, Date finalDate) {
        this.rentalDate = rentalDate;
        this.finalDate = finalDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
