package com.bechris100.remote4j.commons.utils.io;

import com.bechris100.remote4j.commons.utils.Utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class UnixPartition {

    private final String block, mount, fsType, mntOptions;
    private final long totalSpace, freeSpace, usableSpace;
    private final File root;

    protected UnixPartition(String block, String mount, String fsType, String mountOptions, long totalSpace, long freeSpace, long usableSpace, File root) {
        this.block = block;
        this.mount = mount;
        this.fsType = fsType;
        this.mntOptions = mountOptions;
        this.totalSpace = totalSpace;
        this.freeSpace = freeSpace;
        this.usableSpace = usableSpace;
        this.root = root;
    }

    public String getBlock() {
        return block;
    }

    public String getMount() {
        return mount;
    }

    public String getFilesystemType() {
        return fsType;
    }

    public long getTotalSpace() {
        return totalSpace;
    }

    public long getFreeSpace() {
        return freeSpace;
    }

    public long getUsableSpace() {
        return usableSpace;
    }

    public String getMountOptions() {
        return mntOptions;
    }

    public boolean canCurrentUserWrite() {
        return root.canWrite();
    }

    public boolean canCurrentUserRead() {
        return root.canRead();
    }

    public static List<UnixPartition> getPartitions(boolean filterSpecials) throws IOException {
        String contents = new String(FileUtil.read("/proc/mounts"));
        String[] lines = contents.split(Utility.getLineSeparator(contents));

        List<UnixPartition> partitions = new ArrayList<>();

        for (String line : lines) {
            StringTokenizer tokenizer = new StringTokenizer(line, " ");
            String blk = tokenizer.nextToken(),
                    mnt = tokenizer.nextToken(),
                    type = tokenizer.nextToken(),
                    opt = tokenizer.nextToken();

            if (filterSpecials) {
                if ((blk.contains("proc") || mnt.contains("proc") || type.contains("proc")) ||
                        (blk.contains("systemd") || mnt.contains("systemd") || type.contains("systemd")) ||
                        (blk.contains("binmft_misc") || mnt.contains("binmft_misc") || type.contains("binmft_misc")) ||
                        (blk.contains("udev") || mnt.contains("udev") || type.contains("udev")) ||
                        (blk.contains("devpts") || mnt.contains("devpts") || type.contains("devpts")) ||
                        (blk.contains("fuse") || mnt.contains("fuse") || type.contains("fuse")) ||
                        (blk.contains("pstore") || mnt.contains("pstore") || type.contains("pstore")) ||
                        type.contains("tmp"))
                    continue;

                if (blk.contains("none"))
                    continue;
            }

            File root = new File(mnt);

            partitions.add(new UnixPartition(blk, mnt, type, opt,
                    root.getTotalSpace(), root.getFreeSpace(), root.getUsableSpace(), root));
        }

        return partitions;
    }

}
