package com.example.geoshare.MarkLocation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geoshare.Database.RealtimeDatabase.MarkLocationDatabase;
import com.example.geoshare.R;

import java.util.ArrayList;
import java.util.List;

public class MarkLocationListAdapter extends RecyclerView.Adapter<MarkLocationListAdapter.ViewHolder> {
    private Context mContext;
    private List<MarkerLocationModel> markerLocationList;
    public MarkLocationListAdapter(Context context){
        this.mContext =context;
        this.markerLocationList = new ArrayList<>();
    }
    @NonNull
    @Override
    public MarkLocationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mark_location_list, parent, false);
        return new MarkLocationListAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MarkLocationListAdapter.ViewHolder holder, int position) {
        MarkerLocationModel marker = markerLocationList.get(position);
        holder.txtTypeMarker.setText(marker.getTypeMarker());
        holder.txtAddressMarker.setText(marker.getAddress());
        switch (marker.getTypeMarker()){
            case "Home":{
                holder.typeMarkerIcon.setImageResource(R.drawable.ic_home);
                break;
            }
            case "School":{
                holder.typeMarkerIcon.setImageResource(R.drawable.ic_school);
                break;
            }
            case "Workplace":{
                holder.typeMarkerIcon.setImageResource(R.drawable.ic_company);
                break;
            }
            case "Restaurant":{
                holder.typeMarkerIcon.setImageResource(R.drawable.ic_restaurant);
                break;
            }
        }
        holder.buttonDeleteMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MarkLocationDatabase.getInstance().deleteMarkerLocation(marker, mContext);

            }
        });
    }
 

    public void addMarkerLocationToList(MarkerLocationModel markerLocation) {
//        this.markerLocationList = new ArrayList<>(this.markerLocationList);
        this.markerLocationList.add(markerLocation);
        this.notifyDataSetChanged();
    }
    public void clearMarkerLocationList() {
        this.markerLocationList.clear();;
        this.markerLocationList = new ArrayList<>();
        this.notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return markerLocationList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView typeMarkerIcon;
        TextView txtTypeMarker, txtAddressMarker;
        ImageButton buttonDeleteMarker;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            typeMarkerIcon = itemView.findViewById(R.id.mark_location_list_type_icon);
            txtTypeMarker = itemView.findViewById(R.id.mark_location_list_type_textview);
            txtAddressMarker = itemView.findViewById(R.id.mark_location_list_address_textview);
            buttonDeleteMarker = itemView.findViewById((R.id.button_delete_mark_location_list));
        }
    }
}
