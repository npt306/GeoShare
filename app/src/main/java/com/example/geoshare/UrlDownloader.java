package com.example.geoshare;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlDownloader extends AsyncTask<String, Void, String>{
    String api = "";

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

        JSONObject jObject = null;
        String status = "";
        try {
            jObject = new JSONObject(result);
            status = jObject.getString("status");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (status.equals("OK")){
            if (api.equals("directions")){
                Log.d("DEBUG TAG", "API Directions");

                // wait for screen to change to MainActivity
                if (Search.getInstance() != null) Search.getInstance().finish();
                SystemClock.sleep(2000);

                new DirectionsResultParserTask().execute(result);
            }

            if (api.equals("places")){
                Log.d("DEBUG TAG", "API Place");

                new PlacesResultParserTask().execute(result);
            }

            return;
        }

        // configure dialog for error msg
        AlertDialog.Builder builder = new AlertDialog.Builder(Search.getInstance());
        builder.setTitle("Results: " + api.toUpperCase() + " API")
                .setPositiveButton("OK", null);

        if (status.equals("ZERO_RESULTS")){
            builder.setMessage("No available directions found.");
        }

        if (status.equals("OVER_QUERY_LIMIT")){
            builder.setMessage("Over query limit. Please try again tomorrow.");
        }

        // build dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws Exception {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        // define api
        if (strUrl.contains("api/place/")){
            api = "places";
        }
        if (strUrl.contains("api/directions/")){
            api = "directions";
        }

        try {
            Log.d("DEBUG TAG", "Start Downloading");
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

        Log.d("DEBUG TAG", "Finish Downloading");

        return data; // to be used by onPostExecute()
    }
}
