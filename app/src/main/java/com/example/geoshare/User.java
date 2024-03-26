package com.example.geoshare;

import java.util.ArrayList;

public class User {
    private String id;
    private String username;
    private String status;
    private String imageURL;
    private ArrayList<String> friendList;
    private String locLat;
    private String locLong;

    public User(String id, String username, String status, String imageURL, ArrayList<String> friendList, String locLat, String locLong) {
        this.id = id;
        this.username = username;
        this.status = status;
        this.imageURL = imageURL;
        this.friendList = friendList;
        this.locLat = locLat;
        this.locLong = locLong;
    }
    public User() {

    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getLocLat() {
        return locLat;
    }

    public String getLocLong() {
        return locLong;
    }

    public ArrayList<String> getFriendList() {
        return friendList;
    }

    public void setId(String id) {
        this.id = id;
    }
}
