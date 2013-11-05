package com.hdm.crowdmusic.fragments;


import android.app.TabActivity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hdm.crowdmusic.R;


/**
 * Created by jules on 04/11/13.
 */
public class ServerAutomaticFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_serverautomatic, container, false);
        return v;
    }


}
