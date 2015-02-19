package com.social.hashtag.model;

/**
 * Created by R_Dhar on 2/15/2015.
 */
public class ListItem {
    private String title;
    private String value;
    private int imageId;

    public ListItem(String title) {
        setTitle(title);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
