package com.example.cba.mymusicplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by cba on 2017-04-09.
 */

public class MusicPlayer extends MainActivity implements SeekBar.OnSeekBarChangeListener {

    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private SeekBar songProgressBar;
    private Button btnNextSong;
    private Button btnPreviousSong;
    Handler mHandler = new Handler();
    private Utilities utils;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        songProgressBar = (SeekBar) findViewById(R.id.songTimer);
        songCurrentDurationLabel = (TextView) findViewById(R.id.currentTime);
        songTotalDurationLabel = (TextView) findViewById(R.id.endTime);
        btnNextSong = (Button) findViewById(R.id.skipForward);
        btnPreviousSong = (Button) findViewById(R.id.skipBack);

        utils = new Utilities();
        updateProgressBar();

        songProgressBar.setOnSeekBarChangeListener(this);

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

        btnNextSong.setOnClickListener(new Button.OnClickListener(){

            public void onClick(View v) {
                musicSrv.playNext();
            }
        });

        btnPreviousSong.setOnClickListener(new Button.OnClickListener(){

            public void onClick(View v) {
                musicSrv.playPrevious();
            }
        });


    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = musicSrv.player.getDuration();
            long currentDuration = musicSrv.player.getCurrentPosition();

            // Displaying Total Duration time
            songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    /**
     *
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = musicSrv.player.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        musicSrv.player.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

}
