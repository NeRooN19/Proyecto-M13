package data;

import java.io.Serializable;

public class QueryFilter implements Serializable {
    private static final long serialVersionUID = 8;

    private String platformName;
    private String categoryName;
    private float minScore;
    private float maxScore;
    private String minDate;
    private String maxDate;
    private String dateSearchParam;
    private String scoreSearchParam;


    public QueryFilter() {
    }

    public QueryFilter(String platformName, String categoryName, float minScore, float maxScore, String minDate, String maxDate, String dateSearchParam, String scoreSearchParam) {
        this.platformName = platformName;
        this.categoryName = categoryName;
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.minDate = minDate;
        this.maxDate = maxDate;
        this.dateSearchParam = dateSearchParam;
        this.scoreSearchParam = scoreSearchParam;
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

    public float getMinScore() {
        return minScore;
    }

    public void setMinScore(float minScore) {
        this.minScore = minScore;
    }

    public float getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(float maxScore) {
        this.maxScore = maxScore;
    }

    public String getMinDate() {
        return minDate;
    }

    public void setMinDate(String minDate) {
        this.minDate = minDate;
    }

    public String getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(String maxDate) {
        this.maxDate = maxDate;
    }

    public String getDateSearchParam() {
        return dateSearchParam;
    }

    public void setDateSearchParam(String dateSearchParam) {
        this.dateSearchParam = dateSearchParam;
    }

    public String getScoreSearchParam() {
        return scoreSearchParam;
    }

    public void setScoreSearchParam(String scoreSearchParam) {
        this.scoreSearchParam = scoreSearchParam;
    }
}
