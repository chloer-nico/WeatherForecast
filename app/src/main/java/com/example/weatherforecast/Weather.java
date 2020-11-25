package com.example.weatherforecast;

import java.util.List;

/**
 * @author dhx
 * Json解析的数据实体类
 */
public class Weather {
    private String status;
    private String count;
    private String info;
    private String infocode;
    private List<lives> lives;

    public Weather() {
    }

    public Weather(String status, String count, String info, String infocode, List<com.example.weatherforecast.lives> lives) {
        this.status = status;
        this.count = count;
        this.info = info;
        this.infocode = infocode;
        this.lives = lives;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfocode() {
        return infocode;
    }

    public void setInfocode(String infocode) {
        this.infocode = infocode;
    }

    public List<com.example.weatherforecast.lives> getLives() {
        return lives;
    }

    public void setLives(List<com.example.weatherforecast.lives> lives) {
        this.lives = lives;
    }
}
