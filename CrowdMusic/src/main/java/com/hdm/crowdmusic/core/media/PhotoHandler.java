package com.hdm.crowdmusic.core.media;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.widget.Toast;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.gui.activities.ClientActivity;
import com.hdm.crowdmusic.gui.activities.MainActivity;
import com.hdm.crowdmusic.util.Utility;
import org.json.JSONObject;

/**
 * Created by Hanno on 03.12.13.
 */
public class PhotoHandler implements android.hardware.Camera.PictureCallback {

    private final Context context;
    private final Activity activity;

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        QRCodeHolder.getInstance().busy = true;

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

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            QRCodeHolder.getInstance().busy = false;
        }

        if (QRCodeHolder.getInstance().getQrCode() != null) {
            Toast toast = Toast.makeText(activity,  R.string.toast_configuring_with_qr, 4 );
            toast.show();

            if (!Utility.connectToWIFI(activity,QRCodeHolder.getInstance().getQrCode())) {
                Toast toastNoWLAN = Toast.makeText(activity,  R.string.toast_please_enable_wlan, 4 );
                toastNoWLAN.show();
            } else {
                Toast toastNoWLAN = Toast.makeText(activity,  R.string.toast_trying_to_connect_wlan, 4 );
                toastNoWLAN.show();
            }
            Intent intent = new Intent(activity, ClientActivity.class);
            activity.startActivity(intent);
        } else {
            Toast toast = Toast.makeText(activity, R.string.toast_no_qr_found, 4);
            toast.show();
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
        }

    }

    public PhotoHandler(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }
}