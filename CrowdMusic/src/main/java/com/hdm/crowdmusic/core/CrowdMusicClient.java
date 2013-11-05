package com.hdm.crowdmusic.core;

import org.teleal.cling.binding.LocalServiceBindingException;
import org.teleal.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.teleal.cling.model.DefaultServiceManager;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.*;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDN;

import java.io.IOException;

public class CrowdMusicClient {
    private LocalDevice localDevice;

    public LocalDevice getLocalDevice() {
        return localDevice;
    }

    public CrowdMusicClient() {
        try {
            this.localDevice = createDevice();
        } catch (ValidationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    LocalDevice createDevice() throws ValidationException, LocalServiceBindingException, IOException {
        DeviceIdentity identity =
                new DeviceIdentity(
                        UDN.uniqueSystemIdentifier("CrowdMusicClient")
                );

        DeviceType type =
                new UDADeviceType("CMCDevice", 1);

        DeviceDetails details =
                new DeviceDetails(
                        "CrowdMusicClient",
                        new ManufacturerDetails("HdM"),
                        new ModelDetails(
                                "CrowdMusic",
                                "Playlist for a crowd.",
                                "v1"
                        )
                );

        LocalService<CrowdMusicServerService> crowdMusicService = new AnnotationLocalServiceBinder().read(CrowdMusicClientService.class);
        crowdMusicService.setManager(new DefaultServiceManager(crowdMusicService, CrowdMusicServerService.class));

        return new LocalDevice(identity, type, details, crowdMusicService);
    }
}
