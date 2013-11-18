package com.hdm.crowdmusic.core.streaming;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;

import com.hdm.crowdmusic.util.Utility;

import java.net.InetAddress;

public class HTTPServerService extends Service {
    private HTTPServer server;
    private HTTPBinder binder = new HTTPBinder();

    @Override
    public void onCreate() {
        super.onCreate();

        final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        InetAddress ip = Utility.getWifiInetAddress(wifiManager);

        server = new HTTPServer(8080, ip);
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
