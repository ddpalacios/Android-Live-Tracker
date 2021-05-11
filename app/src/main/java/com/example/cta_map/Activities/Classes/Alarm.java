package com.example.cta_map.Activities.Classes;

import java.io.Serializable;
import java.util.ArrayList;

public class Alarm  implements Serializable {
    private Integer tues;
    private Integer wens;
    private Integer thur;
    private Integer fri;
    private Integer sat;
    private String  map_id;
    private String hour;

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    private String direction;

    public String getAlarm_id() {
        return alarm_id;
    }

    public void setAlarm_id(String alarm_id) {
        this.alarm_id = alarm_id;
    }

    private String alarm_id;

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    private String min;

    public ArrayList<Integer> getWeek_label_list() {
        return week_label_list;
    }

    public void setWeek_label_list(ArrayList<Integer> week_label_list) {
        this.week_label_list = week_label_list;
    }

    private ArrayList<Integer> week_label_list;

    public String getWeekLabel() {
        return weekLabel;
    }

    public void setWeekLabel(String weekLabel) {
        this.weekLabel = weekLabel;
    }

    private String weekLabel;

    public String getMap_id() {
        return map_id;
    }

    public void setMap_id(String map_id) {
        this.map_id = map_id;
    }

    private String station_name;
    private String station_type;

    public String getStationName() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public String getStationType() {
        return station_type;
    }

    public void setStationType(String station_type) {
        this.station_type = station_type;
    }

    public Integer getIsRepeating() {
        return isRepeating;
    }

    public void setIsRepeating(Integer isRepeating) {
        this.isRepeating = isRepeating;
    }

    private Integer isRepeating;

    public Integer getTues() {
        return tues;
    }

    public void setTues(Integer tues) {
        this.tues = tues;
    }

    public Integer getWens() {
        return wens;
    }

    public void setWens(Integer wens) {
        this.wens = wens;
    }

    public Integer getThur() {
        return thur;
    }

    public void setThur(Integer thur) {
        this.thur = thur;
    }

    public Integer getFri() {
        return fri;
    }

    public void setFri(Integer fri) {
        this.fri = fri;
    }

    public Integer getSat() {
        return sat;
    }

    public void setSat(Integer sat) {
        this.sat = sat;
    }

    public Integer getSun() {
        return sun;
    }

    public void setSun(Integer sun) {
        this.sun = sun;
    }

    private Integer sun;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getMon() {
        return mon;
    }

    public void setMon(Integer mon) {
        this.mon = mon;
    }

    private String time;
    private Integer mon;
}
