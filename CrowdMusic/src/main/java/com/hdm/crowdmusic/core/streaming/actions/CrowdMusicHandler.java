package com.hdm.crowdmusic.core.streaming.actions;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hdm.crowdmusic.util.Utility;
import org.apache.http.*;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLDecoder;

public class CrowdMusicHandler<T> implements HttpRequestHandler {


    private final Executable<T> executable;

    public CrowdMusicHandler(Executable<T> executable ) {
        this.executable = executable;
    }
    private final T getPostData(HttpEntity entity) {

        try {
            T value;
            String postData = EntityUtils.toString(entity);
            String[] parameters = postData.split("&");


            String jsonString = parameters[0].replace("key=", "");
            Gson gson = new Gson();

            jsonString = URLDecoder.decode(jsonString, "utf-8");
            Type type = new TypeToken<T>(){}.getType();
            value = gson.fromJson(jsonString, type);

            return value;
        } catch (IOException e) {
            Log.e(Utility.LOG_TAG_HTTP, "Error while extracting post data: " + e.getMessage());
            return null;
        }
    }

    public final T getPostData(HttpRequest httpRequest) {
        if (httpRequest instanceof HttpEntityEnclosingRequest) {
            HttpEntity entity = ((HttpEntityEnclosingRequest) httpRequest).getEntity();
            T postData = getPostData(entity);
            return postData;
        } else return null;
    }

    @Override
    public final void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
        //System.out.println(getPostData(httpRequest));
        executable.execute(getPostData(httpRequest));
    }
}
