package top.evalexp.tools.common.util;

import top.evalexp.tools.Main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

public class ResourceUtil {
    public static URL getResource(String path) {
        return Objects.requireNonNull(Main.class.getClassLoader().getResource(path));
    }

    public static InputStream getResourceInputStream(String path) throws IOException {
        return getResource(path).openStream();
    }

    public static byte[] readAllBytes(InputStream is) throws IOException {
        int b;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((b = is.read()) != -1) {
            baos.write(b);
        }
        baos.close();
        return baos.toByteArray();
    }
}
