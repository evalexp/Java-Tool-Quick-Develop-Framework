package top.evalexp.tools.common.classloader;

import com.fasterxml.jackson.databind.ObjectMapper;
import top.evalexp.tools.entity.plugin.Manifest;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginClassLoader extends ClassLoader {
    private ClassLoader defaultClassLoader;
    private List<JarFile> jars = new ArrayList<>();
    private final String[] DEFAULT_LOAD_CLASS_PREFIX = new String[] {"top.evalexp", "java."};
    private void construct(File[] jars) throws IOException {
        for (File jar : jars) {
            if (!jar.exists()) throw new FileNotFoundException();
            this.jars.add(new JarFile(jar));
        }
        this.defaultClassLoader = Thread.currentThread().getContextClassLoader();
    }

//    public PluginClassLoader(File[] jars) throws IOException {
//        this.construct(jars);
//    }

    public PluginClassLoader(File jar) throws IOException {
        this.construct(new File[] {jar});
    }

    public PluginClassLoader(String filename) throws IOException {
        this.construct(new File[] {new File(filename)});
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        for (String PREFIX : this.DEFAULT_LOAD_CLASS_PREFIX) {
            if (name.startsWith(PREFIX)) {
                try {
                    return this.loadByDefault(name);
                } catch (ClassNotFoundException e) {}
            }
        }
        try {
            return this.loadByPlugin(name);
        } catch (ClassNotFoundException e) {}
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
