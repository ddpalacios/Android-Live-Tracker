package com.example.cta_map.Activities.Classes;

import java.util.ArrayList;

public class Station {
    private String station_type;
    private String stop_id;
    private String map_id;
    private String northbound;

    public String getNorthbound() {
        return northbound;
    }

    public void setNorthbound(String northbound) {
        this.northbound = northbound;
    }

    public String getSouthbound() {
        return southbound;
    }

    public void setSouthbound(String southbound) {
        this.southbound = southbound;
    }

    private String southbound;

    public ArrayList<String> getStation_type_list() {
        return station_type_list;
    }

    public void setStation_type_list(ArrayList<String> station_type_list) {
        this.station_type_list = station_type_list;
    }

    private ArrayList<String> station_type_list;

    public String getStop_id() {
        return stop_id;
    }

    public Boolean getIsTarget() {
        return isTarget;
    }

    public void setIsTarget(Boolean target) {
        isTarget = target;
    }

    private Boolean isTarget;

    public void setStop_id(String stop_id) {
        this.stop_id = stop_id;
    }

    public String getMap_id() {
        return map_id;
    }

    public void setMap_id(String map_id) {
        this.map_id = map_id;
    }

    public String getDirection_id() {
        return direction_id;
    }

    public void setDirection_id(String direction_id) {
        this.direction_id = direction_id;
    }

    private String direction_id;

    public String getStop_name() {
        return stop_name;

    }

    public void setStop_name(String stop_name) {
        this.stop_name = stop_name;
    }

    private String stop_name;
    private Double lat;

    public String getStation_type() {
        return station_type;
    }

    public void setStation_type(String station_type) {
        this.station_type = station_type;
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

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    private Double lon;
    private String station_name;



}
