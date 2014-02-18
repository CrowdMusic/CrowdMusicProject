package com.hdm.crowdmusic.core.streaming.actions;

public interface IAction<T> {
    public String getPostTarget();
    public T getParam();
}
