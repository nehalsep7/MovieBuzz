package com.example.android.moviebuzzapp;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

public class DetailView extends AppCompatActivity {

    String key_data;
    ImageButton movieButton;
    private static String Title,Overview,PosterUrl,Rating;
    private static ArrayList<String> keys_videos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Inside DetailView", "Detail View");
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            Log.i("Saved","Instance");
        }
        setContentView(R.layout.activity_detail_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent i = getIntent();
        if(i.getStringExtra("Title") != null){
            Title = i.getStringExtra("Title");
        }
        if(i.getStringExtra("OverView") != null){
            Overview = i.getStringExtra("OverView");
        }
        if(i.getStringExtra("Url") != null){
            PosterUrl = i.getStringExtra("Url");
        }
        if(i.getStringExtra("Rating") != null){
            Rating = i.getStringExtra("Rating");
        }
        if(i.getStringArrayListExtra("Keys") != null){
            keys_videos = i.getStringArrayListExtra("Keys");
        }
        if(keys_videos.size() != 0){
            key_data = keys_videos.get(0);
        }
        else{
            key_data = " ";
        }

        movieButton = (ImageButton)findViewById(R.id.videoButton);
        movieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Clicked","Button Clicked");
                Intent intent = new Intent(getApplicationContext(),VideoActivity.class);
                intent.putExtra("Key",key_data);
                startActivity(intent);
            }
        });
        FragmentManager manager = getFragmentManager();
        Details fragment = (Details) manager.findFragmentById(R.id.fragment);
        fragment.changeData(PosterUrl, Title, Overview, Rating,key_data);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i("Method:","Save Instance");
        outState.putString("Title",Title);
        super.onSaveInstanceState(outState);
    }
}
