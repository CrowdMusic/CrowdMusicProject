package com.hdm.crowdmusic.core.media;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

/**
 * Created by Hanno on 03.12.13.
 */
public class Preview extends ViewGroup implements SurfaceHolder.Callback {

    private Camera camera;
    private SurfaceView surfaceView = null;
    private SurfaceHolder holder = null;

    public Preview(Context context, Camera camera, Activity activity) {
        super(context);
        this.camera = camera;

        surfaceView = new SurfaceView(context);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        holder = surfaceView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setPreviewDisplay() {
        try {
            //camera.open();
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e) {
            // This case can actually happen if the user opens and closes the camera too frequently.
            // The problem is that we cannot really prevent this from happening as the user can easily
            // get into a chain of activites and tries to escape using the back button.
            e.printStackTrace();
        }
    }

    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    public SurfaceHolder getPreviewHolder() {
        return holder;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        setPreviewDisplay();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(surfaceView.getWidth(), surfaceView.getHeight());
        requestLayout();
        camera.setParameters(parameters);

    /*
      Important: Call startPreview() to start updating the preview surface. Preview must be
      started before you can take a picture.
    */
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        try {
            if (camera != null) {
                camera.stopPreview();
                camera.setPreviewCallback(null);
                camera.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onLayout(boolean b, int i, int i2, int i3, int i4) {

    }
}
