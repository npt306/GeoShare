package com.example.geoshare.Model;

import com.google.android.gms.maps.model.LatLng;

public class MarkerLocationModel {
    private String typeMarker;
    private LatLng latLng;
    private String key;
    public MarkerLocationModel(LatLng latLng, String typeMarker){
        this.latLng = latLng;
        this.typeMarker = typeMarker;
    }


    public String getTypeMarker() {
        return typeMarker;
    }

    public void setTypeMarker(String typeMarker) {
        this.typeMarker = typeMarker;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
