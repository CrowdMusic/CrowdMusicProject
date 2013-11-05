package com.hdm.crowdmusic.core.devicelistener;

import android.app.ListActivity;
import android.widget.ArrayAdapter;
import com.hdm.crowdmusic.core.CrowdMusicServer;
import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.model.meta.Device;

/**
 * Created by Hanno on 05.11.13.
 */
public class CrowdDevicesBrowser extends AllDevicesBrowser {

    public CrowdDevicesBrowser(ListActivity hostActivity, ArrayAdapter listAdapter) {
        super(hostActivity, listAdapter);
    }

    @Override
    public void refresh(AndroidUpnpService upnpService) {
        listAdapter.clear();
        for (Device device : upnpService.getRegistry().getDevices()) {
            if (device.getIdentity().equals(CrowdMusicServer.CROWDMUSICSERVERIDENTITY)) {
                deviceAdded(device);
            }
        }
    }
}
