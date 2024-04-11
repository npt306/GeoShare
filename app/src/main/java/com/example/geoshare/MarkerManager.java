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

import java.util.Objects;

public class MarkerManager {
    MainActivity callerContext;
    public MarkerManager(Context callerContext){
        this.callerContext = (MainActivity) callerContext;
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
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return outputBitmap;
    }


    // support converting image to bitmap, which will be used as marker icon
    private Bitmap getBitmapFromImage(@DrawableRes int resId){

        BitmapDrawable bitmapdraw=(BitmapDrawable)callerContext.getResources().getDrawable(resId);
        Bitmap bitmap = bitmapdraw.getBitmap();
        bitmap = Bitmap.createScaledBitmap(bitmap, 128, 128, false);

        return cropCircleFromBitmap(bitmap);
    }


    public void createMarker(LatLng location, String title){
        // Tạo một marker mới với thông tin từ Firebase và sử dụng Custom Marker Adapter
        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .title(title)
                .icon(BitmapDescriptorFactory
                .fromBitmap(getMarkerBitmapFromView(R.drawable.avatar, "100%")));
//                .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromImage(R.drawable.avatar)));

        // Thêm marker vào bản đồ
        Marker newMarker = callerContext.getMaps().addMarker(markerOptions);

        if (Objects.requireNonNull(newMarker).getTitle().equals("My location")){
            addListener(newMarker);
        }
    }


    private void addListener(Marker marker){
        // Đặt listener để lắng nghe sự thay đổi trên Firebase và cập nhật marker
        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference()
                .child("batteryLevel")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Lấy dữ liệu từ dataSnapshot và cập nhật giá trị pin
                String batteryInfo = dataSnapshot.child("currentBattery").getValue(String.class);
                // Cập nhật thông tin pin của marker
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.avatar, batteryInfo)));
//                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromImage(R.drawable.avatar)));
                Log.d("DEBUG TAG", "Updating " + marker.getTitle());
                // Xóa marker cũ và thêm marker mới với thông tin đã cập nhật
//                callerContext.getMaps().clear();
//                callerContext.getMaps().addMarker(marker.getMarkerOptions());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });

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
