package com.example.cta_map.Backend.Threading;

import com.example.cta_map.Displayers.Train;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class Message {


    public boolean isSwitchDir() {
        return willSwitchDir;
    }

    public void setSwitchDir(boolean willSwitchDir) {
        this.willSwitchDir = willSwitchDir;
    }

    private boolean willSwitchDir;
    private ArrayList<Train> incoming_trains;
    private String targetDir;

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















































    HashMap<String, ArrayList<HashMap>> parsedTrainData;
    String[] msg;
    String dir;
    HashMap train_etas;
    boolean t;
    TreeMap<Integer, String> tree;
    boolean iswaiting = false;
    boolean x;
    HashMap<String, String> train_content;
    ArrayList<HashMap> ignored;
    String next_stop;
    String main;
    ArrayList<HashMap> chosen_trains;
    String coord;
    List<String> sub;



























    public String[] getRawTrainContent(){
        return this.msg;

    }

    public void setStatus(Boolean waiting){
        this.iswaiting = waiting;
    }

    public void setParsedTrainData(HashMap<String, ArrayList<HashMap>> parsedTrainData){
        this.parsedTrainData = parsedTrainData;

    }

    public HashMap<String, ArrayList<HashMap>> getParsedTrainData() {
        return parsedTrainData;
    }


    public void setTrainMap(TreeMap<Integer, String> map){
        this.tree = map;

    }

    public TreeMap<Integer, String> getTrainMap(){
        return this.tree;
    }

    //
//    public Boolean isWaiting(){
//        return this.iswaiting;
//    }
//
//    public void setSubStations(List<String> sub){this.sub = sub;}
//    public List<String> getSubStations(){return this.sub;}
//
//
//    public HashMap<String, String> getTargetContent(){
//        return this.train_content;
//    }
//    public void setTargetContent(HashMap<String, String> content){
//        this.train_content = content;
//    }
//
//    public void setTrain_etas(HashMap train_etas){
//        this.train_etas = train_etas;
//
//    }
//
//    public void setMainStation(String main){
//        this.main = main;
//    }
//    public String getMainStation(){
//        return this.main;
//    }
//
//
//    public void set_chosen_trains(ArrayList<HashMap> chosen_trains){
//        this.chosen_trains = chosen_trains;
//
//    }
//
//    public ArrayList<HashMap> get_chosen_trains(){
//                return this.chosen_trains;
//    }
//
//    public HashMap get_train_etas(){
//        return this.train_etas;
//    }
//
    public void setRawTrainList(String[] object){
        this.msg = object;

    }
//
//    public void setIgnored(ArrayList<HashMap> ignored){
//        this.ignored = ignored;
//    }
//
//    public ArrayList<HashMap> getIgnored(){
//        return this.ignored;
//    }
//
//    public void setNextStop(String stop){
//        this.next_stop = stop;
//    }
//
//    public String getNextStop(){
//        return this.next_stop;
//    }
//
//    public void setCoord(String lat, String lon){
//        this.coord = lat +","+lon;
//
//    }    public String getCoord(){
//        return this.coord;
//    }
//
//
//    public void setDir(String object){
//        this.dir = object;
//
//    }
//    public String getDir(){
//        return this.dir;
//
//    }
        public void keepSending(boolean x) {
            this.x = x;
        }
    public boolean IsSending(){
        return this.x;
    }
//
//    public boolean getClicked(){
//        return this.t;
//
//    }  public void setClicked(boolean t){
//        this.t = t;
//
//    }



}
