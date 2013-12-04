package com.hdm.crowdmusic.core.streaming;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.hdm.crowdmusic.util.Utility;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class AudioRequestHandler implements HttpRequestHandler {

    private Context context;

    public AudioRequestHandler(Context context) {
        this.context = context;
    }

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
        String method = httpRequest.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        if (!method.equals("GET")) {
            Log.e(Utility.LOG_TAG_HTTP, "Only GET is supportet for audio data.");
        }

        String uri = httpRequest.getRequestLine().getUri();
        Log.i(Utility.LOG_TAG_HTTP, "Object with URI " + uri + " was requested.");

        int id = 0;
        try {
            id = Integer.parseInt(uri.replaceAll("/audio/", ""));
        } catch (NumberFormatException e) {
            Log.e(Utility.LOG_TAG_HTTP, "invalid ID");
            httpResponse.setStatusCode(HttpStatus.SC_NOT_FOUND);
            return;
        }

        Log.i(Utility.LOG_TAG_HTTP, "ID: " + id);

        Uri audioUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        Log.i(Utility.LOG_TAG_HTTP, "URI: " + audioUri);

        httpResponse.setStatusCode(HttpStatus.SC_OK);
    }
}
