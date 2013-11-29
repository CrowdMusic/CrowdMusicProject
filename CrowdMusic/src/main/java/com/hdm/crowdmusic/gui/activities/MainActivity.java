package com.hdm.crowdmusic.gui.activities;


import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;


import android.os.IBinder;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.CrowdMusicClient;
import com.hdm.crowdmusic.core.devicelistener.AllDevicesBrowser;
import com.hdm.crowdmusic.core.devicelistener.CrowdDevicesBrowser;
import com.hdm.crowdmusic.core.streaming.HTTPServerService;
import com.hdm.crowdmusic.core.streaming.IHttpServerService;


import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.android.AndroidUpnpServiceImpl;
import org.teleal.cling.registry.RegistryListener;





public class MainActivity extends ListActivity {

    private AndroidUpnpService upnpService;
    private IHttpServerService httpService;
    private RegistryListener registryListener;
    private CrowdMusicClient crowdMusicClient;
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
        }

        public void onServiceDisconnected(ComponentName className) {
            httpService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //crowdMusicClient = new CrowdMusicClient();

        listAdapter =  new ArrayAdapter(this, R.layout.fragment_client_serverbrowser);
        setListAdapter(listAdapter);
        //registryListener = new AllDevicesBrowser(this, listAdapter);
        registryListener = new CrowdDevicesBrowser(this, listAdapter);

        getApplicationContext().bindService(
                new Intent(this, AndroidUpnpServiceImpl.class),
                upnpServiceConntection,
                Context.BIND_AUTO_CREATE
        );

        getApplicationContext().bindService(
                new Intent(this, HTTPServerService.class),
                httpServiceConnection,
                Context.BIND_AUTO_CREATE
        );

        setContentView(R.layout.activity_main);

    }

    public void startServer(View view) {
        Intent intent = new Intent(this, ServerActivity.class);
        startActivity(intent);
    }
    public void startClient(View view) {
        Intent intent = new Intent(this, ClientActivity.class);
        startActivity(intent);
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

}
