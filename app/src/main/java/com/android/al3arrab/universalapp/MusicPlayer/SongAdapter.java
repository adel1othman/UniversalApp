package com.android.al3arrab.universalapp.MusicPlayer;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.al3arrab.universalapp.R;

import java.util.ArrayList;

/**
 * Created by Adel on 5/10/2017.
 */

public class SongAdapter extends ArrayAdapter<Song> {

    public SongAdapter(Context context, ArrayList<Song> songs) {
        super(context, 0, songs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Song currentSong = getItem(position);

        TextView songTextView = (TextView) listItemView.findViewById(R.id.song);
        if (currentSong.hasSongName()){
            songTextView.setText(currentSong.getmSongName());
        }else {
            String SongName = currentSong.getmStringSongName().replace(".mp3", "");
            songTextView.setText(SongName);
        }
        /*try {
            currentSong.getmSongName();
            songTextView.setText(currentSong.getmSongName());
        }catch (Resources.NotFoundException exception){
            songTextView.setText(currentSong.getmStringSongName());
        }*/

        TextView artistTextView = (TextView) listItemView.findViewById(R.id.artist);
        artistTextView.setText(currentSong.getmArtistName());

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image);
        imageView.setImageResource(currentSong.getmImageResourceId());

        return listItemView;
    }
}