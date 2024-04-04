package com.example.geoshare.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.geoshare.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

public class CustomMarkerAdapter implements GoogleMap.InfoWindowAdapter {

    private final View markerView;
    private Context context;

    public CustomMarkerAdapter(Context context) {
        this.context = context;
        markerView = LayoutInflater.from(context).inflate(R.layout.market_custom, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null; // Return null to use default info window
    }

    @Override
    public View getInfoContents(Marker marker) {
        render(marker, markerView);
        return markerView;
    }

    private void render(Marker marker, View view) {
        ImageView avatarImageView = view.findViewById(R.id.avatarImageView);
        TextView batteryTextView = view.findViewById(R.id.batteryTextView);

        // Set avatar and battery level here
        // For example:
        avatarImageView.setImageResource(R.mipmap.ic_launcher);
        batteryTextView.setText("Battery: 90%");
    }
}
