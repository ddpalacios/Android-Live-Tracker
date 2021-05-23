package com.example.cta_map.Backend.Threading;

import android.os.Handler;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cta_map.Activities.Classes.Station;
import com.example.cta_map.Activities.TrainTimes_Adapter_frag;
import com.example.cta_map.Displayers.Train;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Message implements Serializable {
    public Boolean getDoneNotified() {
        return isDoneNotified;
    }

    public Boolean getIs_retrieving_first_nearest_train() {
        return is_retrieving_first_nearest_train;
    }

    public void setIs_retrieving_first_nearest_train(Boolean is_retrieving_first_nearest_train) {
        this.is_retrieving_first_nearest_train = is_retrieving_first_nearest_train;
    }

    private Boolean is_retrieving_first_nearest_train;
    public Train getCurrentNotificationTrain() {
        return currentNotificationTrain;
    }

    public void setCurrentNotificationTrain(Train currentNotificationTrain) {
        this.currentNotificationTrain = currentNotificationTrain;
    }

    private Train currentNotificationTrain;

    public Boolean getMadeBroadcastSwitch() {
        return madeBroadcastSwitch;
    }

    public void setMadeBroadcastSwitch(Boolean madeBroadcastSwitch) {
        this.madeBroadcastSwitch = madeBroadcastSwitch;
    }

    private Boolean madeBroadcastSwitch;

    public void setDoneNotified(Boolean doneNotified) {
        isDoneNotified = doneNotified;
    }

    private Boolean isDoneNotified;

    public Boolean getDestoryed() {
        return isDestoryed;
    }

    public void setDestoryed(Boolean destoryed) {
        isDestoryed = destoryed;
    }

    /*
        Object that Interacts with threads

         */

    public Boolean getAlarmTriggered() {
        return AlarmTriggered;
    }

    public void setAlarmTriggered(Boolean alarmTriggered) {
        AlarmTriggered = alarmTriggered;
    }

    Boolean AlarmTriggered;
    Double user_lat;

    public Train getNew_next_train_to_track() {
        return new_next_train_to_track;
    }

    public void setNew_next_train_to_track(Train new_next_train_to_track) {
        this.new_next_train_to_track = new_next_train_to_track;
    }

    Train new_next_train_to_track;

    public Double getUser_lat() {
        return user_lat;
    }

    public Boolean getInMinutes() {
        return inMinutes;
    }

    public void setInMinutes(Boolean inMinutes) {
        this.inMinutes = inMinutes;
    }

    public Boolean getInStations() {
        return inStations;
    }

    public void setInStations(Boolean inStations) {
        this.inStations = inStations;
    }

    Boolean inMinutes, inStations;

    public void setUser_lat(Double user_lat) {
        this.user_lat = user_lat;
    }

    public Double getUser_lon() {
        return user_lon;
    }

    public void setUser_lon(Double user_lon) {
        this.user_lon = user_lon;
    }

    Double user_lon;
    public Boolean getSharingFullConnection() {
        return isSharingFullConnection;
    }

    public void setSharingFullConnection(Boolean sharingFullConnection) {
        isSharingFullConnection = sharingFullConnection;
    }

    Boolean isSharingFullConnection;

    private String notification_message, notification_subtitle;
    private Boolean isDestoryed;

    public String getNotification_message() {
        return notification_message;
    }

    public void setNotification_message(String notification_message) {
        this.notification_message = notification_message;
    }

    public String getNotification_subtitle() {
        return notification_subtitle;
    }

    public void setNotification_subtitle(String notification_subtitle) {
        this.notification_subtitle = notification_subtitle;
    }

    public TrainTimes_Adapter_frag getTrainTimes_adapter_frag() {
        return trainTimes_adapter_frag;
    }

    public void setTrainTimes_adapter_frag(TrainTimes_Adapter_frag trainTimes_adapter_frag) {
        this.trainTimes_adapter_frag = trainTimes_adapter_frag;
    }

    public Handler getHandler() {
        return handler;
    }


    public Boolean getRefreshed() {
        return refreshed;
    }

    public void setRefreshed(Boolean refreshed) {
        this.refreshed = refreshed;
    }

    private Boolean refreshed;
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    private Handler handler;

    public String getStop_id() {
        return stop_id;
    }

    public void setStop_id(String stop_id) {
        this.stop_id = stop_id;
    }

    private String stop_id;

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

    public Train getNearestTrain() {
        return nearestTrain;
    }

    public void setNearestTrain(Train nearestTrain) {
        this.nearestTrain = nearestTrain;
    }

    private Train nearestTrain;
    private Content_Parser_Thread content_parser_thread;
    private Thread t1;
    private Thread t2;
    private Boolean redNotified;

    public Boolean getApproachingNotified() {
        return isApproachingNotified;
    }

    public void setApproachingNotified(Boolean approachingNotified) {
        isApproachingNotified = approachingNotified;
    }

    private Boolean isApproachingNotified;

    public Boolean getRedNotified() {
        return redNotified;
    }

    public void setRedNotified(Boolean redNotified) {
        this.redNotified = redNotified;
    }

    public Boolean getYellowNotified() {
        return yellowNotified;
    }

    public void setYellowNotified(Boolean yellowNotified) {
        this.yellowNotified = yellowNotified;
    }

    public Boolean getGreenNotified() {
        return greenNotified;
    }

    public void setGreenNotified(Boolean greenNotified) {
        this.greenNotified = greenNotified;
    }

    private Boolean yellowNotified;
    private Boolean greenNotified;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;


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


    public Station getTarget_station() {
        return target_station;
    }

    public void setTarget_station(Station target_station) {
        this.target_station = target_station;
    }

    public String getFinalDest() {
        return finalDest;
    }

    public void setFinalDest(String finalDest) {
        this.finalDest = finalDest;
    }

    private String finalDest;
    private Station target_station;
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
    private boolean ScheduledGreenNotified;
    private boolean ScheduledYellowNotified;

    public boolean isScheduledGreenNotified() {
        return ScheduledGreenNotified;
    }

    public void setScheduledGreenNotified(boolean scheduledGreenNotified) {
        ScheduledGreenNotified = scheduledGreenNotified;
    }

    public boolean isScheduledYellowNotified() {
        return ScheduledYellowNotified;
    }

    public void setScheduledYellowNotified(boolean scheduledYellowNotified) {
        ScheduledYellowNotified = scheduledYellowNotified;
    }

    public boolean isScheduledNotified() {
        return ScheduledRedNotified;
    }

    public void setScheduledRedNotified(boolean scheduledRedNotified) {
        ScheduledRedNotified = scheduledRedNotified;
    }

    private boolean ScheduledRedNotified;

    public boolean isDelayedNotified() {
        return isDelayedNotified;
    }

    public void setDelayedNotified(boolean delayedNotified) {
        isDelayedNotified = delayedNotified;
    }

    public void setScheduledNotified(boolean scheduledNotified) {
        isScheduledNotified = scheduledNotified;
    }

    public boolean getScheduledNotified() {
        return isScheduledNotified;
    }

    private  boolean isDelayedNotified, isScheduledNotified;


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
