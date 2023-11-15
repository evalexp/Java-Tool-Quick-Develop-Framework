package top.evalexp.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import top.evalexp.tools.common.classloader.PluginClassLoader;
import top.evalexp.tools.common.util.PathUtil;
import top.evalexp.tools.common.util.ZipUtil;
import top.evalexp.tools.entity.plugin.Manifest;
import top.evalexp.tools.impl.plugin.CommandContext;
import top.evalexp.tools.impl.plugin.CommandResult;
import top.evalexp.tools.interfaces.plugin.IPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        if (args.length != 0) {
            if (args[0].toLowerCase().equals("-gui"))
                initialGUI();
            else
                initialCMD(args[0], Arrays.copyOfRange(args, 1, args.length));
        }
    }

    public static void initialGUI() {
        // TODO:
    }

    public static void initialCMD(String plugin, String[] args) {
        String origin_name = plugin;
        if (plugin.startsWith("@")) {
            // for windows absolute path,
            if (plugin.contains(":")) {
                plugin = plugin.substring(1);
            } else {
                plugin = Paths.get(PathUtil.getCurrentPath(), plugin.substring(1)).toAbsolutePath().toString();
            }
        } else {
                plugin = ZipUtil.getPluginByPluginName(plugin);
        }
        if (plugin == null || !new File(plugin).exists()) {
            System.out.println(String.format("[!] Error: Plugin \"%s\" not found.", origin_name));
            System.exit(-1);
        }
        File plugin_file = new File(plugin);
        CommandContext context = new CommandContext();
        try {
            Manifest manifest = new ObjectMapper().readValue(ZipUtil.getZipFile(plugin_file, "manifest.json"), Manifest.class);
            PluginClassLoader pluginClassLoader = new PluginClassLoader(plugin_file);
            Class<?> entry = pluginClassLoader.loadClass(manifest.getEntry());
            Object plugin_object = entry.newInstance();
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
        } catch (InstantiationException | IllegalAccessException j) {
            System.out.println("[!] Error: Plugin error, do you set a default constructor for entry class?");
        }
    }
}