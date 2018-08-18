package com.android.al3arrab.universalapp.MusicPlayer.Visualizer;

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.al3arrab.universalapp.MusicPlayer.MyPlayer;
import com.android.al3arrab.universalapp.R;

public class SettingsActivity extends AppCompatActivity {

    int songId = -1;
    String songID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vizulizer_settings);
        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().hasExtra("intSongID")){
            songId  = getIntent().getIntExtra("intSongID", -1);
        }else {
            songID  = getIntent().getStringExtra("StringSongID");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent goBackToPlayer = new Intent(this, MyPlayer.class);
        if (songId != -1){
            goBackToPlayer.putExtra("intSongID", songId);
        }else {
            goBackToPlayer.putExtra("StringSongID", songID);
        }
        NavUtils.navigateUpTo(this, goBackToPlayer);
        //NavUtils.navigateUpFromSameTask(this);
        //startActivity(goBackToPlayer);
    }

/*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent goBackToPlayer = new Intent(this, MyPlayer.class);
            if (songId != -1){
                goBackToPlayer.putExtra("intSongID", songId);
            }else {
                goBackToPlayer.putExtra("StringSongID", songID);
            }
            //NavUtils.navigateUpTo(this, goBackToPlayer);
            NavUtils.navigateUpFromSameTask(this);
            //startActivity(goBackToPlayer);
        }
        return super.onOptionsItemSelected(item);
    }*/
}