package top.evalexp.tools.common.util;

import top.evalexp.tools.Main;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class PathUtil {
    /**
     * @return jar's dir
     */
    public static String getCurrentPath() {
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = path.substring(1);
        path = path.substring(0, path.lastIndexOf('/'));
        try {
            return URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return path;
        }
    }

    /**
     * @return jar's name
     */
    public static String getCurrentFilename() {
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = path.substring(path.lastIndexOf('/') + 1);
        try {
            return URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return path;
        }
    }
}
