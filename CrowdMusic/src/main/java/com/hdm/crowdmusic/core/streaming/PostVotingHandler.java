package com.hdm.crowdmusic.core.streaming;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import com.hdm.crowdmusic.core.CrowdMusicPlaylist;
import com.hdm.crowdmusic.util.Utility;
import org.apache.http.*;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Locale;

public class PostVotingHandler implements HttpRequestHandler {

    private Context context;

    public PostVotingHandler(Context context) {
        this.context = context;
    }

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
        String method = httpRequest.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        if (!method.equals("GET")) {
            Log.e(Utility.LOG_TAG_HTTP, "Only GET is supportet for votings data.");
        }

        String uri = httpRequest.getRequestLine().getUri();
        Log.i(Utility.LOG_TAG_HTTP, "Object with URI " + uri + " was requested.");



        int id = 0;
        boolean isUpvote = isUpVote(uri);
        try {
            id = Integer.parseInt(uri.replaceAll("/vote/up/", ""));
            id = Integer.parseInt(uri.replaceAll("/vote/down/", ""));
        } catch (NumberFormatException e) {
            Log.e(Utility.LOG_TAG_HTTP, "invalid ID");
            httpResponse.setStatusCode(HttpStatus.SC_NOT_FOUND);
            return;
        }

        Log.i(Utility.LOG_TAG_HTTP, "ID: " + id);

        Uri audioUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        Log.i(Utility.LOG_TAG_HTTP, "URI: " + audioUri);

        CrowdMusicPlaylist playList = CrowdMusicPlaylist.getInstance();
        HttpEntity entity = ((HttpEntityEnclosingRequest) httpRequest).getEntity();
        if (isUpvote) {
            playList.upvote(id, getIPOfPoster(entity));
        } else { // isDownvote
            playList.downvote(id, getIPOfPoster(entity));
        }
    }

    private boolean isUpVote(String uri) {
        if (uri.contains("/vote/up/")) {
            return true;
        }
        return false;
    }

    private String getIPOfPoster(HttpEntity entity) {
        try {
            String postData = EntityUtils.toString(entity);
            String[] parameters = postData.split("&");

            int id = Integer.parseInt(parameters[0].replace("id=", ""));
            String ip = parameters[1].replace("ip=", "");

            return ip;
        } catch (IOException e) {
            Log.e(Utility.LOG_TAG_HTTP, "Error while extracting IP address: " + e.getMessage());
            return "";
        }
    }
}
