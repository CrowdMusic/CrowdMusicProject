package com.hdm.crowdmusic.core.streaming;

import android.media.MediaPlayer;
import android.net.Uri;

public interface IMediaPlayerService {
    public void play(Uri uri);
    public void play(String url);
    public void stop();
    public void pause();
    public void resume();
    public boolean isPlaying();
    public boolean hasTrack();
    public void playPause();
    public void restartCurrentTrack();
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener);
}
