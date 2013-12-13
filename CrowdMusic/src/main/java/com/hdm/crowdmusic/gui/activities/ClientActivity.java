package com.hdm.crowdmusic.gui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.CrowdMusicClient;
import com.hdm.crowdmusic.gui.fragments.ClientLocalTracksFragment;
import com.hdm.crowdmusic.gui.fragments.ClientServerPlaylistFragment;
import com.hdm.crowdmusic.gui.support.TabListener;

public class ClientActivity extends Activity implements ClientLocalTracksFragment.OnClientRequestedListener {


    private CrowdMusicClient crowdMusicClient;


    private String serverIP;
    private String clientIP;
    private int port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent lastIntent = getIntent();
        clientIP = lastIntent.getStringExtra("clientIP");
        serverIP = lastIntent.getStringExtra("serverIP");
        port = lastIntent.getIntExtra("port", 8080);

        crowdMusicClient = new CrowdMusicClient(getApplicationContext(), clientIP);

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

        crowdMusicClient.init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.server, menu);
        return true;
    }

    @Override
    public CrowdMusicClient OnClientRequestedListener() {
        return crowdMusicClient;
    }

    @Override
    public String OnServerRequestedListener() {
        return serverIP;
    }

    @Override
    public int OnPortRequestedListener() {
        return port;
    }


}

