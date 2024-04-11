package com.example.geoshare;

import java.sql.Timestamp;

public class ChatMessage {
    private String messageID;
    private String message;
    private String sender;
    private String receiver;

    public ChatMessage() {
    }

    public ChatMessage(String messageID, String message, String sender, String receiver) {
        this.messageID = messageID;
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getMessageID() {
        return messageID;
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
}
