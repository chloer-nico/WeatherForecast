package com.example.weatherforecast;

import java.util.List;

/**
 * @author dhx
 * 行政区域规划API的内部类
 */
public class districts {
private String citycode;
private String adcode;
private String name;
private String center;
private String level;
private List<districts> districts;

    public districts() {
    }

    public districts(String citycode, String adcode, String name, String center, String level, List<com.example.weatherforecast.districts> districts) {
        this.citycode = citycode;
        this.adcode = adcode;
        this.name = name;
        this.center = center;
        this.level = level;
        this.districts = districts;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<com.example.weatherforecast.districts> getDistricts() {
        return districts;
    }

    public void setDistricts(List<com.example.weatherforecast.districts> districts) {
        this.districts = districts;
    }
}
