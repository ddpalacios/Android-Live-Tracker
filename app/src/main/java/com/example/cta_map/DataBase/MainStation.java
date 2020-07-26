package com.example.cta_map.DataBase;

public class MainStation {
    String TrainLine;
    String NorthBound;
    String SouthBound, s2;
    public MainStation(String TrainLine, String NorthBound, String SouthBound, String s2){
        this.TrainLine = TrainLine;
        this.NorthBound = NorthBound;
        this.SouthBound = SouthBound;
        this.s2 = s2;

    }

    public void setTrainLine(String trainLine) {
        this.TrainLine = trainLine;
    }


    public String getTrainLine() {
        return this.TrainLine;
    }

    public void setNorthBound(String northBound) {
        this.NorthBound = northBound;
    }


    public String getNorthBound() {
        return this.NorthBound;
    }

    public void setSouthBound(String southBound) {
        this.SouthBound = southBound;
    }

    public String getSouthBound() {
        return this.SouthBound;
    }
    public String getSouthBound2() {
        return this.s2;
    }
}
