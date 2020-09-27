package com.example.cta_map.DataBase;

import java.util.HashMap;
import java.util.HashSet;

public class CTA_Stations {
    private String id;
    private String station_name;
    private String coord;
    private String red;
    private String blue;
    private String green;
    private String brown;
    private String purple;
    private String yellow;
    private String pink;
    private String orange;
    public CTA_Stations(String id){
        this.id = id;
    }

    public void setRed(String state){
        this.red = state;
    }
    public void setBlue(String state){
        this.blue= state;
    }
    public void setName(String station_name){
        this.station_name = station_name;
    }
    public void setGreen(String state){
        this.green= state;

    }
    public void setBrown(String state){
        this.brown= state;

    }
    public void setPurple(String state){
        this.purple = state;
    }
    public void setYellow(String state){
        this.yellow = state;
    }
    public void setPink(String state){
        this.pink = state;
    }
    public void setOrange(String state){
        this.orange = state;
    }

    public void setCoordinates(String coord){
        this.coord = coord;
    }


    public String getRed(){
        return this.red;
    }
    public String  getBlue(){
        return this.blue;
    }
    public String  getGreen(){
        return this.green;
    }
    public String  getBrown(){
        return this.brown;
    }
    public String  getPurple(){
        return this.purple;
    }
    public String  getYellow(){
        return this.yellow;
    }
    public String  getPink(){
        return this.pink;
    }
    public String  getOrange(){
        return this.orange;
    }
    public String getStation_name(){return this.station_name;}
    public String  getID(){
        return this.id;
    }

    public HashMap<String, String> getCoordinates(){
        HashMap<String, String> coordinates = new HashMap<>();
        this.coord = coord.replaceAll("[()]","").trim();
        coordinates.put("lat", this.coord.split(",")[0]);
        coordinates.put("lon", this.coord.split(",")[1]);

        return coordinates;

    }





}
