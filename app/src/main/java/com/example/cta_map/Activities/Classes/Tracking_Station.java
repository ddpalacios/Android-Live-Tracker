package com.example.cta_map.Activities.Classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Tracking_Station implements Serializable{
    private String station_name;
    private String station_type;
    private String stop_id;
    private String chosen_direction;
    private Double lat, lon;

    public Tracking_Station() {

    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public String getStation_type() {
        return station_type;
    }

    public void setStation_type(String station_type) {
        this.station_type = station_type;
    }

    public String getStop_id() {
        return stop_id;
    }

    public void setStop_id(String stop_id) {
        this.stop_id = stop_id;
    }

    public String getChosen_direction() {
        return chosen_direction;
    }

    public void setChosen_direction(String chosen_direction) {
        this.chosen_direction = chosen_direction;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;

    }
}
