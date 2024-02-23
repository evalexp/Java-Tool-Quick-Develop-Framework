package top.evalexp.tools.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import top.evalexp.tools.entity.plugin.Manifest;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ZipUtil {
    /**
     * get file's data from a zip
     * @param zipFile zip file object
     * @param filename the file in zip, for example a/b/c/d.txt
     * @return the file's data
     * @throws IOException  when file not found or can't read
     */
    public static byte[] getZipFile(File zipFile, String filename) throws IOException {
        if (!zipFile.exists()) throw new FileNotFoundException();
        ZipFile zip = new ZipFile(zipFile);
        ZipEntry entry = zip.getEntry(filename);
        if (entry == null) throw new ZipException();
        byte[] data = ResourceUtil.readAllBytes(zip.getInputStream(entry));
        zip.close();
        return data;
    }

    /**
     * get plugin path by plugin's name
     * @param name plugin name
     * @return plugin path if exist
     */
    public static String getPluginByPluginName(String name)  {
        File path = Paths.get(PathUtil.getCurrentPath(), "plugins").toFile();
        if (!path.exists()) return null;
        for (File file : path.listFiles()) {
            if (file.getName().equals(name)) return file.getAbsolutePath();
            if (file.isFile() && file.getName().endsWith(".jar")) {
                try {
                    Manifest manifest = new ObjectMapper().readValue(getZipFile(file, "manifest.json"), Manifest.class);
                    if (manifest.getName().equals(name)) return file.getAbsolutePath();
                } catch (IOException e) {}
            }
        }
        return null;
    }

    public static List<Manifest> getPluginList() {
        List<Manifest> manifests = new ArrayList<>();
        File path = Paths.get(PathUtil.getCurrentPath(), "plugins").toFile();
        if (!path.exists()) return null;
        for (File file : path.listFiles()) {
            try {
                Manifest manifest = new ObjectMapper().readValue(getZipFile(file, "manifest.json"), Manifest.class);
                manifest.setPath(file.getAbsolutePath());
                manifests.add(manifest);
            } catch (Exception e) {}
        }
        return manifests;
    }
    public static String getPluginListString() {
        List<Manifest> manifests = getPluginList();
        if (manifests == null) return null;
        int name_max_len = -1;
        int author_max_len = -1;
        int version_max_len = - 1;
        for (Manifest manifest : manifests) {
            if (manifest.getName().length() > name_max_len) name_max_len = manifest.getName().length();
            if (manifest.getAuthor().length() > author_max_len) author_max_len = manifest.getAuthor().length();
            if (manifest.getVersion().length() > version_max_len) version_max_len = manifest.getVersion().length();
        }
        int name_len = (name_max_len + 8 - 1) / 8;
        int author_len = (author_max_len + 8 -1) / 8;
        int version_len = (version_max_len + 8 - 1) / 8;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Name");
        for (int i = 0; i < name_len; i++) stringBuilder.append("\t");
        stringBuilder.append("Author");
        for (int i = 0; i < author_len; i++) stringBuilder.append("\t");
        stringBuilder.append("Version");
        for (int i = 0; i < version_len; i++) stringBuilder.append("\t");
        stringBuilder.append("Description\n");
        String template = "%-" + name_len * 8 + "s%-" + author_len * 8 + "s%-" + version_len * 8 + "s%s\n";
        for (Manifest manifest : manifests) stringBuilder.append(String.format(template, manifest.getName(), manifest.getAuthor(), manifest.getVersion(), manifest.getDescription()));
        return stringBuilder.toString();
    }
}
