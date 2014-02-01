package com.hdm.crowdmusic.core.streaming.actions;

public interface Executable<T> {
    public void execute(final T postData);
}
