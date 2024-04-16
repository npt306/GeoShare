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
        Log.d("timeStamp", String.valueOf(timeStamp));

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
    public String getDateFromTimeStamp(){
        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm dd/MM"); //this format changeable
        dateFormatter.setTimeZone(TimeZone.getDefault());
        return dateFormatter.format(this.timeStamp);

//        Instant instant = Instant.ofEpochMilli ( now );
//        ZonedDateTime zdt = ZonedDateTime.ofInstant ( instant , ZoneOffset.UTC );
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern ( "HH:mm dd/MM" );
//        String output = formatter.format ( zdt );
//        return output;
    }
//    public String getDateTimeStamp() {
//        String date = longToDateString(timeStamp, "dd.MM.yyyyy");
//        String time = longToDateString(timeStamp, "HH:mm");
//        return date;
//    }
//    public String getTimeTimeStamp() {
//        String time = longToDateString(timeStamp, "HH:mm");
//        return time;
//    }
}
