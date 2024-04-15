package com.example.geoshare;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlDownloader extends AsyncTask<String, Void, String>{
    Context callerContext;

    private static UrlDownloader instance;

    private UrlDownloader() {

    }

    public static UrlDownloader getInstance(Context context){
        if (instance == null) {
            synchronized (LocationManager.class) {
                if (instance == null) {
                    instance = new UrlDownloader();
                }
            }
        }
        instance.callerContext = context;
        return instance;
    }

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

        if (api.equals("directions")){
            Log.d("DEBUG TAG", "API Directions");
            DirectionsResultParserTask parserTask = new DirectionsResultParserTask(callerContext);
            parserTask.execute(result);
        }

        if (api.equals("place")){
            Log.d("DEBUG TAG", "API Place");

            PlacesResultParserTask parserTask = new PlacesResultParserTask();
            parserTask.execute(result);
        }
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
        return data; // to be used by onPostExecute()
    }
}
