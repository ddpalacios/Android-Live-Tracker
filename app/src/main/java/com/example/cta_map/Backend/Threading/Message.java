package com.example.cta_map.Backend.Threading;

import android.content.Context;
import android.os.Handler;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.TrainTimes_Adapter_frag;
import com.example.cta_map.Displayers.Train;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class Message implements Serializable {


    /*
    Object that Interacts with threads

     */

    public TrainTimes_Adapter_frag getTrainTimes_adapter_frag() {
        return trainTimes_adapter_frag;
    }

    public void setTrainTimes_adapter_frag(TrainTimes_Adapter_frag trainTimes_adapter_frag) {
        this.trainTimes_adapter_frag = trainTimes_adapter_frag;
    }

    TrainTimes_Adapter_frag trainTimes_adapter_frag;
    API_Caller_Thread api_caller_thread;

    public API_Caller_Thread getApi_caller_thread() {
        return api_caller_thread;
    }

    public void setApi_caller_thread(API_Caller_Thread api_caller_thread) {
        this.api_caller_thread = api_caller_thread;
    }

    public Content_Parser_Thread getContent_parser_thread() {
        return content_parser_thread;
    }

    public void setContent_parser_thread(Content_Parser_Thread content_parser_thread) {
        this.content_parser_thread = content_parser_thread;
    }

    public Thread getT1() {
        return t1;
    }

    public void setT1(Thread t1) {
        this.t1 = t1;
    }

    public Thread getT2() {
        return t2;
    }

    public void setT2(Thread t2) {
        this.t2 = t2;
    }

    Content_Parser_Thread content_parser_thread;
    Thread t1;
    Thread t2;


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


    public HashMap<String, String> getTarget_station() {
        return target_station;
    }

    public void setTarget_station(HashMap<String, String> target_station) {
        this.target_station = target_station;
    }

    private HashMap<String, String> target_station;
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


    public RecyclerView getRV() {
        return RV;
    }

    public void setRV(RecyclerView RV) {
        this.RV = RV;
    }

    private RecyclerView RV;
}
