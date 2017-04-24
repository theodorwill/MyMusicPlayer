package com.example.cba.mymusicplayer;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import static android.R.attr.description;

/**
 * Created by cba on 2017-04-09.
 */

public class MusicPlayer extends MainActivity implements SeekBar.OnSeekBarChangeListener {

    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private SeekBar songProgressBar;
    private ToggleButton btnPlayPause;
    private ToggleButton btnShuffle;
    private ToggleButton btnRepeat;
    private Button btnNextSong;
    private Button btnPreviousSong;
    Handler mHandler = new Handler();
    private Utilities utils;
    private ImageView imageArt;
    private TextView titleText;
    private TextView artistText;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        songProgressBar = (SeekBar) findViewById(R.id.songTimer);
        songCurrentDurationLabel = (TextView) findViewById(R.id.currentTime);
        songTotalDurationLabel = (TextView) findViewById(R.id.endTime);
        btnNextSong = (Button) findViewById(R.id.skipForward);
        btnPreviousSong = (Button) findViewById(R.id.skipBack);
        btnPlayPause = (ToggleButton) findViewById(R.id.playPause);
        btnRepeat = (ToggleButton) findViewById(R.id.repeat);
        btnShuffle = (ToggleButton) findViewById(R.id.shuffle);
        titleText = (TextView) findViewById(R.id.songTitle);
        artistText = (TextView) findViewById(R.id.artistName);

        utils = new Utilities();
        updateProgressBar();



        songProgressBar.setOnSeekBarChangeListener(this);

        btnPlayPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    musicSrv.resumeSong();
                } else {
                    musicSrv.pauseSong();
                    Toast.makeText(MusicPlayer.this,"Paused",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnShuffle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnRepeat.setChecked(false);
                    musicSrv.resetPlayer();
                    musicSrv.shuffleOn();
                    Toast.makeText(MusicPlayer.this,"Shuffle on",
                            Toast.LENGTH_SHORT).show();
                } else {
                    musicSrv.resetPlayer();
                    Toast.makeText(MusicPlayer.this,"Shuffle off",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRepeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnShuffle.setChecked(false);
                    musicSrv.resetPlayer();
                    musicSrv.repeatOn();
                    Toast.makeText(MusicPlayer.this,"Repeat song on",
                            Toast.LENGTH_SHORT).show();
                } else {
                    musicSrv.resetPlayer();
                    Toast.makeText(MusicPlayer.this,"Repeat song off",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        //knapp för att gå till nästa låt
        btnNextSong.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {
                musicSrv.playNext();
                btnPlayPause.setChecked(true);
            }
        });

        //knapp för att spela föregående låt samt gå tbx till början.
        btnPreviousSong.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {

                if (songProgressBar.getProgress() <= 0) {
                    musicSrv.playPrevious();
                    btnPlayPause.setChecked(true);
                } else {
                    musicSrv.player.seekTo(0);
                    updateProgressBar();
                    btnPlayPause.setChecked(true);
                }
            }
        });

    }

    public void onClick(View v){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
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


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    //sker när användare interagerar med SeekBar
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    //sker när användare slutar interagera med seekBar
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = musicSrv.player.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // fram eller tillbaka till olika positioner
        musicSrv.player.seekTo(currentPosition);

        // uppdatera timer
        updateProgressBar();
    }



}
