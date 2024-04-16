package com.example.geoshare;

import java.util.ArrayList;

public class Report {
    private String sender;
    private String receiver;
    private String reportDescription;
    private ArrayList<String> reportProblems;
    public Report(String sender, String receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.reportProblems = new ArrayList<>();
        this.reportDescription = "";
    }
    public Report(String sender, String receiver, String reportDescription) {
        this.sender = sender;
        this.receiver = receiver;
        this.reportDescription = reportDescription;
        this.reportProblems = new ArrayList<>();
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

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReportDescription(String reportDescription) {
        this.reportDescription = reportDescription;
    }
}
