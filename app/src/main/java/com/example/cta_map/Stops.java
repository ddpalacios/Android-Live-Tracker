package com.example.cta_map;

public class Stops {
    String name;
    int color;
    public Stops(String name, int color){
        this.name = name;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        return name;
    }
}
