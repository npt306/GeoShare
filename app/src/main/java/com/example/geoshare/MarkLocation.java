package com.example.geoshare;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.geoshare.Database.RealtimeDatabase.MarkLocationDatabase;
import com.example.geoshare.Database.RealtimeDatabase.RealtimeDatabase;
import com.example.geoshare.Model.MarkerLocationModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
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
//                activity.recreate();
//                maps.addMarker(new MarkerOptions().position(point).title(selected));
                maps.addMarker((typeIconMarker(new MarkerLocationModel(point, selected))));
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
                txtAddress.setText(point.latitude + ", " +point.longitude);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void readMarkersFromDatabase() {
        DatabaseReference markLocationReference = MarkLocationDatabase.getInstance().getReferenceToCurrentUser();
        markLocationReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<MarkerLocationModel> markerList = new ArrayList<>();
                    for (DataSnapshot typeSnapshot : dataSnapshot.getChildren()) {
                        String typeMarker = typeSnapshot.getKey();
                        for (DataSnapshot markerSnapshot : typeSnapshot.getChildren()) {
                            double latitude = markerSnapshot.child("latitude").getValue(Double.class);
                            double longitude = markerSnapshot.child("longitude").getValue(Double.class);
                            LatLng latLng = new LatLng(latitude, longitude);
                            String key = markerSnapshot.getKey();
                            MarkerLocationModel marker = new MarkerLocationModel(latLng, typeMarker);
                            marker.setKey(key);
                            markerList.add(marker);
                        }
                    }
                // Sau khi đọc xong, bạn có thể sử dụng danh sách markerList để làm gì đó
                // Ví dụ: hiển thị các marker lên bản đồ
                displayMarkers(markerList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
                Log.e("ReadMarkers", "Failed to read data", databaseError.toException());
            }
        });
    }

    public void displayMarkers(List<MarkerLocationModel> markerList) {
        for (MarkerLocationModel marker : markerList) {
            // Thêm marker vào bản đồ
            maps.addMarker(typeIconMarker(marker));
        }
    }
    public MarkerOptions typeIconMarker(MarkerLocationModel marker){
        MarkerOptions markerOptions = new MarkerOptions()
                .position(marker.getLatLng())
                .title(marker.getTypeMarker());

        // Chọn Custom Marker Icon tùy thuộc vào loại marker
        switch (marker.getTypeMarker()) {
            case "Home":
                markerOptions.icon(getBitmapDescriptorFromVector(activity.getApplicationContext(),R.drawable.ic_home_marker));
                break;
            case "School":
                markerOptions.icon(getBitmapDescriptorFromVector(activity.getApplicationContext(),R.drawable.ic_school_marker));
                break;
            case "Workplace":
                markerOptions.icon(getBitmapDescriptorFromVector(activity.getApplicationContext(),R.drawable.ic_workplace_marker));
                break;
            case "Restaurant":
                markerOptions.icon(getBitmapDescriptorFromVector(activity.getApplicationContext(),R.drawable.ic_restaurant_marker));
                break;
            default:
                // Sử dụng icon mặc định nếu không có loại marker nào phù hợp
                markerOptions.icon(getBitmapDescriptorFromVector(activity.getApplicationContext(),R.drawable.ic_default_marker));
                break;
        }
        return markerOptions;
    }
    private BitmapDescriptor getBitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}