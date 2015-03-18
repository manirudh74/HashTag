package com.social.hashtag.authentication;

import android.net.Uri;

import java.net.URI;
import java.util.HashMap;

/**
 * Created by payojb on 3/17/2015.
 */
public class AuthenticationCompletionDispatcher {
    private HashMap<String, BaseOAuthHandler<String>> oAuthHandlerCache;

    public AuthenticationCompletionDispatcher(){
        oAuthHandlerCache = new HashMap<String, BaseOAuthHandler<String>>();
    }

    public void addHandler(BaseOAuthHandler<String> handler){
        oAuthHandlerCache.put(getHostFromUrl(handler.getCallbackUrl()), handler);
    }

    public String dispatchAndGetToken(String url) throws Exception{
        String key = getHostFromUrl(url);
        if(!oAuthHandlerCache.containsKey(key))
            throw new Exception("Handler not found for authentication redirect flow");
        return oAuthHandlerCache.get(key).completeTokenFlow(url);
    }

    private String getHostFromUrl(String url){
        return Uri.parse(url).getHost();
    }
}
