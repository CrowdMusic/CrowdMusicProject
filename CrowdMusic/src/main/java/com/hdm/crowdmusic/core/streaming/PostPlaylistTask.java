package com.hdm.crowdmusic.core.streaming;

import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.Gson;
import com.hdm.crowdmusic.core.CrowdMusicTrack;
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

public class PostPlaylistTask extends AsyncTask<List<CrowdMusicTrack>, Void, HttpResponse> {
    private String clientIP;
    private int port;

    public PostPlaylistTask(String clientIP, int port) {
        this.clientIP = clientIP;
        this.port = port;
    }

    @Override
    public HttpResponse doInBackground(List<CrowdMusicTrack>... params) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://" + clientIP + ":" + port + "/postplaylist");

        List<CrowdMusicTrack> playList = params[0];


        Gson gson = new Gson();
        String json = gson.toJson(playList);

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("ip", clientIP));
            nameValuePairs.add(new BasicNameValuePair("playlist", json.toString()));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            Log.e(Utility.LOG_TAG_HTTP, "Error while preparing post data: " + e.getMessage());
            return null;
        }

        try {
            Log.i(Utility.LOG_TAG_HTTP, "Trying to send Post-Request with URL: " + httpPost.getRequestLine());
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