package com.social.hashtag.api;

import com.social.hashtag.authentication.BaseOAuthHandler;
import com.social.hashtag.model.HashTaggedItem;

import java.util.ArrayList;

/**
 * Created by payojb on 3/20/2015.
 */
public class AggregateApiHandler extends BaseApiHandler<HashTaggedItem> {
    private ArrayList<BaseApiHandler<HashTaggedItem>> apiHandlers;

    public AggregateApiHandler(){
        this.apiHandlers = new ArrayList<BaseApiHandler<HashTaggedItem>>();
    }
    public AggregateApiHandler(ArrayList<BaseApiHandler<HashTaggedItem>> ah){
        this.apiHandlers = ah;
    }

    public void addHandler(BaseApiHandler<HashTaggedItem> item){
        apiHandlers.add(item);
    }

    protected ArrayList<HashTaggedItem> getItems(String hashTag){
        ArrayList<HashTaggedItem> items = new ArrayList<>();
       for(int i =0 ;i<apiHandlers.size();i++){
           items.addAll(apiHandlers.get(i).getItems(hashTag));
       }
        return items;
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
