package com.example.weatherforecast;

/**
 * @author dhx
 * listview的实体类city
 */
public class City {
    private String province;
    private String city;
    private String adcode;

    public City() {
    }

    public City(String province, String city,String adcode) {
        this.province = province;
        this.city = city;
        this.adcode=adcode;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
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
