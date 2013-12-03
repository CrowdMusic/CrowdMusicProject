package com.hdm.crowdmusic.core;


import android.graphics.Bitmap;
import android.graphics.Color;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class CrowdMusicQrCode {

    private CrowdMusicQrCode() {
    }

    public static Bitmap getNetworkQr() {
        QRCodeWriter writer = new QRCodeWriter();
        try {

            BitMatrix matrix = writer.encode(
                    "test", BarcodeFormat.QR_CODE, 400, 400
            );
            return toBitmap(matrix);

        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Bitmap toBitmap(BitMatrix matrix) {
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }
}
