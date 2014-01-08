package com.hdm.crowdmusic.gui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.CrowdMusicClient;
import com.hdm.crowdmusic.core.CrowdMusicTrack;
import com.hdm.crowdmusic.core.streaming.AudioRequestHandler;
import com.hdm.crowdmusic.core.streaming.IHttpServerService;
import com.hdm.crowdmusic.core.streaming.actions.CrowdMusicHandler;
import com.hdm.crowdmusic.core.streaming.actions.Executable;
import com.hdm.crowdmusic.core.streaming.actions.ICrowdMusicAction;
import com.hdm.crowdmusic.core.streaming.actions.SimplePostTask;
import com.hdm.crowdmusic.gui.fragments.ClientLocalTracksFragment;
import com.hdm.crowdmusic.gui.fragments.ClientServerPlaylistFragment;
import com.hdm.crowdmusic.gui.support.IOnClientRequestListener;
import com.hdm.crowdmusic.gui.support.TabListener;
import com.hdm.crowdmusic.util.Constants;
import com.hdm.crowdmusic.util.Utility;

import java.util.List;

public class ClientActivity extends Activity implements IOnClientRequestListener {

    private CrowdMusicClient crowdMusicClient;
    private IHttpServerService httpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent lastIntent = getIntent();
        String serverIP = lastIntent.getStringExtra("serverIP");

        String clientIP = Utility.getWifiIpAddress();
        crowdMusicClient = new CrowdMusicClient(getApplicationContext(), clientIP, serverIP);

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

    private ServiceConnection httpServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            httpService = (IHttpServerService) service;

            httpService.registerHandler("/audio/*", new AudioRequestHandler(getApplicationContext()));
            httpService.registerHandler("/track/request", new CrowdMusicHandler<CrowdMusicTrack>(new Executable<CrowdMusicTrack>() {
                @Override
                public void execute(final CrowdMusicTrack postData) {
                    SimplePostTask<CrowdMusicTrack> task = new SimplePostTask<CrowdMusicTrack>(getClientData().getServerIP(), Constants.PORT);
                    task.execute(new ICrowdMusicAction<CrowdMusicTrack>() {
                        @Override
                        public String getPostTarget() {
                            return "track/response";
                        }

                        @Override
                        public CrowdMusicTrack getParam() {
                            return postData;
                        }
                    });
                }
            }));
            httpService.registerHandler("/postplaylist*", new CrowdMusicHandler<List<CrowdMusicTrack>>(new Executable<List<CrowdMusicTrack>>() {
                @Override
                public void execute(final List<CrowdMusicTrack> postData) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getClientData().setPlaylist(postData);
                        }
                    });
                }
            }));
        }

        public void onServiceDisconnected(ComponentName className) {
            httpService = null;
        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        crowdMusicClient.init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.server, menu);
        return true;
    }

    //TODO: Just give a copy and not the real reference
    @Override
    public CrowdMusicClient getClientData() {
        return crowdMusicClient;
    }
}

