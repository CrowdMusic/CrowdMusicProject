package com.hdm.crowdmusic.core.streaming;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hdm.crowdmusic.core.CrowdMusicPlaylist;
import com.hdm.crowdmusic.core.CrowdMusicTrack;
import com.hdm.crowdmusic.util.Utility;
import org.apache.http.*;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.List;
import java.util.Locale;

public class PostPlaylistHandler implements HttpRequestHandler {

    private Context context;

    public PostPlaylistHandler(Context context) {
        this.context = context;
    }

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
        String method = httpRequest.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        if (method.equals("GET")) {
            Log.e(Utility.LOG_TAG_HTTP, "Only POST is supported for votings.");
        }

        String uri = httpRequest.getRequestLine().getUri();
        Log.i(Utility.LOG_TAG_HTTP, "Object with URI " + uri + " was requested.");

        HttpEntity entity = ((HttpEntityEnclosingRequest) httpRequest).getEntity();

        // TODO: This could be enhanced with a process method in the Voting class.
        if (httpRequest instanceof HttpEntityEnclosingRequest) {
            List<CrowdMusicTrack> playList = getPostData(entity);

            CrowdMusicPlaylist.getInstance().setPlaylist(playList);
            Log.e(Utility.LOG_TAG_HTTP,"PLAYLIST POSTET AND SET: " + playList.size());

            httpResponse.setStatusCode(HttpStatus.SC_OK);
            return;
        }
    }

    private List<CrowdMusicTrack> getPostData(HttpEntity entity) {
        String clientIP = "";
        List<CrowdMusicTrack> playList;

        try {
            String postData = EntityUtils.toString(entity);
            String[] parameters = postData.split("&");

            clientIP = String.valueOf(parameters[0].replace("ip=", ""));

            String jsonString = parameters[1].replace("playlist=", "");
            Gson gson = new Gson();

            jsonString = URLDecoder.decode(jsonString, "utf-8");
            Type type = new TypeToken<List<CrowdMusicTrack>>(){}.getType();
            playList = gson.fromJson(jsonString, type);

            return playList;
        } catch (IOException e) {
            Log.e(Utility.LOG_TAG_HTTP, "Error while extracting post data: " + e.getMessage());
            return null;
        }
    }
}
