package com.android.al3arrab.universalapp.Youtube;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Adel on 5/24/2017.
 */

public class YTLoader extends AsyncTaskLoader<List<YTVideo>> {

    private String mUrl;

    public YTLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<YTVideo> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        List<YTVideo> videos = YTQueryUtils.fetchYTData(mUrl);
        return videos;
    }
}