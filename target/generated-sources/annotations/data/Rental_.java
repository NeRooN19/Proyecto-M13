package data;

import data.User;
import data.Videogame;
import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2022-11-12T00:46:56", comments="EclipseLink-2.7.9.v20210604-rNA")
@StaticMetamodel(Rental.class)
public class Rental_ { 

    public static volatile SingularAttribute<Rental, Date> finalDate;
    public static volatile SingularAttribute<Rental, Date> rentalDate;
    public static volatile SingularAttribute<Rental, Videogame> videogame;
    public static volatile SingularAttribute<Rental, User> user;
    public static volatile SingularAttribute<Rental, Integer> rentalID;

}