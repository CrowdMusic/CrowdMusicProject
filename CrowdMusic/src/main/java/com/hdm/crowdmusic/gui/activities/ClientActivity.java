package com.hdm.crowdmusic.gui.activities;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.CrowdMusicClient;
import com.hdm.crowdmusic.core.CrowdMusicTrack;
import com.hdm.crowdmusic.core.devicelistener.CrowdDevicesBrowser;
import com.hdm.crowdmusic.core.streaming.AudioRequestHandler;
import com.hdm.crowdmusic.core.streaming.HTTPServerService;
import com.hdm.crowdmusic.core.streaming.IHttpServerService;
import com.hdm.crowdmusic.gui.support.PlaylistTrackAdapter;
import com.hdm.crowdmusic.util.Utility;
import org.teleal.cling.registry.RegistryListener;

public class ClientActivity extends ListActivity {

    private IHttpServerService httpService;
    private RegistryListener registryListener;
    private CrowdMusicClient crowdMusicClient;
    ArrayAdapter<CrowdMusicTrack> listAdapter;

    String serverIP;

    private ServiceConnection httpServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            httpService = (IHttpServerService) service;

            httpService.registerHandler("/audio/*", new AudioRequestHandler(getApplicationContext()));
        }

        public void onServiceDisconnected(ComponentName className) {
            httpService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listAdapter =  new ArrayAdapter(this, R.layout.fragment_client_serverbrowser);
        setListAdapter(listAdapter);

        registryListener = new CrowdDevicesBrowser(this, listAdapter);


        Intent lastIntent = getIntent();
        serverIP = lastIntent.getStringExtra("ip");

        final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        String ip = Utility.getWifiInetAddress(wifiManager).getHostAddress();
        int port = 8080;

        crowdMusicClient = new CrowdMusicClient(getApplicationContext(), ip);

        Intent httpIntent = new Intent(this, HTTPServerService.class);
        httpIntent.putExtra("ip", ip);
        httpIntent.putExtra("port", port);

        getApplicationContext().bindService(
                httpIntent,
                httpServiceConnection,
                Context.BIND_AUTO_CREATE
        );

        listAdapter = new PlaylistTrackAdapter(this,
                android.R.layout.simple_list_item_1, crowdMusicClient.getTrackList());
        setListAdapter(listAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        crowdMusicClient.init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.server, menu);
        return true;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final CrowdMusicTrack selectedTrack = (CrowdMusicTrack) listAdapter.getItem(position);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), selectedTrack.getTrackName(), 2).show();
            }
        });
    }
}
