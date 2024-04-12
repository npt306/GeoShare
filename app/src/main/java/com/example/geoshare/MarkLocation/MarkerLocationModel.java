package com.example.geoshare.MarkLocation;

import com.google.android.gms.maps.model.LatLng;

public class MarkerLocationModel {
    public MarkerLocationModel() {
    }
    private String typeMarker;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String address;
    private LatLng latLng;
    private String key;
    public MarkerLocationModel(LatLng latLng, String typeMarker, String address){
        this.latLng = latLng;
        this.typeMarker = typeMarker;
        this.address = address;
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
