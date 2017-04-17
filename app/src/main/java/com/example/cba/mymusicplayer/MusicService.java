package com.example.cba.mymusicplayer;


import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import static android.R.id.progress;


/**
 * Created by cba on 2017-04-02.
 */

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    public MediaPlayer player;
    private ArrayList<Song> songs;
    private int songPosn;
    private final IBinder musicBind = new MusicBinder();
    private boolean shuffle=false;
    private Random rand;

    public void onCreate(){
        //create the service
        super.onCreate();
        songPosn=0;
        player = new MediaPlayer();
        initMusicPlayer();
        player.setOnCompletionListener(this);
    }

    public void initMusicPlayer(){
        //definiera spelarens egenskaper
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void playSong(){
        //play a song
        player.reset();
        //get song
        Song playSong = songs.get(songPosn);
        //get id
        long currSong = playSong.getID();

        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    public void resumeSong(){
        player.start();
    }

    public void pauseSong(){
        player.pause();
    }

    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    public void getDuration(){
        player.getDuration();
    }

    public void seekTo(){
        player.seekTo(progress*1000);
    }

    public void setShuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }
    public void playNext(){
        if(shuffle){
            int newSong = songPosn;
            while(newSong==songPosn){
                newSong=rand.nextInt(songs.size());
            }
            songPosn=newSong;
        }
        else{
            songPosn++;
            if(songPosn >=songs.size()) songPosn=0;
        }
        playSong();
    }

    public void playPrevious(){
        songPosn --;
        playSong();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer player) {
        if(player.getCurrentPosition() > 0){
            player.reset();
            playNext();
        }

    }

    @Override
    public boolean onError(MediaPlayer player, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer player) {
        //börja "playback"
        player.start();
    }
}
