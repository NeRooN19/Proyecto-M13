package helpers;

import java.io.Serializable;

public class QueryFilter implements Serializable {

    private static final long serialVersionUID = 8;

    private String platformName;
    private String categoryName;
    private float score;
    private String date;
    private String name;

    public QueryFilter() {
    }

    public QueryFilter(String platformName, String categoryName, float score, String date, String name) {
        this.platformName = platformName;
        this.categoryName = categoryName;
        this.score = score;
        this.date = date;
        this.name = name;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
