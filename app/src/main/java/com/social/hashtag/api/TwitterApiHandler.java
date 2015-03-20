package com.social.hashtag.api;

import com.social.hashtag.authentication.BaseOAuthHandler;
import com.social.hashtag.model.HashTaggedItem;

import java.util.ArrayList;

/**
 * Created by payojb on 3/20/2015.
 */
public class TwitterApiHandler extends BaseApiHandler<HashTaggedItem> {

    public TwitterApiHandler(BaseOAuthHandler oah){
        super(oah);
    }
    public TwitterApiHandler(BaseOAuthHandler oah, int numRetries){
        super(oah, numRetries);
    }

    protected ArrayList<HashTaggedItem> getItems(String hashTag){
        //TODO: make actual call
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
