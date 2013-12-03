package com.hdm.crowdmusic.core.streaming;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.hdm.crowdmusic.util.Utility;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HTTPServerService extends Service {
    private HTTPServer server;
    private HTTPBinder binder = new HTTPBinder();

    private String ip;
    private int port;

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        server.stopServer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(Utility.LOG_TAG_HTTP, "onBind received");
        this.ip = intent.getStringExtra("ip");
        this.port = intent.getIntExtra("port", 8080);
        Log.i(Utility.LOG_TAG_HTTP, "Following parameters were transfered: " + ip + ":" + port);

        if (server == null) {
            try {
                server = new HTTPServer(port, InetAddress.getByName(ip));
                server.startServer();
            } catch (UnknownHostException e) {
                Log.e(Utility.LOG_TAG_HTTP, e.getMessage());
            }
        }

        return binder;
    }

    private class HTTPBinder extends Binder implements IHttpServerService {}
}
