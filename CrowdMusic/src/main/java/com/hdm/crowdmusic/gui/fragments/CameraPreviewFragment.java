package com.hdm.crowdmusic.gui.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.media.Preview;

public class CameraPreviewFragment extends Fragment {

    private Camera camera;
    private Preview preview;

    public CameraPreviewFragment(Camera camera, Activity activity) {
        this.camera = camera;

        preview = new Preview(activity.getApplicationContext(), camera, activity);
    }

    //public CameraPreviewFragment() {}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public Preview getPreview() {
        return preview;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_camera_preview, container, false);
        return rootView;
    }
}
