package com.hdm.crowdmusic.core.streaming;

import android.os.AsyncTask;
import android.util.Log;
import com.hdm.crowdmusic.core.CrowdMusicTrackVoting;
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

public class PostVotingTask extends AsyncTask<CrowdMusicTrackVoting, Void, HttpResponse> {
    private String serverIP;
    private int port;

    public PostVotingTask(String serverIP, int port) {
        this.serverIP = serverIP;
        this.port = port;
    }

    @Override
    public HttpResponse doInBackground(CrowdMusicTrackVoting... params) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://" + serverIP + ":" + port);

        CrowdMusicTrackVoting voting = params[0];


        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("id", "" + voting.getTrack().getId()));
            nameValuePairs.add(new BasicNameValuePair("category", voting.getCategory().toString()));
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