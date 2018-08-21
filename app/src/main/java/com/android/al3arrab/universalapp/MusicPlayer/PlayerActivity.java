package com.android.al3arrab.universalapp.MusicPlayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.al3arrab.universalapp.MainActivity;
import com.android.al3arrab.universalapp.R;
import com.android.al3arrab.universalapp.Utility;

import java.util.ArrayList;

import static com.android.al3arrab.universalapp.MainActivity.songs;

public class PlayerActivity extends AppCompatActivity {

    EditText search;
    String srch;
    Button all;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result != PackageManager.PERMISSION_GRANTED) {
            Utility.verifyStoragePermissions(this);
        } else {
            songs = SongsList.getAllSongs(this);
        }

        all = findViewById(R.id.btnAll);
        search = findViewById(R.id.etSearch);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()){
                    ListView listView = findViewById(R.id.songsList);

                    listView.setAdapter(null);
                }else if(!s.equals("") ){
                    srch = s.toString().toLowerCase();
                    String searchingSong, searchingArtist;
                    final ArrayList<Song> foundSongs = new ArrayList<>();

                    for (Song item : songs){
                        if (item.hasSongName()){
                            searchingSong = getString(item.getmSongName()).toLowerCase();
                        }else {
                            searchingSong = item.getmStringSongName().replace(".mp3", "").toLowerCase();
                        }

                        searchingArtist = getString(item.getmArtistName()).toLowerCase();
                        if (searchingSong.contains(srch) || searchingArtist.contains(srch)){
                            foundSongs.add(item);
                        }else {
                            if (foundSongs.contains(item)){
                                foundSongs.remove(item);
                            }
                        }
                    }

                    SongAdapter adapter = new SongAdapter(getBaseContext(), foundSongs);

                    ListView listView = findViewById(R.id.songsList);

                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            Song song = foundSongs.get(position);

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
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                search.setFocusable(true);
                search.setFocusableInTouchMode(true);

                return false;
            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SongsIntent = new Intent(PlayerActivity.this, SongsList.class);

                startActivity(SongsIntent);
            }
        });
    }

    private String[] getMusic() {
        final Cursor mCursor = managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media.DISPLAY_NAME }, null, null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

        int count = mCursor.getCount();

        String[] songs = new String[count];
        int i = 0;
        if (mCursor.moveToFirst()) {
            do {
                songs[i] = mCursor.getString(0);
                i++;
            } while (mCursor.moveToNext());
        }

        mCursor.close();

        return songs;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int result:grantResults) {
            if (result == PackageManager.PERMISSION_GRANTED) {
                songs = SongsList.getAllSongs(this);
                return;
            }
        }

        Toast.makeText(this, getResources().getString(R.string.read_ext_storage), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
    }
}