package com.hdm.crowdmusic;

import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import static com.hdm.crowdmusic.R.*;

public class CreateServerActivity extends ActionBarActivity {

    private FragmentTabHost mTabHost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_createserver);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(id.container, new PlaceholderFragment())
                    .commit();
        }

        mTabHost = (FragmentTabHost)findViewById(R.id.tabhost);

        mTabHost.setup(this, getSupportFragmentManager(), id.tabFrameLayout);
        mTabHost.addTab(
                mTabHost.newTabSpec("automatic").setIndicator("Automatic"),
                ServerAutomaticFragment.class, null);
        mTabHost.setup(this, getSupportFragmentManager(), id.tabFrameLayout);
        mTabHost.addTab(
                mTabHost.newTabSpec("manual").setIndicator("Manual"),
                ServerManualFragment.class, null);
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
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
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
