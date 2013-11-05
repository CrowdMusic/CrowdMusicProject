package com.hdm.crowdmusic;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;



import android.view.*;
import android.widget.Button;


import java.io.File;




public class MainActivity extends Activity {

    File imgFile = new File("R.drawable.crowdmusic");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new Fragment())
                    .commit();
        }

        Button createButton = (Button) findViewById(R.id.create_button);
        Button connectButton = (Button) findViewById(R.id.connect_button);

        createButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CreateServerActivity.class);
                startActivity(intent);
                System.out.println("onClick called");
            }
        });
        connectButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
//                Intent intent = new Intent(this, ConnectServerActivity.class);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    public void switchToCreateServer() {

    }

}