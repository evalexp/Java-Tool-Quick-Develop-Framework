package top.evalexp.tools.common.classloader;

import com.fasterxml.jackson.databind.ObjectMapper;
import top.evalexp.tools.common.util.ResourceUtil;
import top.evalexp.tools.entity.plugin.Manifest;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginClassLoader extends ClassLoader {
    private ClassLoader defaultClassLoader;
    private List<JarFile> jars = new ArrayList<>();
    private final String[] DEFAULT_LOAD_CLASS_PREFIX = new String[] {"top.evalexp", "java."};

    /**
     * initial class loader for jars
     * @param jars
     * @throws IOException
     */
    public PluginClassLoader(File[] jars) throws IOException {
        for (File jar : jars) {
            if (!jar.exists()) throw new FileNotFoundException();
            this.jars.add(new JarFile(jar));
        }
        this.defaultClassLoader = Thread.currentThread().getContextClassLoader();
    }

    public PluginClassLoader(File jar) throws IOException {
        this(new File[] {jar});
    }

    public PluginClassLoader(String filename) throws IOException {
        this(new File[] {new File(filename)});
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        for (JarFile jar : jars) {
            if (name.startsWith("/")) {
                name = name.substring(1);
            }
            JarEntry entry = jar.getJarEntry(name);
            if (entry != null) {
                try {
                    return jar.getInputStream(entry);
                } catch (IOException e) {
                }
            }
        }
        return super.getResourceAsStream(name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // load class by default loader, for core class
        for (String PREFIX : this.DEFAULT_LOAD_CLASS_PREFIX) {
            if (name.startsWith(PREFIX)) {
                try {
                    return this.loadByDefault(name);
                } catch (ClassNotFoundException e) {}
            }
        }
        // load class by jar
        try {
            return this.loadByPlugin(name);
        } catch (ClassNotFoundException e) {}
        // not found in jar, load class by default loader
        return this.loadByDefault(name);
    }

    private Class<?> loadByDefault(String name) throws ClassNotFoundException {
        return this.defaultClassLoader.loadClass(name);
    }

    private Class<?> loadByPlugin(String name) throws ClassNotFoundException{
        byte[] classBytes = this.getClassBytes(name);
        if (classBytes != null && classBytes.length != 0) {
            return this.defineClass(classBytes, 0, classBytes.length);
        }
        throw new ClassNotFoundException();
    }

    private byte[] getClassBytes(String name) {
        for (JarFile jar : this.jars) {
            JarEntry entry = jar.getJarEntry(name.replaceAll("\\.", "/") + ".class");
            if (entry != null) {
                try {
                    return jar.getInputStream(entry).readAllBytes();
                } catch (IOException e) {
                    return new byte[] {};
                }
            }
        }
        return new byte[]{};
    }
}
