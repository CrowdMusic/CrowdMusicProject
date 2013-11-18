package com.hdm.crowdmusic.core.streaming;

import android.util.Log;

import com.hdm.crowdmusic.util.Utility;

import org.apache.http.HttpException;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTPServer {

    private int port;
    private InetAddress inetAddress;

    private HttpParams httpParams;
    private HttpRequestHandlerRegistry handlerRegistry;

    private HTTPServerThread httpServerThread;

    public HTTPServer(int port, InetAddress inetAddress) {
        this.port = port;
        this.inetAddress = inetAddress;

        httpParams = new BasicHttpParams();
        handlerRegistry = new HttpRequestHandlerRegistry();
    }

    synchronized public void startServer() {
        Log.d(Utility.LOG_TAG_HTTP, "Starting HTTP-Server...");

        try {
            httpServerThread = new HTTPServerThread(port, inetAddress, httpParams, handlerRegistry);
            httpServerThread.startServerThread();
        } catch (IOException e) {
            Log.e(Utility.LOG_TAG_HTTP, "Server could not be started: " + e.getMessage());
        }
    }

    synchronized public void stopServer() {
        if(httpServerThread != null) {
            Log.d(Utility.LOG_TAG_HTTP, "Stopping HTTP-Server...");
            httpServerThread.stopServerThread();
        }
    }

    static class HTTPServerThread extends Thread {
        private volatile boolean isRunning = false;

        private BasicHttpContext httpContext;
        private DefaultHttpServerConnection connection;
        final HttpService httpService;
        final HttpParams params;
        final ServerSocket serverSocket;

        HTTPServerThread(int port, InetAddress ip, HttpParams params, HttpRequestHandlerRegistry handlerRegistry) throws IOException {
            Log.d(Utility.LOG_TAG_HTTP, "Initialize HTTP-Server...");
            this.params = params;
            serverSocket = new ServerSocket(port, 0, ip);

            if(serverSocket.isBound())
                Log.d(Utility.LOG_TAG_HTTP, "Socket bound to port: " + serverSocket.getLocalPort() + " and InetAdress: " + serverSocket.getInetAddress());
            else
                Log.d(Utility.LOG_TAG_HTTP, "Socket not bound!");

            BasicHttpProcessor httpProcessor = new BasicHttpProcessor();
            httpContext = new BasicHttpContext();

            httpProcessor.addInterceptor(new ResponseDate());
            httpProcessor.addInterceptor(new ResponseServer());
            httpProcessor.addInterceptor(new ResponseContent());
            httpProcessor.addInterceptor(new ResponseConnControl());

            httpService = new HttpService(httpProcessor, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory());
            httpService.setParams(params);
            httpService.setHandlerResolver(handlerRegistry);
            Log.d(Utility.LOG_TAG_HTTP, "HTTP-Server initialized.");
        }

        @Override
        public void run() {
            while (isRunning) {
                try {
                    Log.d(Utility.LOG_TAG_HTTP, "Listening for incoming connections...");
                    final Socket socket = serverSocket.accept();
                    Log.d(Utility.LOG_TAG_HTTP, "Incoming connection with IP: " + socket.getInetAddress());
                    connection = new DefaultHttpServerConnection();

                    connection.bind(socket, params);
                    httpService.handleRequest(connection, httpContext);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (HttpException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        connection.shutdown();
                    } catch (IOException e) {
                        Log.d(Utility.LOG_TAG_HTTP, "Closing connection failed: " + e.getMessage());
                    }
                }
            }
        }

        public void startServerThread() {
            isRunning = true;
            this.start();
        }

        public void stopServerThread() {
            try {
                isRunning = false;
                if (!serverSocket.isClosed()) {
                    Log.d(Utility.LOG_TAG_HTTP, "Closing Server Socket.");
                    serverSocket.close();
                }
            } catch (IOException e) {
                Log.e(Utility.LOG_TAG_HTTP, "Exception while closing socket: " + e.getMessage());
            }
        }
    }
}
