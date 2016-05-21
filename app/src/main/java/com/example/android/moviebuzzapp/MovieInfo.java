package com.example.android.moviebuzzapp;

/**
 * Created by kumnehal on 05/01/16.
 */
public class MovieInfo{
    public String movieId;
    public String posterURL;
    public String popularity;
    public String title;
    public String overview;
    public String language;
    public String votes;
    public  MovieInfo(){ super();}
    public MovieInfo(String movieId,String poster,String popularity,String title,String overview,String language,String votes){
        super();
        this.movieId = movieId;
        this.posterURL=poster;
        this.popularity=popularity;
        this.title=title;
        this.overview=overview;
        this.language=language;
        this.votes=votes;
    }
    public String toString(){
        return posterURL+" " + popularity + " " + title ;
    }
}
