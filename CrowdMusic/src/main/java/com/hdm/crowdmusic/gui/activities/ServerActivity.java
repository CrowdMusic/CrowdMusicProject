package com.hdm.crowdmusic.gui.activities;

import android.app.*;
import android.content.*;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.CrowdMusicPlaylist;
import com.hdm.crowdmusic.core.CrowdMusicServer;
import com.hdm.crowdmusic.core.CrowdMusicTrack;
import com.hdm.crowdmusic.core.network.AccessPoint;
import com.hdm.crowdmusic.core.streaming.IMediaPlayerService;
import com.hdm.crowdmusic.core.streaming.MediaPlayerService;
import com.hdm.crowdmusic.gui.fragments.ServerAdminUsersFragment;
import com.hdm.crowdmusic.gui.fragments.ServerPlaylistFragment;
import com.hdm.crowdmusic.util.Utility;
import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.android.AndroidUpnpServiceImpl;
import org.teleal.cling.registry.RegistrationException;

import java.net.InetAddress;

import static com.hdm.crowdmusic.R.id;
import static com.hdm.crowdmusic.R.layout;

public class ServerActivity extends Activity {

    private CrowdMusicServer crowdMusicServer;
    private AndroidUpnpService upnpService;
    private IMediaPlayerService mediaService;

    private Bitmap wifiQrCode;
    private AccessPoint accessPoint;

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
            accessPoint.disable();
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


        final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        accessPoint = new AccessPoint(getApplicationContext());
        handleAPModalDialog();
        InetAddress ip = Utility.getWifiInetAddress(wifiManager);
        crowdMusicServer = new CrowdMusicServer(ip.getHostAddress());



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

        // If the dialog was alread shown, do nothing. This is for example the case
        // when switching from landscape to portrait. See Issue 23.
        if (AccessPoint.isApDialogShown()) return;
        AccessPoint.setApDialogShown(true);

        final Activity currentActivity = this;

        if (accessPoint.isWifiConnected()) {

            DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    accessPoint.enable();
                    Toast toast = Toast.makeText(currentActivity.getApplicationContext(), R.string.dialog_create_wlan_ap_created + "\n" + R.string.server_activity_created_server, 2);
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

                    accessPoint.enable();
                    Toast.makeText(getApplicationContext(), R.string.server_activity_created_server, 2).show();
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


    AlertDialog getModalDialog(final Activity currentActivity, String dialog, DialogInterface.OnClickListener ok, DialogInterface.OnClickListener cancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(dialog)
                .setTitle(R.string.dialog_title_create_wlan);


        builder.setPositiveButton(android.R.string.yes, ok);
        builder.setNegativeButton(android.R.string.no, cancel);

        AlertDialog alertDialog = builder.create();
        return alertDialog;
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
                CrowdMusicTrack track = CrowdMusicPlaylist.getInstance().getNextTrack();
                if (track != null) {
                    Log.i(Utility.LOG_TAG_HTTP, "With IP: " + Utility.buildURL(track));
                    mediaService.play(Utility.buildURL(track));
                }
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

            refresh();
        }

        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                ft.detach(mFragment);
            }
        }

        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            refresh();
        }

        private void refresh() {
            if (mFragment != null && mFragment instanceof ServerPlaylistFragment) {
                if (((ServerPlaylistFragment) mFragment).getListAdapter() == null) return;
                ((ServerPlaylistFragment) mFragment).setUpAdapter();
            }
        }
    }
}


