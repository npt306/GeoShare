package com.example.geoshare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.geoshare.Database.Authentication.Authentication;
import com.example.geoshare.Database.RealtimeDatabase.RealtimeDatabase;
import com.example.geoshare.Database.Storage.Storage;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MarkerManager {
    private static volatile MarkerManager instance;
    private HashMap<String, Marker> markerHashMap = new HashMap<>();
    private GoogleMap googleMap;
    MainActivity callerContext;

    public interface MarkerImageCallback {
        void onMarkerImageLoaded(Bitmap bitmap);
    }

    public MarkerManager(Context callerContext) {
        this.callerContext = (MainActivity) callerContext;
        this.markerHashMap = new HashMap<>();
    }

    public void setGoogleMap(GoogleMap map){
        this.googleMap = map;
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

    // Crop middle circle from a bitmap
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


    public void createMarker(LatLng location, String markerId) {
        loadMarkerBitmapFromView(markerId, "100%", new MarkerImageCallback() {
            @Override
            public void onMarkerImageLoaded(Bitmap bitmap) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(location)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap));

                if (markerHashMap.containsKey(markerId)) {
                    Log.d("Old marker id: ", markerId);
                    Marker oldMarker = markerHashMap.get(markerId);
                    assert oldMarker != null;
                    oldMarker.setVisible(true);
                    Objects.requireNonNull(oldMarker).setPosition(location);
//                    googleMap.addMarker(markerOptions);
                } else {
                    if (googleMap == null) {
                        googleMap = MainActivity.getInstance().getMaps();
                        createMarker(location, markerId);
                        return;
                    }
                    Marker newMarker = googleMap.addMarker(markerOptions);
                    assert newMarker != null;
                    newMarker.setTag(markerId);
                    markerHashMap.put(markerId, newMarker);

                    Log.d("New marker id: ", markerId);
//                    if (Objects.equals(markerId, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
//                        addBatteryChangeListener(newMarker, markerId);
//                    }
                    addBatteryChangeListener(newMarker, markerId);

                }
            }
        });
    }

    private void addBatteryChangeListener(Marker marker, String markerId) {
        Log.d("battery listener", markerId);
        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference()
                .child("batteryLevel")
                .child(markerId);
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get data from dataSnapshot and update battery value
                String batteryInfo = dataSnapshot.child("currentBattery").getValue(String.class);
                // Update marker's battery info
                loadMarkerBitmapFromView(markerId, batteryInfo, new MarkerImageCallback() {
                    @Override
                    public void onMarkerImageLoaded(Bitmap bitmap) {
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        Log.d("DEBUG TAG", "Updating " + marker.getTitle());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
            }
        });
    }

    // Function to hide marker without removing from HashMap
    public void hideMarker(String friendId) {
        Marker marker = markerHashMap.get(friendId);
        if (marker != null) {
            marker.setVisible(false);
        }
    }

    // Tải bitmap từ layout view
    private void loadMarkerBitmapFromView(String userId, String batteryInfo, MarkerImageCallback callback) {
        View customMarkerView = ((LayoutInflater) callerContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.market_custom, null);
        CircleImageView markerImageView = customMarkerView.findViewById(R.id.avatarImageView);
        TextView markerBattery = customMarkerView.findViewById(R.id.batteryTextView);
        markerBattery.setText(batteryInfo + "%");
        RealtimeDatabase.getInstance()
                .getUsersReference()
                .child(userId)
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
                                                        callback.onMarkerImageLoaded(generateMarkerBitmapFromView(customMarkerView));
                                                    }
                                                });
                                            }
                                        });
                            } else {
                                markerImageView.setImageResource(R.drawable.avatar);

                                callback.onMarkerImageLoaded(generateMarkerBitmapFromView(customMarkerView));
                            }
                        } else {
                            // User info not found, handle accordingly
                            markerImageView.setImageResource(R.drawable.avatar);
                            Log.d("Load image to marker", "User not available");
                            callback.onMarkerImageLoaded(generateMarkerBitmapFromView(customMarkerView));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors if needed
                        Log.d("Load image to marker", databaseError.getMessage());
                        callback.onMarkerImageLoaded(generateMarkerBitmapFromView(customMarkerView));
                    }
                });
    }

    // Tạo bitmap từ layout view
    private Bitmap generateMarkerBitmapFromView(View customMarkerView) {
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    // Sự kiện khi nhấn vào marker
    public void setMarkerClickListener() {

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker m) {
                String tag = (String) m.getTag();
                assert tag != null;
                if(tag.equals("MarkLocation")){

                }
                else {
                    showUserCustomDialog(tag, m.getPosition());
                }
                return true;
            }
        });
    }

    // Hiển thị custom dialog khi nhấn vào marker
    private void showUserCustomDialog(String markerId, LatLng position) {
        Log.d("show diaglog profile", markerId + "click");

        // Viết code để hiển thị custom dialog ở đây

        AlertDialog.Builder builder = new AlertDialog.Builder(callerContext);
        View dialogView;
        CircleImageView avatar;
        TextView name;
        TextView dob;
        EditText status;
        TextView battery;
        TextView speed;
        TextView totalFriend;
        Button buttonUpdateStatus;
        Button buttonProfile;
        ImageView buttonClose;
        EditText address;

        if(markerId.equals(Authentication.getInstance().getCurrentUserId())){
            dialogView = LayoutInflater.from(callerContext).inflate(R.layout.dialog_profile, null);
            status = dialogView.findViewById(R.id.status_dialog_profile);
            buttonUpdateStatus = dialogView.findViewById(R.id.button_update_status_dialog_profile);
            buttonUpdateStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String statusInf = String.valueOf(status.getText());
                    if(statusInf.length() >= 30){
                        Toast.makeText(callerContext.getApplicationContext(), "Status too long. Please enter again (Maximum 30 characters)", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference()
                            .child("Users")
                            .child(markerId)
                            .child("status");
                    statusRef.setValue(statusInf).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(callerContext.getApplicationContext(), "Update status completed", Toast.LENGTH_SHORT).show();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Xử lý khi có lỗi xảy ra
                                    Toast.makeText(callerContext.getApplicationContext(), "Update status failed", Toast.LENGTH_SHORT).show();

                                }
                            });;
                }
            });
        }
        else {
            dialogView = LayoutInflater.from(callerContext).inflate(R.layout.dialog_friend_profile, null);
            status = dialogView.findViewById(R.id.status_dialog_profile);
            status.setEnabled(false);
            address = dialogView.findViewById(R.id.friend_address_dialog_profile);
            address.setText("Address: " + getAddressFromLatLng(position));
            buttonProfile = dialogView.findViewById(R.id.button_profile_dialog_profile);
            buttonProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(callerContext.getApplicationContext(), FriendProfile.class);
                    intent.putExtra("friendID", markerId);
                    callerContext.startActivity(intent);
                }
            });
        }
        builder.setView(dialogView);

        avatar = dialogView.findViewById(R.id.avatar_dialog_profile);
        name = dialogView.findViewById(R.id.name_dialog_profile);
        dob = dialogView.findViewById(R.id.dob_dialog_profile);
        battery = dialogView.findViewById(R.id.battery_dialog_profile);
        speed = dialogView.findViewById(R.id.speed_dialog_profile);
        totalFriend = dialogView.findViewById(R.id.total_friend_dialog_profile);
        buttonClose = dialogView.findViewById(R.id.button_close_dialog_profile);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DatabaseReference usersRef = RealtimeDatabase.getInstance().getUsersReference().child(markerId);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String imageURL = dataSnapshot.child("imageURL").getValue(String.class);
                    if(!Objects.equals(imageURL, "default")) {
                        StorageReference storageRef = Storage.getInstance().getUsersAvatarReference();
                        storageRef.child(imageURL).getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(callerContext.getApplicationContext()).load(uri).into(avatar);
                                    }
                                });
                    }
                    String username = dataSnapshot.child("username").getValue(String.class);
                    name.setText(username);
                    String userDob = dataSnapshot.child("dob").getValue(String.class);
                    dob.setText(userDob);
                } else {
                    // Không tìm thấy thông tin người dùng, xử lý tương ứng
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });

        DatabaseReference batteryRef = FirebaseDatabase.getInstance().getReference()
                .child("batteryLevel")
                .child(markerId);
        batteryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get data from dataSnapshot and update battery value
                String batteryInfo = dataSnapshot.child("currentBattery").getValue(String.class);
                // Update marker's battery info
                battery.setText(batteryInfo + "%");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
            }
        });

        DatabaseReference speedRef = FirebaseDatabase.getInstance().getReference()
                .child("Locations")
                .child(markerId);
        batteryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get data from dataSnapshot and update battery value
                String speedInf = dataSnapshot.child("speed").getValue(String.class);
                if (speedInf != null) {
                    // Có giá trị cho trường "speed"
                    speed.setText(speedInf + " km/h");
                } else {
                    // Không có giá trị cho trường "speed"
                    speed.setText("0 km/h");
                }
                // Update marker's battery info

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
            }
        });

        DatabaseReference totalFriendRef = FirebaseDatabase.getInstance().getReference()
                .child("Friends")
                .child(markerId)
                .child("friendList");
        totalFriendRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long totalFriends = dataSnapshot.getChildrenCount();
                // Sử dụng totalFriends theo nhu cầu của bạn
                Log.d("FriendCount", "Số lượng bạn bè: " + totalFriends);
                totalFriend.setText(String.valueOf(totalFriends));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
            }
        });

        DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(markerId);
        statusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String statusInf = dataSnapshot.child("status").getValue(String.class);
                if(statusInf.isEmpty()){
                    if(!markerId.equals(Authentication.getInstance().getCurrentUserId())){
                        status.setVisibility(View.GONE);
                    }
                }
                else {
                status.setText(statusInf);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private String getAddressFromLatLng(LatLng point){
        Geocoder geocoder = new Geocoder(callerContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String addressLine = address.getAddressLine(0);
                return addressLine.toString();
            } else {
                return point.latitude + ", " +point.longitude;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
