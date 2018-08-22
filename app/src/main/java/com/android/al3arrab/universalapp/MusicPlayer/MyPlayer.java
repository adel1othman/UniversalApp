package com.android.al3arrab.universalapp.MusicPlayer;

import android.Manifest;
import android.annotation.SuppressLint;
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
import java.util.concurrent.TimeUnit;

import static com.android.al3arrab.universalapp.MainActivity.songs;

public class MyPlayer extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    /*public static final String PLAYBACK_STATE_KEY = "PLAYBACK_STATE_KEY";
    public static final String SONG_ID_KEY = "SONG_ID_KEY";*/

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
    Handler myHandler = new Handler();
    boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        mVisualizerView = findViewById(R.id.activity_visualizer);

        if (songs == null || songs.size() == 0){
            new SongsList();
            songs = SongsList.getAllSongs(this);
        }

        if (getIntent().hasExtra("intSongID")){
            songId  = getIntent().getIntExtra("intSongID", 0);
        }else {
            songID  = getIntent().getStringExtra("StringSongID");
        }

        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        song = findViewById(R.id.songName);
        artist = findViewById(R.id.artistName);
        album = findViewById(R.id.albumImage);
        progressTime = findViewById(R.id.startTime);
        fullTime = findViewById(R.id.fullTime);
        seekBar = findViewById(R.id.seekbar);
        prev = findViewById(R.id.btnPrev);
        play = findViewById(R.id.btnPlay);
        pause = findViewById(R.id.btnPause);
        next = findViewById(R.id.btnNext);

        play.setEnabled(false);

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
                currentTime = 0;
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
                currentTime = 0;
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
            @SuppressLint("DefaultLocale")
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
                    Toast.makeText(this, getBaseContext().getResources().getString(R.string.audio_permission), Toast.LENGTH_LONG).show();
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

    @SuppressLint("DefaultLocale")
    private void onSongChanged(){
        releaseMediaPlayer();
        if (songs.get(songIndex).hasSongName()){
            song.setText(songs.get(songIndex).getmSongName());
        }else {
            String SongName = songs.get(songIndex).getmStringSongName();
            song.setText(SongName);
        }

        artist.setText(songs.get(songIndex).getmArtistName());
        album.setImageResource(songs.get(songIndex).getmImageResourceId());

        if (songs.get(songIndex).hasAudioResource()){
            mMediaPlayer = MediaPlayer.create(this, songs.get(songIndex).getmAudioResourceId());
        }else {
            Uri mySong = Uri.parse(songs.get(songIndex).getmAudioResourcePath());
            mMediaPlayer = MediaPlayer.create(this, mySong);
        }

        if (!firstTime){
            mAudioInputReader = new AudioInputReader(mVisualizerView, this, mMediaPlayer);
        }

        if (currentTime != 0){
            mMediaPlayer.seekTo(currentTime);
        }

        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(mCompletionListener);

        fullTime.setText(String.format("%dm:%ds", TimeUnit.MILLISECONDS.toMinutes((long) mMediaPlayer.getDuration()),
                TimeUnit.MILLISECONDS.toSeconds((long) mMediaPlayer.getDuration()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) mMediaPlayer.getDuration()))));

        myHandler.postDelayed(UpdateSongTime,100);
    }

    private Runnable UpdateSongTime = new Runnable() {
        @SuppressLint("DefaultLocale")
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

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(PLAYBACK_STATE_KEY, currentTime);
        outState.putInt(SONG_ID_KEY, songIndex);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null){
            currentTime = savedInstanceState.getInt(PLAYBACK_STATE_KEY);
            songIndex = savedInstanceState.getInt(SONG_ID_KEY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (songs != null){
            onSongChanged();
        }
    }*/
}
