package com.hdm.crowdmusic.util;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import com.hdm.crowdmusic.core.network.AccessPoint;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class Utility {
    public static final String LOG_TAG = "CM";
    public static final String LOG_TAG_HTTP = "CM HTTP";
    public static final String LOG_TAG_MEDIA = "CM MEDIA";
    public static final String LOG_TAG_AP = "CM AP";

    public static InetAddress getWifiInetAddress(WifiManager manager) {

        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            return null;
        }

        int wifiIP = manager.getConnectionInfo().getIpAddress();
        int reverseWifiIP = Integer.reverseBytes(wifiIP);

        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = iface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                int byteArrayToInt = byteArrayToInt(inetAddress.getAddress(), 0);
                if (byteArrayToInt == wifiIP || byteArrayToInt == reverseWifiIP) {
                    return inetAddress;
                }
            }
        }
        return null;
    }

    public static int byteArrayToInt(byte[] arr, int offset) {
        if (arr == null || arr.length - offset < 4)
            return -1;

        int r0 = (arr[offset] & 0xFF) << 24;
        int r1 = (arr[offset + 1] & 0xFF) << 16;
        int r2 = (arr[offset + 2] & 0xFF) << 8;
        int r3 = arr[offset + 3] & 0xFF;
        return r0 + r1 + r2 + r3;
    }

    public static boolean connectToWIFI(Activity activity, JSONObject qrCode) {
        try {
            return connectToWIFI(activity, qrCode.getString(AccessPoint.JSON_KEY_SSID), qrCode.getString(AccessPoint.JSON_KEY_KEY));
        } catch (Exception e) {
            return false;
        }
    }
    public static boolean connectToWIFI(Activity activity, String ssid, String key) {
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\""+ssid+"\""; //IMPORTANT! This should be in Quotes!!
        wc.priority = 40;
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);

        wc.preSharedKey = "\""+key+"\"";
        wc.wepKeys[0] = "\""+key+"\""; //This is the WEP Password
        wc.wepTxKeyIndex = 0;

        WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        int res = wifiManager.addNetwork(wc);
        Log.d("WifiPreference", "add Network returned " + res);
        boolean es = wifiManager.saveConfiguration();
        Log.d("WifiPreference", "saveConfiguration returned " + es );
        boolean b = wifiManager.enableNetwork(res, true);
        Log.d("WifiPreference", "enableNetwork returned " + b );

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        if (list == null) {
            return false;
        }
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();

                break;
            }
        }

        return true;
    }
}
