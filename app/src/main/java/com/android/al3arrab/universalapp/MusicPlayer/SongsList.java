package com.android.al3arrab.universalapp.MusicPlayer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.al3arrab.universalapp.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SongsList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);

        final ArrayList<Song> songs = new ArrayList<>();
        songs.add(new Song(R.string.aho, R.string.amr, R.raw.aho_lel_we_adda, R.drawable.shoft));
        songs.add(new Song(R.string.atr, R.string.ahmed, R.raw.atr_el_hayah, R.drawable.mekky));
        songs.add(new Song(R.string.dawar, R.string.ahmed, R.raw.dawar_benafsak, R.drawable.mekky));
        songs.add(new Song(R.string.elhassah, R.string.ahmed, R.raw.el_hassah_el_sabaa, R.drawable.mekky));
        songs.add(new Song(R.string.elhelm, R.string.ahmed, R.raw.el_helm, R.drawable.mekky));
        songs.add(new Song(R.string.elleila, R.string.amr, R.raw.el_leila, R.drawable.wayah));
        songs.add(new Song(R.string.shoft, R.string.amr, R.raw.shoft_el_ayam, R.drawable.shoft));
        songs.add(new Song(R.string.srce, R.string.nzb, R.raw.srce_vatreno, R.drawable.srce));
        songs.add(new Song(R.string.svad, R.string.alex, R.raw.svadiba, R.drawable.svadiba));
        songs.add(new Song(R.string.terca, R.string.silente, R.raw.terca_na_tisinu, R.drawable.silente));
        songs.add(new Song(R.string.pirate, R.string.dk, R.raw.the_pirate_bay_song, R.drawable.dubioza));
        songs.add(new Song(R.string.treble, R.string.svadbas, R.raw.treblebass, R.drawable.treblebass));
        songs.add(new Song(R.string.wayah, R.string.amr, R.raw.wayah, R.drawable.wayah));
        songs.add(new Song(R.string.zorica, R.string.mejasi, R.raw.zorica, R.drawable.zorica));

        songs.addAll(getMusic(this));

        SongAdapter adapter = new SongAdapter(this, songs);

        ListView listView = findViewById(R.id.list);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Song song = songs.get(position);

                Intent intent = new Intent(getBaseContext(), MyPlayer.class);
                if (song.hasAudioResource()){
                    intent.putExtra("intSongID", song.getmAudioResourceId());
                }else {
                    intent.putExtra("StringSongID", song.getmAudioResourcePath());
                }

                TaskStackBuilder.create(getBaseContext())
                        .addNextIntentWithParentStack(intent)
                        .startActivities();
            }
        });
    }

    private static List<Song> getMusic(Context context) {
        Cursor mCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.MediaColumns.TITLE, MediaStore.MediaColumns.DATA }, null, null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

        int count = 0;
        if (mCursor != null) {
            count = mCursor.getCount();
        }

        List<Song> mySongs = new ArrayList<>();

        if (count > 0){
            int i = 0;
            if (mCursor.moveToFirst()) {
                do {
                    String songTitle = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE));

                    String songPath = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));

                    mySongs.add(new Song(songTitle, R.string.unknown_artist, songPath, R.drawable.unknown_music));

                    i++;
                } while (mCursor.moveToNext());
            }
        }

        if (mCursor != null) {
            mCursor.close();
        }

        return mySongs;
    }

    public static List<Song> getAllSongs(Context context){
        List<Song> songs = new ArrayList<>();

        songs.add(new Song(R.string.aho, R.string.amr, R.raw.aho_lel_we_adda, R.drawable.shoft));
        songs.add(new Song(R.string.atr, R.string.ahmed, R.raw.atr_el_hayah, R.drawable.mekky));
        songs.add(new Song(R.string.dawar, R.string.ahmed, R.raw.dawar_benafsak, R.drawable.mekky));
        songs.add(new Song(R.string.elhassah, R.string.ahmed, R.raw.el_hassah_el_sabaa, R.drawable.mekky));
        songs.add(new Song(R.string.elhelm, R.string.ahmed, R.raw.el_helm, R.drawable.mekky));
        songs.add(new Song(R.string.elleila, R.string.amr, R.raw.el_leila, R.drawable.wayah));
        songs.add(new Song(R.string.shoft, R.string.amr, R.raw.shoft_el_ayam, R.drawable.shoft));
        songs.add(new Song(R.string.srce, R.string.nzb, R.raw.srce_vatreno, R.drawable.srce));
        songs.add(new Song(R.string.svad, R.string.alex, R.raw.svadiba, R.drawable.svadiba));
        songs.add(new Song(R.string.terca, R.string.silente, R.raw.terca_na_tisinu, R.drawable.silente));
        songs.add(new Song(R.string.pirate, R.string.dk, R.raw.the_pirate_bay_song, R.drawable.dubioza));
        songs.add(new Song(R.string.treble, R.string.svadbas, R.raw.treblebass, R.drawable.treblebass));
        songs.add(new Song(R.string.wayah, R.string.amr, R.raw.wayah, R.drawable.wayah));
        songs.add(new Song(R.string.zorica, R.string.mejasi, R.raw.zorica, R.drawable.zorica));

        songs.addAll(getMusic(context));

        return songs;
    }

    @Override
    public void onBackPressed() {
    }
}
