package com.example.geoshare;

import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.geoshare.Database.RealtimeDatabase.MarkLocationDatabase;
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
    RadioGroup radioGroupMarkLocation;
    private Marker marker;
    Button buttonMarkLocation, buttonMarkerList;
    String selected;

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
        buttonMarkLocation = view.findViewById(R.id.button_mark_location);
        buttonMarkerList = view.findViewById(R.id.button_marker_list);
        radioGroupMarkLocation = view.findViewById(R.id.mark_location_radio_group);

        buttonMarkLocation.setOnClickListener(v -> {

            if(!selected.isEmpty()){
                MarkLocationDatabase.getInstance().addNewMarkerLocation(point, selected);
                Toast.makeText(activity, "Mark location as "+selected, Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
            else {
                Toast.makeText(activity, "Please choose type location", Toast.LENGTH_SHORT).show();
            }
        });

        buttonMarkerList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "Marker list", Toast.LENGTH_SHORT).show();
            }
        });

        radioGroupMarkLocation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i != -1){
                    RadioButton radioButton = view.findViewById(i);
                    selected = radioButton.getText().toString();
                }
            }
        });

        bottomSheetDialog.setOnDismissListener(dialog -> {
            if (marker != null) {
                marker.remove();
            }
            Toast.makeText(activity, "Bottom sheet dismiss", Toast.LENGTH_SHORT).show();
            selected = "";
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