package com.hdm.crowdmusic.gui.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
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
        //camera.startPreview();

    }

    public Camera getCamera() {
        return camera;
    }

    public void onClick(View view) {
        PhotoHandler photoHandler = new PhotoHandler(getApplicationContext(), this);
        QRCodeHolder.getInstance().busy = true;
        camera.takePicture(null, null, photoHandler);



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