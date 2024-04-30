package com.example.geoshare;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

public class BanListItem implements Serializable {
    private final String userId;
    private final String userName;
    private final String banDate;
    private final String unbanDate;
    private final String reportDescription;
    private final ArrayList<String> banReasons;
    private final Uri image;

    public BanListItem(String userId, String userName, String banDate, String unbanDate, String reportDescription, ArrayList<String> banReasons, Uri image) {
        this.userId = userId;
        this.userName = userName;
        this.banDate = banDate;
        this.unbanDate = unbanDate;
        this.reportDescription = reportDescription;
        this.banReasons = banReasons;
        this.image = image;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getBanDate() { return banDate; }
    public String getUnbanDate() {return unbanDate; }
    public String getReportDescription() { return reportDescription; }
    public ArrayList<String> getBanReasons() { return banReasons; }
    public Uri getImage() { return image; }
}
