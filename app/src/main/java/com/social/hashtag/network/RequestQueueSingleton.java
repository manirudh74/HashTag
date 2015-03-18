package com.social.hashtag.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by payojb on 3/17/2015.
 */
public class RequestQueueSingleton {

    private static RequestQueueSingleton instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Context context;

    private RequestQueueSingleton(Context context){
        this.context = context;
        this.requestQueue = getRequestQueue();
    }

    public static synchronized RequestQueueSingleton getInstance(Context context){
        if(instance == null)
            instance = new RequestQueueSingleton(context);
        return instance;
    }

    private RequestQueue getRequestQueue(){
        if(requestQueue == null)
            return Volley.newRequestQueue(context.getApplicationContext());
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
