package com.example.masha.hratamz2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class Start extends AppCompatActivity {

    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //Create the interstitial
        interstitialAd = new InterstitialAd(this);

        //Set your unit id. This is test id.
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        //Create request
        AdRequest adRequest = new AdRequest.Builder().build();

        //Start loading.
        interstitialAd.loadAd(adRequest);

        //Once request isloaded, display ad.
        interstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                displayInterstitial();
            }
        });
    }

    public void displayInterstitial() {

        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
    }

    public void startGame(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    //Disable Return button
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }
}
