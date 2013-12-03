package com.hdm.crowdmusic.gui.activities;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.CrowdMusicClient;
import com.hdm.crowdmusic.core.devicelistener.AllDevicesBrowser;
import com.hdm.crowdmusic.core.devicelistener.CrowdDevicesBrowser;
import com.hdm.crowdmusic.core.devicelistener.DeviceDisplay;
import com.hdm.crowdmusic.core.streaming.HTTPServerService;
import com.hdm.crowdmusic.core.streaming.IHttpServerService;
import com.hdm.crowdmusic.util.Utility;

import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.android.AndroidUpnpServiceImpl;
import org.teleal.cling.registry.RegistryListener;

import java.io.IOException;

public class ClientActivity extends ListActivity {

    private IHttpServerService httpService;
    private RegistryListener registryListener;
    private CrowdMusicClient crowdMusicClient;
    ArrayAdapter listAdapter;

    private ServiceConnection httpServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            httpService = (IHttpServerService) service;
        }

        public void onServiceDisconnected(ComponentName className) {
            httpService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        crowdMusicClient = new CrowdMusicClient(getApplicationContext());

        listAdapter =  new ArrayAdapter(this, R.layout.fragment_client_serverbrowser);
        setListAdapter(listAdapter);

        registryListener = new CrowdDevicesBrowser(this, listAdapter);


        Intent lastIntent = getIntent();
        String ip = lastIntent.getStringExtra("ip");
        int port = 8080;

        Intent httpIntent = new Intent(this, HTTPServerService.class);
        httpIntent.putExtra("ip", ip);
        httpIntent.putExtra("port", port);

        getApplicationContext().bindService(
                httpIntent,
                httpServiceConnection,
                Context.BIND_AUTO_CREATE
        );
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
        DeviceDisplay selectedDeviceDisplay = (DeviceDisplay) listAdapter.getItem(position);
        final String deviceDetails = selectedDeviceDisplay.getDevice().getDetails().getFriendlyName();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), deviceDetails, 2).show();
            }
        });
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_client_serverbrowser, container, false);
            return rootView;
        }
    }
}
