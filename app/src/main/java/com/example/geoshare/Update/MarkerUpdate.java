package com.example.geoshare.Update;

import android.graphics.Bitmap;

import androidx.annotation.DrawableRes;

import com.example.geoshare.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Location;
import com.google.android.gms.maps.model.LatLng;

public class MarkerUpdate {
    public static void updateMarker(GoogleMap map, Location location, String batteryInfo) {
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions()
                .position(myLocation)
                .title("My location")
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.mipmap.ic_launcher, "Battery: " + batteryInfo)));

        map.clear();
        map.addMarker(markerOptions);
        map.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
    }

    private static Bitmap getMarkerBitmapFromView(@DrawableRes int resId, String batteryInfo) {
        // Code tạo marker bitmap từ view đã được định nghĩa trong MainActivity
        // ...
        return null;
    }
}
