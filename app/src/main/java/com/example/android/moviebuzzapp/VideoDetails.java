package com.example.android.moviebuzzapp;

/**
 * Created by kumnehal on 05/01/16.
 */
public class VideoDetails {
    String key;
    String trailerName;
    String trailerSite;
    public VideoDetails(){super();}
    public VideoDetails(String key,String trailerName,String trailerSite){
        super();
        this.key = key;
        this.trailerName=trailerName;
        this.trailerSite=trailerSite;
    }
}
