package com.example.geoshare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    ImageButton imageButtonProfile, imageButtonInvite, buttonLocation, buttonChat;
    private GoogleMap maps;
    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser == null){
            Intent intent = new Intent(getApplicationContext(), SignIn.class);
            startActivity(intent);
            finish();
        }




        imageButtonInvite = findViewById(R.id.btnInvite);
        imageButtonProfile =findViewById(R.id.btnProfile);
        buttonLocation = findViewById(R.id.btnCurrentLocation);
        buttonChat = findViewById(R.id.btnChat);
        imageButtonInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Invite.class);
                startActivity(intent);
                finish();
            }
        });

        imageButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
                finish();
            }
        });

        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focusToMyLocation();
            }
        });

        buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Chat.class);
                startActivity(intent);
                finish();
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
    }



    private int getCurrentBatteryLevel() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;

        return (int)(batteryPct*100);
    }
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    currentLocation = location;

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
                    mapFragment.getMapAsync(MainActivity.this);
                }
            }
        });


    }
    public void focusToMyLocation() {
        CameraPosition camPos = new CameraPosition.Builder()
                .target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                .zoom(12)
                .bearing(currentLocation.getBearing())
                .tilt(70)
                .build();
        maps.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
        maps.animateCamera(camUpd3);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        maps = googleMap;

        YourBatteryChangeListener batteryChangeListener = new YourBatteryChangeListener();
        batteryChangeListener.startBatteryChangeListener();

        LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        // Tạo một marker mới với thông tin từ Firebase và sử dụng Custom Marker Adapter
        MarkerOptions markerOptions = new MarkerOptions()
                .position(myLocation)
                .title("My location")
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.mipmap.ic_launcher, "Battery: 100%")));

        // Thêm marker vào bản đồ
        maps.addMarker(markerOptions);
        maps.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

        // Đặt listener để lắng nghe sự thay đổi trên Firebase và cập nhật marker

        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Lấy dữ liệu từ dataSnapshot và cập nhật giá trị pin
                String batteryInfo = dataSnapshot.child("battery").getValue(String.class);
                // Cập nhật thông tin pin của marker
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.mipmap.ic_launcher, "Battery: " + batteryInfo)));
                // Xóa marker cũ và thêm marker mới với thông tin đã cập nhật
                maps.clear();
                maps.addMarker(markerOptions);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }
    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId, String batteryInfo) {
        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.market_custom, null);
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

    private class YourBatteryChangeListener {
        private BroadcastReceiver batteryReceiver;

        public void startBatteryChangeListener() {
            batteryReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    float batteryPct = level / (float) scale;
                    String batteryInfo = String.valueOf((int) (batteryPct * 100)) + "%";

                    // Cập nhật dữ liệu pin lên Firebase
                    updateBatteryInfo(batteryInfo);
                }
            };

            // Đăng ký BroadcastReceiver để lắng nghe sự thay đổi pin
            registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }

        public void stopBatteryChangeListener() {
            // Ngừng lắng nghe sự thay đổi pin
            if (batteryReceiver != null) {
                unregisterReceiver(batteryReceiver);
            }
        }
    }

    // Cập nhật dữ liệu pin lên Firebase
    private void updateBatteryInfo(String batteryInfo) {
        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        firebaseRef.child("battery").setValue(batteryInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase", "Dữ liệu pin đã được cập nhật thành công.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firebase", "Lỗi khi cập nhật dữ liệu pin: " + e.getMessage());
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }else {
                Toast.makeText(this,"Location permission is denied, please allow permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void transfer(String sou, String des){
        des = sou;
    }
    // Hàm để tạo Bitmap từ layout XML
    private Bitmap createMarkerBitmap() {
        View customMarkerView = getLayoutInflater().inflate(R.layout.market_custom, null);
        ImageView avatarImageView = customMarkerView.findViewById(R.id.avatarImageView);
        TextView batteryTextView = customMarkerView.findViewById(R.id.batteryTextView);
//        String batteryPercentage = String.valueOf(getCurrentBatteryLevel());
        String batteryPercentage = "";
        Integer batteryInteger;

        DatabaseReference batteryRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        batteryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer battery = dataSnapshot.child("battery").getValue(Integer.class);
                    Toast.makeText(MainActivity.this, String.valueOf(battery),Toast.LENGTH_SHORT).show();
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



    /**
     * A method to prepare a query to Direction API
     * @param origin a starting location
     * @param dest a finish location
     * @return query url for api
     */
    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters
                + "&key=AIzaSyCGc2dm5WJgaBbVaAYeqStrIqnV6oVEHzg";

        return url;
    }


    private void getDirection(LatLng origin, LatLng dest){
        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        // and draw routes
        downloadTask.execute(url);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);
        }

        /**
         * A method to download json data from url
         */
        private String downloadUrl(String strUrl) throws Exception {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.connect();

                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }
    }


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        /**
         * A class to parse the Google Places in JSON format
         */

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // setting for routes
                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

            // Drawing polyline in the Google Map for the i-th route
            maps.addPolyline(lineOptions);
        }
    }

}