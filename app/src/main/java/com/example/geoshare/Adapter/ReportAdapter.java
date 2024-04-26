package com.example.geoshare.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.geoshare.AdminReportDetail;
import com.example.geoshare.Database.RealtimeDatabase.RealtimeDatabase;
import com.example.geoshare.R;
import com.example.geoshare.Report;
import com.example.geoshare.ReportListItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReportAdapter extends ArrayAdapter<ReportListItem> {
    private Context context;
    private List<ReportListItem> items;

    public ReportAdapter(Context context, List<ReportListItem> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.report_list_item, parent, false);
        }

        ReportListItem chosenItem = items.get(position);

        // get UI elements
        CircleImageView itemImage = convertView.findViewById(R.id.user_image);
        TextView itemName = convertView.findViewById(R.id.user_name);
        Button showButton = convertView.findViewById(R.id.show_button);
        TextView report_time = convertView.findViewById(R.id.report_time);

        // upload UI elements
        if (chosenItem.getImage() != null){
            Glide.with(context).load(chosenItem.getImage()).into(itemImage);
        }

        itemName.setText(chosenItem.getReceiverName());
        // get hours
        int hours = getHoursFromTimestamps(chosenItem.getTimestamp());
        if (hours > 24){
            int days = hours / 24;
            report_time.setText(days + "d");
        } else {
            report_time.setText(hours + "h");
        }

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // add data from db
                DatabaseReference reportRef = RealtimeDatabase.getInstance()
                        .getReportsReference()
                        .child(chosenItem.getTimestamp())
                        .child(chosenItem.getSenderId());
                reportRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // get report detail
                        String receiverId = snapshot.child("receiver").getValue(String.class);
                        String reportDescription = snapshot.child("reportDescription").getValue(String.class);
                        ArrayList<String> reportProblems = (ArrayList<String>) snapshot.child("reportProblems").getValue();
                        Report thisReport = new Report(chosenItem.getSenderId(), receiverId, reportDescription, reportProblems, chosenItem.getTimestamp());

                        // start activity
                        Intent intent = new Intent(context, AdminReportDetail.class);
                        intent.putExtra("chosenReport", thisReport);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        return convertView;
    }

    private static int getHoursFromTimestamps(String strTimestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date reportDate;
        try {
            reportDate = dateFormat.parse(strTimestamp);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Date currentDate = new Date();
        long millis = currentDate.getTime() - reportDate.getTime();
        int hours   = (int) ((millis / (1000*60*60)));
        return hours;
    }
}

