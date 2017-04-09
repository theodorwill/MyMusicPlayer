package com.example.cba.mymusicplayer;


import android.media.MediaPlayer;
import android.os.Bundle;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.ToggleButton;


/**
 * Created by cba on 2017-04-02.
 */

public class MusicPlayer extends MainActivity {

    ToggleButton playPause;
    SeekBar musicBar;
    MediaPlayer mediaPlayer;
    List<String> list;
    ListAdapter adapter;
    ListView listView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

    }

}
