package com.hdm.crowdmusic.core.streaming.actions;

import android.os.AsyncTask;
import android.util.Log;

import com.hdm.crowdmusic.core.Track;
import com.hdm.crowdmusic.util.Utility;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class PostAudioTask extends AsyncTask<Track, Void, HttpResponse> {
    private String serverIP;
    private int port;

    public PostAudioTask(String serverIP, int port) {
        this.serverIP = serverIP;
        this.port = port;
    }

    @Override
    public HttpResponse doInBackground(Track... params) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://" + serverIP + ":" + port);

        Track track = params[0];

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