package com.example.android.moviebuzzapp;

import android.app.FragmentManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
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
    FragmentManager manager;
    public static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private final static String API_KEY = "42c195dfd3c7af3d02056ccdb542f2d5";
    private static int idValue;
    private static String movie_type;
    ApiInterface apiService;
    Call<MovieResponse> call;
    Call<VideoResponse> callVideo;
    TextView userName;
    int movie_id;
    ArrayList<String> trailer_key;
    SharedPreferences sharedPreferences;
    String user_name,user_email,profile_pic;
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.popular) {
            Log.i("Type","Popular");
            idValue=1;
            movie_type = "Popular";
            getSupportActionBar().setTitle("Popular");
            fetchData(idValue);

        } else if (id == R.id.top_rated) {
            Log.i("Type","Top Rated");
            idValue=2;
            movie_type = "Top Rated";
            getSupportActionBar().setTitle("Top Rated");
            fetchData(idValue);
        } else if (id == R.id.upcoming) {
            Log.i("Type", "Upcoming");
            idValue=3;
            movie_type = "Upcoming";
            getSupportActionBar().setTitle("Upcoming");
            fetchData(idValue);
        } else if (id == R.id.now_playing) {
            Log.i("Type", "Now Playing");
            idValue=4;
            movie_type = "Now Playing";
            getSupportActionBar().setTitle("Now Playing");
            fetchData(idValue);
        }else if(id == R.id.login){
            Log.i("Login","Login");
            getSupportActionBar().setTitle("Login");
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

    public void fetchData(int idValue){

        Log.i("Id", String.valueOf(idValue));
        if(movies != null)
        movies.clear();
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
        if(userName != null){
            Log.i("User name nav", (String) userName.getText());
        }
        if(idValue == 0){
            idValue=1;
        }
        if(movie_type == null){
            movie_type = "Popular";
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);
        TextView username = (TextView)headerLayout.findViewById(R.id.user_name);
        ImageView profilePic = (ImageView)headerLayout.findViewById(R.id.profilePic);
        TextView userEmail = (TextView)headerLayout.findViewById(R.id.profileEmail);
        Log.i("Header User", username.getText().toString());
        load();
        Log.i("Profile Name:", user_name);
        username.setText(user_name);
        userEmail.setText(user_email);
        if(profile_pic != null && profile_pic != " "){
            Log.i("Profile pic",profile_pic);
            Picasso.with(getApplicationContext()).load(profile_pic).into(profilePic);
        }
        Log.i("Header User", username.getText().toString());
        gridView = (GridView)findViewById(R.id.gridView);
        trailer_key = new ArrayList<String>();
        fetchData(idValue);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                trailer_key.clear();
                manager = getFragmentManager();
                final Details fragment = (Details) manager.findFragmentById(R.id.fragment);
                final MovieParams clicked = movies.get(position);
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
                        for (Video listItem : videos) {
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
    }
    public void load(){
        sharedPreferences = getSharedPreferences("ProfileData",Context.MODE_PRIVATE);
        user_name  = sharedPreferences.getString("userName", " ");
        user_email = sharedPreferences.getString("userEmail"," ");
        profile_pic = sharedPreferences.getString("profilePic"," ");
    }

}
