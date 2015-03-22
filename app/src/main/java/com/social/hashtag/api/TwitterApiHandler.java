package com.social.hashtag.api;

import android.content.Context;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.social.hashtag.authentication.BaseOAuthHandler;
import com.social.hashtag.authentication.TwitterOAuthHandler;
import com.social.hashtag.authentication.token.TwitterAuthToken;
import com.social.hashtag.model.HashTaggedItem;
import com.social.hashtag.network.RequestQueueSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

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

    protected ArrayList<HashTaggedItem> getItems(String hashTag) throws JSONException{
        OAuthRequest request = new OAuthRequest(Verb.GET,twitterURL+hashTag);
        TwitterOAuthHandler oah = (TwitterOAuthHandler)oAuthHandler;
        oah.getOAuthService().signRequest(oah.getLastToken(), request);
        Response response = request.send();
        String str = response.getBody();
        JSONObject json = new JSONObject(str);

        ArrayList<HashTaggedItem> result = new ArrayList<HashTaggedItem>();
        JSONArray tweets = json.getJSONArray("statuses");
        for (int i = 0; i < tweets.length(); i++)
        {
            JSONObject tweet = tweets.getJSONObject(i);
            String text = tweet.getString("text");
            HashTaggedItem hti = new HashTaggedItem();
            hti.hashTag = hashTag;
            hti.itemValue = text;
            JSONArray media = json.getJSONArray("media");
            if(media!=null && media.length()>0){
                String imgUrl = ((JSONObject)media.get(0)).getString("media_url_https");
                hti.imgUrl = imgUrl;
            }
            result.add(hti);
        }
        return result;
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
