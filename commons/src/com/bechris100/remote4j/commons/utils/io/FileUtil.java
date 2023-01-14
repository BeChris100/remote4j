package com.bechris100.remote4j.commons.utils.io;


import com.bechris100.remote4j.commons.utils.Utility;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtil {

    public static boolean exists(String path) {
        return new File(path).exists();
    }

    public static void createFile(String filePath, boolean createDirs) throws IOException {
        File file = new File(filePath);
        File dirs = new File(filePath.substring(0, Utility.getLastPathSeparator(filePath, false)));

        if (!dirs.exists()) {
            if (createDirs)
                createDirectory(dirs.getPath());
            else
                throw new FileNotFoundException("No directories at \"" + dirs.getPath() + "\" were found");
        }

        if (!file.createNewFile())
            throw new IOException("File at \"" + filePath + "\" could not be created");
    }

    public static void createDirectory(String path) throws IOException {
        File dir = new File(path.substring(0, Utility.getLastPathSeparator(path, true)));

        if (dir.exists()) {
            if (dir.isFile())
                throw new FileAlreadyExistsException("File at \"" + path + "\" already exists");
            else
                throw new FileAlreadyExistsException("Directory at \"" + path + "\" already exists");
        }

        if (!dir.mkdirs())
            throw new IOException("Could not create new directories at \"" + path + "\"");
    }

    public static char[] read(String filePath) throws IOException {
        File file = new File(filePath);

        if (!file.exists())
            throw new FileNotFoundException("File at \"" + filePath + "\" not found");

        if (file.isDirectory())
            throw new IllegalStateException("\"" + filePath + "\" is a directory");

        if (!file.canRead())
            throw new AccessDeniedException("Read-access for file \"" + filePath + "\" denied");

        FileInputStream fis = new FileInputStream(file);
        StringBuilder str = new StringBuilder();
        int data;

        while ((data = fis.read()) != -1)
            str.append((char) data);

        fis.close();
        return str.toString().toCharArray();
    }

    public static String removeName(String path) {
        String cutPath = removePath(path);
        return path.replaceFirst(cutPath, "");
    }

    public static String removePath(String path) {
        int sep = Utility.getLastPathSeparator(path, false);
        return path.substring(sep + 1);
    }

    public static void makeDirs(String dirPath) throws Exception {
        File dir = new File(removeName(dirPath));

        if (dir.exists())
            return;

        if (!dir.mkdirs())
            throw new AccessDeniedException("Could not make new directories at \"" + removeName(dirPath) + "\"");
    }

    public static void write(String filePath, char[] contents) throws IOException {
        File file = new File(filePath);

        if (!file.exists())
            createFile(filePath, true);

        if (file.exists() && file.isDirectory())
            throw new IllegalStateException("\"" + filePath + "\" is a directory");

        if (file.exists() && !file.canWrite())
            throw new AccessDeniedException("\"" + filePath + "\" could not be overwritten");

        if (!file.canWrite())
            throw new AccessDeniedException("\"" + filePath + "\" could not be written");

        FileOutputStream fos = new FileOutputStream(file);
        for (char c : contents)
            fos.write(c);

        fos.close();
    }

    public static List<String> listDirectory(String dirPath, boolean nameSort, boolean removePaths, boolean ignoreErrors) throws IOException {
        List<String> data = new ArrayList<>();
        File dir = new File(dirPath);

        if (!dir.exists())
            throw new FileNotFoundException("\"" + dirPath + "\" does not exist");

        if (!dir.canRead()) {
            if (!ignoreErrors)
                throw new AccessDeniedException("Cannot access \"" + dirPath + "\"");
        }

        if (dir.isFile()) {
            data.add(dirPath);

            if (removePaths)
                data.set(0, removePath(data.get(0)));

            return data;
        }

        File[] files = dir.listFiles();
        if (files == null)
            return data;

        for (File file : files)
            data.add(file.getPath());

        if (removePaths) {
            if (data.size() != 0)
                data.replaceAll(FileUtil::removePath);
        }

        if (nameSort) {
            if (data.size() != 0)
                Collections.sort(data);
        }

        return data;
    }

    public static List<String> scanForSpecifiedName(String path, String name, boolean excludeAddingFolders, boolean ignoreErrors) throws IOException {
        List<String> results = new ArrayList<>();
        File root = new File(path);

        if (root.isFile()) {
            if (path.contains(name))
                results.add(path);

            return results;
        }

        List<String> data = listDirectory(path, true, false, ignoreErrors);

        for (String item : data) {
            File file = new File(item);

            if (excludeAddingFolders) {
                if (file.getPath().contains(name) && file.isFile())
                    results.add(file.getPath());
            } else {
                if (file.getPath().contains(name))
                    results.add(file.getPath());
            }

            if (file.isDirectory())
                results.addAll(scanForSpecifiedName(item, name, excludeAddingFolders, ignoreErrors));
        }

        return results;
    }

    public static List<String> scanWithSpecificEnding(String path, String ending, boolean ignoreErrors) throws IOException {
        List<String> results = new ArrayList<>();
        File root = new File(path);

        if (root.isFile()) {
            if (path.endsWith(ending))
                results.add(path);

            return results;
        }

        List<String> data = listDirectory(path, true, false, ignoreErrors);

        for (String item : data) {
            File file = new File(item);

            if (file.isFile()) {
                if (file.getPath().endsWith(ending))
                    results.add(item);
            }

            if (file.isDirectory())
                results.addAll(scanWithSpecificEnding(item, ending, ignoreErrors));
        }

        return results;
    }

    public static List<String> scanFiles(String path, boolean ignoreErrors) throws IOException {
        List<String> results = new ArrayList<>();
        File root = new File(path);

        if (root.isFile())
            return results;

        List<String> data = listDirectory(path, true, false, ignoreErrors);

        for (String itemPath : data) {
            File file = new File(itemPath);

            if (file.isFile())
                results.add(itemPath);

            if (file.isDirectory())
                results.addAll(scanFiles(itemPath, ignoreErrors));
        }

        return results;
    }

    public static List<String> scanFolders(String path, boolean ignoreErrors) throws IOException {
        List<String> results = new ArrayList<>();
        File root = new File(path);

        if (root.isFile())
            return results;

        List<String> data = listDirectory(path, true, false, ignoreErrors);

        for (String itemPath : data) {
            File file = new File(itemPath);

            if (file.isFile())
                continue;

            if (file.isDirectory()) {
                results.add(itemPath);
                results.addAll(scanFolders(itemPath, ignoreErrors));
            }
        }

        return results;
    }

    public static void delete(String path) throws Exception {
        File inner = new File(path);

        if (!inner.exists())
            return;

        if (inner.isFile()) {
            if (!inner.delete())
                throw new IOException("Could not delete \"" + path + "\"");

            return;
        }

        if (listDirectory(path, false, false, false).size() == 0) {
            if (!inner.delete())
                throw new IOException("Could not delete \"" + path + "\"");

            return;
        }

        File[] files = inner.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            BasicFileAttributes fileAttrib = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

            if (fileAttrib.isDirectory())
                delete(file.getPath());

            if (fileAttrib.isRegularFile()) {
                if (!file.delete())
                    throw new IOException("Could not delete \"" + file.getPath() + "\"");
            }

            if (fileAttrib.isSymbolicLink()) {
                if (!file.delete())
                    throw new IOException("Could not delete \"" + file.getPath() + "\"");
            }

            if (fileAttrib.isOther()) {
                if (!file.delete())
                    throw new IOException("Could not delete \"" + file.getPath() + "\"");
            }
        }

        if (!inner.delete())
            throw new Exception("Could not delete \"" + path + "\"");
    }
}
