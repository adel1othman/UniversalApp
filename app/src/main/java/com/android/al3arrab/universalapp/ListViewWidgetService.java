package com.android.al3arrab.universalapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.android.al3arrab.universalapp.MusicPlayer.Song;

import java.util.ArrayList;
import java.util.List;

import static com.android.al3arrab.universalapp.MainActivity.songs;

public class ListViewWidgetService extends RemoteViewsService {

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListViewRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class ListViewRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<Song> records;

    public ListViewRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
    }

    public void onCreate() {
        records = new ArrayList<>();
    }

    public RemoteViews getViewAt(int position) {

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.now_playing_widget_content);

        Song data = records.get(position);

        rv.setImageViewResource(R.id.image_widget, data.getmImageResourceId());

        if (data.hasAudioResource()){
            rv.setTextViewText(R.id.song_widget, mContext.getResources().getString(data.getmSongName()));
        }else {
            rv.setTextViewText(R.id.song_widget, data.getmStringSongName());
        }

        rv.setTextViewText(R.id.artist_widget, mContext.getResources().getString(data.getmArtistName()));

        Bundle extras = new Bundle();

        //extras.putInt(NowPlayingWidget.EXTRA_ITEM, position);

        if (data.hasAudioResource()){
            extras.putInt("intSongID", data.getmAudioResourceId());
        }else {
            extras.putString("StringSongID", data.getmAudioResourcePath());
        }

        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        rv.setOnClickFillInIntent(R.id.widget_container, fillInIntent);

        return rv;
    }

    public int getCount(){
        Log.e("size=",records.size()+"");
        return records.size();
    }

    public void onDataSetChanged(){
        records = songs;
    }

    public int getViewTypeCount(){
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public void onDestroy(){
        records.clear();
    }

    public boolean hasStableIds() {
        return true;
    }

    public RemoteViews getLoadingView() {
        return null;
    }
}
