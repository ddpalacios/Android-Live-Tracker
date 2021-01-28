package com.example.cta_map.Activities;

import java.util.HashMap;

public class HomePage {
    private String name;
    private String color;
    private String dir;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public HashMap<String, String> getTracking_station() {
        return tracking_station;
    }

    public void setTracking_station(HashMap<String, String> tracking_station) {
        this.tracking_station = tracking_station;
    }

    private HashMap<String,String> tracking_station;
}
