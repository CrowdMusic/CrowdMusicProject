package com.hdm.crowdmusic.core.streaming;

import android.content.Context;
import android.util.Log;

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
            String body = EntityUtils.toString(entity);
            Log.i(Utility.LOG_TAG_HTTP, body);
        }

        httpResponse.setStatusCode(HttpStatus.SC_OK);
    }
}
