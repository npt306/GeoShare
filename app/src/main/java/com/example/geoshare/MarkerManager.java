package com.example.geoshare;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class MarkerManager {
    private static volatile MarkerManager instance;
    private HashMap<String, Marker> markerHashMap = new HashMap<>();

    MainActivity callerContext;
    public MarkerManager(Context callerContext){
        this.callerContext = (MainActivity) callerContext;
        this.markerHashMap = new HashMap<>();
    }

    public static MarkerManager getInstance() {
        if (instance == null) {
            synchronized (MarkerManager.class) {
                if (instance == null) {
                    instance = new MarkerManager(MainActivity.getInstance());
                }
            }
        }
        return instance;
    }

    // crop middle circle from a bitmap
    private Bitmap cropCircleFromBitmap(Bitmap bitmap){

        final int r = bitmap.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(r, r, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(outputBitmap);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawCircle((float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2,
                (float) bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return outputBitmap;
    }

    private Bitmap getBitmapFromImage(@DrawableRes int resId){
        BitmapDrawable bitmapdraw = (BitmapDrawable)callerContext.getResources().getDrawable(resId);
        Bitmap bitmap = bitmapdraw.getBitmap();
        bitmap = Bitmap.createScaledBitmap(bitmap, 128, 128, false);

        return cropCircleFromBitmap(bitmap);
    }

    public void createMarker(LatLng location, String MarkerID) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.avatar, "100%")));

        if (markerHashMap.containsKey(MarkerID)) {
            Marker oldMarker = markerHashMap.get(MarkerID);
            assert oldMarker != null;
            oldMarker.setVisible(true);
            Objects.requireNonNull(oldMarker).setPosition(location);
        } else {
            Marker newMarker = callerContext.getMaps().addMarker(markerOptions);
            markerHashMap.put(MarkerID, newMarker);

            if (Objects.equals(Objects.requireNonNull(newMarker).getTitle(), "My location")){
                addListener(newMarker);
            }
        }
    }

    private void addListener(Marker marker){
        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference()
                .child("batteryLevel")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Lấy dữ liệu từ dataSnapshot và cập nhật giá trị pin
                String batteryInfo = dataSnapshot.child("currentBattery").getValue(String.class);
                // Cập nhật thông tin pin của marker
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.avatar, batteryInfo)));
//                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromImage(R.drawable.avatar)));
                Log.d("DEBUG TAG", "Updating " + marker.getTitle());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }

    // Hàm để ẩn marker nhưng không xóa khỏi HashMap
    public void hideMarker(String friendId) {
        Marker marker = markerHashMap.get(friendId);
        if (marker != null) {
            marker.setVisible(false);
        }
    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId, String batteryInfo) {
        View customMarkerView = ((LayoutInflater) callerContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.market_custom, null);
        ImageView markerImageView = customMarkerView.findViewById(R.id.avatarImageView);
        TextView markerBatteryTextView = customMarkerView.findViewById(R.id.batteryTextView);
        markerImageView.setImageResource(resId);
        markerBatteryTextView.setText(batteryInfo);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }


    // Hàm để tạo Bitmap từ layout XML
    private Bitmap createMarkerBitmap() {
        View customMarkerView = callerContext.getLayoutInflater().inflate(R.layout.market_custom, null);
        ImageView avatarImageView = customMarkerView.findViewById(R.id.avatarImageView);
        TextView batteryTextView = customMarkerView.findViewById(R.id.batteryTextView);
//        String batteryPercentage = String.valueOf(getCurrentBatteryLevel());
        String batteryPercentage = "";
        Integer batteryInteger;

        DatabaseReference batteryRef = FirebaseDatabase.getInstance().getReference().child("batteryLevel").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        batteryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer battery = dataSnapshot.child("currentBattery").getValue(Integer.class);
                    Toast.makeText(callerContext, String.valueOf(battery),Toast.LENGTH_SHORT).show();
                    batteryTextView.setText(battery + "%");
//                    transfer(battery, batteryPercentage);
                } else {
                    // Không tìm thấy thông tin người dùng, xử lý tương ứng
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });

        batteryTextView.setText(batteryPercentage + "%");
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        customMarkerView.draw(canvas);
        return bitmap;
    }
}
