package com.example.cta_map.DataBase;

import java.nio.DoubleBuffer;
import java.util.HashMap;
import java.util.HashSet;

public class CTA_Stops {
    public String STOP_ID = "STOP_ID";
    public String DIRECTION_ID = "DIRECTION_ID";
    public String STOP_NAME = "STOP_NAME";
    public String STATION_NAME = "STATION_NAME";
    public String STATION_DESCRIPTIVE_NAME = "STATION_DESCRIPTIVE_NAME";
    public String MAP_ID = "MAP_ID";
    public String ADA = "ADA";
    public String RED = "RED";
    public String BLUE = "BLUE";
    public String G = "G";
    public String BRN = "BRN";
    public String Y = "Y";
    public String P = "P";
    public String PEXP = "PEXP";
    public String PINK = "PINK";
    public String ORG = "ORG";
    public Double LAT;
    private String station_type;

    public String getStation_type() {
        return station_type;
    }

    public void setStation_type(String station_type) {
        this.station_type = station_type;
    }

    public Double LON;
    public String getSTOP_ID() {
        return STOP_ID;
    }

    public void setSTOP_ID(String STOP_ID) {
        this.STOP_ID = STOP_ID;
    }

    public String getDIRECTION_ID() {
        return DIRECTION_ID;
    }

    public void setDIRECTION_ID(String DIRECTION_ID) {
        this.DIRECTION_ID = DIRECTION_ID;
    }

    public String getSTOP_NAME() {
        return STOP_NAME;
    }

    public void setSTOP_NAME(String STOP_NAME) {
        this.STOP_NAME = STOP_NAME;
    }

    public String getSTATION_NAME() {
        return STATION_NAME;
    }

    public void setSTATION_NAME(String STATION_NAME) {
        this.STATION_NAME = STATION_NAME;
    }

    public String getMAP_ID() {
        return MAP_ID;
    }

    public void setMAP_ID(String MAP_ID) {
        this.MAP_ID = MAP_ID;
    }

    public String getADA() {
        return ADA;
    }

    public void setADA(String ADA) {
        this.ADA = ADA;
    }

    public String getRED() {
        return RED;
    }

    public void setRED(String RED) {
        this.RED = RED;
    }

    public String getBLUE() {
        return BLUE;
    }

    public void setBLUE(String BLUE) {
        this.BLUE = BLUE;
    }

    public String getG() {
        return G;
    }

    public void setG(String g) {
        G = g;
    }

    public String getBRN() {
        return BRN;
    }

    public void setBRN(String BRN) {
        this.BRN = BRN;
    }

    public String getY() {
        return Y;
    }

    public void setY(String y) {
        Y = y;
    }

    public String getP() {
        return P;
    }

    public void setP(String p) {
        P = p;
    }

    public String getPEXP() {
        return PEXP;
    }

    public void setPEXP(String PEXP) {
        this.PEXP = PEXP;
    }

    public String getPINK() {
        return PINK;
    }

    public void setPINK(String PINK) {
        this.PINK = PINK;
    }

    public String getORG() {
        return ORG;
    }

    public void setORG(String ORG) {
        this.ORG = ORG;
    }

    public Double getLAT() {
        return LAT;
    }

    public void setLAT(Double LAT) {
        this.LAT = LAT;
    }

    public Double getLON() {
        return LON;
    }

    public void setLON(Double LON) {
        this.LON = LON;
    }




}