package com.example.weatherforecast;

/**
 * @author dhx
 * listview的实体类city
 */
public class City {
    private String province;
    private String city;

    public City() {
    }

    public City(String province, String city) {
        this.province = province;
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
