package com.social.hashtag.util;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.social.hashtag.MainActivity;

import org.json.JSONArray;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by R_Dhar on 2/18/2015.
 */
public class OAuthJSONArrayRequest<T> extends JsonArrayRequest{

    private HashMap<String, String> params;
    private OAuthRequest oAuthRequest;
    private String url;
    public static String twitterCustomerKey = "OKeponOljpT5Ap6l1vfkjlMjb";
    public static String twitterCustomerSecret = "lmWlIxwNL4y8eM8STuseWx1zt8QfcA0rVAqu4npB6xLGs630O8";
    //public static String twitterAccessToken = "50584263-hn3k4oIeLUy0G36loEZQP4VwvQfKuZiLZnN3XJUYV";
    //public static String twitterAccessTokenSecret = "1l3urChgsmzAXCUEiji3ZAvnpWCcmq5jL5N8s8Z1AbOvW";

    public OAuthJSONArrayRequest(String url, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
        this.url=url;
        params = new HashMap<String, String>();
    }

    @Override
    public String getUrl() {
        if(oAuthRequest == null) {
            buildOAuthRequest();

            for(Map.Entry<String, String> entry : oAuthRequest.getOauthParameters().entrySet()) {
                params.put(entry.getKey(), entry.getValue());
            }
        }
        /*String url = super.getUrl() + getParameterString();
        Log.i("", "$$$$");
        Log.i("", url);
        Log.i("", "$$$$");*/
        return this.url;
    }

    private void buildOAuthRequest()  {
        oAuthRequest = new OAuthRequest(getVerb(), super.getUrl());
        for(Map.Entry<String, String> entry : params.entrySet()) {
            oAuthRequest.addQuerystringParameter(entry.getKey(), entry.getValue());
        }

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    OAuthService service = getOauthService();
                    Token token = service.getRequestToken();
                    service.signRequest(token, oAuthRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        try{
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private OAuthService getOauthService(){
        OAuthService service = new ServiceBuilder()
                .provider(TwitterApi.class)
                .apiKey(twitterCustomerKey)
                .apiSecret(twitterCustomerSecret)
                .build();
        return service;
    }

    private Verb getVerb() {
        switch (getMethod()) {
            case Method.GET:
                return Verb.GET;
            case Method.DELETE:
                return Verb.DELETE;
            case Method.POST:
                return Verb.POST;
            case Method.PUT:
                return Verb.PUT;
            default:
                return Verb.GET;
        }
    }

    private String getParameterString() {
        StringBuilder sb = new StringBuilder("?");
        Iterator<String> keys = params.keySet().iterator();
        while(keys.hasNext()) {
            String key = keys.next();
            sb.append(String.format("&%s=%s", key, params.get(key)));
        }
        return sb.toString();
    }
}
