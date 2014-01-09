package com.hdm.crowdmusic.gui.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.devicelistener.AllDevicesBrowser;
import com.hdm.crowdmusic.core.devicelistener.CrowdDevicesBrowser;
import com.hdm.crowdmusic.core.devicelistener.DeviceDisplay;
import com.hdm.crowdmusic.core.network.AccessPoint;
import com.hdm.crowdmusic.core.streaming.HTTPServerService;
import com.hdm.crowdmusic.util.Constants;
import com.hdm.crowdmusic.util.Utility;
import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.android.AndroidUpnpServiceImpl;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.registry.RegistryListener;

public class MainActivity extends ListActivity {

    private AccessPoint accessPoint;

    private AndroidUpnpService upnpService;
    private RegistryListener registryListener;
    ArrayAdapter listAdapter;

    private ServiceConnection upnpServiceConnection = new ServiceConnection() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listAdapter =  new ArrayAdapter(this, R.layout.fragment_client_serverbrowser);
        setListAdapter(listAdapter);

        registryListener = new CrowdDevicesBrowser(this, listAdapter);

        getApplicationContext().bindService(
                new Intent(this, AndroidUpnpServiceImpl.class),
                upnpServiceConnection,
                Context.BIND_AUTO_CREATE
        );

        setContentView(R.layout.activity_main);
        switchServerButtons();
    }

    public void switchServerButtons() {
        final Button createServerButton = (Button)findViewById(R.id.button_createserver);
        final Button configureServerButton = (Button)findViewById(R.id.button_configureserver);

        if (createServerButton == null || configureServerButton == null) return;

        if (isServerStartetOnThisDevice()) {
            createServerButton.setVisibility(View.GONE);
            configureServerButton.setVisibility(View.VISIBLE);
        } else {
            createServerButton.setVisibility(View.VISIBLE);
            configureServerButton.setVisibility(View.GONE);
        }
        this.getListView().invalidate();
    }

    private boolean isServerStartetOnThisDevice() {

        for (int i = 0; i < getListAdapter().getCount(); i++){
            DeviceDisplay deviceDisplay = (DeviceDisplay) listAdapter.getItem(i);
            final String serverDeviceDetails = deviceDisplay.getDevice().getDetails().getModelDetails().getModelNumber();
            for(LocalDevice localDevice: upnpService.getRegistry().getLocalDevices()) {
                if (localDevice.getDetails().getModelDetails().getModelNumber().equals(serverDeviceDetails)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        switchServerButtons();
        super.onResume();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        DeviceDisplay selectedDeviceDisplay = (DeviceDisplay) listAdapter.getItem(position);
        final String deviceDetails = selectedDeviceDisplay.getDevice().getDetails().getModelDetails().getModelNumber();

        Intent clientIntent = new Intent(this, ClientActivity.class);
        clientIntent.putExtra("serverIP", deviceDetails);
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
        accessPoint = new AccessPoint(getApplicationContext());
        handleAPModalDialog();
        transitToServerActivity(view);
    }

    public void transitToServerActivity(View view) {
        Intent intent = new Intent(this, ServerActivity.class);
        startActivity(intent);
    }

    public void handleAPModalDialog() {

        // If the dialog was alread shown, do nothing. This is for example the case
        // when switching from landscape to portrait. See Issue 23.
        if (AccessPoint.isApDialogShown()) return;
        AccessPoint.setApDialogShown(true);

        final Activity currentActivity = this;

        if (accessPoint.isWifiConnected()) {

            DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    accessPoint.enable();
                    Toast toast = Toast.makeText(currentActivity.getApplicationContext(), R.string.dialog_create_wlan_ap_created + "\n" + R.string.server_activity_created_server, 2);
                    toast.show();
                }
            };
            DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast toast = Toast.makeText(currentActivity.getApplicationContext(), R.string.dialog_create_wlan_no_ap_created, 2);
                    toast.show();
                }
            };

            Dialog dialog = getModalDialog(this, getApplicationContext().getString(R.string.dialog_create_wlan), ok, cancel);
            dialog.show();
        } else {

            DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    accessPoint.enable();
                    Toast.makeText(getApplicationContext(), R.string.server_activity_created_server, 2).show();
                }
            };
            DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast toast = Toast.makeText(currentActivity.getApplicationContext(), R.string.dialog_create_wlan_no_ap_created, 2);
                    toast.show();
                }
            };

            Dialog dialog = getModalDialog(this, getApplicationContext().getString(R.string.dialog_create_wlan_no_wifi_enabled_or_active), ok, cancel);
            dialog.show();
        }
    }
    AlertDialog getModalDialog(final Activity currentActivity, String dialog, DialogInterface.OnClickListener ok, DialogInterface.OnClickListener cancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(dialog)
                .setTitle(R.string.dialog_title_create_wlan);


        builder.setPositiveButton(android.R.string.yes, ok);
        builder.setNegativeButton(android.R.string.no, cancel);

        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }
}
