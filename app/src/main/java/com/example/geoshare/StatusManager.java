package com.example.geoshare;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.geoshare.Database.RealtimeDatabase.RealtimeDatabase;
import com.google.firebase.database.DatabaseReference;


public class StatusManager {
//        private static final long STATUS_RESET_DELAY = 24 * 60 * 60 * 1000; // 24 hours in milliseconds
    private static final long STATUS_RESET_DELAY = 5 * 1000; // 24 hours in milliseconds

    private DatabaseReference userStatusRef;
        public StatusManager() {
            userStatusRef = RealtimeDatabase.getInstance().getCurrentUserStatusReference();
        }
        public void updateUserStatus(String status) {
            userStatusRef.child("status").setValue(status).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("UserStatus", "User status updated successfully.");
                    scheduleStatusReset();
                } else {
                    Log.e("UserStatus", "Failed to update user status.", task.getException());
                }
            });
        }
        private void scheduleStatusReset() {
            // Lên lịch xóa trạng thái sau 24 giờ
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                userStatusRef.child("status").removeValue();
                Log.d("UserStatus", "User status removed after 24 hours.");
            }, STATUS_RESET_DELAY);
        }
}
