package com.example.cta_map.Backend.Threading;

import com.example.cta_map.Displayers.Train;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class Message {


    /*
    Object that Interacts with threads

     */


    public Boolean getDirectionChanged() {
        return DirectionChanged;
    }

    public void setDirectionChanged(Boolean directionChanged) {
        DirectionChanged = directionChanged;
    }

    private Boolean DirectionChanged;

    public Boolean getSendingNotifications() {
        return IsSendingNotifications;
    }

    public void setSendingNotifications(Boolean sendingNotifications) {
        IsSendingNotifications = sendingNotifications;
    }

    private  Boolean IsSendingNotifications;
    private ArrayList<Train> incoming_trains;
    private String targetDir;

    public String getTARGET_MAP_ID() {
        return TARGET_MAP_ID;
    }

    public void setTARGET_MAP_ID(String TARGET_MAP_ID) {
        this.TARGET_MAP_ID = TARGET_MAP_ID;
    }

    private String TARGET_MAP_ID;
    boolean x;

    public ArrayList<Train> getOld_trains() {
        return old_trains;
    }

    public void setOld_trains(ArrayList<Train> old_trains) {
        this.old_trains = old_trains;
    }

    ArrayList<Train> old_trains;



    public void setDir(String targetDir) {
        this.targetDir = targetDir;
    }

    public String getDir() {
        return this.targetDir;
    }

    public String getTarget_name() {
        return target_name;
    }

    public String getTarget_type() {
        return target_type;
    }

    public void setTarget_type(String target_type) {
        this.target_type = target_type;
    }

    private String target_type;

    public void setTarget_name(String target_name) {
        this.target_name = target_name;
    }

    private String target_name;

    public void setIncoming_trains(ArrayList<Train> incoming_trains){
        this.incoming_trains = incoming_trains;
    }
    public ArrayList<Train> getIncoming_trains(){
        return this.incoming_trains;
    }


        public void keepSending(boolean x) {
            this.x = x;
        }
    public boolean IsSending(){
        return this.x;
    }



}
