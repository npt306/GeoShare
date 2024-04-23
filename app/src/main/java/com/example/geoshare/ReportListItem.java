package com.example.geoshare;

public class ReportListItem {
    private String receiverName;
    private String senderId;
    private String timestamp;
    private int imageId;

    public ReportListItem(String receiverName, String senderId, String timestamp, int imageId) {
        this.receiverName = receiverName;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.imageId = imageId;
    }

    // Getter và Setter
    public String getReceiverName() { return receiverName; }
    public String getSenderId(){return senderId;}
    public String getTimestamp() {return timestamp;}
    public int getImageId() { return imageId; }
}
