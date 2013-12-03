package com.hdm.crowdmusic.gui.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.media.PhotoHandler;
import com.hdm.crowdmusic.core.media.QRCodeHolder;
import com.hdm.crowdmusic.gui.fragments.CameraPreviewFragment;

/**
 * Created by Hanno on 03.12.13.
 */
public class MakePhotoActivity extends Activity {
    private final static String DEBUG_TAG = "MakePhotoActivity";
    private Camera camera;
    private int cameraId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_photo);

        // do we have a camera?
        if (!getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
                    .show();
        } else {
            cameraId = findBackFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(this, "No back facing camera found.",
                        Toast.LENGTH_LONG).show();
            } else {
                camera = Camera.open(cameraId);
                camera.startPreview();

            }
        }


        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        CameraPreviewFragment fragment = new CameraPreviewFragment(camera, this);
        fragmentTransaction.add(R.id.activity_make_photo, fragment);
        fragmentTransaction.commit();

        LinearLayout preview = (LinearLayout) findViewById(R.id.activity_make_photo);
        preview.addView(fragment.getPreview().getSurfaceView());
        camera.startPreview();

    }

    public Camera getCamera() {
        return camera;
    }

    public void onClick(View view) {
        PhotoHandler photoHandler = new PhotoHandler(getApplicationContext());
        camera.takePicture(null, null, photoHandler);
        // TODO: Do whatever you want with the saved QRCode


        if (QRCodeHolder.getInstance().getQrCode() != null) {
            Toast toast = Toast.makeText(this, "success with " + QRCodeHolder.getInstance().getQrCode().toString(), 4 );
            toast.show();
            // TODO: CONNECT WLAN
            Intent intent = new Intent(this, ClientActivity.class);
        } else {
            Toast toast = Toast.makeText(this, "fail", 4);
            toast.show();
            Intent intent = new Intent(this, MainActivity.class);
        }

    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.d(DEBUG_TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    protected void onPause() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
        super.onPause();
    }
}