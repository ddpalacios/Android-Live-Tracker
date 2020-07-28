package com.example.cta_map;

public class Tracking_Station {
    String name;
    String eta;
    public Tracking_Station(String name, String eta){
        this.name = name;
        this.eta = eta;
    }

    public String getName() {
        return name;
    }

    public String getEta() {
        return eta;
    }
}
