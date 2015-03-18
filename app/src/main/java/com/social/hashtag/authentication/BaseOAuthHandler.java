package com.social.hashtag.authentication;

import com.social.hashtag.util.OAuthUIRedirectHandler;

/**
 * Created by payojb on 3/17/2015.
 */
public abstract class BaseOAuthHandler<T> {
    protected OAuthUIRedirectHandler oAuthUIRedirectHandler;
    public abstract String getCallbackUrl();
    public abstract Boolean hasTokenExpired(T token);
    public abstract void initiateTokenFlow();
    public abstract T completeTokenFlow(String url);

    protected BaseOAuthHandler(OAuthUIRedirectHandler oaurh){
        this.oAuthUIRedirectHandler = oaurh;
    }
}
