package com.hdm.crowdmusic.core.streaming.actions;

import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.Gson;
import com.hdm.crowdmusic.util.Utility;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class SimplePostTask<T> extends AsyncTask<IAction<T>, Void, HttpResponse> {
    private String serverIP;
    private int port;
    private IOnSuccessHandler onPostSuccessHandler;
    private IOnFailureHandler onPostFailureHandler;

    public SimplePostTask(String serverIP, int port, IOnSuccessHandler onSuccessHandler, IOnFailureHandler onFailureHandler) {
        this.serverIP = serverIP;
        this.port = port;
        this.onPostSuccessHandler = onSuccessHandler;
        this.onPostFailureHandler = onFailureHandler;
    }
    public SimplePostTask(String serverIP, int port) {
        this(serverIP, port, null, null);
    }

    @Override
    public HttpResponse doInBackground(IAction<T>... actions) {

        HttpClient httpClient = new DefaultHttpClient();

        HttpResponse response = null;
        for (IAction<T> action: actions) {
            HttpPost httpPost = new HttpPost("http://" + serverIP + ":" + port + "/" + action.getPostTarget());
            T param = action.getParam();

            Gson gson = new Gson();
            String json = gson.toJson(param);

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("key", json));
                nameValuePairs.add(new BasicNameValuePair("class", param.getClass().getName()));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            } catch (UnsupportedEncodingException e) {
                Log.e(Utility.LOG_TAG_HTTP, "Error while preparing post data: " + e.getMessage());
            }
            try {
                response = httpClient.execute(httpPost);
            } catch (IOException e) {
                Log.e(Utility.LOG_TAG_HTTP, "Error while executing post request: " + e.getMessage());
            }
        }
        return response;
    }

    @Override
    protected void onPostExecute(HttpResponse httpResponse) {
        if (httpResponse == null) {
            if (onPostFailureHandler != null) {
                onPostFailureHandler.execute();
            }
        } else {
            if (onPostSuccessHandler != null) {
                onPostSuccessHandler.execute();
            }
        }
    }
}