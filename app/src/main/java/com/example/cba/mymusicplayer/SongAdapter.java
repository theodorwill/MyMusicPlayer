package com.example.cba.mymusicplayer;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by TW on 2017-04-09.
 */

public class SongAdapter extends BaseAdapter {

    private ArrayList<Song> songs;
    private LayoutInflater songInf;

    public SongAdapter(Context c, ArrayList<Song> theSongs){
        songs=theSongs;
        songInf=LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //addera till song layout
        LinearLayout songLay = (LinearLayout)songInf.inflate
                (R.layout.song, parent, false);
        //Skaffa titel och artis vy
        TextView songView = (TextView)songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
        //skaffa song position
        Song currSong = songs.get(position);
        //skaffa titel och artist strängar
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());
        //sätt positionen till en tag
        songLay.setTag(position);
        return songLay;
    }
}
