package com.example.geoshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.geoshare.Adapter.ReportAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdminReportActivity extends AppCompatActivity {
    private List<ReportListItem> itemList;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_report);

        LinearLayout backButton = (LinearLayout) findViewById(R.id.back_layout);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                startActivity(intent);
                finish();
            }
        });

        listView = findViewById(R.id.custom_listview);

        itemList = new ArrayList<>();
        itemList.add(new ReportListItem("Thanh Phuong", R.drawable.user_ranking));
        itemList.add(new ReportListItem("Thanh", R.drawable.user_ranking));

        ReportAdapter adapter = new ReportAdapter(this, itemList);
        listView.setAdapter(adapter);
    }
}
