package com.social.hashtag.util;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.social.hashtag.MainActivity;

import org.json.JSONArray;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;
import org.scribe.model.Token;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by R_Dhar on 2/18/2015.
 */
public class OAuthJSONArrayRequest<T> extends JsonArrayRequest{

    private HashMap<String, String> params;
    private OAuthRequest oAuthRequest;

    public OAuthJSONArrayRequest(String url, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
        params = new HashMap<String, String>();
    }


    public void addParameter(String key, String value) {
        params.put(key, value);
    }

    @Override
    protected Map<String, String> getParams() {
        return params;
    }

    @Override
    public String getUrl() {
        if(oAuthRequest == null) {
            buildOAuthRequest();

            for(Map.Entry<String, String> entry : oAuthRequest.getOauthParameters().entrySet()) {
                addParameter(entry.getKey(), entry.getValue());
            }
        }
        return super.getUrl() + getParameterString();
    }

    private void buildOAuthRequest() {
        oAuthRequest = new OAuthRequest(getVerb(), super.getUrl());
        for(Map.Entry<String, String> entry : getParams().entrySet()) {
            oAuthRequest.addQuerystringParameter(entry.getKey(), entry.getValue());
        }
        Token token = MainActivity.getOauthService().getRequestToken();
        MainActivity.getOauthService().signRequest(token, oAuthRequest);
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
