package com.hdm.crowdmusic.core.media;

import org.json.JSONObject;

/**
 * Created by Hanno on 03.12.13.
 */
public class QRCodeHolder {

    private static QRCodeHolder instance;

    private JSONObject qrCode;

    public static QRCodeHolder getInstance() {
        if (instance == null) {
            instance = new QRCodeHolder();
        }
        return instance;
    }

    private QRCodeHolder() {

    }

    public JSONObject getQrCode() {
        return qrCode;
    }

    public void setQrCode(JSONObject qrCode) {
        this.qrCode = qrCode;
    }
}
