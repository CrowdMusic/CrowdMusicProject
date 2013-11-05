package com.hdm.crowdmusic.activities;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
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
import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.android.AndroidUpnpServiceImpl;
import org.teleal.cling.registry.RegistryListener;

public class ClientActivity extends ListActivity {

    private AndroidUpnpService clientService;
    private RegistryListener registryListener;
    private CrowdMusicClient crowdMusicClient;
    ArrayAdapter listAdapter;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            clientService = (AndroidUpnpService) service;

            // Refresh the list with all known devices
            ((AllDevicesBrowser) registryListener).refresh(clientService);

            // Getting ready for future device advertisements
            clientService.getRegistry().addListener(registryListener);

            // Search asynchronously for all devices
            clientService.getControlPoint().search();
        }

        public void onServiceDisconnected(ComponentName className) {
            clientService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        crowdMusicClient = new CrowdMusicClient();

        listAdapter =  new ArrayAdapter(this, R.layout.fragment_client_serverbrowser);
        setListAdapter(listAdapter);
        //registryListener = new AllDevicesBrowser(this, listAdapter);
        registryListener = new CrowdDevicesBrowser(this, listAdapter);

        getApplicationContext().bindService(
                new Intent(this, AndroidUpnpServiceImpl.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        );
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
        final String deviceDetails = selectedDeviceDisplay.getDevice().getDisplayString();
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
