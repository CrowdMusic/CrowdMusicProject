package com.hdm.crowdmusic.core.streaming.actions;

import android.app.Activity;
import android.util.Log;
import com.google.gson.Gson;
import com.hdm.crowdmusic.util.Utility;
import org.apache.http.*;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URLDecoder;

public class CrowdMusicHandler<T> implements HttpRequestHandler {

    private final Executable<T> executable;

    public CrowdMusicHandler(Executable<T> executable ) {
        this.executable = executable;
    }
    private Object getPostData(HttpEntity entity) {

        try {
            String postData = EntityUtils.toString(entity);
            String[] parameters = postData.split("&");


            String jsonString = parameters[0].replace("key=", "");
            final String className = parameters[1].replace("class=", "");
            Class clazz = Class.forName(className);
            Gson gson = new Gson();

            jsonString = URLDecoder.decode(jsonString, "utf-8");
            return gson.fromJson(jsonString, clazz);

        } catch (IOException e) {
            Log.e(Utility.LOG_TAG_HTTP, "Error while extracting post data: " + e.getMessage());
            return null;
        } catch (ClassNotFoundException e) {
            Log.e(Utility.LOG_TAG_HTTP, "Error because of missing class: " + e.getMessage());
            return null;
        }
    }

    public final T getPostData(HttpRequest httpRequest) {
        if (httpRequest instanceof HttpEntityEnclosingRequest) {
            HttpEntity entity = ((HttpEntityEnclosingRequest) httpRequest).getEntity();
            T postData = (T) getPostData(entity);
            return postData;
        } else return null;
    }


    @Override
    public final void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
        //System.out.println(getPostData(httpRequest));
        try {
            executable.execute(getPostData(httpRequest));
        } catch (ClassCastException e) {
            Log.e("CROWDMUSIC", "Mach vernünftige Typen!");
            Log.e("CROWDMUSIC", e.toString());
        }
    }
}
