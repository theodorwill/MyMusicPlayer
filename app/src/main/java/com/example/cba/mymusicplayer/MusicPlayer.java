package com.example.cba.mymusicplayer;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.ToggleButton;

/**
 * Created by cba on 2017-04-09.
 */

public class MusicPlayer extends MainActivity implements MediaPlayerControl {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        ToggleButton toggle = (ToggleButton) findViewById(R.id.playPause);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    musicSrv.resumeSong();
                } else {
                    musicSrv.pauseSong();
                }
            }
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int pos) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
