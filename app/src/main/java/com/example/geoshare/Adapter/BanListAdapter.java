package com.example.geoshare.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.geoshare.AdminBanDetail;
import com.example.geoshare.BanListItem;
import com.example.geoshare.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BanListAdapter extends ArrayAdapter<BanListItem> {
    private Context context;
    private List<BanListItem> items;

    public BanListAdapter(Context context, List<BanListItem> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.banned_list_item, parent, false);
        }

        BanListItem chosenItem = items.get(position);

        // get UI elements
        CircleImageView itemImage = convertView.findViewById(R.id.user_image);
        TextView itemName = convertView.findViewById(R.id.user_name);
        Button showButton = convertView.findViewById(R.id.show_button);

        // upload UI elements
        if (chosenItem.getURIImage() != null){
            Glide.with(context).load(chosenItem.getUriImage()).into(itemImage);
        }

        itemName.setText(chosenItem.getUserName());

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start activity
                Intent intent = new Intent(context, AdminBanDetail.class);
                intent.putExtra("chosenBan", chosenItem);
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}

