package com.hdm.crowdmusic.gui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.CrowdMusicClient;
import com.hdm.crowdmusic.gui.fragments.ClientLocalTracksFragment;
import com.hdm.crowdmusic.gui.fragments.ServerPlaylistFragment;
import com.hdm.crowdmusic.gui.support.OnClientRequestListener;
import com.hdm.crowdmusic.gui.support.TabListener;
import com.hdm.crowdmusic.util.Constants;
import com.hdm.crowdmusic.util.Utility;

public class ClientActivity extends Activity implements OnClientRequestListener {

    private CrowdMusicClient crowdMusicClient;

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
                .setTabListener(new TabListener<ServerPlaylistFragment>(
                        this, "playlist", ServerPlaylistFragment.class)));


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

    //TODO: Just give a copy and not the real reference
    @Override
    public CrowdMusicClient getClientData() {
        return crowdMusicClient;
    }
}

