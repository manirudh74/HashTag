package com.social.hashtag;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.social.hashtag.adapter.CustomListAdapter;
import com.social.hashtag.app.AppController;
import com.social.hashtag.authentication.TwitterOAuthHandler;
import com.social.hashtag.model.Movie;
import com.social.hashtag.network.GsonRequest;
import com.social.hashtag.network.RequestQueueSingleton;
import com.social.hashtag.util.OAuthJSONArrayRequest;
import com.social.hashtag.util.OAuthUIRedirectHandler;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Trend;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.api.TimelinesResources;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends ActionBarActivity {
    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();

    // Movies json url
    private static final String twitterURL = "https://api.twitter.com/1.1/trends/place.json?id=1";
    private ProgressDialog pDialog;
    private List<Movie> movieList = new ArrayList<Movie>();
    private ListView listView;
    private CustomListAdapter adapter;

    private static AlertDialogManager alert = new AlertDialogManager();
    private Context context;
    private SharedPreferences sharedPreferences;

    private ExecutorService networkAccessThreadPool;

    //UI Elements
    private Button loginButton;
    private TextView lblUserName;
    private TextView lblDebug;

    private Boolean firstInitialization = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        if (!cd.isConnectingToInternet()) {
            alert.showAlertDialog(this, "Internet Connection Error", "Please connect to the internet", false);
        }

        networkAccessThreadPool = Executors.newFixedThreadPool(5);

        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
        lblDebug = (TextView) findViewById(R.id.lblDebug);
        lblUserName = (TextView) findViewById(R.id.lblUserName);

        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, movieList);
        listView.setAdapter(adapter);

        showProgressDialog("Loading...");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1b1b1b")));

        loginButton = (Button) findViewById(R.id.btnLoginTwitter);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //OAuth request token flow
                loginToTwitter();
            }
        });
        hideProgressDialog();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri redirectUri = intent.getData();
        if (redirectUri != null) {
            handleOAuthRedirectForAccessToken(redirectUri);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

    private void hideProgressDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private void showProgressDialog(String message) {
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage(message);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }
    //TWITTER FUNCTIONS

    public void loginToTwitter(){
        TwitterOAuthHandler handler = new TwitterOAuthHandler(
            new OAuthUIRedirectHandler(){
                @Override
                public void RedirectToAuthorizationUrl(String authorizationUrl){
                     startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authorizationUrl)));
                }
            },
            getApplicationContext());
        try {
            handler.initiateTokenFlow();
        }catch (Exception e){
            alert.showAlertDialog(this, "Login Failed", "Login to Twitter Failed", false);
        }
    }

    public void handleOAuthRedirectForAccessToken(Uri redirectUri) {
        //TODO: identify which service(Twitter, fb, etc) the redirect is for and dispatch to appropriate handler
    }

    private void makeApiCall() {

    }
}