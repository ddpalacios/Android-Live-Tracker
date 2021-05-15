package com.example.cta_map.Displayers;

import com.example.cta_map.DataBase.CTA_Stops;

import java.io.Serializable;
import java.util.ArrayList;

public class TrainStops implements Serializable {

    public Boolean getViewIcon() {
        return viewIcon;
    }

    public void setViewIcon(Boolean viewIcon) {
        this.viewIcon = viewIcon;
    }

    /*
    <train>
                <rn>831</rn>
                <destSt>30089</destSt>
                <destNm>95th/Dan Ryan</destNm>
                <trDr>5</trDr>
                <nextStaId>41400</nextStaId>
                <nextStpId>30270</nextStpId>
                <nextStaNm>Roosevelt</nextStaNm>
                <prdt>20201224 22:19:47</prdt>
                <arrT>20201224 22:20:47</arrT>
                <isApp>1</isApp>
                <isDly>0</isDly>
                <flags />
                <lat>41.87678</lat>
                <lon>-87.62756</lon>
                <heading>178</heading>
            </train>


     */
    private Boolean viewIcon;
    private String rn;
    private String destSt;
    private String trDr;
    private String nextStaId;
    private String nextStpID;
    private String nextStaNm;
    private String prdt;
    private String arrT;
    private String staId ;
    private String stpId ;


    private String staNm ;

    public String getStaId() {
        return staId;
    }

    public void setStaId(String staId) {
        this.staId = staId;
    }

    public String getStpId() {
        return stpId;
    }

    public void setStpId(String stpId) {
        this.stpId = stpId;
    }

    public String getStaNm() {
        return staNm;
    }

    public void setStaNm(String staNm) {
        this.staNm = staNm;
    }

    public String getStpDe() {
        return stpDe;
    }

    public void setStpDe(String stpDe) {
        this.stpDe = stpDe;
    }

    public String getRt() {
        return rt;
    }

    public void setRt(String rt) {
        this.rt = rt;
    }

    private String stpDe ;
    private String rt;

    public ArrayList<CTA_Stops> getRemaining_stops() {
        return remaining_stops;
    }

    public void setRemaining_stops(ArrayList<CTA_Stops> remaining_stops) {
        this.remaining_stops = remaining_stops;
    }

    private ArrayList<CTA_Stops> remaining_stops;

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    private Boolean isSelected;



    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    private String userStatus;
    private String isApp;

    public Double getUser_lat() {
        return user_lat;
    }

    public void setUser_lat(Double user_lat) {
        this.user_lat = user_lat;
    }

    public Double getUser_lon() {
        return user_lon;
    }

    public void setUser_lon(Double user_lon) {
        this.user_lon = user_lon;
    }

    public Double getUser_to_target_distance() {
        return user_to_target_distance;
    }

    public void setUser_to_target_distance(Double user_to_target_distance) {
        this.user_to_target_distance = user_to_target_distance;
    }

    private Double user_to_target_distance;
    private Double user_lat;
    private Double user_lon;
    private String isDly;
    private String isFlt;

    public String getIsFlt() {
        return isFlt;
    }

    public void setIsFlt(String isFlt) {
        this.isFlt = isFlt;
    }

    public Integer getNextStopEtA() {
        return nextStopEtA;
    }

    public void setNextStopEtA(Integer nextStopEtA) {
        this.nextStopEtA = nextStopEtA;
    }

    private Integer nextStopEtA;
    private Double lat;

    public String getTarget_id() {
        return target_id;
    }

    public void setTarget_id(String target_id) {
        this.target_id = target_id;
    }

    private String target_id;
    private Double lon;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;
    private String heading;
    private Integer target_eta;

    public String getTrain_type() {
        return train_type;
    }

    public void setTrain_type(String train_type) {
        this.train_type = train_type;
    }

    private String train_type;

    public Integer getTarget_eta() {
        return target_eta;
    }

    public void setTarget_eta(Integer target_eta) {
        this.target_eta = target_eta;
    }


    public Double getTarget_distance() {
        return target_distance;
    }

    public void setTarget_distance(Double target_distance) {
        this.target_distance = target_distance;
    }

    public Double getNext_stop_distance() {
        return next_stop_distance;
    }

    public void setNext_stop_distance(Double next_stop_distance) {
        this.next_stop_distance = next_stop_distance;
    }

    private Double target_distance;
    private Double next_stop_distance;
    public String getDestNm() {
        return destNm;
    }

    public void setDestNm(String destNm) {
        this.destNm = destNm;
    }

    private String destNm;

    public String getRn() {
        return rn;
    }

    public void setRn(String rn) {
        this.rn = rn;
    }

    public String getDestSt() {
        return destSt;
    }

    public void setDestSt(String destSt) {
        this.destSt = destSt;
    }

    public String getTrDr() {
        return trDr;
    }

    public void setTrDr(String trDr) {
        this.trDr = trDr;
    }

    public String getNextStaId() {
        return nextStaId;
    }

    public void setNextStaId(String nextStaId) {
        this.nextStaId = nextStaId;
    }

    public String getNextStpID() {
        return nextStpID;
    }

    public void setNextStpID(String nextStpID) {
        this.nextStpID = nextStpID;
    }

    public String getNextStaNm() {
        return nextStaNm;
    }

    public void setNextStaNm(String nextStaNm) {
        this.nextStaNm = nextStaNm;
    }

    public String getPrdt() {
        return prdt;
    }

    public void setPrdt(String prdt) {
        this.prdt = prdt;
    }

    public String getArrT() {
        return arrT;
    }

    public void setArrT(String arrT) {
        this.arrT = arrT;
    }

    public String getIsApp() {
        return isApp;
    }

    public void setIsApp(String isApp) {
        this.isApp = isApp;
    }

    public String getIsDly() {
        return isDly;
    }

    public void setIsDly(String isDly) {
        this.isDly = isDly;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }


}
