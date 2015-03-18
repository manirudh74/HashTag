package com.social.hashtag.util;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.social.hashtag.MainActivity;

import org.json.JSONArray;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.ParameterList;
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
    private OAuthUIRedirectHandler oauthFlow;
    static final String twitterCustomerKey = "NNtogSAkXzWLPp9DgtnX7utpm";
    static final String twitterCustomerSecret = "bO7UW3HIEGtHyz5aEFNe7gEjn3ZPbmvf4EzTqvBMwGs4vJwf9B";
    static final String twitterCallbackUrl = "payojoauth://someurl";

    public OAuthJSONArrayRequest(String url, OAuthUIRedirectHandler oaf, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
        this.url=url;
        this.oauthFlow = oaf;
        params = new HashMap<String, String>();
    }

    @Override
    public String getUrl() {
        buildOAuthRequest();

        for(Map.Entry<String, String> entry : oAuthRequest.getOauthParameters().entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }
        ParameterList pl = oAuthRequest.getQueryStringParams();

        ParameterList bl = oAuthRequest.getBodyParams();
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
                    OAuthService service = new ServiceBuilder()
                            .provider(TwitterApi.class)
                            .apiKey(twitterCustomerKey)
                            .apiSecret(twitterCustomerSecret)
                            .callback(twitterCallbackUrl)
                            .build();
                    Token requestToken = service.getRequestToken();
                    String authorizationUrl = service.getAuthorizationUrl(requestToken);
                    oauthFlow.RedirectToAuthorizationUrl(authorizationUrl);
//                    Log.i("", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//                    Log.i("", authorizationUrl);
//                    Log.i("", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//                    Token t = new Token("16070214-Az96wf3GNDU4TJISIGeqItvXAi2DlG8sxfXYA4kRC", "HcVetHKmw0TNhhNUygId9yCJItJ3Q6nhfbnH3Yjn5VgE2");
//                    service.signRequest(t, oAuthRequest);
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
