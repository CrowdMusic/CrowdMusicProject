package com.hdm.crowdmusic.gui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.Server;
import com.hdm.crowdmusic.core.Track;
import com.hdm.crowdmusic.core.streaming.HTTPServerService;
import com.hdm.crowdmusic.core.streaming.IHttpServerService;
import com.hdm.crowdmusic.core.streaming.IMediaPlayerService;
import com.hdm.crowdmusic.core.streaming.MediaPlayerService;
import com.hdm.crowdmusic.core.streaming.actions.Handler;
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

public class ServerActivity extends Activity implements IOnServerRequestListener, MediaPlayer.OnCompletionListener{

    private Server server;
    private AndroidUpnpService upnpService;
    private IHttpServerService httpServerService;
    private IMediaPlayerService mediaService;


    private ServiceConnection upnpServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            upnpService = (AndroidUpnpService) service;

            try {
                upnpService.getRegistry().addDevice(server.getLocalDevice());
            } catch (RegistrationException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            upnpService = null;
        }
    };
    private ServiceConnection mediaServiceConnection;
    private ServiceConnection httpServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(Utility.LOG_TAG_MEDIA, "httpServerService connected.");
            httpServerService = (IHttpServerService) service;

            httpServerService.registerHandler("/track/post", new Handler<Track>(new Executable<Track>() {
                @Override
                public void execute(Track postData) {
                    getServerData().getPlaylist().addTrack(postData);
                }
            }));

            httpServerService.registerHandler("/vote/up", new Handler<Vote>(new Executable<Vote>() {
                @Override
                public void execute(final Vote postData) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getServerData().getPlaylist().upvote(postData.getTrackId(), postData.getIp());
                            getServerData().notifyAllClients();
                        }
                    });
                }
            }));
            httpServerService.registerHandler("/vote/down", new Handler<Vote>(new Executable<Vote>() {
                @Override
                public void execute(final Vote postData) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getServerData().getPlaylist().downvote(postData.getTrackId(), postData.getIp());
                            getServerData().notifyAllClients();
                        }
                    });
                }
            }));

            httpServerService.registerHandler("/register", new Handler<String>(
                new Executable<String>() {
                @Override
                public void execute(final String postData) {
                    android.os.Handler mainHandler = new android.os.Handler(getApplicationContext().getMainLooper());
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
            httpServerService.registerHandler("/unregister", new Handler<String>(new Executable<String>() {
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

        final ServerActivity temp = this;

        mediaServiceConnection= new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i(Utility.LOG_TAG_MEDIA, "MediaPlayerService connected.");
                mediaService = (IMediaPlayerService) service;
                mediaService.setOnCompletionListener(temp);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(Utility.LOG_TAG_MEDIA, "MediaPlayerService disconnected.");
                mediaService = null;
            }
        };

        setupCrowdMusicServer();
        setContentView(layout.activity_createserver);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(id.container, new ServerPlaylistFragment())
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
                .setTabListener(new TabListener<ServerAdminUsersFragment>(this, "admin",
                        ServerAdminUsersFragment.class)));


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
        notifyServerview();
    }

    private void notifyServerview() {
        getServerData().notifyServerview();
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
                    Track track = getServerData().getPlaylist().getNextTrack();
                    if (track != null) {
                        mediaService.play(Utility.buildURL(track));
                    }
                }else
                {
                    mediaService.playPause();
                }
                return true;
            case id.action_next_track:
                Track track = getServerData().getPlaylist().getNextTrack();
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
            server = new Server(this, ip, this);
        }
    }

    //TODO: Return copy instead of the real thing
    @Override
    public Server getServerData() {
        return server;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Track track = getServerData().getPlaylist().getNextTrack();
        if (track != null) {
            mediaService.play(Utility.buildURL(track));
        }
    }
}


