package com.example.geoshare;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityReceiver extends BroadcastReceiver {

    public interface ConnectivityListener {
        void onNetworkConnectionChanged(boolean isConnected);
        void onWifiReconnected();
    }

    private ConnectivityListener listener;

    public ConnectivityReceiver(ConnectivityListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            listener.onNetworkConnectionChanged(isConnected);

            // Check if WiFi is reconnected
            if (isConnected && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                listener.onWifiReconnected();
            }
        }
    }
}

