package com.hdm.crowdmusic.core.streaming;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MediaPlayerService extends Service{
    //TODO: Stream and play audio files in the background
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
