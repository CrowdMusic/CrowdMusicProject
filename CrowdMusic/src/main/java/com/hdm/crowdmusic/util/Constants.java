package com.hdm.crowdmusic.util;

public class Constants {
    private static Constants instance;

    public static Constants getInstance() {
        if (instance == null) {
            instance = new Constants();
        }
        return instance;
    }

    private Constants() {};

    public int getPort() {
        return 8080;
    }
}
