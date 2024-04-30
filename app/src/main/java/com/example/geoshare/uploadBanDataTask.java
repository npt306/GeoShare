package com.example.geoshare;

import android.os.AsyncTask;

import com.example.geoshare.Database.RealtimeDatabase.RealtimeDatabase;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;

public class uploadBanDataTask extends AsyncTask<Report, Integer, String> {
    Report chosenItem;
    @Override
    protected String doInBackground(Report... chosenItems) {
        chosenItem = chosenItems[0];
        DatabaseReference bannedUsersRef = RealtimeDatabase.getInstance().getBannedUsersReference().child(chosenItem.getReceiverId());

        Date banDate = new Date();
        Date unbanDate = new Date(banDate.getTime() + (long)(30L * 3600 * 1000 * 24)); // 30d in millis
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        bannedUsersRef.child("banReasons").setValue(chosenItem.getReportProblems());
        bannedUsersRef.child("reportDescription").setValue(chosenItem.getReportDescription());
        bannedUsersRef.child("unbanDate").setValue(dateFormat.format(unbanDate));
        bannedUsersRef.child("banDate").setValue(dateFormat.format(banDate));
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        AdminReportDetail.getInstance().removeCurrentReport();
    }
}
