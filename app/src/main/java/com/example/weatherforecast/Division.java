package com.example.weatherforecast;

import java.util.List;

/**
 * @author dhx
 * 行政区域查询API的json数据实体类
 */
public class Division {
    private String status;
    private String info;
    private String infocode;
    private String count;
    private suggestion suggestion;
    private List<districts> districts;

    public List<com.example.weatherforecast.districts> getDistricts() {
        return districts;
    }

    public void setDistricts(List<com.example.weatherforecast.districts> districts) {
        this.districts = districts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public com.example.weatherforecast.suggestion getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(com.example.weatherforecast.suggestion suggestion) {
        this.suggestion = suggestion;
    }
}
