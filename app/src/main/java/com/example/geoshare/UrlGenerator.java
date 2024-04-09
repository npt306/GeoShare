package com.example.geoshare;

import com.google.android.gms.maps.model.LatLng;

public class UrlGenerator {
    /**
     * A method to prepare a query to Direction API
     * @param origin a starting location
     * @param dest a finish location
     * @return query url for api
     */
    public static String getDirectionsUrl(LatLng origin, LatLng dest) {

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

    /**
     * A method to prepare a query to Direction API
     * @param keyword a text search key
     * @return query url for api
     */
    public static String getPlaceQueryUrl(String keyword) {
        // Building the url to the web service
        keyword = keyword.replace(" ", "%20");
        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + keyword
                + "&key=AIzaSyCGc2dm5WJgaBbVaAYeqStrIqnV6oVEHzg";

        return url;
    }
}
