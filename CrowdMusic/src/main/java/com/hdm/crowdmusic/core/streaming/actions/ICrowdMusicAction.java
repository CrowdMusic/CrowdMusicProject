package com.hdm.crowdmusic.core.streaming.actions;

public interface ICrowdMusicAction<T> {
    public String getPostTarget();
    public T getParam();
}
