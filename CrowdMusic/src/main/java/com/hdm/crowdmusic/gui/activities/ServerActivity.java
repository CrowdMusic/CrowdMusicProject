package com.hdm.crowdmusic.gui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.*;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.CrowdMusicServer;
import com.hdm.crowdmusic.core.CrowdMusicTrack;
import com.hdm.crowdmusic.core.streaming.HTTPServerService;
import com.hdm.crowdmusic.core.streaming.IHttpServerService;
import com.hdm.crowdmusic.core.streaming.IMediaPlayerService;
import com.hdm.crowdmusic.core.streaming.MediaPlayerService;
import com.hdm.crowdmusic.core.streaming.actions.CrowdMusicHandler;
import com.hdm.crowdmusic.core.streaming.actions.Executable;
import com.hdm.crowdmusic.core.streaming.actions.Vote;
import com.hdm.crowdmusic.gui.fragments.ServerAdminUsersFragment;
import com.hdm.crowdmusic.gui.fragments.ServerPlaylistFragment;
import com.hdm.crowdmusic.gui.support.IOnServerRequestListener;
import com.hdm.crowdmusic.gui.support.TabListener;
import com.hdm.crowdmusic.util.Constants;
import com.hdm.crowdmusic.util.Utility;
import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.android.AndroidUpnpServiceImpl;
import org.teleal.cling.registry.RegistrationException;

import static com.hdm.crowdmusic.R.id;
import static com.hdm.crowdmusic.R.layout;

public class ServerActivity extends Activity implements IOnServerRequestListener {

    private CrowdMusicServer crowdMusicServer;
    private AndroidUpnpService upnpService;
    private IHttpServerService httpServerService;
    private IMediaPlayerService mediaService;

    private ServiceConnection upnpServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            upnpService = (AndroidUpnpService) service;

            try {
                upnpService.getRegistry().addDevice(crowdMusicServer.getLocalDevice());
            } catch (RegistrationException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            upnpService = null;
        }
    };
    private ServiceConnection mediaServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(Utility.LOG_TAG_MEDIA, "MediaPlayerService connected.");
            mediaService = (IMediaPlayerService) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(Utility.LOG_TAG_MEDIA, "MediaPlayerService disconnected.");
            mediaService = null;
        }
    };
    private ServiceConnection httpServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(Utility.LOG_TAG_MEDIA, "httpServerService connected.");
            httpServerService = (IHttpServerService) service;

            httpServerService.registerHandler("/track/post", new CrowdMusicHandler<CrowdMusicTrack>(new Executable<CrowdMusicTrack>() {
                @Override
                public void execute(CrowdMusicTrack postData) {
                    getServerData().getPlaylist().addTrack(postData);
                }
            }));

            httpServerService.registerHandler("/vote/up", new CrowdMusicHandler<Vote>(new Executable<Vote>() {
                @Override
                public void execute(final Vote postData) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getServerData().getPlaylist().upvote(postData.getTrackId(), postData.getIp());
                        }
                    });
                }
            }));
            httpServerService.registerHandler("/vote/down", new CrowdMusicHandler<Vote>(new Executable<Vote>() {
                @Override
                public void execute(final Vote postData) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getServerData().getPlaylist().downvote(postData.getTrackId(), postData.getIp());
                        }
                    });
                }
            }));

            httpServerService.registerHandler("/register", new CrowdMusicHandler<String>(new Executable<String>() {
                @Override
                public void execute(final String postData) {
                    Handler mainHandler = new Handler(getApplicationContext().getMainLooper());
                    mainHandler.post(

                    new Runnable() {
                        @Override
                        public void run() {
                            getServerData().registerClient(postData);
                            // TODO: Notify only the one new client
                            getServerData().notifyAllClients();
                        }
                    });
                }
            }));
            httpServerService.registerHandler("/unregister", new CrowdMusicHandler<String>(new Executable<String>() {
                @Override
                public void execute(final String postData) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getServerData().unregisterClient(postData);
                        }
                    });
                }
            }));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(Utility.LOG_TAG_MEDIA, "httpServerService disconnected.");
            httpServerService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(layout.activity_createserver);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(id.container, new PlaceholderFragment())
                    .commit();
        }

        final ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(1, ActionBar.DISPLAY_SHOW_TITLE);

        bar.addTab(bar.newTab()
                .setText("Playlist")
                .setTabListener(new TabListener<ServerPlaylistFragment>(
                        this, "playlist", ServerPlaylistFragment.class)));


        bar.addTab(bar.newTab()
                .setText("Users")
                .setTabListener(new TabListener<ServerAdminUsersFragment>(
                        this, "admin", ServerAdminUsersFragment.class)));

        setupCrowdMusicServer();

        getApplicationContext().bindService(
                new Intent(this, AndroidUpnpServiceImpl.class),
                upnpServiceConnection,
                Context.BIND_AUTO_CREATE
        );

        getApplicationContext().bindService(
                new Intent(this, MediaPlayerService.class),
                mediaServiceConnection,
                Context.BIND_AUTO_CREATE
        );

        String clientIP = Utility.getWifiIpAddress();

        Intent httpIntent = new Intent(this, HTTPServerService.class);
        httpIntent.putExtra("ip", clientIP);
        httpIntent.putExtra("port", Constants.PORT);

        getApplicationContext().bindService(
                httpIntent,
                httpServiceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_server, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case id.action_settings:
                return true;

//          Streaming Test, throw in your own data to test!
            case id.action_play_pause:
                Log.i(Utility.LOG_TAG_HTTP, "Trying to stream audio...");

                if (! mediaService.hasTrack())
                {
                    CrowdMusicTrack track = getServerData().getPlaylist().getNextTrack();
                    if (track != null) {
                        mediaService.play(Utility.buildURL(track));
                    }
                }else
                {
                    mediaService.playPause();
                }
                return true;
            case id.action_next_track:
                CrowdMusicTrack track = getServerData().getPlaylist().getNextTrack();
                if (track != null) {
                    mediaService.play(Utility.buildURL(track));
                }
                return true;
            case id.action_previous_track:
                mediaService.restartCurrentTrack();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    private void setupCrowdMusicServer() {
        String ip = Utility.getWifiIpAddress();
        if (ip != null) {
            crowdMusicServer = new CrowdMusicServer(ip);
        }
    }

    //TODO: Return copy instead of the real thing
    @Override
    public CrowdMusicServer getServerData() {
        return crowdMusicServer;
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(layout.fragment_createserver, container, false);
            return rootView;
        }
    }
}


