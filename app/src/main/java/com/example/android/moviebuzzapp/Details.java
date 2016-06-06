package com.example.android.moviebuzzapp;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class Details extends Fragment {

    ImageView imageView;
    TextView textView;
    TextView titleView;
    View rootView;
    ImageButton movieButton;
    RelativeLayout videoLayouts;
    String key_video;
    Button favorites;

    public Details() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_details, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        //Log.i("Image View",imageView.toString());
        textView = (TextView)rootView.findViewById(R.id.overview);
        titleView = (TextView)rootView.findViewById(R.id.titleView);
        videoLayouts = (RelativeLayout)rootView.findViewById(R.id.videoLayout);
        favorites = (Button)rootView.findViewById(R.id.favoriteButton);
        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favorites.getText().toString().equalsIgnoreCase("Mark As Favourite")){
                    favorites.setText("Remove From Favorites");
                }else{
                    favorites.setText("Mark As Favourite");
                }
            }
        });
        videoLayouts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Clicked","Play Trailer");
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void changeData(String posterUrl,String title,String overview,String rating,String key){
        Picasso.with(getActivity()).load(posterUrl).into(imageView);
        textView.setText(overview);
        titleView.setText(title);
        key_video = key;

    }

}
