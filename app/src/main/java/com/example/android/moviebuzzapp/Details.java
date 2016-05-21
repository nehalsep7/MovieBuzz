package com.example.android.moviebuzzapp;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    public Details() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_details, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        Log.i("Image View",imageView.toString());
        textView = (TextView)rootView.findViewById(R.id.overview);
        titleView = (TextView)rootView.findViewById(R.id.titleView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void changeData(String posterUrl,String title,String overview,String rating){
        Picasso.with(getActivity()).load(posterUrl).into(imageView);
        textView.setText(overview);
        titleView.setText(title);

    }
}
