package com.hdm.crowdmusic.util;

import android.util.Log;
import com.hdm.crowdmusic.core.Track;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Utility {
    public static final String LOG_TAG = "CM";
    public static final String LOG_TAG_HTTP = "CM HTTP";
    public static final String LOG_TAG_MEDIA = "CM MEDIA";
    public static final String LOG_TAG_AP = "CM AP";

    public static String getWifiIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan") || intf.getName().contains("ap")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                            .hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()
                                && (inetAddress.getAddress().length == 4)) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(LOG_TAG, ex.toString());
        }
        return null;
    }

    public static String buildURL(Track track) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(track.getIp());
        sb.append(":" + Constants.PORT);
        sb.append("/audio/");
        sb.append(track.getId());
        return sb.toString();
    }
}
