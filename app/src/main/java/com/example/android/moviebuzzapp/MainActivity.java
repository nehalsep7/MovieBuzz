package com.example.android.moviebuzzapp;

import android.app.FragmentManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
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

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;



public class MainActivity extends AppCompatActivity {
    MovieInfo movieInfo;
    ArrayList<String> MovieTitles = new ArrayList<String>();
    ArrayList<Bitmap> MoviePoster = new ArrayList<Bitmap>();
    ArrayList<String> MoviePopularity = new ArrayList<String>();
    ArrayList<String> MovieOverview = new ArrayList<String>();
    ArrayList<String> MovieLanguage = new ArrayList<String>();
    ArrayList<MovieInfo> MovieDetailList = new ArrayList<MovieInfo>();
    ArrayList<VideoDetails> videoDetailsList = new ArrayList<VideoDetails>();
    ArrayList<VideoDetails> fetchedList = new ArrayList<VideoDetails>();
    GridView gridView;
    MovieAdapter adapter;
    SQLiteDatabase movieDB;
    MovieInfo details;
    FragmentManager manager;
    VideoDetails videoDetails;
    Map<Integer,ArrayList<VideoDetails>> videoMap = new HashMap<Integer,ArrayList<VideoDetails>>();
    public class MovieAdapter extends ArrayAdapter<MovieInfo>{

        public MovieAdapter(Context context, ArrayList<MovieInfo> movie) {
            super(context, R.layout.list_item,movie);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            movieInfo = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
            }
            ImageView moviePoster = (ImageView)convertView.findViewById(R.id.moviePoster);
            Picasso.with(getApplicationContext()).load(movieInfo.posterURL).into(moviePoster);
            return convertView;
        }
    }

    public class MovieDetails extends AsyncTask<String,Void,String >{

        @Override
        protected String doInBackground(String... params) {
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
                String posterUrl="";
                String overview;
                String popularity;
                String title;
                String language;
                String voteAvg;
                String movie_id;
                String key;
                String trailerName;
                String trailerSite;
                movieDB.execSQL("DELETE FROM Movie_details");

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                for(int i = 0; i<jsonArray.length();i++){
                    JSONObject jsonPart = jsonArray.getJSONObject(i);
                    posterUrl="http://image.tmdb.org/t/p/w185/"+jsonPart.getString("poster_path");
                    movie_id = jsonPart.getString("id");
                    Log.i("Movie Id",movie_id);

                    overview=jsonPart.getString("overview");
                    popularity=jsonPart.getString("popularity");
                    title = jsonPart.getString("title");
                    language=jsonPart.getString("original_language");
                    voteAvg=jsonPart.getString("vote_average");
                    MovieTitles.add(title);
                    MoviePopularity.add(popularity);
                    MovieOverview.add(overview);
                    MovieLanguage.add(language);
                    String sql = "INSERT INTO Movie_details(movieId,posterURL,title,rating,votes,overview) VALUES ( ? , ? , ? , ? , ? , ? )";
                    SQLiteStatement statement = movieDB.compileStatement(sql);
                    statement.bindString(1,movie_id);
                    statement.bindString(2, posterUrl);
                    statement.bindString(3,title);
                    statement.bindString(4,popularity);
                    statement.bindString(5,voteAvg);
                    statement.bindString(6, overview);
                    statement.execute();
                    url = new URL("http://api.themoviedb.org/3/movie/" +movie_id+ "/videos?api_key=42c195dfd3c7af3d02056ccdb542f2d5");
                    httpURLConnection = (HttpURLConnection)url.openConnection();
                    inputStream = httpURLConnection.getInputStream();
                    reader = new InputStreamReader(inputStream);
                    data = reader.read();
                    String content="";
                    while (data != -1) {
                        char current = (char) data;
                        content += current;
                        data = reader.read();
                    }
                    videoDetailsList.clear();
                    JSONObject jsonObjectVideo = new JSONObject(content);
                    JSONArray jsonArrayVideo = jsonObjectVideo.getJSONArray("results");
                    for(int j=0;j<jsonArrayVideo.length();j++){
                        JSONObject videoPart = jsonArrayVideo.getJSONObject(j);
                        key = videoPart.getString("key");
                        trailerName = videoPart.getString("name");
                        trailerSite = videoPart.getString("site");
                        videoDetails = new VideoDetails(key,trailerName,trailerSite);
                    }

                    videoDetailsList.add(videoDetails);
                    videoMap.put(Integer.parseInt(movie_id),videoDetailsList);

                }

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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            updateList();
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.i("clicked","clciked");
                manager=getFragmentManager();
                    fetchedList.clear();
                    Details fragment = (Details) manager.findFragmentById(R.id.fragment);
                    MovieInfo clicked = MovieDetailList.get(position);
                    String idToMatch = clicked.movieId;
                    Log.i("id",idToMatch);
                    fetchedList = videoMap.get(Integer.parseInt(idToMatch));
                    VideoDetails fetchedObject = fetchedList.get(0);
                    Log.i("key",fetchedObject.key);
                    if(fragment != null && fragment.isVisible()){
                        fragment.changeData(clicked.posterURL,clicked.title,clicked.overview,clicked.votes);
                    }
                    else{
                        Intent intent = new Intent(getApplicationContext(),DetailView.class);
                        intent.putExtra("Title",clicked.title);
                        intent.putExtra("OverView",clicked.overview);
                        intent.putExtra("Url",clicked.posterURL);
                        intent.putExtra("Rating",clicked.votes);

                        startActivity(intent);
                    }
                }
            });
            adapter.notifyDataSetChanged();

           // gridView.setAdapter(adapter);
        }
    }
    public void updateList(){
        Log.i("Updating List","updated");
        //details = null;
        Cursor c = movieDB.rawQuery("SELECT * FROM Movie_details", null);
        int idIndex = c.getColumnIndex("movieId");
        int posterIndex = c.getColumnIndex("posterURL");
        int popularityIndex = c.getColumnIndex("rating");
        int titleIndex = c.getColumnIndex("title");
        int overViewIndex = c.getColumnIndex("overview");
        int votesIndex = c.getColumnIndex("votes");
        int language = c.getColumnIndex("language");
        c.moveToFirst();
        while( c != null && !c.isAfterLast()){
            details = new MovieInfo(c.getString(idIndex),c.getString(posterIndex),c.getString(popularityIndex),c.getString(titleIndex),c.getString(overViewIndex),c.getString(language),c.getString(votesIndex));
            MovieDetailList.add(details);
            c.moveToNext();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Method","On Create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MovieDetails movieDetails = new MovieDetails();
        movieDB = this.openOrCreateDatabase("Movies",MODE_PRIVATE,null);
        movieDB.execSQL("CREATE TABLE IF NOT EXISTS Movie_details (id INTEGER PRIMARY KEY,movieId VARCHAR,posterURL VARCHAR,title VARCHAR,duration VARCHAR,rating VARCHAR,votes VARCHAR,overview VARCHAR,language VARCHAR)");
        updateList();
        gridView = (GridView)findViewById(R.id.gridView);
        adapter = new MovieAdapter(getApplicationContext(),MovieDetailList);
        gridView.setAdapter(adapter);
        try {
            movieDetails.execute("http://api.themoviedb.org/3/movie/popular?api_key=42c195dfd3c7af3d02056ccdb542f2d5");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
