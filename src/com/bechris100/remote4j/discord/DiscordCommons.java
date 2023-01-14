package com.bechris100.remote4j.discord;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DiscordCommons {

    public static List<String> fixedPaths = new ArrayList<>();

    public static final Color DISCORD_BACKGROUND_COLOR = Color.decode("#36393f");

    public static boolean IN_DEBUGGER_STATE = true;

    public static class WindowDisposableOperation {

        public static Window window;

        /**
         * If the value is false, it should NOT be possible to close the current window. Only by task killing it will be possible.
         * Default value is true.
         */
        public static boolean DISPOSABLE_WINDOW = true;

    }

}
