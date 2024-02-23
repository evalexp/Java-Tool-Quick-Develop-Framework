package top.evalexp.tools.ui.form;

import top.evalexp.tools.common.classloader.PluginClassLoader;
import top.evalexp.tools.entity.plugin.Manifest;
import top.evalexp.tools.impl.plugin.GUIContext;
import top.evalexp.tools.impl.plugin.GUIResult;
import top.evalexp.tools.interfaces.plugin.IPlugin;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class PluginPanel extends JPanel {
    private final Manifest manifest;
    private final PluginClassLoader loader;
    private final GUIContext guiContext;
    private final GUIResult guiResult;
    private final MainForm mainForm;
    public void adjustSize() {
        this.guiContext.adjustOutputHeight();
    }
    public PluginPanel(Manifest manifest, MainForm mainForm) throws IOException, ClassNotFoundException {
        this.manifest = manifest;
        this.loader = new PluginClassLoader(new File(manifest.getPath()));
        this.guiContext = new GUIContext(this);
        this.guiResult = new GUIResult();
        this.mainForm = mainForm;
        this.setupUI();
    }

    /**
     * load class and render ui
     * @throws ClassNotFoundException throw when load class failed
     */
    private void setupUI() throws ClassNotFoundException {
        // try to load class
        Class<?> entry = loader.loadClass(this.manifest.getEntry());
        // java bytecode version error
        if (entry.equals(UnsupportedClassVersionError.class)) {
            throw new UnsupportedClassVersionError();
        }
        try {
            // construct new object
            Object plugin_object = entry.getConstructor().newInstance();
            if (plugin_object instanceof IPlugin) {
                // setup plugin then wait for handle
                ((IPlugin) plugin_object).setup(this.guiContext);
                this.guiContext.prepareForHandle((IPlugin) plugin_object, this.guiResult);
                // render ui
                this.guiContext.render();
            } else throw new Exception();
        } catch (Exception e) {
            throw new ClassNotFoundException();
        }
    }

    public void exit() {
        this.loader.exit();
    }

    public MainForm getMainForm() {
        return mainForm;
    }
}
