package com.hdm.crowdmusic.core.devicelistener;

import android.app.Activity;
import android.widget.ArrayAdapter;
import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.model.meta.Device;

public class DevicesBrowser extends AllDevicesBrowser {

    public DevicesBrowser(Activity hostActivity, ArrayAdapter listAdapter) {
        super(hostActivity, listAdapter);
    }

    @Override
    public void refresh(AndroidUpnpService upnpService) {
        listAdapter.clear();
        for (Device device : upnpService.getRegistry().getDevices()) {
            // Temp disabled, because the check is done in the alldevicesbrowser (line 76) already..
            // maybe TODO: Change this fuckup. But it works.
            //if (device.getIdentity().getUdn().getIdentifierString().substring(25).equals(Server.CROWD_MUSIC_SERVER_IDENTITY.getUdn().getIdentifierString().substring(25))) {
            if(true) {
                deviceAdded(device);
            }
        }
    }
}
