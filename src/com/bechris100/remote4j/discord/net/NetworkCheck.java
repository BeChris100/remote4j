package com.bechris100.remote4j.discord.net;

import java.io.IOException;
import java.net.*;

public class NetworkCheck {

    public static boolean a() {
        try {
            URL url = new URL("https://www.youtube.com");
            URLConnection connection = url.openConnection();
            connection.connect();

            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }

}
