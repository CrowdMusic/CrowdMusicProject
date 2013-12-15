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
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.devicelistener.AllDevicesBrowser;
import com.hdm.crowdmusic.core.devicelistener.CrowdDevicesBrowser;
import com.hdm.crowdmusic.core.devicelistener.DeviceDisplay;
import com.hdm.crowdmusic.core.network.AccessPoint;
import com.hdm.crowdmusic.core.streaming.*;
import com.hdm.crowdmusic.util.Utility;
import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.android.AndroidUpnpServiceImpl;
import org.teleal.cling.registry.RegistryListener;

public class MainActivity extends ListActivity {

    private final int PORT = 8080;
    private String ip;

    private AndroidUpnpService upnpService;
    private IHttpServerService httpService;
    private RegistryListener registryListener;
    ArrayAdapter listAdapter;

    private ServiceConnection upnpServiceConntection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            upnpService = (AndroidUpnpService) service;

            // Refresh the list with all known devices
            ((AllDevicesBrowser) registryListener).refresh(upnpService);

            // Getting ready for future device advertisements
            upnpService.getRegistry().addListener(registryListener);

            // Search asynchronously for all devices
            upnpService.getControlPoint().search();
        }

        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
        }
    };

    private ServiceConnection httpServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            httpService = (IHttpServerService) service;

            httpService.registerHandler("/audio/*", new AudioRequestHandler(getApplicationContext()));
            httpService.registerHandler("/", new PostAudioHandler(getApplicationContext()));
            httpService.registerHandler("/vote/*", new PostVotingHandler(getApplicationContext()));
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

        final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        ip = Utility.getWifiInetAddress(wifiManager).getHostAddress();

        getApplicationContext().bindService(
                new Intent(this, AndroidUpnpServiceImpl.class),
                upnpServiceConntection,
                Context.BIND_AUTO_CREATE
        );

        Intent httpIntent = new Intent(this, HTTPServerService.class);
        httpIntent.putExtra("ip", ip);
        httpIntent.putExtra("port", PORT);

        getApplicationContext().bindService(
                httpIntent,
                httpServiceConnection,
                Context.BIND_AUTO_CREATE
        );

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        DeviceDisplay selectedDeviceDisplay = (DeviceDisplay) listAdapter.getItem(position);
        final String deviceDetails = selectedDeviceDisplay.getDevice().getDetails().getModelDetails().getModelNumber();

        Intent clientIntent = new Intent(this, ClientActivity.class);
        clientIntent.putExtra("clientIP", ip);                  //IP des Geräts
        clientIntent.putExtra("serverIP", deviceDetails);       //IP des Servers
        clientIntent.putExtra("port", PORT);
        startActivity(clientIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startServer(View view) {
        AccessPoint.setApDialogShown(false);
        Intent intent = new Intent(this, ServerActivity.class);
        startActivity(intent);
    }
}
