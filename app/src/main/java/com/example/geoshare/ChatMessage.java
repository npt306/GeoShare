package com.example.geoshare;

import android.text.format.DateFormat;
import android.util.Log;

import com.google.firebase.database.ServerValue;
import com.google.type.Date;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class ChatMessage {
    private String messageID;
    private String message;
    private String sender;
    private String receiver;
    private Long timeStamp;

    public ChatMessage() {
    }

    public ChatMessage(String messageID, String message, String sender, String receiver) {
        this.messageID = messageID;
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.timeStamp = System.currentTimeMillis();
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
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

    public Long getTimeStamp() {
        return timeStamp;
    }

    public String getDateFromTimeStamp() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm dd/MM"); // this format changeable
        dateFormatter.setTimeZone(TimeZone.getDefault());
        Log.d("timeStamp", String.valueOf(this.timeStamp));
        return dateFormatter.format(this.timeStamp);
    }
}
