package com.hdm.crowdmusic.gui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.Client;
import com.hdm.crowdmusic.core.Track;
import com.hdm.crowdmusic.core.streaming.AudioRequestHandler;
import com.hdm.crowdmusic.core.streaming.HTTPServerService;
import com.hdm.crowdmusic.core.streaming.IHttpServerService;
import com.hdm.crowdmusic.core.streaming.actions.*;
import com.hdm.crowdmusic.gui.fragments.ClientLocalTracksFragment;
import com.hdm.crowdmusic.gui.fragments.ClientServerPlaylistFragment;
import com.hdm.crowdmusic.gui.support.IOnClientRequestListener;
import com.hdm.crowdmusic.gui.support.NoServerResponseDialog;
import com.hdm.crowdmusic.gui.support.TabListener;
import com.hdm.crowdmusic.util.Constants;
import com.hdm.crowdmusic.util.Utility;

public class ClientActivity extends Activity implements IOnClientRequestListener {

    private Client client;
    private IHttpServerService httpService;

    private IOnFailureHandler noResponse;

    private ServiceConnection httpServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity temp = this;
        noResponse = new IOnFailureHandler() {

            @Override
            public void execute() {
                new NoServerResponseDialog(temp).show();
            }
        };

        httpServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                httpService = (IHttpServerService) service;

                httpService.registerHandler("/audio/*", new AudioRequestHandler(getApplicationContext()));
                httpService.registerHandler("/track/request", new Handler<Track>(new Executable<Track>() {
                    @Override
                    public void execute(final Track postData) {
                        SimplePostTask<Track> task = new SimplePostTask<Track>(getClientData().getServerIP(), Constants.PORT, null, noResponse);
                        task.execute(new IAction<Track>() {
                            @Override
                            public String getPostTarget() {
                                return "track/response";
                            }

                            @Override
                            public Track getParam() {
                                return postData;
                            }
                        });
                    }
                }));
                httpService.registerHandler("/postplaylist*", new Handler<Tracklist>(new Executable<Tracklist>() {
                    @Override
                    public void execute(final Tracklist postData) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getClientData().setPlaylist(postData.getList());
                            }
                        });
                    }
                }));
            }

            public void onServiceDisconnected(ComponentName className) {
                httpService = null;
            }
        };



        Intent lastIntent = getIntent();
        String serverIP = lastIntent.getStringExtra("serverIP");

        String clientIP = Utility.getWifiIpAddress();
        client = new Client(this, clientIP, serverIP);

        Intent httpIntent = new Intent(this, HTTPServerService.class);
        httpIntent.putExtra("ip", clientIP);
        httpIntent.putExtra("port", Constants.PORT);

        getApplicationContext().bindService(
            httpIntent,
            httpServiceConnection,
            Context.BIND_AUTO_CREATE
         );

        final ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(1, ActionBar.DISPLAY_SHOW_TITLE);

        bar.addTab(bar.newTab()
                .setText("Playlist")
                .setTabListener(new TabListener<ClientServerPlaylistFragment>(
                        this, "playlist", ClientServerPlaylistFragment.class)));


        bar.addTab(bar.newTab()
                .setText("My Music")
                .setTabListener(new TabListener<ClientLocalTracksFragment>(
                        this, "music", ClientLocalTracksFragment.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        client.init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.server, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //TODO: Just give a copy and not the real reference
    @Override
    public Client getClientData() {
        return client;
    }
}

