package com.hdm.crowdmusic.gui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.widget.Toast;


import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.CrowdMusicClient;
import com.hdm.crowdmusic.core.CrowdMusicServer;
import com.hdm.crowdmusic.core.streaming.IMediaPlayerService;
import com.hdm.crowdmusic.core.streaming.MediaPlayerService;
import com.hdm.crowdmusic.gui.fragments.ServerAdminUsersFragment;
import com.hdm.crowdmusic.gui.fragments.ServerPlaylistFragment;
import com.hdm.crowdmusic.util.Utility;

import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.android.AndroidUpnpServiceImpl;
import org.teleal.cling.registry.RegistrationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.hdm.crowdmusic.R.*;

public class ServerActivity extends Activity {

    public static int REQUESTCODE_WLAN_ACTIVATED = 1;

    private CrowdMusicServer crowdMusicServer;

    private AndroidUpnpService upnpService;
    private IMediaPlayerService mediaService;

    private ServiceConnection upnpServiceConntection = new ServiceConnection() {
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

    private ServiceConnection mediaServiceConntection = new ServiceConnection() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(layout.activity_createserver);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(id.container, new PlaceholderFragment())
                    .commit();
        }

        crowdMusicServer = new CrowdMusicServer();

        getApplicationContext().bindService(
                new Intent(this, AndroidUpnpServiceImpl.class),
                upnpServiceConntection,
                Context.BIND_AUTO_CREATE
        );

        getApplicationContext().bindService(
                new Intent(this, MediaPlayerService.class),
                mediaServiceConntection,
                Context.BIND_AUTO_CREATE
        );

        handleAPModalDialog();
        Toast.makeText(getApplicationContext(), R.string.server_activity_created_server, 2).show();

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
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void handleAPModalDialog() {
        final Activity currentActivity = this;

        if (isWifiConnected()) {

            DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    createWifiAccessPoint();
                    Toast toast = Toast.makeText(currentActivity.getApplicationContext(), R.string.dialog_create_wlan_ap_created, 2);
                    toast.show();
                }
            };
            DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast toast = Toast.makeText(currentActivity.getApplicationContext(), R.string.dialog_create_wlan_no_ap_created, 2);
                    toast.show();
                }
            };

            Dialog dialog = getModalDialog(this, getApplicationContext().getString(R.string.dialog_create_wlan), ok, cancel);
            dialog.show();
        } else {

            DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent configIntent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
                    currentActivity.startActivityForResult(configIntent, REQUESTCODE_WLAN_ACTIVATED);
                }
            };
            DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast toast = Toast.makeText(currentActivity.getApplicationContext(), R.string.dialog_create_wlan_no_ap_created, 2);
                    toast.show();
                }
            };

            Dialog dialog = getModalDialog(this, getApplicationContext().getString(R.string.dialog_create_wlan_no_wifi_enabled_or_active), ok, cancel);
            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUESTCODE_WLAN_ACTIVATED) {
            Toast.makeText(getApplicationContext(), R.string.server_activity_created_server, 2).show();
        }
    }


    public void createWifiAccessPoint() {

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
        //Get all declared methods in WifiManager class
        Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();
        boolean methodFound = false;

        for (Method method: wmMethods){
            if (method.getName().equals("setWifiApEnabled")){
                methodFound = true;
                WifiConfiguration netConfig = new WifiConfiguration();
                netConfig.SSID = "\"CrowdMusicAccessPoint\"";
                netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

                try {
                    // Indicator for success - accesspoint status
                    boolean apstatus = (Boolean) method.invoke(wifiManager, netConfig,true);
                    for (Method isWifiApEnabledmethod: wmMethods)
                    {
                        if (isWifiApEnabledmethod.getName().equals("isWifiApEnabled")){
                            while (!(Boolean)isWifiApEnabledmethod.invoke(wifiManager)){
                                // Keep it running until ...
                            };
                            for (Method method1: wmMethods){
                                if(method1.getName().equals("getWifiApState")){
                                    method1.invoke(wifiManager);
                                }
                            }
                        }
                    }

//                    if(apstatus)
//                    {
//                        System.out.println("WLAN AP SUCCESS");
//                    }
//                    else
//                    {
//                        System.out.println("WLAN AP FAILED");
//                    }
                }
                catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
//        if (!methodFound){
//            //statusView.setText("Your phone's API does not contain setWifiApEnabled method to configure an access point");
//        }
    }

    AlertDialog getModalDialog(final Activity currentActivity, String dialog, DialogInterface.OnClickListener ok, DialogInterface.OnClickListener cancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(dialog)
                .setTitle(R.string.dialog_title_create_wlan);


        builder.setPositiveButton(android.R.string.yes, ok);
        builder.setNegativeButton(android.R.string.no, cancel);

        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }

    public boolean isWifiConnected() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mWifi.isConnected();
    }

    public void enableWifi() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    public void disconnectWifi() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.disconnect();
    }

    public boolean isWifiEnabled() {

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
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

    public class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;
        private final Bundle mArgs;
        private Fragment mFragment;

        public TabListener(Activity activity, String tag, Class<T> clz) {
            this(activity, tag, clz, null);
        }

        public TabListener(Activity activity, String tag, Class<T> clz, Bundle args) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
            mArgs = args;

            mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
            if (mFragment != null && !mFragment.isDetached()) {
                FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
                ft.detach(mFragment);
                ft.commit();
            }
        }

        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            if (mFragment == null) {
                mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                ft.attach(mFragment);
            }
        }

        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                ft.detach(mFragment);
            }
        }

        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }
    }
}


