package com.social.hashtag;
import java.util.ArrayList;
import java.util.List;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;
import com.social.hashtag.adapter.CustomListAdapter;
import com.social.hashtag.api.AggregateApiHandler;
import com.social.hashtag.api.TwitterApiHandler;
import com.social.hashtag.authentication.BaseOAuthHandler;
import com.social.hashtag.authentication.TwitterOAuthHandler;
import com.social.hashtag.model.Movie;
import com.social.hashtag.model.UIItem;
import com.social.hashtag.util.OAuthUIRedirectHandler;
import com.social.hashtag.util.UIUpdateCallback;

import twitter4j.Twitter;

public class MainActivity extends ActionBarActivity {

    private static final String twitterURL = "https://api.twitter.com/1.1/trends/place.json?id=1";
    private ProgressDialog pDialog;
    private List<Movie> movieList = new ArrayList<Movie>();
    private ListView listView;
    private CustomListAdapter adapter;

    private static AlertDialogManager alert = new AlertDialogManager();
    private Boolean loginInProgress = false;

    private BaseOAuthHandler currentOAuthHandler;//only one oauth flow will be active at one time.
    private TwitterOAuthHandler twitterOAuthHandler;
    private AggregateApiHandler aggregateApiHandler = new AggregateApiHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        if (!cd.isConnectingToInternet()) {
            alert.showAlertDialog(this, "Internet Connection Error", "Please connect to the internet", false);
        }

        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, movieList);
        listView.setAdapter(adapter);

        showProgressDialog("Loading...");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1b1b1b")));

        //Add any new button here and implement its OAuth handler
        //Implement a function to call the OAuth handler
        //Add a case in handleLoginButtonClick and call the function from there.
        //That should take care of hooking up OAuth flow for the new service.
        hookupLoginButtonHandlers(new int[]{
                R.id.btnLoginTwitter,
                R.id.btnLoginFacebook,
                R.id.btnLoginGoogle
        });
        //OAuth Handlers
        twitterOAuthHandler = new TwitterOAuthHandler(
                new OAuthUIRedirectHandler(){
                    @Override
                    public void RedirectToAuthorizationUrl(String authorizationUrl){
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authorizationUrl)));
                    }
                },
                getApplicationContext());

        //Add Api handlers
        aggregateApiHandler.addHandler(new TwitterApiHandler(twitterOAuthHandler));

        findViewById(R.id.btnMakeApiCall).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                aggregateApiHandler.handle("somehashTagTODOChange", new UIUpdateCallback(){
                    @Override
                    public void UpdateListItemsForHashTag(ArrayList<? extends UIItem> hashTaggedItems){
                        //TODO: given a list of ui items, add them to UI.
                    }
                });
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

    private void hookupLoginButtonHandlers(int[] loginButtonIds){
        for(int i = 0; i<loginButtonIds.length; i++) {
            findViewById(loginButtonIds[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleLoginButtonClick(v);
                }
            });
        }
    }

    private void handleLoginButtonClick(View loginButton){
        int viewId = loginButton.getId();
        if(loginInProgress) {
            alert.showAlertDialog(this, "Login in progress", "Another login is in progress. Please complete it first.", false);
            return;
        }

        loginInProgress = true;//this will be cleared when oauthFlow is complete(in handleOAuthRedirectForAccessToken())
        switch (viewId){
            case R.id.btnLoginTwitter:
                loginToTwitter();
                break;
            case R.id.btnLoginFacebook:
                alert.showAlertDialog(this, "Ain't hooked up", "Hook me up brah. Won't you hook a brotha up?", false);
                break;
            case R.id.btnLoginGoogle:
                alert.showAlertDialog(this, "Ain't hooked up", "Hook me up brah. Won't you hook a brotha up?", false);
                break;
            default:
                loginInProgress = false;
                break;
        }
    }

    private void loginToTwitter(){
        currentOAuthHandler = twitterOAuthHandler;
        try {
            currentOAuthHandler.initiateTokenFlow();
        }catch (Exception e){
            alert.showAlertDialog(this, "OAuth Request Token Failure", "Login to Twitter Failed", false);
        }
    }

    private void handleOAuthRedirectForAccessToken(Uri redirectUri) {
        try{
            currentOAuthHandler.completeTokenFlow(redirectUri);
        }catch (Exception e){
            alert.showAlertDialog(this, "OAuth Access Token Failure", "Login to Twitter Failed", false);
        }finally {
            loginInProgress = false;
        }
    }
}