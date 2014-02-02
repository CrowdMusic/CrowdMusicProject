package com.hdm.crowdmusic.core.devicelistener;

import android.app.Activity;
import android.widget.ArrayAdapter;
import com.hdm.crowdmusic.core.CrowdMusicServer;
import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.model.meta.Device;

public class CrowdDevicesBrowser extends AllDevicesBrowser {

    public CrowdDevicesBrowser(Activity hostActivity, ArrayAdapter listAdapter) {
        super(hostActivity, listAdapter);
    }

    @Override
    public void refresh(AndroidUpnpService upnpService) {
        listAdapter.clear();
        for (Device device : upnpService.getRegistry().getDevices()) {
            if (device.getIdentity().equals(CrowdMusicServer.CROWD_MUSIC_SERVER_IDENTITY)) {
                deviceAdded(device);
            }
        }
    }
}
