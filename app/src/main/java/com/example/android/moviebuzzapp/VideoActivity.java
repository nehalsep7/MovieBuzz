package com.example.android.moviebuzzapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        WebView webView = (WebView)findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        Intent i = getIntent();
        String Key = i.getStringExtra("Key");
        Log.i("Key:",Key);
        webView.loadUrl("https://www.youtube.com/watch?v="+Key);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
//    @Override
//    public void onBackPressed() {
//        Log.i("Back","Back");
////        Intent mIntent = new Intent();
////        mIntent.putExtra("Keys",Key);
////        setResult(RESULT_OK, mIntent);
////        super.onBackPressed();
//    }

}
