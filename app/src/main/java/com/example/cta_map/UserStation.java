package com.example.cta_map;

public class UserStation {
    String station_name;
    String station_type;
    Double station_lat;
    Double station_lon;
    Integer id;


    public UserStation(String station_name, String station_type){
        this.station_name = station_name;
        this.station_type = station_type;
        this.station_lat = null;
        this.station_lon = null;
        this.id = null;

    }

    public void setID(Integer id){
        this.id = id;

    }
    public Integer getID(){
        return this.id;
    }


    public String getStation_name(){
        return this.station_name;

    }
    public String getStation_type(){
        return this.station_type;
    }
    public void setTrain_lat(Double lat){
        this.station_lat = lat;
    }
    public void setTrain_lon(Double lon){
        this.station_lon = lon;
    }

    public Double getStationLat(){
        return this.station_lat;
    }

    public Double getStationLon(){
        return this.station_lon;
    }


}