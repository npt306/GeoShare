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
import com.example.geoshare.BannedListItem;
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

public class BannedListAdapter extends ArrayAdapter<BannedListItem> {
    private Context context;
    private List<BannedListItem> items;

    public BannedListAdapter(Context context, List<BannedListItem> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.banned_list_item, parent, false);
        }

        BannedListItem chosenItem = items.get(position);

        // get UI elements
        CircleImageView itemImage = convertView.findViewById(R.id.user_image);
        TextView itemName = convertView.findViewById(R.id.user_name);
        Button showButton = convertView.findViewById(R.id.show_button);

        // upload UI elements
        if (chosenItem.getImage() != null){
            Glide.with(context).load(chosenItem.getImage()).into(itemImage);
        }

        itemName.setText(chosenItem.getUserName());

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }
}

