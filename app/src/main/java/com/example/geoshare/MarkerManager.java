package com.example.geoshare;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.geoshare.Database.Authentication.Authentication;
import com.example.geoshare.Database.RealtimeDatabase.RealtimeDatabase;
import com.example.geoshare.Database.Storage.Storage;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MarkerManager {
    private static volatile MarkerManager instance;
    private HashMap<String, Marker> markerHashMap = new HashMap<>();

    MainActivity callerContext;

    public interface MarkerImageCallback {
        void onMarkerImageLoaded(Bitmap bitmap);
    }

    public MarkerManager(Context callerContext) {
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
    private Bitmap cropCircleFromBitmap(Bitmap bitmap) {
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

    private Bitmap getBitmapFromImage(@DrawableRes int resId) {
        BitmapDrawable bitmapdraw = (BitmapDrawable) callerContext.getResources().getDrawable(resId);
        Bitmap bitmap = bitmapdraw.getBitmap();
        bitmap = Bitmap.createScaledBitmap(bitmap, 128, 128, false);

        return cropCircleFromBitmap(bitmap);
    }

    public void createMarker(LatLng location, String MarkerID) {

        getMarkerBitmapFromView(MarkerID, "100%", new MarkerImageCallback() {
            @Override
            public void onMarkerImageLoaded(Bitmap bitmap) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(location)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap));

                if (markerHashMap.containsKey(MarkerID)) {
                    Log.d("Old marker id: ", MarkerID);
                    Marker oldMarker = markerHashMap.get(MarkerID);
                    assert oldMarker != null;
                    oldMarker.setVisible(true);
                    Objects.requireNonNull(oldMarker).setPosition(location);
                } else {
                    Marker newMarker = callerContext.getMaps().addMarker(markerOptions);
                    markerHashMap.put(MarkerID, newMarker);

                    Log.d("New marker id: ", MarkerID);
                    if (Objects.equals(MarkerID, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                        addListener(newMarker, MarkerID);
                    }
                }
            }
        });
    }

    private void addListener(Marker marker, String MarkerId) {
        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference()
                .child("batteryLevel")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Lấy dữ liệu từ dataSnapshot và cập nhật giá trị pin
                String batteryInfo = dataSnapshot.child("currentBattery").getValue(String.class);
                // Cập nhật thông tin pin của marker
                getMarkerBitmapFromView(MarkerId, batteryInfo, new MarkerImageCallback() {
                    @Override
                    public void onMarkerImageLoaded(Bitmap bitmap) {
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        Log.d("DEBUG TAG", "Updating " + marker.getTitle());
                    }
                });
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

    private void getMarkerBitmapFromView(String userID, String batteryInfo, MarkerImageCallback callback) {
        View customMarkerView = ((LayoutInflater) callerContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.market_custom, null);
        CircleImageView markerImageView = customMarkerView.findViewById(R.id.avatarImageView);
        TextView markerBatteryTextView = customMarkerView.findViewById(R.id.batteryTextView);

        RealtimeDatabase.getInstance()
                .getUsersReference()
                .child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String imageURL = dataSnapshot.child("imageURL").getValue(String.class);
                            if (!Objects.equals(imageURL, "default")) {
                                StorageReference storageRef = Storage.getInstance().getUsersAvatarReference();
                                storageRef.child(imageURL).getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Glide.with(callerContext.getApplicationContext()).asBitmap().load(uri).into(new SimpleTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                        markerImageView.setImageBitmap(resource);
                                                        callback.onMarkerImageLoaded(getMarkerBitmapFromView(customMarkerView, batteryInfo));
                                                    }
                                                });
                                            }
                                        });
                            } else {
                                markerImageView.setImageResource(R.drawable.avatar);
                                callback.onMarkerImageLoaded(getMarkerBitmapFromView(customMarkerView, batteryInfo));
                            }
                        } else {
                            // Không tìm thấy thông tin người dùng, xử lý tương ứng
                            markerImageView.setImageResource(R.drawable.avatar);
                            Log.d("Load image to marker", "User not available");
                            callback.onMarkerImageLoaded(getMarkerBitmapFromView(customMarkerView, batteryInfo));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý lỗi nếu cần
                        Log.d("Load image to marker", databaseError.getMessage());
                        callback.onMarkerImageLoaded(getMarkerBitmapFromView(customMarkerView, batteryInfo));
                    }
                });
    }

    private Bitmap getMarkerBitmapFromView(View customMarkerView, String batteryInfo) {
        CircleImageView markerImageView = customMarkerView.findViewById(R.id.avatarImageView);
        TextView markerBatteryTextView = customMarkerView.findViewById(R.id.batteryTextView);
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
