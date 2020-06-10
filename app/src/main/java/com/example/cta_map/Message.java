package com.example.cta_map;

import java.util.ArrayList;
import java.util.HashMap;

public class Message {
    String[] msg;
    String dir;
    ArrayList<Integer> train_etas;
    boolean t;
    boolean x;
    ArrayList<HashMap> chosen_trains;

    public String[] getMsg(){
        return this.msg;

    }

    public void setTrain_etas(ArrayList<Integer> train_etas){
        this.train_etas = train_etas;

    }

    public void set_chosen_trains(ArrayList<HashMap> chosen_trains){
        this.chosen_trains = chosen_trains;

    }

    public ArrayList<HashMap> get_chosen_trains(){
                return this.chosen_trains;
    }

    public ArrayList<Integer> get_train_etas(){
        return this.train_etas;
    }

    public void setMsg(String[] object){
        this.msg = object;

    }

    public void setDir(String object){
        this.dir = object;

    }
    public String getDir(){
        return this.dir;

    }
    public void keepSending(boolean x){
        this.x = x;

    }
    public boolean IsSending(){
        return this.x;
    }

    public boolean getClicked(){
        return this.t;

    }  public void setClicked(boolean t){
        this.t = t;

    }



}
