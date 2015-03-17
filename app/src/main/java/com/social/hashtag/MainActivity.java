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
import com.social.hashtag.model.Movie;
import com.social.hashtag.network.GsonRequest;
import com.social.hashtag.network.RequestQueueSingleton;
import com.social.hashtag.util.OAuthFlow;
import com.social.hashtag.util.OAuthJSONArrayRequest;

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


    // Preference Constants
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_REQUEST_TOKEN = "oauth_request_token";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

    static final String TWITTER_CALLBACK_URL = "payojoauth://someurl";

    // Twitter

    static String TWITTER_CONSUMER_KEY = "NNtogSAkXzWLPp9DgtnX7utpm";
    static String TWITTER_CONSUMER_SECRET = "bO7UW3HIEGtHyz5aEFNe7gEjn3ZPbmvf4EzTqvBMwGs4vJwf9B";
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
    private static Twitter twitter = getTwitterInstance();
    private RequestToken requestToken;

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

        twitter = getTwitterInstance();

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
        //OAuth access token flow
        if (!isTwitterLoggedInAlready()) {
            Uri redirectUri = intent.getData();
            if (redirectUri != null && redirectUri.toString().startsWith(TWITTER_CALLBACK_URL)) {
                handleOAuthRedirectForAccessToken(redirectUri);
            }
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
        RequestQueueSingleton requestQueueSingleton = RequestQueueSingleton.getInstance(getApplicationContext());
        //requestQueueSingleton.addToRequestQueue(new GsonRequest<Object>());
        requestQueueSingleton.addToRequestQueue(new OAuthJSONArrayRequest("https://api.twitter.com/oauth/request_token",
                    new OAuthFlow(){
                        @Override
                        public void RedirectToAuthorizationUrl(String authorizationUrl){
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authorizationUrl)));
                        }
                    },
                    new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                        Log.d("", "@@@@@@@@@@@@@@@@22");
                                        Log.d("", response.toString());
                                        Log.d("", "@@@@@@@@@@@@@@@@22");

                                                // Parsing json
                                                        for (int i = 0; i < response.length(); i++) {
                                                try {

                                                                JSONObject obj = response.getJSONObject(i);
                                                        Log.i("Json Response",obj.toString());
                                                        Movie movie = new Movie();
                                                        movie.setTitle(obj.getString("title"));
                                                        movie.setThumbnailUrl(obj.getString("image"));
                                                        movie.setRating(((Number) obj.get("rating"))
                                                                .doubleValue());
                                                        movie.setYear(obj.getInt("releaseYear"));

                                                                // Genre is json array
                                                                        JSONArray genreArry = obj.getJSONArray("genre");
                                                        ArrayList<String> genre = new ArrayList<String>();
                                                       for (int j = 0; j < genreArry.length(); j++) {
                                                                genre.add((String) genreArry.get(j));
                                                            }
                                                        movie.setGenre(genre);

                                                                // adding movie to movies array
                                                                        movieList.add(movie);

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                   }

                                                // notifying list adapter about data changes
                                                        // so that it renders the list view with updated data
                                                                adapter.notifyDataSetChanged();
                                    }
                            },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                })
                .setRetryPolicy(new DefaultRetryPolicy(0,  DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                )
        );
    }

    public void loginToTwitter_twitter4j() {
        if (isTwitterLoggedInAlready()) {
            String token = sharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, null);
            String secret = sharedPreferences.getString(PREF_KEY_OAUTH_SECRET, null);
            // Create the twitter access token from the credentials we got previously
            AccessToken at = new AccessToken(token, secret);
            twitter.setOAuthAccessToken(at);
            makeApiCall();
            return;
            //Toast.makeText(context, "Already Logged into twitter", Toast.LENGTH_LONG).show();
        }

        Future future = networkAccessThreadPool.submit(new Callable<RequestToken>() {
            @Override
            public RequestToken call() throws Exception {
                RequestToken requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
                return requestToken;
            }
        });
        try {
            requestToken = (RequestToken) future.get();
        } catch (Exception e) {
            alert.showAlertDialog(getApplicationContext(), "Twitter OAuth Request Token Request Failed", e.getMessage(), false);
        }
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
    }

    public void handleOAuthRedirectForAccessToken(Uri redirectUri) {

        final String verifier = redirectUri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

        try {
            Future future = networkAccessThreadPool.submit(new Callable<AccessToken>() {
                @Override
                public AccessToken call() throws Exception {
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                    return accessToken;
                }
            });
            AccessToken accessToken = (AccessToken) future.get();

            SharedPreferences.Editor e = sharedPreferences.edit();
            e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
            e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
            e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
            e.commit();
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

            loginButton.setVisibility(View.GONE);

            long userID = accessToken.getUserId();
            User user = twitter.showUser(userID);
            future = networkAccessThreadPool.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return twitter.getScreenName();
                }
            });
            String username = (String) future.get();
            lblUserName.setText(Html.fromHtml("<b>Welcome " + username + "</b>"));
            lblUserName.setVisibility(View.VISIBLE);

            makeApiCall();
        } catch (Exception e) {
            alert.showAlertDialog(getApplicationContext(), "Twitter OAuth Access Token Request Failed", e.getMessage(), false);
        }
    }

    private void makeApiCall() {

        //TEST - DELETE
        showProgressDialog("Getting places from Twitter");
        Future future = networkAccessThreadPool.submit(new Callable<TimelinesResources>() {
            @Override
            public TimelinesResources call() throws Exception {
                return twitter.timelines();
                //return twitter.getPlaceTrends(12590014).getTrends();
            }
        });

        try {
            final TimelinesResources trends = (TimelinesResources) future.get();

            future = networkAccessThreadPool.submit(new Callable<ResponseList<Status>>() {
                @Override
                public ResponseList<Status> call() throws Exception {
                    return trends.getRetweetsOfMe();
                    //return twitter.getPlaceTrends(12590014).getTrends();
                }
            });

            ResponseList<Status> r = (ResponseList<Status>)future.get();

            for(Status s: r){
                lblDebug.append(s.getText());
            }

            loginButton.setVisibility(View.GONE);

//            for (int i = 0; i < trends.length; i++) {
//                Trend t = trends[i];
//                lblDebug.append("Name: " + t.getName());
//                lblDebug.append("  Query: " + t.getQuery());
//                lblDebug.append(" URL: " + t.getURL());
//                lblDebug.append("\n");
//        }
        } catch (Exception e) {
            alert.showAlertDialog(getApplicationContext(), "Twitter API Call Failed", e.getMessage(), false);
        }

        hideProgressDialog();
    }

    private static Twitter getTwitterInstance() {

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
        builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
        Configuration configuration = builder.build();

        TwitterFactory factory = new TwitterFactory(configuration);
        return factory.getInstance();
    }

    private boolean isTwitterLoggedInAlready() {
        // return twitter login status from Shared Preferences
        return sharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }

}