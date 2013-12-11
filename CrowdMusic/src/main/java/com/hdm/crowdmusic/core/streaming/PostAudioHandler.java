package com.hdm.crowdmusic.core.streaming;

import android.content.Context;
import android.util.Log;

import com.hdm.crowdmusic.core.CrowdMusicTrack;
import com.hdm.crowdmusic.util.Utility;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.util.Locale;

public class PostAudioHandler implements HttpRequestHandler {

    private Context context;

    public PostAudioHandler(Context context) {
        this.context = context;
    }

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
        String method = httpRequest.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        if (!method.equals("POST")) {
            Log.e(Utility.LOG_TAG_HTTP, "Only POST is supportet for posting audio data.");
            httpResponse.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            return;
        }

        if (httpRequest instanceof HttpEntityEnclosingRequest) {
            HttpEntity entity = ((HttpEntityEnclosingRequest) httpRequest).getEntity();
            CrowdMusicTrack track = getPostData(entity);
            Log.i(Utility.LOG_TAG_HTTP, "ID: " + track.getId() + " | IP: " + track.getIp() + " | Artist: " + track.getArtist() + " | Track: " + track.getTrackName());
        }

        httpResponse.setStatusCode(HttpStatus.SC_OK);
    }

    private CrowdMusicTrack getPostData(HttpEntity entity) {
        int id;
        String ip;
        String artist;
        String trackName;

        try {
            String postData = EntityUtils.toString(entity);
            String[] parameters = postData.split("&");

            id = Integer.parseInt(parameters[0].replace("id=", ""));
            ip = parameters[1].replace("ip=", "");
            artist = parameters[2].replace("artist=", "").replace("+", " ").replace("%3C", "<").replace("%3E", ">");
            trackName = parameters[3].replace("track=", "").replace("+", " ");

            return new CrowdMusicTrack(id, ip, artist, trackName);
        } catch (IOException e) {
            Log.e(Utility.LOG_TAG_HTTP, e.getMessage());
            return null;
        }
    }
}
