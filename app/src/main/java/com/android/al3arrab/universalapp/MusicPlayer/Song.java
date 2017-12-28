package com.android.al3arrab.universalapp.MusicPlayer;

/**
 * Created by Adel on 5/10/2017.
 */

public class Song {
    private int mArtistName;
    private int mImageResourceId;

    private String mStringSongName;
    private String mAudioResourcePath;

    private int mSongName = NO_SONG_NAME_PROVIDED;
    private static final int NO_SONG_NAME_PROVIDED = -1;
    private int mAudioResourceId = NO_AUDIO_ID_PROVIDED;
    private static final int NO_AUDIO_ID_PROVIDED = -1;

    Song(int songName, int artistName, int audioResourceId, int imageResourceId) {
        mSongName = songName;
        mArtistName = artistName;
        mAudioResourceId = audioResourceId;
        mImageResourceId = imageResourceId;
    }

    Song(String songName, int artistName, String audioResourcePath, int imageResourceId) {
        mStringSongName = songName;
        mArtistName = artistName;
        mAudioResourcePath = audioResourcePath;
        mImageResourceId = imageResourceId;
    }

    int getmSongName() {
        return mSongName;
    }

    int getmArtistName() {
        return mArtistName;
    }

    int getmAudioResourceId() {
        return mAudioResourceId;
    }

    int getmImageResourceId() {
        return mImageResourceId;
    }

    String getmStringSongName() {
        return mStringSongName;
    }

    String getmAudioResourcePath() {
        return mAudioResourcePath;
    }

    public boolean hasSongName(){
        return mSongName != NO_SONG_NAME_PROVIDED;
    }

    public boolean hasAudioResource(){
        return mAudioResourceId != NO_AUDIO_ID_PROVIDED;
    }
}
