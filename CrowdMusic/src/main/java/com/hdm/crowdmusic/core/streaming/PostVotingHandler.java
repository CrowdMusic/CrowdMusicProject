package com.hdm.crowdmusic.core.streaming;

import android.content.Context;
import android.util.Log;
import com.hdm.crowdmusic.core.CrowdMusicPlaylist;
import com.hdm.crowdmusic.core.CrowdMusicTrackVoting;
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
        if (method.equals("GET")) {
            Log.e(Utility.LOG_TAG_HTTP, "Only POST is supported for votings.");
        }

        String uri = httpRequest.getRequestLine().getUri();
        Log.i(Utility.LOG_TAG_HTTP, "Object with URI " + uri + " was requested.");

        CrowdMusicPlaylist playList = CrowdMusicPlaylist.getInstance();
        HttpEntity entity = ((HttpEntityEnclosingRequest) httpRequest).getEntity();

        // TODO: This could be enhanced with a process method in the Voting class.
        if (httpRequest instanceof HttpEntityEnclosingRequest) {
            CrowdMusicTrackVoting voting = getPostData(entity);
            if (voting.getCategory() == CrowdMusicTrackVoting.CATEGORY.DOWN) {
                playList.downvote(voting.getTrack().getId(), voting.getTrack().getIp());
            } else {
                playList.upvote(voting.getTrack().getId(), voting.getTrack().getIp());
            }
            httpResponse.setStatusCode(HttpStatus.SC_OK);
            return;
        }
    }

    private CrowdMusicTrackVoting getPostData(HttpEntity entity) {
        int id;
        CrowdMusicTrackVoting.CATEGORY category;
        String ip = "";

        try {
            String postData = EntityUtils.toString(entity);
            String[] parameters = postData.split("&");

            id = Integer.parseInt(parameters[0].replace("id=", ""));
            category = CrowdMusicTrackVoting.CATEGORY.valueOf(parameters[1].replace("category=", ""));
            ip = parameters[2].replace("ip=", "");

            return new CrowdMusicTrackVoting(CrowdMusicPlaylist.getInstance().getFromPlaylistById(id), category, ip);
        } catch (IOException e) {
            Log.e(Utility.LOG_TAG_HTTP, "Error while extracting post data: " + e.getMessage());
            return null;
        }
    }
}
