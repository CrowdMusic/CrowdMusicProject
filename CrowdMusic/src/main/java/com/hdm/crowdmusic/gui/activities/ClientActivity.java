package com.hdm.crowdmusic.gui.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.CrowdMusicClient;
import com.hdm.crowdmusic.core.CrowdMusicTrack;
import com.hdm.crowdmusic.core.devicelistener.CrowdDevicesBrowser;
import com.hdm.crowdmusic.gui.support.PlaylistTrackAdapter;
import com.hdm.crowdmusic.util.Utility;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.teleal.cling.registry.RegistryListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ClientActivity extends ListActivity {

    private RegistryListener registryListener;
    private CrowdMusicClient crowdMusicClient;
    ArrayAdapter<CrowdMusicTrack> listAdapter;

    private String serverIP;
    private String clientIP;
    private int port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listAdapter =  new ArrayAdapter(this, R.layout.fragment_client_serverbrowser);
        setListAdapter(listAdapter);

        registryListener = new CrowdDevicesBrowser(this, listAdapter);


        Intent lastIntent = getIntent();
        clientIP = lastIntent.getStringExtra("clientIP");
        serverIP = lastIntent.getStringExtra("serverIP");
        port = lastIntent.getIntExtra("port", 8080);

        crowdMusicClient = new CrowdMusicClient(getApplicationContext(), clientIP);

        listAdapter = new PlaylistTrackAdapter(this,
                android.R.layout.simple_list_item_1, crowdMusicClient.getTrackList());
        setListAdapter(listAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        crowdMusicClient.init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.server, menu);
        return true;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final CrowdMusicTrack selectedTrack = (CrowdMusicTrack) listAdapter.getItem(position);
        new PostAudioTask().execute(selectedTrack);
    }

    class PostAudioTask extends AsyncTask<CrowdMusicTrack, Void, HttpResponse> {

        @Override
        protected HttpResponse doInBackground(CrowdMusicTrack... params) {

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://" + serverIP + ":" + port);

            CrowdMusicTrack track = params[0];

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("id", "" + track.getId()));
                nameValuePairs.add(new BasicNameValuePair("ip", track.getIp()));
                nameValuePairs.add(new BasicNameValuePair("artist", track.getArtist()));
                nameValuePairs.add(new BasicNameValuePair("track", track.getTrackName()));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (UnsupportedEncodingException e) {
                Log.e(Utility.LOG_TAG_HTTP, "Error while preparing post data: " + e.getMessage());
                return null;
            }

            try {
                Log.i(Utility.LOG_TAG_HTTP, "Trying to send Get-Request with URL: " + httpPost.getRequestLine());
                HttpResponse response = httpClient.execute(httpPost);
                return response;
            } catch (IOException e) {
                Log.e(Utility.LOG_TAG_HTTP, "Error while executing post request: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(HttpResponse httpResponse) {
            if (httpResponse == null) {
                Log.i(Utility.LOG_TAG_HTTP, "Exception was thrown!");
            } else {
                Log.i(Utility.LOG_TAG_HTTP, "Answer from server: " + httpResponse.getStatusLine().toString());
            }
        }
    }
}
