package com.example.geoshare.Battery;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class BatteryService extends Service {
    private BatteryStatusListener batteryStatusListener;

    @Override
    public void onCreate() {
        super.onCreate();
        batteryStatusListener = new BatteryStatusListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Lắng nghe sự thay đổi pin và cập nhật lên Firebase

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        batteryStatusListener.unregisterReceiver();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}