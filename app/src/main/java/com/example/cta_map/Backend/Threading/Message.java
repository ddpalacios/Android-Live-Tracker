package com.example.cta_map.Backend.Threading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class Message {

    private ArrayList<IncomingTrains> incoming_trains;



    public void setIncoming_trains(ArrayList<IncomingTrains> incoming_trains){
        this.incoming_trains = incoming_trains;
    }
    public ArrayList<IncomingTrains> getIncoming_trains(){
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
