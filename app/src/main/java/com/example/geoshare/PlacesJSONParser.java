package com.example.geoshare;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlacesJSONParser {

    /** Receives a JSONObject and returns a list of lists containing places information */
    public List<String[]> parse(JSONObject jsonObject){
        List<String[]> results = new ArrayList<String[]>();

        try {
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject place = resultsArray.getJSONObject(i);
                String name = place.getString("name");
                String address = place.getString("formatted_address");

                JSONObject location = place.getJSONObject("geometry")
                        .getJSONObject("location");
                String lat = location.getString("lat");
                String lng = location.getString("lng");

                Log.d("DEBUG TAG", "Name: " + name + ", lat:" + lat + ", lng: " + lng);

                results.add(new String[]{name, address, lat, lng});
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return results;
    }
}