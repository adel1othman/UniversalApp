package com.android.al3arrab.universalapp.MusicPlayer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.al3arrab.universalapp.MusicPlayer.Visualizer.AudioVisuals.AudioInputReader;
import com.android.al3arrab.universalapp.MusicPlayer.Visualizer.AudioVisuals.VisualizerView;
import com.android.al3arrab.universalapp.MusicPlayer.Visualizer.SettingsActivity;
import com.android.al3arrab.universalapp.R;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MyPlayer extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE = 88;
    private VisualizerView mVisualizerView;
    private AudioInputReader mAudioInputReader;
    static MediaPlayer mMediaPlayer;
    AudioManager mAudioManager;

    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {

                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                mMediaPlayer.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                releaseMediaPlayer();
            }
        }
    };

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            songIndex++;
            if (songIndex > songs.size() - 1){
                songIndex = 0;
            }
            firstTime = false;
            onSongChanged();
        }
    };

    Button prev, play, pause, next;
    TextView song, artist, progressTime, fullTime;
    ImageView album;
    SeekBar seekBar;
    int songId, songIndex, currentTime, skBarProgress;
    String songID;
    ArrayList<Song> songs;
    Handler myHandler = new Handler();
    boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        mVisualizerView = findViewById(R.id.activity_visualizer);

        if (getIntent().hasExtra("intSongID")){
            songId  = getIntent().getIntExtra("intSongID", 0);
        }else {
            songID  = getIntent().getStringExtra("StringSongID");
        }


        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        song = (TextView)findViewById(R.id.songName);
        artist = (TextView)findViewById(R.id.artistName);
        album = (ImageView)findViewById(R.id.albumImage);
        progressTime = (TextView)findViewById(R.id.startTime);
        fullTime = (TextView)findViewById(R.id.fullTime);
        seekBar = (SeekBar)findViewById(R.id.seekbar);
        prev = (Button)findViewById(R.id.btnPrev);
        play = (Button)findViewById(R.id.btnPlay);
        pause = (Button)findViewById(R.id.btnPause);
        next = (Button)findViewById(R.id.btnNext);

        songs = new ArrayList<Song>();
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

        String[] myMusic = getMusic();
        for (String item:myMusic) {
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            String path = extStorageDirectory + File.separator + item;

            songs.add(new Song(item, R.string.unknown_artist, path, R.drawable.unknown_music));
        }

        play.setEnabled(false);

        //releaseMediaPlayer();

        int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            for (int i = 0; i < songs.size(); i++){
                if (songs.get(i).hasAudioResource()){
                    if (songs.get(i).getmAudioResourceId() == songId){
                        songIndex = i;
                        break;
                    }
                }else {
                    if (songs.get(i).getmAudioResourcePath().equals(songID)){
                        songIndex = i;
                        break;
                    }
                }

            }

            onSongChanged();
        }

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause.setEnabled(true);
                play.setEnabled(false);
                songIndex--;
                if (songIndex < 0){
                    songIndex = songs.size() - 1;
                }
                firstTime = false;
                onSongChanged();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause.setEnabled(true);
                play.setEnabled(false);
                songIndex++;
                if (songIndex > songs.size() - 1){
                    songIndex = 0;
                }
                firstTime = false;
                onSongChanged();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.pause();
                play.setEnabled(true);
                pause.setEnabled(false);
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.start();
                play.setEnabled(false);
                pause.setEnabled(true);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                skBarProgress = progress;
                progressTime.setText(String.format("%dm:%ds", TimeUnit.MILLISECONDS.toMinutes((long) progress),
                        TimeUnit.MILLISECONDS.toSeconds((long) progress) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) progress))));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                myHandler.removeCallbacks(UpdateSongTime);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.seekTo(skBarProgress);
                myHandler.postDelayed(UpdateSongTime,100);
            }
        });

        setupSharedPreferences();
        setupPermissions();
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
    public void onStop() {
        super.onStop();

        //releaseMediaPlayer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.visualizer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            if (songs.get(songIndex).hasAudioResource()){
                startSettingsActivity.putExtra("intSongID", songs.get(songIndex).getmAudioResourceId());
            }else {
                startSettingsActivity.putExtra("StringSongID", songs.get(songIndex).getmAudioResourcePath());
            }
            TaskStackBuilder.create(getBaseContext())
                    .addNextIntentWithParentStack(startSettingsActivity)
                    .startActivities();
            //startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mVisualizerView.setShowBass(sharedPreferences.getBoolean(getString(R.string.pref_show_bass_key),
                getResources().getBoolean(R.bool.pref_show_bass_default)));
        mVisualizerView.setShowMid(sharedPreferences.getBoolean(getString(R.string.pref_show_mid_range_key),
                getResources().getBoolean(R.bool.pref_show_mid_range_default)));
        mVisualizerView.setShowTreble(sharedPreferences.getBoolean(getString(R.string.pref_show_treble_key),
                getResources().getBoolean(R.bool.pref_show_treble_default)));
        loadColorFromPreferences(sharedPreferences);
        loadSizeFromSharedPreferences(sharedPreferences);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void loadColorFromPreferences(SharedPreferences sharedPreferences) {
        mVisualizerView.setColor(sharedPreferences.getString(getString(R.string.pref_color_key),
                getString(R.string.pref_color_green_value)));
    }

    private void loadSizeFromSharedPreferences(SharedPreferences sharedPreferences) {
        float minSize = Float.parseFloat(sharedPreferences.getString(getString(R.string.pref_size_key),
                getString(R.string.pref_size_default)));
        mVisualizerView.setMinSizeScale(minSize);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_show_bass_key))) {
            mVisualizerView.setShowBass(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_show_bass_default)));
        } else if (key.equals(getString(R.string.pref_show_mid_range_key))) {
            mVisualizerView.setShowMid(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_show_mid_range_default)));
        } else if (key.equals(getString(R.string.pref_show_treble_key))) {
            mVisualizerView.setShowTreble(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_show_treble_default)));
        } else if (key.equals(getString(R.string.pref_color_key))) {
            loadColorFromPreferences(sharedPreferences);
        } else if (key.equals(getString(R.string.pref_size_key))) {
            float minSize = Float.parseFloat(sharedPreferences.getString(getString(R.string.pref_size_key), "0.1"));
            mVisualizerView.setMinSizeScale(minSize);
        }
    }

    private void setupPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String[] permissionsWeNeed = new String[]{ Manifest.permission.RECORD_AUDIO };
                requestPermissions(permissionsWeNeed, MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE);
            }
        } else {
            mAudioInputReader = new AudioInputReader(mVisualizerView, this, mMediaPlayer);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mAudioInputReader = new AudioInputReader(mVisualizerView, this, mMediaPlayer);

                } else {
                    Toast.makeText(this, "Permission for audio not granted. Visualizer can't run.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();

            mMediaPlayer = null;

            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    private void onSongChanged(){
        releaseMediaPlayer();
        if (songs.get(songIndex).hasSongName()){
            song.setText(songs.get(songIndex).getmSongName());
        }else {
            String SongName = songs.get(songIndex).getmStringSongName().replace(".mp3", "");
            song.setText(SongName);
        }

        artist.setText(songs.get(songIndex).getmArtistName());
        album.setImageResource(songs.get(songIndex).getmImageResourceId());

        if (songs.get(songIndex).hasAudioResource()){
            mMediaPlayer = MediaPlayer.create(this, songs.get(songIndex).getmAudioResourceId());
        }else {
            Uri mySong = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + songs.get(songIndex).getmStringSongName());
            mMediaPlayer = MediaPlayer.create(this, mySong);
        }

        if (!firstTime){
            mAudioInputReader = new AudioInputReader(mVisualizerView, this, mMediaPlayer);
        }
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(mCompletionListener);

        fullTime.setText(String.format("%dm:%ds", TimeUnit.MILLISECONDS.toMinutes((long) mMediaPlayer.getDuration()),
                TimeUnit.MILLISECONDS.toSeconds((long) mMediaPlayer.getDuration()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) mMediaPlayer.getDuration()))));

        myHandler.postDelayed(UpdateSongTime,100);
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            if (mMediaPlayer != null){
                currentTime = mMediaPlayer.getCurrentPosition();
                progressTime.setText(String.format("%dm:%ds", TimeUnit.MILLISECONDS.toMinutes((long) currentTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) currentTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) currentTime))));
                seekBar.setMax(mMediaPlayer.getDuration());
                seekBar.setProgress(currentTime);
                myHandler.postDelayed(this, 100);
            }
        }
    };

    @Override
    public void onBackPressed() {
    }
}
