package com.social.hashtag.adapter;

/**
 * Created by R_Dhar on 2/16/2015.
 */

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.social.hashtag.R;
import com.social.hashtag.app.AppController;
import com.social.hashtag.model.HashTaggedItem;
import com.social.hashtag.model.Movie;

public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<HashTaggedItem> items;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, List<HashTaggedItem> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int location) {
        return items.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);
        TextView text = (TextView) convertView.findViewById(R.id.tweet_text);

        HashTaggedItem item = items.get(position);
        thumbNail.setImageUrl(item.imgUrl, imageLoader);
        text.setText(item.itemValue);

        return convertView;
    }

}
