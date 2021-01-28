package com.example.cta_map;

import java.io.StringReader;
import java.util.HashMap;

public class Stops {
    String name;
    String color;
    String dir;
    HashMap<String, String> tracking_station;
    public Stops(HashMap<String, String> tracking_station , String name, String color, String dir){
        this.name = name;
        this.color = color;
        this.dir = dir;
        this.tracking_station = tracking_station;
    }
    public String getDir(){
        return this.dir;
    }

    public String getColor() {
        return this.color;
    }

    public String getName() {
        return this.name;
    }

    public HashMap<String, String> getTracking_station(){
        return this.tracking_station;
    }

}
