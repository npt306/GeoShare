package com.example.geoshare.Battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.example.geoshare.Database.Authentication.Authentication;
import com.example.geoshare.Database.RealtimeDatabase.RealtimeDatabase;
import com.google.firebase.database.DatabaseReference;


public class BatteryStatusListener {
    private Context context;
    private DatabaseReference database;

    public BatteryStatusListener(Context context) {
        this.context = context;
        this.database = RealtimeDatabase.getInstance().getCurrentBatteryLevelReference();
        context.registerReceiver(batteryStatusReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private BroadcastReceiver batteryStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPercentage = (level * 100) / scale;

            updateBatteryStatusOnFirebase(batteryPercentage);
        }
    };

    private void updateBatteryStatusOnFirebase(int batteryPercentage) {
        if(Authentication.getInstance().getCurrentUser() != null){
            database.setValue(String.valueOf(batteryPercentage));
        }
    }

    public void unregisterReceiver() {
        context.unregisterReceiver(batteryStatusReceiver);
    }
}
