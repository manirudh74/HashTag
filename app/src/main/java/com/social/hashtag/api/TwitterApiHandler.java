package com.social.hashtag.api;

import android.content.Context;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.social.hashtag.authentication.BaseOAuthHandler;
import com.social.hashtag.model.HashTaggedItem;
import com.social.hashtag.network.RequestQueueSingleton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by payojb on 3/20/2015.
 */
public class TwitterApiHandler extends BaseApiHandler<HashTaggedItem> {
    private static final String twitterURL = "https://api.twitter.com/1.1/search/tweets.json?q=%23";
    private Context context;

    public TwitterApiHandler(BaseOAuthHandler oah, Context context){
        super(oah);
        this.context = context;
    }
    public TwitterApiHandler(BaseOAuthHandler oah, int numRetries, Context context){
        super(oah, numRetries);
        this.context = context;
    }

    protected ArrayList<HashTaggedItem> getItems(String hashTag){
        RequestQueueSingleton requestQueue = RequestQueueSingleton.getInstance(context);
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(twitterURL, null, future, future);
        requestQueue.addToRequestQueue(request);

        try {
            JSONObject response = future.get(30, TimeUnit.SECONDS);
        }catch (Exception ex){
            //TODO: don't eat this exception. fix this.
        }

        return new ArrayList<>();
    }

    protected boolean shouldRetry(Exception e){
        //TODO: add logic here
        return false;
    }
    protected boolean isTokenExpirationError(Exception e){
        //TODO: add logic here
        return false;
    }
}
