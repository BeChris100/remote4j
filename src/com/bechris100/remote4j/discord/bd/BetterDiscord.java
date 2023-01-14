package com.bechris100.remote4j.discord.bd;

import com.bechris100.remote4j.commons.ProcessList;
import com.bechris100.remote4j.commons.utils.OperatingSystem;

import java.io.IOException;
import java.util.List;

public class BetterDiscord {

    private final OperatingSystem operatingSystem;

    public BetterDiscord(OperatingSystem operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public boolean hasBetterDiscord() throws IOException {
        return false;
    }

    public boolean isDiscordRunning() {
        List<ProcessList.ProcessInfo> processes = ProcessList.listProcesses();

        for (ProcessList.ProcessInfo process : processes) {
            if (process.process().equals("Discord") || process.process().equals("discord"))
                return true;
        }

        return false;
    }

    private String getDiscordProcess() {
        if (!isDiscordRunning())
            return "";

        List<ProcessList.ProcessInfo> processes = ProcessList.listProcesses();

        for (ProcessList.ProcessInfo runningProcess : processes) {
            if (runningProcess.process().contains("iscord"))
                return runningProcess.process();
        }

        return "";
    }

    public boolean killDiscord() throws Exception {
        if (!isDiscordRunning())
            return true;

        Process process = null;

        switch (operatingSystem) {
            case LINUX, MAC_OS -> process = Runtime.getRuntime().exec(new String[]{"/usr/bin/bash", "-c",
                    "pkill", getDiscordProcess()});
            case WINDOWS -> process = Runtime.getRuntime().exec(new String[]{"cmd", "/c",
                    "taskkill", "/f", "/im", getDiscordProcess()});
            default -> throw new RuntimeException("Unsupported operating system");
        }

        return process.waitFor() == 0;
    }

    /**
     * This first requires Discord to be killed. The Process Kill is required to hide the truth about Better Discord being injected for free Nitro.
     *
     * @throws IOException May throw an {@link IOException} if the File could not be written to it
     */
    public void injectPlugin() throws IOException {
    }

    /**
     * This first requires Discord to be killed and plugin to be sitting in BetterDiscord.
     *
     * @throws IOException 
     */
    public void injectEnablePlugin() throws IOException {
    }

}
