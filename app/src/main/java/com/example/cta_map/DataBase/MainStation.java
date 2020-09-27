package com.example.cta_map.DataBase;

public class MainStation {
    public String TrainLine;
    public String NorthBound;
    public String SouthBound;
    public MainStation(String TrainLine, String NorthBound, String SouthBound){
        this.TrainLine = TrainLine;
        this.NorthBound = NorthBound;
        this.SouthBound = SouthBound;

    }
    public MainStation CreateMainStationObject(String station_type, String northBound, String southBound){
        return new MainStation(station_type, northBound, southBound);
    }
    private void setTrainLine(String trainLine) {
        this.TrainLine = trainLine;
    }
    public String getTrainLine() {
        return this.TrainLine;
    }
    private void setNorthBound(String northBound) {
        this.NorthBound = northBound;
    }
    public String getNorthBound() {
        return this.NorthBound;
    }
    private void setSouthBound(String southBound) {
        this.SouthBound = southBound;
    }
    public String getSouthBound() {
        return this.SouthBound;
    }
}
