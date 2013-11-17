package com.hdm.crowdmusic.core.streaming;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class HTTPServerService extends Service {
    private HTTPServer server;
    private HTTPBinder binder = new HTTPBinder();

    @Override
    public void onCreate() {
        super.onCreate();

        server = new HTTPServer();
        server.startServer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        server.stopServer();
    }

    @Override
    public IBinder onBind(Intent intent) { //TODO: Return binder to control http server configuration
        return binder;
    }

    private class HTTPBinder extends Binder implements IHttpServerService {}
}
