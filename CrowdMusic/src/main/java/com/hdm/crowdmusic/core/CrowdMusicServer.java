package com.hdm.crowdmusic.core;

import android.app.Activity;

import com.hdm.crowdmusic.core.streaming.CrowdMusicUser;
import com.hdm.crowdmusic.core.streaming.CrowdMusicUserList;
import com.hdm.crowdmusic.core.streaming.actions.CrowdMusicTracklist;
import com.hdm.crowdmusic.core.streaming.actions.ICrowdMusicAction;
import com.hdm.crowdmusic.core.streaming.actions.SimplePostTask;
import com.hdm.crowdmusic.util.Constants;
import org.teleal.cling.binding.LocalServiceBindingException;
import org.teleal.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.teleal.cling.model.DefaultServiceManager;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.*;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDN;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.List;


public class CrowdMusicServer {

    public static DeviceIdentity CROWD_MUSIC_SERVER_IDENTITY = new DeviceIdentity(UDN.uniqueSystemIdentifier("CrowdMusicServer"));
    private LocalDevice localDevice;

    private CrowdMusicPlaylist playlist;

    private String serverIP;
    private CrowdMusicUserList userList;
    private final PropertyChangeSupport pcs;

    public CrowdMusicServer(String serverIP, Activity parentActivity) {
        this.serverIP = serverIP;
        this.playlist = new CrowdMusicPlaylist();
        this.pcs = new PropertyChangeSupport(this);
        this.userList = new CrowdMusicUserList();
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
    public List<CrowdMusicUser> getClientList() { return userList.getUserList(); } //TODO: Return a copy, not the real one
    public LocalDevice getLocalDevice() {
        return localDevice;
    }

    public void registerClient(String clientIP) {
        CrowdMusicUser user = new CrowdMusicUser(clientIP);
        userList.addUser(user); // TODO: update only when the user is new
        pcs.firePropertyChange( "users", null, userList);

    }
    public void unregisterClient(String clientIP) {

    }

    public void registerListener(PropertyChangeListener listener){
        pcs.addPropertyChangeListener(listener);
    }
    public void notifyAllClients() {
        for (CrowdMusicUser user: userList.getUserList()) {
            SimplePostTask<CrowdMusicTracklist> task = new SimplePostTask<CrowdMusicTracklist>(user.getIp(), Constants.PORT, null, null);
            task.execute(new ICrowdMusicAction<CrowdMusicTracklist>(){

                @Override
                public String getPostTarget() {
                    return "postplaylist";
                }

                @Override
                public CrowdMusicTracklist getParam() {
                    return new CrowdMusicTracklist(getPlaylist().getPlaylist());
                }
            });

        }
    }

    public CrowdMusicPlaylist getPlaylist() {
        return playlist;
    }
}
