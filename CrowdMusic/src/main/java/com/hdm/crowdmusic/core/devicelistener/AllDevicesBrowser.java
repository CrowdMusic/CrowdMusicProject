package com.hdm.crowdmusic.core.devicelistener;

import android.app.Activity;
import android.app.ListActivity;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;

public class AllDevicesBrowser extends DefaultRegistryListener {

    private Activity hostActivity;
    protected ArrayAdapter<DeviceDisplay> listAdapter;

    public AllDevicesBrowser(Activity hostActivity, ArrayAdapter listAdapter) {
        this.hostActivity = hostActivity;
        this.listAdapter = listAdapter;
    }

    private AllDevicesBrowser() {
    }

    public void refresh(AndroidUpnpService upnpService) {
        listAdapter.clear();
        for (Device device : upnpService.getRegistry().getDevices()) {
            deviceAdded(device);
        }
    }

    @Override
    public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
        deviceAdded(device);
    }

    @Override
    public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {
        hostActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(
                        hostActivity.getApplication(), "Discovery failed of '" + device.getDisplayString() + "': " +
                        (ex != null ? ex.toString() : "Couldn't retrieve device/service descriptors"),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
        deviceRemoved(device);
    }

    @Override
    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        deviceAdded(device);
    }

    @Override
    public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        deviceRemoved(device);
    }

    @Override
    public void localDeviceAdded(Registry registry, LocalDevice device) {
        deviceAdded(device);
    }

    @Override
    public void localDeviceRemoved(Registry registry, LocalDevice device) {
        deviceRemoved(device);
    }

    public void deviceAdded(final Device device) {
        hostActivity.runOnUiThread(new Runnable() {
            public void run() {
                DeviceDisplay d = new DeviceDisplay(device);
                if (d.toString().contains("CrowdMusic")) {
                    int position = listAdapter.getPosition(d);
                    if (position >= 0) {
                        // Device already in the list, re-set new value at same position
                        listAdapter.remove(d);
                        listAdapter.insert(d, position);
                    } else {
                        listAdapter.add(d);
                    }
                }
            }
        });
    }

    public void deviceRemoved(final Device device) {
        hostActivity.runOnUiThread(new Runnable() {
            public void run() {
                listAdapter.remove(new DeviceDisplay(device));
            }
        });
    }
}
