package com.hdm.crowdmusic.core.streaming;

import org.apache.http.protocol.HttpRequestHandler;

public interface IHttpServerService {
    public void registerHandler(String pattern, HttpRequestHandler handler);
    public void unregisterHandler(String pattern);
    public int getPort();
}
