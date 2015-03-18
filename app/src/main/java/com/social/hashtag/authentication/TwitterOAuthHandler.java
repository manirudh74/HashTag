package com.social.hashtag.authentication;

import com.social.hashtag.util.OAuthUIRedirectHandler;

/**
 * Created by payojb on 3/17/2015.
 */
public class TwitterOAuthHandler implements BaseOAuthHandler<String> {

    public TwitterOAuthHandler(OAuthUIRedirectHandler oaurh){
        super(oaurh);
    }
}
