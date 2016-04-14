package com.example.android.moviebuzzapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    MovieInfo movieInfo;
    ArrayList<String> MovieTitles = new ArrayList<String>();
    ArrayList<Bitmap> MoviePoster = new ArrayList<Bitmap>();
    ArrayList<String> MoviePopularity = new ArrayList<String>();
    ArrayList<String> MovieOverview = new ArrayList<String>();
    ArrayList<String> MovieLanguage = new ArrayList<String>();
    ArrayList<MovieInfo> MovieDetailList = new ArrayList<MovieInfo>();
    GridView gridView;
    MovieAdapter adapter;
    public class MovieInfo{
        public Bitmap poster;
        public String popularity;
        public String title;
        public String overview;
        public String language;
        public  MovieInfo(){ super();}
        public MovieInfo(Bitmap poster,String popularity,String title,String overview,String language){
            super();
            this.poster=poster;
            this.popularity=popularity;
            this.title=title;
            this.overview=overview;
            this.language=language;
        }
        public String toString(){
            return poster+" " + popularity + " " + title ;
        }
    }
    public class MovieAdapter extends ArrayAdapter<MovieInfo>{

        public MovieAdapter(Context context, ArrayList<MovieInfo> movie) {
            super(context, R.layout.list_item,movie);
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           // return super.getView(position, convertView, parent);
            movieInfo = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
            }
//            TextView movieTitle = (TextView)convertView.findViewById(R.id.movieName);
//            TextView movieLang = (TextView)convertView.findViewById(R.id.movieLanguage);
            ImageView moviePoster = (ImageView)convertView.findViewById(R.id.moviePoster);
//            TextView popularity = (TextView)convertView.findViewById(R.id.popularityView);
//            movieTitle.setText(movieInfo.title);
//            movieLang.setText(movieInfo.language);
//            popularity.setText(movieInfo.popularity);
            moviePoster.setImageBitmap(movieInfo.poster);
            return convertView;
        }
    }

    public class MovieDetails extends AsyncTask<String,Void,Bitmap >{

        @Override
        protected Bitmap doInBackground(String... params) {
            URL url;
            String result = "";
            HttpURLConnection httpURLConnection = null;
            try {
                url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data= reader.read();
                while (data != -1){
                    char current = (char)data;
                    result+=current;
                    data=reader.read();
                }
                //Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                // return myBitmap;
                String posterUrl="";
                String overview;
                String popularity;
                String title;
                String language;
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                for(int i = 0; i<jsonArray.length();i++){
                    JSONObject jsonPart = jsonArray.getJSONObject(i);
                    posterUrl=jsonPart.getString("poster_path");
                    MovieDetails poster = new MovieDetails();
                    url = new URL("http://image.tmdb.org/t/p/w185/" + posterUrl);
                    httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.connect();
                    inputStream = httpURLConnection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                    overview=jsonPart.getString("overview");
                    popularity=jsonPart.getString("popularity");
                    title = jsonPart.getString("title");
                    language=jsonPart.getString("original_language");
                    Log.i("Poster Url",posterUrl);
                    Log.i("Overview",overview);
                    Log.i("Popularity",popularity);
                    Log.i("Title", title);
                    MovieTitles.add(title);
                    MoviePopularity.add(popularity);
                    MovieOverview.add(overview);
                    MovieLanguage.add(language);
                    MovieInfo details = new MovieInfo(myBitmap,popularity,title,overview,language);
                    MovieDetailList.add(details);
                }
                adapter = new MovieAdapter(getApplicationContext(),MovieDetailList);
                //gridView.setAdapter(new MovieAdapter(getApplicationContext(),MovieDetailList));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            gridView.setAdapter(adapter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MovieDetails movieDetails = new MovieDetails();
        gridView = (GridView)findViewById(R.id.gridView);
        try {
            movieDetails.execute("http://api.themoviedb.org/3/movie/popular?api_key=42c195dfd3c7af3d02056ccdb542f2d5").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //Spinner spinner = (Spinner)findViewById(R.id.spinner);
       // ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,R.array.sortBy_list,android.R.layout.simple_spinner_item);
        //arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinner.setAdapter(arrayAdapter);
        GridView gridView = (GridView)findViewById(R.id.gridView);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
