package com.hdm.crowdmusic.core.streaming;

import android.net.Uri;

public interface IMediaPlayerService {
    public void play(Uri uri);
    public void play(String url);
    public void stop();
    public void pause();
    public void resume();
}
