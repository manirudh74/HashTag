package com.social.hashtag.authentication;

import android.net.Uri;
import com.social.hashtag.util.OAuthUIRedirectHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by payojb on 3/17/2015.
 */
public abstract class BaseOAuthHandler<T> {
    protected OAuthUIRedirectHandler oAuthUIRedirectHandler;
    protected ExecutorService networkAccessThreadPool;//threadpool to make network calls
    public abstract boolean isLoggedIn();
    public abstract T getLastToken();
    public abstract Boolean hasTokenExpired(T token);
    public abstract void refreshToken();
    public abstract void initiateTokenFlow() throws Exception;
    public abstract T completeTokenFlow(Uri uri) throws Exception;
    public abstract void logout();

    protected BaseOAuthHandler(OAuthUIRedirectHandler oaurh){
        this.oAuthUIRedirectHandler = oaurh;
        this.networkAccessThreadPool = Executors.newFixedThreadPool(5);
    }
}
