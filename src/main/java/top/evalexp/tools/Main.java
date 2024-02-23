package top.evalexp.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import top.evalexp.tools.common.classloader.PluginClassLoader;
import top.evalexp.tools.common.util.PathUtil;
import top.evalexp.tools.common.util.ZipUtil;
import top.evalexp.tools.entity.plugin.Manifest;
import top.evalexp.tools.impl.plugin.CommandContext;
import top.evalexp.tools.impl.plugin.CommandResult;
import top.evalexp.tools.interfaces.plugin.IPlugin;
import top.evalexp.tools.ui.GUI;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        if (args.length != 0) {
            // GUI Mode
            if (args[0].toLowerCase().equals("-gui"))
                initialGUI();
            else
            // CMD Mode
                initialCMD(args[0], Arrays.copyOfRange(args, 1, args.length));
        // empty args, list plugin
        } else initialCMD("-l", new String[]{});
    }

    public static void initialGUI() {
        // show GUI
        GUI gui = new GUI();
        gui.ui();
    }

    public static void initialCMD(String plugin, String[] args) {
        String origin_name = plugin;
        // support @ syntax
        if (plugin.startsWith("@")) {
            // for windows absolute path,
            if (plugin.contains(":")) {
                plugin = plugin.substring(1);
            } else {
                plugin = Paths.get(PathUtil.getCurrentPath(), plugin.substring(1)).toAbsolutePath().toString();
            }
        // list plugin
        } else if (plugin.toLowerCase().equals("--list") || plugin.toLowerCase().equals("-l")) {
            String list_str = ZipUtil.getPluginListString();
            if (list_str != null && !list_str.equals("")) System.out.println(list_str);
            else System.out.println("No plugin found.");
            System.exit(0);
        } else {
        // find plugin on $BaseDir/plugins
            plugin = ZipUtil.getPluginByPluginName(plugin);
        }
        if (plugin == null || !new File(plugin).exists()) {
            System.out.println(String.format("[!] Error: Plugin \"%s\" not found.", origin_name));
            System.exit(-1);
        }
        // Load plugin file
        File plugin_file = new File(plugin);
        // initial cmd mode context
        CommandContext context = new CommandContext();
        try {
            // read plugin manifest
            Manifest manifest = new ObjectMapper().readValue(ZipUtil.getZipFile(plugin_file, "manifest.json"), Manifest.class);
            PluginClassLoader pluginClassLoader = new PluginClassLoader(plugin_file);
            Class<?> entry = pluginClassLoader.loadClass(manifest.getEntry());
            Object plugin_object = entry.getConstructor().newInstance();
            // plugin entry => setup and handle
            if (plugin_object instanceof IPlugin) {
                ((IPlugin) plugin_object).setup(context);
                context.beforeHandle(args);
                ((IPlugin) plugin_object).handle(new CommandResult());
            } else {
                System.out.println("[!] Error: Entry not implement IContext.");
            }
        } catch (IOException e) {
            System.out.println("[!] Error: Plugin format error, did you pack it as a jar?");
        } catch (ClassNotFoundException f) {
            System.out.println("[!] Error: Load plugin error, some class could not be found");
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException j) {
            System.out.println("[!] Error: Plugin error, do you set a default constructor for entry class?");
        }
    }
}