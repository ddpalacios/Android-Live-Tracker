package com.example.cta_map;

public class Stations {
    String name, type, dir;
    public Stations(String name, String type, String dir){
        this.name= name;
        this.type = type;
        this.dir = dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getName(){
        return this.name;
    }

    public String getDir() {
        return dir;
    }

    public String getType() {
        return type;
    }

}
