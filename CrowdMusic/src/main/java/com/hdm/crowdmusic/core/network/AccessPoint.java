package com.hdm.crowdmusic.core.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import com.hdm.crowdmusic.util.Utility;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class AccessPoint {

    public static final String DEFAULT_AP_NAME_PREFIX = "CrowdMusicAccessPoint@";

    public static final String JSON_KEY_SSID = "ssid";
    public static final String JSON_KEY_KEY = "key";

    private static boolean apDialogShown = false;

    private static AccessPoint instance;
    private boolean initialized = false;

    public static AccessPoint getInstance() {
        if (instance == null) {
            instance = new AccessPoint();
        }
        return instance;
    }


    private AccessPoint() {

    }


    // Test purposes
    public static JSONObject defaultJsonObject;
    static {
        defaultJsonObject = new JSONObject();
        try {
            defaultJsonObject.put(JSON_KEY_KEY, "testtest");
            defaultJsonObject.put(JSON_KEY_SSID, "test");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private WifiManager wifiManager;
    private ConnectivityManager connManager;
    private NetworkInfo mWifi;
    private Context context;

    private boolean enabled = false;
    private String key = "";
    private String ssid = "";


    public AccessPoint(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    }

    public void enable() {
        enable(DEFAULT_AP_NAME_PREFIX + Build.MODEL, "");

        //try {
        //    enable((String) defaultJsonObject.get(JSON_KEY_SSID), (String) defaultJsonObject.get(JSON_KEY_SSID));
        //} catch (JSONException e) {
        //    e.printStackTrace();
        //}
    }

    public void enable(String ssid, String key) {
        this.key = key;
        this.ssid = ssid;
        createWifiAccessPoint(ssid, key);
    }


    public boolean isWifiConnected() {
        return mWifi.isConnected();
    }

    public void enableWifi() {
        wifiManager.setWifiEnabled(true);
    }
    public void disableWifi() {
        wifiManager.setWifiEnabled(false);
    }

    public void disconnectWifi() {
        wifiManager.disconnect();
    }

    public boolean isWifiEnabled() {
        return wifiManager.isWifiEnabled();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void disable() {
        disconnectWifi();
        enabled = false;
    }

    public JSONObject getConfigJSON() {
        JSONObject object = new JSONObject();

        // debug purposes
        // object = defaultJsonObject;

        if (enabled) {
            try {
                object.put(JSON_KEY_SSID, ssid);
                object.put(JSON_KEY_KEY, key);
            } catch (JSONException e) {
                Log.e(Utility.LOG_TAG_AP, e.toString());
            }
        }

        return object;
    }

    private void createWifiAccessPoint(String ssid, String key) {

        disableWifi();

        //Get all declared methods in WifiManager class
        Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();
        boolean methodFound = false;

        for (Method method : wmMethods) {
            if (method.getName().equals("setWifiApEnabled")) {
                methodFound = true;
                WifiConfiguration netConfig = new WifiConfiguration();
                netConfig.SSID = "\"" + ssid + "\"";
                if (key != "") {
                    netConfig.preSharedKey = "\"" + key+ "\"";
                    netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                    netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                    netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                } else {
                    netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                }
                try {
                    // Indicator for success - accesspoint status
                    enabled = (Boolean) method.invoke(wifiManager, netConfig, true);
                    for (Method isWifiApEnabledmethod : wmMethods) {
                        if (isWifiApEnabledmethod.getName().equals("isWifiApEnabled")) {
                            while (!(Boolean) isWifiApEnabledmethod.invoke(wifiManager)) {
                                // Keep it running until ...
                            }
                            ;
                            for (Method method1 : wmMethods) {
                                if (method1.getName().equals("getWifiApState")) {
                                    method1.invoke(wifiManager);
                                }
                            }
                        }
                    }
//
//                    if(enabled)
//                    {
//                        System.out.println("WLAN AP SUCCESS");
//                    }
//                    else
//                    {
//                        System.out.println("WLAN AP FAILED");
//                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
//        if (!methodFound){
//            //statusView.setText("Your phone's API does not contain setWifiApEnabled method to configure an access point");
//        }
    }

    public static boolean isApDialogShown() {
        return apDialogShown;
    }

    public static void setApDialogShown(boolean apDialogShown) {
        AccessPoint.apDialogShown = apDialogShown;
    }

}
