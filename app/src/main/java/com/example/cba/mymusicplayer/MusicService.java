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
 * Created by TW on 2017-04-02.
 */

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    public MediaPlayer player;
    private ArrayList<Song> songs;
    private int songPosn;
    private final IBinder musicBind = new MusicBinder();
    private boolean shuffle = false;
    private boolean repeat = false;

    public void onCreate(){
        //skapa service
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
    //sätter låtlistan till arrayen
    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
    //spela en låt
    public void playSong(){
        //återställ spelaren så att den inte knasar
        player.reset();
        //skaffa låt position av nuvarande låt
        Song playSong = songs.get(songPosn);
        /**
         * Skaffa låt ID av nuvarande låt (borde också kunna hämta
         * titel samt artist men jag vet inte hur jag ska fästa det
         * på en textview i en annan vy)
         */
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

    /**
     * från och med nu är det ganska uppenbart vad metoderna gör
     */

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

    public void shuffleOn(){
        shuffle = true;
    }

    public void repeatOn(){
        repeat = true;
    }

    public void resetPlayer(){
        shuffle = false;
        repeat = false;
        player.setLooping(false);
    }

    public void playNext(){
        if(shuffle){
            int newSong = songPosn;
            Random rand = new Random();
            while(newSong == songPosn){
                newSong = rand.nextInt(songs.size());
            }
            songPosn=newSong;
        }else if(repeat){
            player.setLooping(true);
            player.reset();
        }
        else{
            songPosn++;
            if(songPosn >=songs.size()) songPosn=0;
        }
        playSong();
    }

    public void playPrevious() {
        if (songPosn > 0) {
            songPosn--;
            playSong();
        }
        else {
            return;
        }
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
        player.reset();
        return false;

    }

    @Override
    public void onPrepared(MediaPlayer player) {
        //börja "playback"
        player.start();
    }
}
