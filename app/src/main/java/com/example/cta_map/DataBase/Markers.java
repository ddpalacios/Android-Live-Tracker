package com.example.cta_map.DataBase;

public class Markers {
    public String getMarker_name() {
        return marker_name;
    }

    public void setMarker_name(String marker_name) {
        this.marker_name = marker_name;
    }

    public String getMarker_type() {
        return marker_type;
    }

    public void setMarker_type(String marker_type) {
        this.marker_type = marker_type;
    }

    public Double getMarker_lat() {
        return marker_lat;
    }

    public void setMarker_lat(Double marker_lat) {
        this.marker_lat = marker_lat;
    }

    public Double getMarker_lon() {
        return marker_lon;
    }

    public void setMarker_lon(Double marker_lon) {
        this.marker_lon = marker_lon;
    }

    public String getMarker_id() {
        return marker_id;
    }

    public void setMarker_id(String marker_id) {
        this.marker_id = marker_id;
    }

    private String marker_id;
    private String marker_name;
    private String marker_type;
    private Double marker_lat;
    private Double marker_lon;


}
