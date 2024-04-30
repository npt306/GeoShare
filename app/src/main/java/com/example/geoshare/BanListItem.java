package com.example.geoshare;

import android.net.Uri;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class BanListItem implements Serializable {
    private final String userId;
    private final String userName;
    private final String banDate;
    private final String unbanDate;
    private final String reportDescription;
    private final ArrayList<String> banReasons;
    private final URI URIImage;

    public BanListItem(String userId, String userName, String banDate, String unbanDate, String reportDescription, ArrayList<String> banReasons, Uri UriImage) {
        this.userId = userId;
        this.userName = userName;
        this.banDate = banDate;
        this.unbanDate = unbanDate;
        this.reportDescription = reportDescription;
        this.banReasons = banReasons;
        try {
            this.URIImage = new URI(UriImage.toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    // Getters
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getBanDate() { return banDate; }
    public String getUnbanDate() {return unbanDate; }
    public String getReportDescription() { return reportDescription; }
    public ArrayList<String> getBanReasons() { return banReasons; }
    public URI getURIImage() { return URIImage; }
    public Uri getUriImage() { return Uri.parse(URIImage.toString()); }
}
