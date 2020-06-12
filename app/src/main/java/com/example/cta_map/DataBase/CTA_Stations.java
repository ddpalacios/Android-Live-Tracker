package com.example.cta_map.DataBase;

public class CTA_Stations {
    String name;
    String red;
    String blue;
    String green;
    String brown;
    String purple;
    String yellow;
    String pink;
    String orange;
    String lat;
    String lon;
    public CTA_Stations(String name){
        this.name = name;
    }

    public void setRed(String state){
        this.red = state;
    }
    public void setBlue(String state){
        this.blue= state;
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
    public void setLat(String state){
        this.lat= state;
    }
    public void setLon(String state){
        this.lon = state;
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
    public String  getName(){
        return this.name;
    }

    public String getLat(){
        return  this.lat;
    }
    public String getLon(){
        return this.lon;
    }






}
