package com.example.cta_map.DataBase;

public class MainStation {
    public String StationType;
    public String NorthBound;

    public String getStationType() {
        return StationType;
    }

    public void setStationType(String stationType) {
        StationType = stationType;
    }

    public String getNorthBound() {
        return NorthBound;
    }

    public void setNorthBound(String northBound) {
        NorthBound = northBound;
    }

    public String getSouthBound() {
        return SouthBound;
    }

    public void setSouthBound(String southBound) {
        SouthBound = southBound;
    }

    public String getExpress() {
        return express;
    }

    public void setExpress(String express) {
        this.express = express;
    }

    public String SouthBound;
    public String express;
}
