package com.hdm.crowdmusic.util;

import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

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
}
