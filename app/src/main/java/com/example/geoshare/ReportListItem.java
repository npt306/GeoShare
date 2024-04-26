package com.example.geoshare;

import android.net.Uri;

public class ReportListItem {
    private String receiverName;
    private String senderId;
    private String timestamp;
    private Uri image;

    public ReportListItem(String receiverName, String senderId, String timestamp, Uri image) {
        this.receiverName = receiverName;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.image = image;
    }

    // Getter và Setter
    public String getReceiverName() { return receiverName; }
    public String getSenderId(){return senderId;}
    public String getTimestamp() {return timestamp;}
    public Uri getImage() { return image; }
}
