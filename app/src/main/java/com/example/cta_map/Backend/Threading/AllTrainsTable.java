package com.example.cta_map.Backend.Threading;

public class AllTrainsTable {
    private String train_id;
    private boolean isNotified;
    private String pred_arrival_time;
    private String next_stop;
    private String next_stop_eta;
    private Double next_stop_distance;
    private boolean isDelayed;

    private boolean isApproaching;
    private Double distance_to_target;
    private String to_target_eta;
    private String tracking_type;
    private Double train_lat;
    private Double train_lon;
    private String target_id;

    public String getTrain_dir() {
        return train_dir;
    }

    public void setTrain_dir(String train_dir) {
        this.train_dir = train_dir;
    }

    private String train_dir;


    public String getTrain_id() {
        return train_id;
    }

    public void setTrain_id(String train_id) {
        this.train_id = train_id;
    }

    public boolean isNotified() {
        return isNotified;
    }

    public void setNotified(boolean notified) {
        isNotified = notified;
    }

    public String getPred_arrival_time() {
        return pred_arrival_time;
    }

    public void setPred_arrival_time(String pred_arrival_time) {
        this.pred_arrival_time = pred_arrival_time;
    }

    public String getNext_stop() {
        return next_stop;
    }

    public void setNext_stop(String next_stop) {
        this.next_stop = next_stop;
    }

    public String getNext_stop_eta() {
        return next_stop_eta;
    }

    public void setNext_stop_eta(String next_stop_eta) {
        this.next_stop_eta = next_stop_eta;
    }

    public Double getNext_stop_distance() {
        return next_stop_distance;
    }

    public void setNext_stop_distance(Double next_stop_distance) {
        this.next_stop_distance = next_stop_distance;
    }

    public boolean isDelayed() {
        return isDelayed;
    }

    public void setDelayed(boolean delayed) {
        isDelayed = delayed;
    }

    public boolean isApproaching() {
        return isApproaching;
    }

    public void setApproaching(boolean approaching) {
        isApproaching = approaching;
    }

    public Double getDistance_to_target() {
        return distance_to_target;
    }

    public void setDistance_to_target(Double distance_to_target) {
        this.distance_to_target = distance_to_target;
    }

    public String getTo_target_eta() {
        return to_target_eta;
    }

    public void setTo_target_eta(String to_target_eta) {
        this.to_target_eta = to_target_eta;
    }

    public String getTracking_type() {
        return tracking_type;
    }

    public void setTracking_type(String tracking_type) {
        this.tracking_type = tracking_type;
    }

    public Double getTrain_lat() {
        return train_lat;
    }

    public void setTrain_lat(Double train_lat) {
        this.train_lat = train_lat;
    }

    public Double getTrain_lon() {
        return train_lon;
    }

    public void setTrain_lon(Double train_lon) {
        this.train_lon = train_lon;
    }

    public String getTarget_id() {
        return target_id;
    }

    public void setTarget_id(String target_id) {
        this.target_id = target_id;
    }





}
