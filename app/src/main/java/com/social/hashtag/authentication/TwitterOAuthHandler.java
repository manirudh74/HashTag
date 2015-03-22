package com.social.hashtag.authentication;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.social.hashtag.authentication.token.TwitterAuthToken;
import com.social.hashtag.network.NetworkAccessThreadPool;
import com.social.hashtag.util.OAuthUIRedirectHandler;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Created by payojb on 3/17/2015.
 */
public class TwitterOAuthHandler extends BaseOAuthHandler<Token> {

    SharedPreferences sharedPreferences;
    Token requestToken;
    Context context;
    OAuthService service;
    boolean isTwitterLogedIn;
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String TWITTER_CALLBACK_URL = "payojoauth://someurl";
    static String TWITTER_CONSUMER_KEY = "NNtogSAkXzWLPp9DgtnX7utpm";
    static String TWITTER_CONSUMER_SECRET = "bO7UW3HIEGtHyz5aEFNe7gEjn3ZPbmvf4EzTqvBMwGs4vJwf9B";
    private Token accessToken;

    public TwitterOAuthHandler(OAuthUIRedirectHandler oaurh, Context c){
        super(oaurh);
        this.context = c;
        this.sharedPreferences = context.getSharedPreferences("MyPref", 0);
        service = new ServiceBuilder()
                .provider(TwitterApi.class)
                .apiKey(TWITTER_CONSUMER_KEY)
                .apiSecret(TWITTER_CONSUMER_SECRET)
                .callback(TWITTER_CALLBACK_URL)
                .build();
    }

    public OAuthService getOAuthService(){
        return service;
    }

    public Boolean hasTokenExpired(){
        //TODO: actually check.
        return false;
    }

    public boolean isLoggedIn(){
        return isTwitterLogedIn;
    }

    public Token getLastToken(){
        return accessToken;
    }

    public void refreshToken(){
        //TODO:refresh token
        //update lasttoken
    }

    public Boolean hasTokenExpired(TwitterAuthToken token){
        //TODO:check for token expiry
        return false;
    }

    public void initiateTokenFlow() throws Exception{
        Future<String> future = NetworkAccessThreadPool.getThreadPool().submit(new Callable<String>(){
            @Override
            public String call(){
                requestToken = service.getRequestToken();
                return service.getAuthorizationUrl(requestToken);
            }
        });

        String authenticationUrl = future.get();
        oAuthUIRedirectHandler.RedirectToAuthorizationUrl(authenticationUrl);
    }

    public synchronized Token completeTokenFlow(Uri uri) throws Exception{
        if(isLoggedIn())
            return getLastToken();
        final String v = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
        final Verifier verifier = new Verifier(v);
        Future future = NetworkAccessThreadPool.getThreadPool().submit(new Callable<Token>() {
            @Override
            public Token call() {
                return service.getAccessToken(requestToken, verifier);
            }
        });
        accessToken = (Token)future.get();
        return accessToken;
    }

    public void logout(){
        //TODO:Actually log out
        isTwitterLogedIn = false;
    }
}
