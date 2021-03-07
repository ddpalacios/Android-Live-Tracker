package com.example.cta_map.Activities.Classes;

public class StationLines {
    String line;
    int color;
    public StationLines(String line, int color){
        this.line = line;
        this.color = color;

    }

    public int getColor() {

        return color;
    }

    public String getLine() {
        return line;
    }
}
