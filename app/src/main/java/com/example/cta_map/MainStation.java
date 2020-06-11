package com.example.cta_map;

public class MainStation {
    String TrainLine;
    String NorthBound;
    String SouthBound;
    public MainStation(String TrainLine, String NorthBound, String SouthBound){
        this.TrainLine = TrainLine;
        this.NorthBound = NorthBound;
        this.SouthBound = SouthBound;

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
}
