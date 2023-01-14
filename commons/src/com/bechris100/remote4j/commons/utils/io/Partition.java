package com.bechris100.remote4j.commons.utils.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Partition {

    private final String mount;
    private final long totalSpace, freeSpace, usableSpace;

    protected Partition(String mount, long totalSpace, long freeSpace, long usableSpace) {
        this.mount = mount;
        this.totalSpace = totalSpace;
        this.freeSpace = freeSpace;
        this.usableSpace = usableSpace;
    }

    public String getMount() {
        return mount;
    }

    public long getFreeSpace() {
        return freeSpace;
    }

    public long getTotalSpace() {
        return totalSpace;
    }

    public long getUsableSpace() {
        return usableSpace;
    }

    public static List<Partition> listPartitions() {
        File[] roots = File.listRoots();
        List<Partition> partitions = new ArrayList<>();

        for (File root : roots)
            partitions.add(new Partition(root.getAbsolutePath(),
                    root.getTotalSpace(), root.getFreeSpace(), root.getUsableSpace()));

        return partitions;
    }

}
