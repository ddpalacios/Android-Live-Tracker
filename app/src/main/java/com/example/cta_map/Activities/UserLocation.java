package com.example.cta_map.Activities;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class UserLocation  {

   Double Lat, Lon;

    public Double getLat() {
        return Lat;
    }

    public void setLat(Double lat) {
        Lat = lat;
    }

    public Double getLon() {
        return Lon;
    }

    public void setLon(Double lon) {
        Lon = lon;
    }

    public Integer getHasLocation() {
        return HasLocation;
    }

    public void setHasLocation(Integer hasLocation) {
        HasLocation = hasLocation;
    }

    Integer HasLocation;
}
