package com.example.geoshare;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Search extends AppCompatActivity {
    ImageButton buttonBack;
    private SearchView searchView;
    private TextView searchInput;
    private ListView searchHistoryListView;

    private List<String> searchHistoryList;
    private ArrayAdapter<String> searchHistoryAdapter;

    // Implement to get context from other intent
    private static Search instance;

    public Search() {
        instance = this;
    }

    public static Search getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        buttonBack = findViewById(R.id.btnSearchBack);

        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(), "AIzaSyBzxW5txxZHhPZCjPrOvrjCE8awoF3IP50");
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        assert autocompleteFragment != null;
        autocompleteFragment.setActivityMode(AutocompleteActivityMode.OVERLAY);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.i("DEBUG TAG", "Place: " + place.getName() + ", " + place.getLatLng());
                addSearchToHistory(place.getName());
                startFindingDirection(place.getLatLng());
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i("DEBUG TAG", "An error occurred: " + status);
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        searchHistoryListView = findViewById(R.id.searchHistoryListView);

        searchHistoryList = MainActivity.getInstance().getSearchHistoryList();
        searchHistoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchHistoryList);
        searchHistoryListView.setAdapter(searchHistoryAdapter);

        searchHistoryAdapter = new ArrayAdapter<String>(this, R.layout.search_history_item, R.id.historyContent, searchHistoryList) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                // Lấy TextView và ImageButton từ layout tùy chỉnh
                TextView textView = view.findViewById(R.id.historyContent);
                ImageButton deleteButton = view.findViewById(R.id.historyContentDeleteButton);

                // Thiết lập sự kiện của TextView
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String selectedQuery = searchHistoryList.get(position);
                        autocompleteFragment.setText(selectedQuery);
//                        EditText autocompleteEditText = (EditText) autocompleteFragment.getView().findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_bar);
//                        autocompleteEditText.requestLayout();

                    }
                });

                // Xử lý sự kiện khi người dùng nhấn vào nút X
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Xóa mục khỏi lịch sử
                        searchHistoryList.remove(position);
                        notifyDataSetChanged();
                    }
                });

                return view;
            }
        };

        searchHistoryListView.setAdapter(searchHistoryAdapter);
    }
    // Hàm để thêm nội dung tìm kiếm vào lịch sử
    private void addSearchToHistory(String query) {
        if (!searchHistoryList.contains(query)) {
            searchHistoryList.add(query);
            searchHistoryAdapter.notifyDataSetChanged();
        }
    }

    private void startFindingDirection(LatLng destination){
        if (destination == null) return;

        // Get 2 locations
        Location location = MainActivity.getInstance().getCurrentLocation();
        LatLng curLocation = new LatLng(location.getLatitude(), location.getLongitude());

        // Getting URL to the Google Directions API
        Log.d("DEBUG TAG", "Preparing to draw path");
        String url = UrlGenerator.getDirectionsUrl(curLocation, destination);
        // Start downloading json data from Google Directions API
        // and draw routes
        new UrlDownloader().execute(url);
    }
}
