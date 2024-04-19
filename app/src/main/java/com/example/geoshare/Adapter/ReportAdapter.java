package com.example.geoshare.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geoshare.AdminReportDetail;
import com.example.geoshare.R;
import com.example.geoshare.ReportListItem;

import java.util.List;

public class ReportAdapter extends ArrayAdapter<ReportListItem> {
    private Context context;
    private List<ReportListItem> items;
    public int selectedPosition = -1;

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

        ReportListItem currentItem = items.get(position);

        ImageView itemImage = convertView.findViewById(R.id.user_image);
        TextView itemName = convertView.findViewById(R.id.user_name);
        Button showButton = convertView.findViewById(R.id.show_button);

        itemImage.setImageResource(currentItem.getImageId());
        itemName.setText(currentItem.getUserName());

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AdminReportDetail.class);
                context.startActivity(intent);
//                Toast.makeText(context, "hello2", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}

