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
import java.util.ArrayList;

public class CrowdMusicServer {

    public static DeviceIdentity CROWD_MUSIC_SERVER_IDENTITY = new DeviceIdentity(UDN.uniqueSystemIdentifier("CrowdMusicServer"));
    private LocalDevice localDevice;

    private String serverIP;
    private ArrayList<String> registeredClients;

    public CrowdMusicServer(String serverIP) {
        this.serverIP = serverIP;

        try {
            this.localDevice = createDevice();
        } catch (ValidationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    LocalDevice createDevice() throws ValidationException, LocalServiceBindingException, IOException {
        DeviceIdentity identity = CROWD_MUSIC_SERVER_IDENTITY;

        DeviceType type =
                new UDADeviceType("CMSDevice", 1);

        DeviceDetails details =
                new DeviceDetails(
                        "CrowdMusicServer " + android.os.Build.MODEL,
                        new ManufacturerDetails("HdM"),
                        new ModelDetails(
                                "CrowdMusic",
                                "Playlist for a crowd.",
                                serverIP
                        )
                );

        LocalService<CrowdMusicUPnPService> crowdMusicService = new AnnotationLocalServiceBinder().read(CrowdMusicUPnPService.class);
        crowdMusicService.setManager(new DefaultServiceManager(crowdMusicService, CrowdMusicUPnPService.class));

        return new LocalDevice(identity, type, details, crowdMusicService);
    }

    public String getServerIP() {
        return serverIP;
    }
    public ArrayList<String> getClientList() { return registeredClients; } //TODO: Return a copy, not the real one
    public LocalDevice getLocalDevice() {
        return localDevice;
    }

    public void registerClient(String clientIP) {
        if (!registeredClients.contains(clientIP)) {
            registeredClients.add(clientIP);
        }
    }
    public void unregisterClient(String clientIP) {
        if (registeredClients.contains(clientIP)) {
            registeredClients.remove(clientIP);
        }
    }

    public void notifyAllClients() {}
}
