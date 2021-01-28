package com.example.cta_map.Activities;

public class FavoriteStation {
    String station_id;

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
    public FavoriteStation(String station_id, String station_name, String station_type){
        this.station_id = station_id;
        this.station_name = station_name;
        this.station_type = station_type;
    }


}
