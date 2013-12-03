package com.hdm.crowdmusic.core.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Hanno on 03.12.13.
 */
public class PhotoHandler implements android.hardware.Camera.PictureCallback {

    private final Context context;

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        QRCodeReader reader = new QRCodeReader();

        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        int[] intArray = new int[bitmap.getWidth()*bitmap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bitmap);

        BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            Result result = reader.decode(bBitmap);
            JSONObject qrCodeResult = new JSONObject(result.getText());
            QRCodeHolder.getInstance().setQrCode(qrCodeResult);

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public PhotoHandler(Context context) {
        this.context = context;
    }
}