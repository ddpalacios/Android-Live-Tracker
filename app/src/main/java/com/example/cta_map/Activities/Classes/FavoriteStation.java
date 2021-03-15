package com.example.cta_map.Activities.Classes;

public class FavoriteStation {
    String station_id;

    public String getTracking() {
        return isTracking;
    }

    public void setTracking(String tracking) {
        isTracking = tracking;
    }

     String isTracking;

    public String getStation_id() {
        return station_id;
    }

    public String getStation_name() {
        return station_name;
    }

    public String getStation_type() {
        return station_type;
    }

    String station_name;
    String station_type;

    public String getStation_dir() {
        return station_dir;
    }

    public void setStation_dir(String station_dir) {
        this.station_dir = station_dir;
    }

    public String getStation_dir_label() {
        return station_dir_label;
    }

    public void setStation_dir_label(String station_dir_label) {
        this.station_dir_label = station_dir_label;
    }

    String station_dir;
    String station_dir_label;

    public FavoriteStation(String station_id, String station_name, String station_type){
        this.station_id = station_id;
        this.station_name = station_name;
        this.station_type = station_type;
    }


}
