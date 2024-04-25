package com.example.geoshare;

import java.io.Serializable;
import java.util.ArrayList;

public class Report implements Serializable {
    private String senderId;
    private String receiverId;
    private String reportDescription;
    private ArrayList<String> reportProblems;
    private String timestamp;
    public Report(String senderId, String receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.reportProblems = new ArrayList<>();
        this.reportDescription = "";
    }
    public Report(String senderId, String receiverId, String reportDescription, ArrayList<String> reportProblems, String timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.reportDescription = reportDescription;
        this.reportProblems = reportProblems;
        this.timestamp = timestamp;
    }
    public void addProblems(String newProblem) {
        if(!this.reportProblems.contains(newProblem)) {
            this.reportProblems.add(newProblem);
        }
    }
    public void removeProblem(String problem) {
        this.reportProblems.remove(problem);
    }
    public String getReportDescription() {
        return reportDescription;
    }
    public ArrayList<String> getReportProblems() {
        return reportProblems;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getTimestamp(){return timestamp;}

    public void setReportDescription(String reportDescription) {
        this.reportDescription = reportDescription;
    }
}
