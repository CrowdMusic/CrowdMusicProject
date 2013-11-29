package com.hdm.crowdmusic.core.streaming;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.hdm.crowdmusic.util.Utility;

import java.io.IOException;

public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener{

    private MediaPlayerBinder binder = new MediaPlayerBinder();

    MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(Utility.LOG_TAG_MEDIA, "Init media player...");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        Log.i(Utility.LOG_TAG_MEDIA, "Init media player completed.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mediaPlayer != null) mediaPlayer.release();
    }

    public void playMusic(Uri uri) {
        try {
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e(Utility.LOG_TAG_MEDIA, e.getMessage());
        }
    }

    public void pauseMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void resumeMusic() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void stopMusic() {
        mediaPlayer.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    private class MediaPlayerBinder extends Binder implements IMediaPlayerService {
        @Override
        public void play(Uri uri) {
            playMusic(uri);
        }

        @Override
        public void stop() {
            stopMusic();
        }

        @Override
        public void pause() {
            pauseMusic();
        }

        @Override
        public void resume() {
            resumeMusic();
        }
    }
}
