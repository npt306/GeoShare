package com.example.geoshare;

import java.sql.Timestamp;

public class ChatMessage {
    private String message;
    private String sender;
    private String receiver;
    private boolean isSeen;
    private Timestamp time;

    public ChatMessage() {
    }

    public ChatMessage(String message, String sender, String receiver, boolean isSeen, Timestamp time) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.isSeen = isSeen;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public Timestamp getTime() {
        return time;
    }
}
