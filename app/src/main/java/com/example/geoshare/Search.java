package com.example.geoshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.List;


public class Search extends AppCompatActivity {
    ImageButton buttonBack;
    private SearchView searchView;
    private TextView searchInput;
    private ListView searchHistoryListView;

    private List<String> searchHistoryList;
    private ArrayAdapter<String> searchHistoryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        buttonBack = findViewById(R.id.btnSearchBack);

//        if(!Places.isInitialized()){
//            Places.initialize(getApplicationContext(), "AIzaSyDYaO1-sACd1-2k9V9UPp7f6uuFFyJttnU");
//        }
//
//
//        PlacesClient placesClient = Places.createClient(this);
//
//
//        // Initialize the AutocompleteSupportFragment.
//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
//        // Specify the types of place data to return.
//        assert autocompleteFragment != null;
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
//
//        // Set up a PlaceSelectionListener to handle the response.
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(@NonNull Place place) {
//                // TODO: Get info about the selected place.
//                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
//            }
//
//
//            @Override
//            public void onError(@NonNull Status status) {
//                // TODO: Handle the error.
//                Log.i(TAG, "An error occurred: " + status);
//            }
//        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        searchView = findViewById(R.id.search);
//        searchInput = findViewById(R.id.searchInput);
        searchHistoryListView = findViewById(R.id.searchHistoryListView);

        searchHistoryList = new ArrayList<>();
        searchHistoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchHistoryList);
        searchHistoryListView.setAdapter(searchHistoryAdapter);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                addSearchToHistory(query);
//                searchInput.setText(query);
                String url = UrlGenerator.getPlaceQueryUrl(query);
                Log.d("DEBUG TAG", "Handling place query");
//                UrlDownloader.getInstance().execute(url);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // You can perform any actions while user is typing here if needed
                return false;
            }
        });

        searchHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy nội dung của mục được chọn từ danh sách lịch sử
                String selectedQuery = searchHistoryList.get(position);
                // Hiển thị nội dung từ lịch sử vào TextView
//                searchInput.setText(selectedQuery);
            }
        });
        searchHistoryAdapter = new ArrayAdapter<String>(this, R.layout.search_history_item, R.id.historyContent, searchHistoryList) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                // Lấy TextView và ImageButton từ layout tùy chỉnh
                TextView textView = view.findViewById(R.id.historyContent);
                ImageButton deleteButton = view.findViewById(R.id.historyContentDeleteButton);

                // Thiết lập nội dung của TextView
//                textView.setText(searchHistoryList.get(position));

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
}
