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
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    MovieParams movieInfo;
    ArrayList<String> MovieTitles = new ArrayList<String>();
    ArrayList<Bitmap> MoviePoster = new ArrayList<Bitmap>();
    ArrayList<String> MoviePopularity = new ArrayList<String>();
    ArrayList<String> MovieOverview = new ArrayList<String>();
    ArrayList<String> MovieLanguage = new ArrayList<String>();
    ArrayList<MovieParams> MovieDetailList = new ArrayList<MovieParams>();

    List<MovieParams> movies ;
    List<Video> videos;
    ArrayList<Video> videoList;
    GridView gridView;
    MovieAdapter adapter;
    SQLiteDatabase movieDB;
    MovieParams details;
    FragmentManager manager;
    Uri uri;
    String Type = "popular";
    public static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private final static String API_KEY = "42c195dfd3c7af3d02056ccdb542f2d5";
    private static int idValue;
    private static String movie_type;
    ApiInterface apiService;
    Call<MovieResponse> call;
    Call<VideoResponse> callVideo;
    TextView movieType;

    int movie_id;
    Video trailer;
    ArrayList<String> trailer_key;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.popular) {
            Log.i("Type","Popular");
            idValue=1;
            movie_type = "Popular";
            movieType.setText(movie_type);
            fetchData(idValue);

        } else if (id == R.id.top_rated) {
            Log.i("Type","Top Rated");
            idValue=2;
            movie_type = "Top Rated";
            movieType.setText(movie_type);
            fetchData(idValue);
        } else if (id == R.id.upcoming) {
            Log.i("Type", "Upcoming");
            idValue=3;
            movie_type = "Upcoming";
            movieType.setText(movie_type);
            fetchData(idValue);
        } else if (id == R.id.now_playing) {
            Log.i("Type", "Now Playing");
            idValue=4;
            movie_type = "Now Playing";
            movieType.setText(movie_type);
            fetchData(idValue);
        }else if(id == R.id.login){
            Log.i("Login","Login");
            Intent intentLogin = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intentLogin);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public class MovieAdapter extends ArrayAdapter<MovieParams>{

        public MovieAdapter(Context context, List<MovieParams> movie) {
            super(context, R.layout.list_item,movie);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            movieInfo = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
            }
            ImageView moviePoster = (ImageView)convertView.findViewById(R.id.moviePoster);
            Picasso.with(getApplicationContext()).load("http://image.tmdb.org/t/p/w185" + movieInfo.getPosterPath()).into(moviePoster);
            return convertView;
        }
    }

//    public class MovieDetails extends AsyncTask<String,Void,String >{
//
//        @Override
//        protected String doInBackground(String... params) {
//            URL url;
//            String result = "";
//            HttpURLConnection httpURLConnection = null;
//            try {
//                url = new URL(params[0]);
//                uri = Uri.parse(params[0]);
//                Type = uri.getLastPathSegment();
//                httpURLConnection = (HttpURLConnection)url.openConnection();
//                InputStream inputStream = httpURLConnection.getInputStream();
//                InputStreamReader reader = new InputStreamReader(inputStream);
//                int data= reader.read();
//                while (data != -1){
//                    char current = (char)data;
//                    result+=current;
//                    data=reader.read();
//                }
//                String posterUrl="";
//                String overview;
//                String popularity;
//                String title;
//                String language;
//                String voteAvg;
//                String movie_id;
//                String key;
//                String trailerName;
//                String trailerSite;
//                movieDB.execSQL("DELETE FROM Movie_details");
//
//                JSONObject jsonObject = new JSONObject(result);
//                JSONArray jsonArray = jsonObject.getJSONArray("results");
//                for(int i = 0; i<jsonArray.length();i++){
//                    JSONObject jsonPart = jsonArray.getJSONObject(i);
//                    posterUrl="http://image.tmdb.org/t/p/w185/"+jsonPart.getString("poster_path");
//                    movie_id = jsonPart.getString("id");
//                    //Log.i("Movie Id",movie_id);
//
//                    overview=jsonPart.getString("overview");
//                    popularity=jsonPart.getString("popularity");
//                    title = jsonPart.getString("title");
//                    language=jsonPart.getString("original_language");
//                    voteAvg=jsonPart.getString("vote_average");
//                    MovieTitles.add(title);
//                    MoviePopularity.add(popularity);
//                    MovieOverview.add(overview);
//                    MovieLanguage.add(language);
//                    String sql = "INSERT INTO Movie_details(movieId,posterURL,title,rating,votes,overview) VALUES ( ? , ? , ? , ? , ? , ? )";
//                    SQLiteStatement statement = movieDB.compileStatement(sql);
//                    statement.bindString(1,movie_id);
//                    statement.bindString(2, posterUrl);
//                    statement.bindString(3,title);
//                    statement.bindString(4,popularity);
//                    statement.bindString(5,voteAvg);
//                    statement.bindString(6, overview);
//                    statement.execute();
//                    url = new URL("http://api.themoviedb.org/3/movie/" +movie_id+ "/videos?api_key=42c195dfd3c7af3d02056ccdb542f2d5");
//                    httpURLConnection = (HttpURLConnection)url.openConnection();
//                    inputStream = httpURLConnection.getInputStream();
//                    reader = new InputStreamReader(inputStream);
//                    data = reader.read();
//                    String content="";
//                    while (data != -1) {
//                        char current = (char) data;
//                        content += current;
//                        data = reader.read();
//                    }
//                    videoDetailsList.clear();
//                    JSONObject jsonObjectVideo = new JSONObject(content);
//                    JSONArray jsonArrayVideo = jsonObjectVideo.getJSONArray("results");
//                    for(int j=0;j<jsonArrayVideo.length();j++){
//                        JSONObject videoPart = jsonArrayVideo.getJSONObject(j);
//                        key = videoPart.getString("key");
//                        trailerName = videoPart.getString("name");
//                        trailerSite = videoPart.getString("site");
//                        videoDetails = new VideoDetails(key,trailerName,trailerSite);
//                    }
//
//                    videoDetailsList.add(videoDetails);
//                    videoMap.put(Integer.parseInt(movie_id),videoDetailsList);
//
//                }
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            updateList(Type);
//            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Log.i("clicked","clciked");
//                manager=getFragmentManager();
//                    fetchedList.clear();
//                    Details fragment = (Details) manager.findFragmentById(R.id.fragment);
//                    MovieInfo clicked = MovieDetailList.get(position);
//                    String idToMatch = clicked.movieId;
//                    //Log.i("id",idToMatch);
//                    fetchedList = videoMap.get(Integer.parseInt(idToMatch));
//                    VideoDetails fetchedObject = fetchedList.get(0);
//                    //Log.i("key",fetchedObject.key);
//                    if(fragment != null && fragment.isVisible()){
//                        fragment.changeData(clicked.posterURL,clicked.title,clicked.overview,clicked.votes);
//                    }
//                    else{
//                        Intent intent = new Intent(getApplicationContext(),DetailView.class);
//                        intent.putExtra("Title",clicked.title);
//                        intent.putExtra("OverView",clicked.overview);
//                        intent.putExtra("Url",clicked.posterURL);
//                        intent.putExtra("Rating",clicked.votes);
//
//                        startActivity(intent);
//                    }
//                }
//            });
//            adapter.notifyDataSetChanged();
//
//           // gridView.setAdapter(adapter);
//        }
//    }
//    public void updateList(String Type){
//        Log.i("Updating List","updated");
//        //details = null;
//        Cursor c = movieDB.rawQuery("SELECT * FROM Movie_details", null);
//        int idIndex = c.getColumnIndex("movieId");
//        int posterIndex = c.getColumnIndex("posterURL");
//        int popularityIndex = c.getColumnIndex("rating");
//        int titleIndex = c.getColumnIndex("title");
//        int overViewIndex = c.getColumnIndex("overview");
//        int votesIndex = c.getColumnIndex("votes");
//        int language = c.getColumnIndex("language");
//        c.moveToFirst();
//        while( c != null && !c.isAfterLast()){
//            details = new MovieInfo(c.getString(idIndex),c.getString(posterIndex),c.getString(popularityIndex),c.getString(titleIndex),c.getString(overViewIndex),c.getString(language),c.getString(votesIndex));
//            MovieDetailList.add(details);
//            c.moveToNext();
//        }
//    }

    public void fetchData(int idValue){

        Log.i("Id", String.valueOf(idValue));
        if(movies != null)
        movies.clear();
        //adapter.notifyDataSetChanged();

        apiService =
                ApiClient.getClient().create(ApiInterface.class);

        call = apiService.getPopularMovies(API_KEY);
        if(idValue == 1)
            call = apiService.getPopularMovies(API_KEY);
        if(idValue == 2)
            call = apiService.getTopRatedMovies(API_KEY);
        if(idValue == 3)
            call = apiService.getUpcomingMovies(API_KEY);
        if(idValue == 4)
            call = apiService.getNowPlayingMovies(API_KEY);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                Log.i("Call",call.toString());
                Log.i("Method","On Response");
                movies = response.body().getResults();
                adapter = new MovieAdapter(getApplicationContext(), movies);
                gridView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.i("Error",t.toString());
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Method", "On Create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(idValue == 0){
            idValue=1;
        }
        movieType = (TextView)findViewById(R.id.movieType);
        if(movie_type == null){
            movie_type = "Popular";
        }
        movieType.setText(movie_type);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        gridView = (GridView)findViewById(R.id.gridView);
        trailer_key = new ArrayList<String>();
        fetchData(idValue);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("clicked", "clciked");
                trailer_key.clear();
                manager = getFragmentManager();
                final Details fragment = (Details) manager.findFragmentById(R.id.fragment);
                final MovieParams clicked = movies.get(position);
//                String idToMatch = String.valueOf(clicked.getId());
//                //Log.i("id",idToMatch);
//                fetchedList = videoMap.get(Integer.parseInt(idToMatch));
//                VideoDetails fetchedObject = fetchedList.get(0);
                //Log.i("key",fetchedObject.key);
//                items_video = new VideoItems();
                movie_id = clicked.getId();
                Log.i("ID", String.valueOf(movie_id));
                apiService =
                        ApiClient.getClient().create(ApiInterface.class);
                callVideo = apiService.getVideoDetails(movie_id, API_KEY);
                callVideo.enqueue(new Callback<VideoResponse>() {
                    @Override
                    public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                        Log.i("Size", String.valueOf(response.body().getResults().size()));
                        videos = response.body().getResults();
                        videoList = new ArrayList<Video>(videos);
                        //list_items = new ArrayList<VideoItems>(videos);
                        // Log.i("SizeList", String.valueOf(response.body().getResults().size()));

                        for (Video listItem : videos) {
                            //listItem.getId();
                            Log.i("Key Value", listItem.getKey());
                            trailer_key.add(listItem.getKey());
                        }

                        Log.i("Key size", String.valueOf(trailer_key.size()));
                        if (fragment != null && fragment.isVisible()) {
                            fragment.changeData("http://image.tmdb.org/t/p/w185" + clicked.getPosterPath(), clicked.getTitle(), clicked.getOverview(), String.valueOf(clicked.getVoteCount()),trailer_key.get(0));
                        } else {
                            Intent intent = new Intent(getApplicationContext(), DetailView.class);
                            intent.putExtra("Title", clicked.getTitle());
                            intent.putExtra("OverView", clicked.getOverview());
                            intent.putExtra("Url", "http://image.tmdb.org/t/p/w185" + clicked.getPosterPath());
                            intent.putExtra("Rating", clicked.getVoteCount().toString());
                            intent.putStringArrayListExtra("Keys", trailer_key);
                            startActivity(intent);
                        }

                    }

                    @Override
                    public void onFailure(Call<VideoResponse> call, Throwable t) {
                        Log.i("Error", t.toString());
                    }
                });

            }
        });
        //MovieDetails movieDetails = new MovieDetails();
        //movieDB = this.openOrCreateDatabase("Movies", MODE_PRIVATE, null);
        //movieDB.execSQL("CREATE TABLE IF NOT EXISTS Movie_details (id INTEGER PRIMARY KEY,movieId VARCHAR,posterURL VARCHAR,title VARCHAR,duration VARCHAR,rating VARCHAR,votes VARCHAR,overview VARCHAR,language VARCHAR,type VARCHAR)");
        //updateList(Type);
//        gridView = (GridView)findViewById(R.id.gridView);
//        adapter = new MovieAdapter(getApplicationContext(),MovieDetailList);
//        gridView.setAdapter(adapter);
      //  Log.i("Type", uri.getLastPathSegment());
//        try {
//            //movieDetails.execute(BASE_URL +Type +"?api_key=42c195dfd3c7af3d02056ccdb542f2d5");
//        } catch (Exception e){
//            e.printStackTrace();
//        }
    }

}
