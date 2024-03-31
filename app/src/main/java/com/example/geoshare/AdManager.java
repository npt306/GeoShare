package com.example.geoshare;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AdManager {
    // Phương thức static để khởi tạo và tải quảng cáo
    public static void loadBannerAd(AdView adView) {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}

