package com.hdm.crowdmusic.core;

import android.app.Activity;
import android.content.Context;
import com.hdm.crowdmusic.core.streaming.User;
import com.hdm.crowdmusic.core.streaming.UserList;
import com.hdm.crowdmusic.core.streaming.actions.Tracklist;
import com.hdm.crowdmusic.core.streaming.actions.IAction;
import com.hdm.crowdmusic.core.streaming.actions.IOnFailureHandler;
import com.hdm.crowdmusic.core.streaming.actions.SimplePostTask;
import com.hdm.crowdmusic.gui.fragments.ServerPlaylistFragment;
import com.hdm.crowdmusic.util.Constants;
import org.teleal.cling.binding.LocalServiceBindingException;
import org.teleal.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.teleal.cling.model.DefaultServiceManager;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.*;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDN;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Server {

    public static DeviceIdentity CROWD_MUSIC_SERVER_IDENTITY = new DeviceIdentity(UDN.uniqueSystemIdentifier("Server"));
    private LocalDevice localDevice;

    private Playlist playlist;

    private String serverIP;
    private UserList userList;
    private final PropertyChangeSupport pcs;
    private PropertyChangeListener serverView;
    private Context context;

    public Server(Context context, String serverIP, Activity parentActivity) {
        this.serverIP = serverIP;
        this.playlist = new Playlist(this);
        this.pcs = new PropertyChangeSupport(this);
        this.userList = new UserList();
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
                        "Server " + android.os.Build.MODEL,
                        new ManufacturerDetails("HdM"),
                        new ModelDetails(
                                "CrowdMusic",
                                "Playlist for a crowd.",
                                serverIP
                        )
                );

        LocalService<UPnPService> crowdMusicService = new AnnotationLocalServiceBinder().read(UPnPService.class);
        crowdMusicService.setManager(new DefaultServiceManager(crowdMusicService, UPnPService.class));

        return new LocalDevice(identity, type, details, crowdMusicService);
    }

    public String getServerIP() {
        return serverIP;
    }
    public List<User> getClientList() { return new ArrayList(userList.getUserList()); }
    public LocalDevice getLocalDevice() {
        return localDevice;
    }

    public void registerClient(String clientIP) {
        User user = new User(clientIP);
        userList.addUser(user);
        pcs.firePropertyChange( "users", null, userList);

    }

    // registers the server playlistview, because it has to be updated
    // on client actions that change the playlist
    public void registerServerview(PropertyChangeListener serverView) {
        this.serverView = serverView;
    }

    public void unregisterClient(String clientIP) {

    }

    public void registerListener(PropertyChangeListener listener){
        pcs.addPropertyChangeListener(listener);
    }
    public void notifyAllClients() {

        for (final User user: userList.getUserList()) {

            IOnFailureHandler noResponse = new IOnFailureHandler() {

                @Override
                public void execute() {
                    userList.getUserList().remove(user);
                    playlist.removeTracks(user);
                }
            };

            SimplePostTask<Tracklist> task = new SimplePostTask<Tracklist>(user.getIp(), Constants.PORT, null, noResponse);
            task.execute(new IAction<Tracklist>(){

                @Override
                public String getPostTarget() {
                    return "postplaylist";
                }

                @Override
                public Tracklist getParam() {
                    return new Tracklist(getPlaylist().getPlaylist());
                }
            });
        }
        notifyServerview();
    }


    public void notifyServerview() {
        if (this.serverView != null) {
            PropertyChangeEvent event = new PropertyChangeEvent(this, ServerPlaylistFragment.PLAYLIST_CHANGE, null, null);
            serverView.propertyChange(event);
        }
    }

    public Playlist getPlaylist() {
        return playlist;
    }

}
