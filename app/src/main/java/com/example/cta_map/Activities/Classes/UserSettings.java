package com.example.cta_map.Activities.Classes;


public class UserSettings {
    public String getAsStations() {
        return AsStations;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String status;

    public void setAsStations(String asStations) {
        AsStations = asStations;
    }

    public String getAsMinutes() {
        return AsMinutes;
    }

    public void setAsMinutes(String asMinutes) {
        AsMinutes = asMinutes;
    }

    String AsStations, AsMinutes;
    public String getIs_sharing_loc() {
        return is_sharing_loc;
    }

    public void setIs_sharing_loc(String is_sharing_loc) {
        this.is_sharing_loc = is_sharing_loc;
    }



    String is_sharing_loc;
    public String getGreen_limit() {
        return green_limit;
    }

    public void setGreen_limit(String green_limit) {
        this.green_limit = green_limit;
    }

    String green_limit;




    public String getYellow_limit() {
        return yellow_limit;
    }

    public void setYellow_limit(String yellow_limit) {
        this.yellow_limit = yellow_limit;
    }

    String yellow_limit;



}


