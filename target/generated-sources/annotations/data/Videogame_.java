package data;

import data.Category;
import data.GameScore;
import data.Platforms;
import data.Rental;
import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2022-11-11T16:12:59", comments="EclipseLink-2.7.9.v20210604-rNA")
@StaticMetamodel(Videogame.class)
public class Videogame_ { 

    public static volatile SingularAttribute<Videogame, Double> finalScore;
    public static volatile SingularAttribute<Videogame, Date> releaseDate;
    public static volatile ListAttribute<Videogame, GameScore> scores;
    public static volatile SingularAttribute<Videogame, String> imagePath;
    public static volatile SingularAttribute<Videogame, String> description;
    public static volatile ListAttribute<Videogame, Rental> rentals;
    public static volatile ListAttribute<Videogame, Platforms> platforms;
    public static volatile SingularAttribute<Videogame, String> name;
    public static volatile SingularAttribute<Videogame, String> publisher;
    public static volatile SingularAttribute<Videogame, String> developer;
    public static volatile SingularAttribute<Videogame, Integer> ID;
    public static volatile SingularAttribute<Videogame, Integer> stock;
    public static volatile ListAttribute<Videogame, Category> category;

}