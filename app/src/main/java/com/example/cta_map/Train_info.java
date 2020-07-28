package com.example.cta_map;

public class Train_info {
    String next_stop_name;
    String next_stop_eta;
    String next_stop_dist;
    String target_name;
    String target_eta;
    String target_distance;
    String train_id;
    public Train_info(String next_stop_name,
                      String next_stop_eta,
                      String next_stop_dist,
                      String target_name,
                      String target_eta,
                      String target_distance,
                      String train_id){

        this.next_stop_name = next_stop_name;
        this.next_stop_eta = next_stop_eta;
        this.next_stop_dist = next_stop_dist;
        this.target_eta  = target_eta;
        this.target_name = target_name;
        this.target_distance = target_distance;
        this.train_id = train_id;
    }


    public String getNext_stop_dist() {
        return next_stop_dist;
    }

    public String getNext_stop_eta() {
        return next_stop_eta;
    }

    public String getNext_stop_name() {
        return next_stop_name;
    }


    public String getTarget_distance() {
        return target_distance;
    }

    public String getTarget_eta() {
        return target_eta;
    }

    public String getTarget_name() {
        return target_name;
    }

    public String getTrain_id() {
        return train_id;
    }


}
