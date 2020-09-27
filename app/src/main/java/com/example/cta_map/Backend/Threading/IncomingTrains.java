package com.example.cta_map.Backend.Threading;

public class IncomingTrains {
    private String rn;
    private String destNm;
    private String trDr;
    private String nextStpId;
    private String nextStaNm;
    private String prdt;
    private String arrT;
    private String isApp;
    private String isDly;
    private Double lat;
    private Double lon;

    public String getRn() {
        return this.rn;
    }

    public String getDestNm() {
        return this.destNm;
    }

    public String getTrDr() {
        return this.trDr;
    }

    public String getNextStpId() {
        return this.nextStpId;
    }

    public String getNextStaNm() {
        return this.nextStaNm;
    }

    public String getPrdt() {
        return this.prdt;
    }

    public String getArrT() {
        return this.arrT;
    }

    public String getIsApp() {
        return this.isApp;
    }

    public String getIsDly() {
        return this.isDly;
    }

    public Double getLat() {
        return this.lat;
    }

    public Double getLon() {
        return this.lon;
    }
    public void setRn(String rn){this.rn = rn;}
    public void setDestNm(String destNm){this.destNm = destNm;};
    public void setTrDr(String trDr){this.trDr = trDr;}
    public void setnextStpId(String nextStpId){this.nextStpId = nextStpId;}
    public void setNextStaNm(String nextStaNm){this.nextStaNm = nextStaNm;}
    public void setPrdt(String prdt){this.prdt = prdt;}
    public void setArrT(String arrT){this.arrT = arrT;}
    public void setIsApp(String isApp){this.isApp = isApp;}
    public void setIsDly(String isDly){this.isDly = isDly;}
    public void setLat(Double lat){this.lat = lat;}
    public void setLon(Double lon){this.lon = lon;}




}
