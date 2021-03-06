package com.hdm.crowdmusic.core.streaming;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.hdm.crowdmusic.util.Constants;
import com.hdm.crowdmusic.util.Utility;

import org.apache.http.protocol.HttpRequestHandler;

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
        server = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(Utility.LOG_TAG_HTTP, "onBind received");
        this.ip = intent.getStringExtra("ip");
        this.port = intent.getIntExtra("port", Constants.PORT);
        Log.i(Utility.LOG_TAG_HTTP, "Following parameters were transferred: " + ip + ":" + port);

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

    private void restartServer(String ip, int port) {
        if (server != null) {
            this.ip = ip;
            this.port = port;

            try {
                server = new HTTPServer(port, InetAddress.getByName(ip));
                server.startServer();
            } catch (UnknownHostException e) {
                Log.e(Utility.LOG_TAG_HTTP, e.getMessage());
            }
        } else {
            Log.e(Utility.LOG_TAG_HTTP, "Server isn't running, nothing to restart here...)");
        }
    }

    private class HTTPBinder extends Binder implements IHttpServerService {
        @Override
        public void registerHandler(String pattern, HttpRequestHandler handler) {
            server.getHandlerRegistry().register(pattern, handler);
        }

        @Override
        public void unregisterHandler(String pattern) {
            server.getHandlerRegistry().unregister(pattern);
        }

        @Override
        public void reconfigureServer(String ip, int port) {
            restartServer(ip, port);
        }
    }
}
