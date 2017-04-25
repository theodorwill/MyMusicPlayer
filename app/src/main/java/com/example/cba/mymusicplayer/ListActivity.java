package com.example.cba.mymusicplayer;
import android.app.Activity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.ListView;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.View;

public class ListActivity extends Activity {

    private ArrayList<Song> songList;
    private ListView songView;
    public MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;

    //binder aktivitet till musicservice
    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songlist);

        songView = (ListView)findViewById(R.id.song_list);

        songList = new ArrayList<Song>();

        //Kalla på låtlistan
        getSongList();
        //Sortera låtarna i alfabetisk ordning
        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        //sätter songAdaptern
        SongAdapter songAdt = new SongAdapter(this, songList);
        songView.setAdapter(songAdt);
    }

    //"Connect:ar" till musicservice
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    //skaffa låt informationen
    public void getSongList(){

        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        //Startar en loop för cursorn så den letar upp alla filer
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //hämta kolumner
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //addera låtar till listan
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
    }
    //när en låt är vald så körs musicservice och sedan går den till nästa aktivitet
    public void songPicked(View view){
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
        startActivity(new Intent(getApplicationContext(), MusicPlayer.class));
    }
    //intent för tillbaka knappen i ListActivity
    public void goBack(View v){
        startActivity(new Intent(getApplicationContext(), MusicPlayer.class));
    }
}
