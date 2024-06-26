package com.example.geoshare;

import android.app.AlertDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to parse the Google Places in JSON format
 */
public class PlacesResultParserTask extends AsyncTask<String, Integer, List<String[]>> {

    // Parsing the data in non-ui thread
    @Override
    protected List<String[]> doInBackground(String... jsonData) {

        JSONObject jObject = null;
        List<String[]> places = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            PlacesJSONParser parser = new PlacesJSONParser();

            places = parser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return places;
    }

    @Override
    protected void onPostExecute(List<String[]> result) {
        Log.d("DEBUG TAG", "Getting alert dialog");

        // prepare list
        List<String> nameList = new ArrayList<String>();
        List<String> addressList = new ArrayList<String>();

        for (int i = 0; i < result.size(); i++){
            nameList.add(result.get(i)[0]);
            addressList.add(result.get(i)[1]);
        }

        // configure adapter
        ArrayAdapter adapter = new ArrayAdapter(Search.getInstance(),
                android.R.layout.simple_list_item_2, android.R.id.text1, nameList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(nameList.get(position));
                text2.setText(addressList.get(position));
                return view;
            }
        };

        // configure builder
        AlertDialog.Builder builder = new AlertDialog.Builder(Search.getInstance());
        builder.setTitle("Search results")
                .setNegativeButton("Cancel", null)
                .setAdapter(adapter, null);

        // build dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        Log.d("DEBUG TAG", "Showed dialog");

        // configure dialog
        dialog.getListView().setItemsCanFocus(false);
        dialog.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // Manage selected item here
                System.out.println("clicked" + position);

                // Get 2 locations
                Location location = MainActivity.getInstance().getCurrentLocation();
                LatLng curLocation = new LatLng(location.getLatitude(), location.getLongitude());
                LatLng desLocation = new LatLng(Double.parseDouble(result.get(position)[2]),
                        Double.parseDouble(result.get(position)[3]));

                // Getting URL to the Google Directions API
                Log.d("DEBUG TAG", "Preparing to draw path");
                String url = UrlGenerator.getDirectionsUrl(curLocation, desLocation);
                // Start downloading json data from Google Directions API
                // and draw routes
                new UrlDownloader().execute(url);
            }
        });

        Log.d("DEBUG TAG", "Showed alert dialog");
    }
}

