package data;

import data.User;
import data.Videogame;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2022-11-11T23:18:07", comments="EclipseLink-2.7.9.v20210604-rNA")
@StaticMetamodel(GameScore.class)
public class GameScore_ { 

    public static volatile SingularAttribute<GameScore, Double> score;
    public static volatile SingularAttribute<GameScore, Videogame> videogame;
    public static volatile SingularAttribute<GameScore, Integer> id;
    public static volatile SingularAttribute<GameScore, User> user;

}