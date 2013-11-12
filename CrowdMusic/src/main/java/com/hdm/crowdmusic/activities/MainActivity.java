package com.hdm.crowdmusic.activities;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import com.hdm.crowdmusic.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends ActionBarActivity {

    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startServer(View view) {
        Intent intent = new Intent(this, ServerActivity.class);
        startActivity(intent);
    }
    public void startClient(View view) {
        Intent intent = new Intent(this, ClientActivity.class);
        startActivity(intent);
    }

    public void createWifiAccessPoint(View view) {
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
