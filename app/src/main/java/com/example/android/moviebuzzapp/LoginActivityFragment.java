package com.example.android.moviebuzzapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginActivityFragment extends Fragment {

    private CallbackManager callbackManager;
    private AccessTokenTracker tracker;
    private ProfileTracker profileTracker;
    private Profile profile;
    LoginButton loginButton;
    String userName;
    String profilePic;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView welcomeText;
    GraphRequest request;
    public LoginActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        sharedPreferences=this.getActivity().getSharedPreferences("ProfileData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        tracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.i("Token:","Changed");
                if(currentAccessToken == null){
                    clearData();
                }

            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.i("Changed", "Profile Changed");
                profile = currentProfile;
                if(profile == null){
                    clearData();
                }
                else{
                    userName = profile.getName();
                    profilePic = String.valueOf(profile.getProfilePictureUri(100, 100));
                    welcomeText.setText("Welcome "+userName);
                    save(userName,profilePic);
                }

            }
        };
        tracker.startTracking();
        profileTracker.startTracking();
    }
    public void save(String userName,String profilePic){
        editor.putString("userName", userName);
        editor.putString("profilePic",profilePic);
        editor.commit();
    }
    public void saveEmail(String userEmail){
        editor.putString("userEmail", userEmail);
        editor.commit();
    }
    public void clearData(){
        editor.putString("userName", " ");
        editor.putString("userEmail"," ");
        editor.putString("profilePic"," ");
        editor.commit();
        welcomeText.setText(" ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_login, container, false);
        loginButton = (LoginButton)view.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email","public_profile","user_friends"));
        loginButton.setFragment(this);
        welcomeText = (TextView)view.findViewById(R.id.nameView);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("Method : ","OnSuccess");
                if(loginResult.getAccessToken() == null){
                    clearData();
                }
                profile = Profile.getCurrentProfile();
                if (profile == null) {
                    clearData();
                } else {
                    userName = profile.getName();
                    profilePic = String.valueOf(profile.getProfilePictureUri(100, 100));
                    Log.i("Profile Pic",profilePic);
                    welcomeText.setText("Welcome " + userName);
                    save(userName,profilePic);

                }
                 request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String email_id = object.getString("email");
                            saveEmail(email_id);
                            Log.i("Email id",email_id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }

        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tracker.startTracking();
        profileTracker.startTracking();
    }
}
