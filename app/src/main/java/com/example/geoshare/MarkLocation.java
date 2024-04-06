package com.example.geoshare;

import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MarkLocation implements GoogleMap.OnMapLongClickListener {
    private MainActivity activity;
    private GoogleMap maps;
    TextView txtAddress;
    private Marker marker;
    Button buttonSetMarkLocation;

    public MarkLocation(MainActivity activity, GoogleMap maps) {
        this.activity = activity;
        this.maps = maps;
    }

    @Override
    public void onMapLongClick(@NonNull LatLng point) {
        // Tạo một Marker tại điểm đã nhấn
        marker = maps.addMarker(new MarkerOptions().position(point).title("Custom location")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        // Hiển thị BottomSheetDialog
        showBottomSheet(point);

        // Lấy địa chỉ từ LatLng
        getAddressFromLatLng(point);
    }

    private void showBottomSheet(LatLng point) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.place_mark_bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        txtAddress = view.findViewById(R.id.mark_location_address_textview);
        buttonSetMarkLocation = view.findViewById(R.id.button_set_mark_location);
        buttonSetMarkLocation.setOnClickListener(v -> {
            Toast.makeText(activity, "Set location", Toast.LENGTH_SHORT).show();
        });

        bottomSheetDialog.setOnDismissListener(dialog -> {
            if (marker != null) {
                marker.remove();
            }
            Toast.makeText(activity, "Bottom sheet dismiss", Toast.LENGTH_SHORT).show();
        });
    }

    private void getAddressFromLatLng(LatLng point) {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String addressLine = address.getAddressLine(0);
                // Cập nhật TextView trong BottomSheetDialog với địa chỉ
                txtAddress.setText(addressLine.toString());
            } else {
                TextView txtAddress = activity.findViewById(R.id.mark_location_address_textview);
                txtAddress.setText(point.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}