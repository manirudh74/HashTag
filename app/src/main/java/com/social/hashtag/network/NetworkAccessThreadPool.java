package com.social.hashtag.network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by payojb on 3/20/2015.
 */
public class NetworkAccessThreadPool {
    private final static ExecutorService threadPool = Executors.newFixedThreadPool(5);

    public static ExecutorService getThreadPool(){
        return threadPool;
    }
}
