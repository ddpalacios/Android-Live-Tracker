package com.example.cta_map;

import java.io.StringReader;

public class Stops {
    String name;
    String color;
    String dir;
    public Stops(String name, String color, String dir){
        this.name = name;
        this.color = color;
        this.dir = dir;
    }
    public String getDir(){
        return this.dir;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }
}
