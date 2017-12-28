package com.android.al3arrab.universalapp.MusicPlayer.Visualizer.AudioVisuals;

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

import android.content.Context;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Build;

public class AudioInputReader {

    private final VisualizerView mVisualizerView;
    private Context mContext;
    private MediaPlayer mPlayer;
    private Visualizer mVisualizer;

    public AudioInputReader(VisualizerView visualizerView, Context context, MediaPlayer mediaPlayer) {
        this.mVisualizerView = visualizerView;
        this.mContext = context;
        initVisualizer(mediaPlayer);
    }

    private void initVisualizer(MediaPlayer mediaPlayer) {
        mPlayer = mediaPlayer;
        //mPlayer.setLooping(true);

        mVisualizer = new Visualizer(mPlayer.getAudioSessionId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mVisualizer.setMeasurementMode(Visualizer.MEASUREMENT_MODE_PEAK_RMS);
            mVisualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
        }

        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                        if (mVisualizer != null && mVisualizer.getEnabled()) {
                            if (bytes[0] != 0 && bytes[1] != 0 && bytes[2] != 0){
                                mVisualizerView.updateFFT(bytes);
                            }
                        }
                    }
                },
                Visualizer.getMaxCaptureRate(), false, true);

        mVisualizer.setEnabled(true);
        //mPlayer.start();
    }

    public void shutdown(boolean isFinishing) {

        if (mPlayer != null) {
            mPlayer.pause();
            if (isFinishing) {
                mVisualizer.release();
                mPlayer.release();
                mPlayer = null;
                mVisualizer = null;
            }
        }

        if (mVisualizer != null) {
            mVisualizer.setEnabled(false);
        }
    }

    public void restart() {

        if (mPlayer != null) {
            mPlayer.start();
        }

        mVisualizer.setEnabled(true);
        mVisualizerView.restart();
    }
}
