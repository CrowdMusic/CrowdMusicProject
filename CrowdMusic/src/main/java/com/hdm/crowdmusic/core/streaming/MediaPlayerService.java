package com.hdm.crowdmusic.core.streaming;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class MediaPlayerService extends Service{
    //TODO: Stream and play audio files in the background
    private MediaPlayerBinder binder = new MediaPlayerBinder();

    MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private class MediaPlayerBinder extends Binder implements IMediaPlayerService {
        @Override
        public void play() {
            
        }

        @Override
        public void stop() {

        }

        @Override
        public void pause() {

        }
    }
}
