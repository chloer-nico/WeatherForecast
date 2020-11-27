package com.example.weatherforecast;

/**
 * @author dhx
 * 行政区域规划的内部类
 */
public class suggestion {
    private String keywords;
    private String cities;

    public suggestion() {
    }

    public suggestion(String keywords, String cities) {
        this.keywords = keywords;
        this.cities = cities;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getCities() {
        return cities;
    }

    public void setCities(String cities) {
        this.cities = cities;
    }
}
