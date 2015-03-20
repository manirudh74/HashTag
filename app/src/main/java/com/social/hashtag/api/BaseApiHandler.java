package com.social.hashtag.api;

import com.social.hashtag.authentication.BaseOAuthHandler;
import com.social.hashtag.model.HashTaggedItem;
import com.social.hashtag.model.UIItem;
import com.social.hashtag.util.UIUpdateCallback;

import java.util.ArrayList;

/**
 * Created by payojb on 3/20/2015.
 */
public abstract class BaseApiHandler<TUIItem extends UIItem> {
    protected BaseOAuthHandler oAuthHandler;
    private int retries = 1;

    protected BaseApiHandler(){}

    protected BaseApiHandler(BaseOAuthHandler oah){
        this.oAuthHandler = oah;
    }
    protected BaseApiHandler(BaseOAuthHandler oah, int numRetries){
        this.oAuthHandler = oah;
        retries = numRetries;
    }

    //TODO: make this a more generic handler?
    public void handle(String hashTag, UIUpdateCallback uiUpdateCallback/*Main activity will provide callback to update UI*/){
        for(int i = 0;i<retries;i++){
            try{
                ArrayList<TUIItem> uiItems = getItems(hashTag);
                uiUpdateCallback.UpdateListItemsForHashTag(uiItems);
            }catch (Exception e){
                if(isTokenExpirationError(e))
                    oAuthHandler.refreshToken();
                else if(!shouldRetry(e))
                    throw e;
            }
        }
    }

    protected abstract ArrayList<TUIItem> getItems(String hashTag);
    protected abstract boolean shouldRetry(Exception e);
    protected abstract boolean isTokenExpirationError(Exception e);
}
