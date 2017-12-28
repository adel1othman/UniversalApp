package com.android.al3arrab.universalapp.Youtube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.al3arrab.universalapp.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Adel on 5/24/2017.
 */

public class YTAdapter extends ArrayAdapter<YTVideo> {

    public YTAdapter(Context context, List<YTVideo> videos) {
        super(context, 0, videos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_youtube_item, parent, false);
        }

        YTVideo currentVideo = getItem(position);

        TextView titleView = (TextView) listItemView.findViewById(R.id.txtTitle);
        titleView.setText(currentVideo.getmTitle());

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.img);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(currentVideo.getmImageURL(), imageView);

        return listItemView;
    }
}