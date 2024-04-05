package com.example.geoshare;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    public void createMarker(LatLng location){
        // Tạo một marker mới với thông tin từ Firebase và sử dụng Custom Marker Adapter
        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
//                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.mipmap.ic_launcher, "Battery: 100%")));
                .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromImage(R.drawable.avatar)));

        // Thêm marker vào bản đồ
        callerContext.getMaps().addMarker(markerOptions);

        // Đặt listener để lắng nghe sự thay đổi trên Firebase và cập nhật marker
        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("batteryLevel").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Lấy dữ liệu từ dataSnapshot và cập nhật giá trị pin
                String batteryInfo = dataSnapshot.child("currentBattery").getValue(String.class);
                // Cập nhật thông tin pin của marker
//                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.mipmap.ic_launcher, "Battery: " + batteryInfo)));
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromImage(R.drawable.avatar)));

                // Xóa marker cũ và thêm marker mới với thông tin đã cập nhật
                callerContext.getMaps().clear();
                callerContext.getMaps().addMarker(markerOptions);
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
}
