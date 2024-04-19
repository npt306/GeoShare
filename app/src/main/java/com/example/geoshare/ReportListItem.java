package com.example.geoshare;

public class ReportListItem {
    private String userName;
    private int imageId;

    public ReportListItem(String userName, int imageId) {
        this.userName = userName;
        this.imageId = imageId;
    }

    // Getter và Setter
    public String getUserName() { return userName; }
    public int getImageId() { return imageId; }
}
