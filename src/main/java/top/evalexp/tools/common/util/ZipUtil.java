package top.evalexp.tools.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import top.evalexp.tools.entity.plugin.Manifest;

import java.io.*;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ZipUtil {
    public static byte[] getZipFile(File zipFile, String filename) throws IOException {
        if (!zipFile.exists()) throw new FileNotFoundException();
        ZipFile zip = new ZipFile(zipFile);
        ZipEntry entry = zip.getEntry(filename);
        if (entry == null) throw new ZipException();
        byte[] data = zip.getInputStream(entry).readAllBytes();
        zip.close();
        return data;
    }

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
}
