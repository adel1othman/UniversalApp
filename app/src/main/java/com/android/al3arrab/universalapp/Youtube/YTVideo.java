package com.android.al3arrab.universalapp.Youtube;

/**
 * Created by Adel on 5/24/2017.
 */

public class YTVideo {

    private String mID;
    private String mTitle;
    private String mImageURL;

    public YTVideo(String ID, String title, String imageURL) {
        mTitle = title;
        mID = ID;
        mImageURL = imageURL;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmID() {
        return mID;
    }

    public String getmImageURL() {
        return mImageURL;
    }
}