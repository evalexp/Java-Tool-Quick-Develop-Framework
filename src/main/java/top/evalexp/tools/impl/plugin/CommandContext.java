package top.evalexp.tools.impl.plugin;

import org.apache.commons.cli.*;
import top.evalexp.tools.common.util.Pair;
import top.evalexp.tools.common.util.PathUtil;
import top.evalexp.tools.common.util.ResourceUtil;
import top.evalexp.tools.impl.component.Enumerate;
import top.evalexp.tools.impl.component.ListArg;
import top.evalexp.tools.impl.component.Switch;
import top.evalexp.tools.impl.component.Text;
import top.evalexp.tools.interfaces.component.IComponent;
import top.evalexp.tools.interfaces.plugin.IContext;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandContext implements IContext {
    private HashMap<String, Pair<IComponent, String>> components = new HashMap<>();
    private final Options options = new Options();
    private final HelpFormatter helpFormatter = new HelpFormatter();
    private final String DEFAULT_VALUE_TEMPLATE = "%s\nNon-Required Argument, default set to [%s]";
    private final String ENUM_VALUE_TEMPLATE = "%s\nOption: %s";

    public CommandContext() {
        this.helpFormatter.setWidth(120);
    }

    public void beforeHandle(String[] args) {
        this.parseArgs(args);
    }

    private void parseArgs(String[] args) {
        CommandLineParser parser = new DefaultParser();
        // print help
        if (args.length == 0 || args[0].toLowerCase().equals("-h") || args[0].toLowerCase().equals("--help")) {
            this.printHelp();
            System.exit(0);
        }
        // support @ syntax
        if (args[0].startsWith("@")) {
            File file = new File(args[0].substring(1));
            try {
                InputStream inputStream = new FileInputStream(file);
                args = Arrays.stream(new String(ResourceUtil.readAllBytes(inputStream)).split("[\n\r ]")).filter(s -> !s.equals("")).toArray(String[]::new);
            } catch (IOException e) {
                System.out.println("[!] Error: Arguments file not found or can't read.");
            }
        }
        this.options.addOption(Option.builder("h").longOpt("help").desc("Print Help").build());
        try {
            // fill component by input arguments
            CommandLine line = parser.parse(this.options, args);
            for (Map.Entry<String, Pair<IComponent, String>> entry : this.components.entrySet()) {
                if (line.hasOption(entry.getKey())) {
                    if (entry.getValue().key() instanceof Enumerate<?>) {
                        if (!((Enumerate<?>) entry.getValue().key()).setSelected(line.getOptionValue(entry.getKey())))
                            throw new ParseException("Invalid selection for " + entry.getKey());
                    } else if (entry.getValue().key() instanceof ListArg) {
                        ((ListArg) entry.getValue().key()).setList(Arrays.asList(line.getOptionValue(entry.getKey()).split(",")));
                    } else if (entry.getValue().key() instanceof Switch) {
                        ((Switch) entry.getValue().key()).doSwitch();
                    } else if (entry.getValue().key() instanceof Text) {
                        ((Text) entry.getValue().key()).setText(line.getOptionValue(entry.getKey()));
                    }
                }
            }

        } catch (ParseException exception) {
            System.out.println("[!] Error: " + exception.getMessage());
            this.printHelp();
            System.exit(-1);
        }
    }

    private void printHelp() {
        this.helpFormatter.printHelp(String.format("java -jar %s plugin_name/@pluginPath [args.../@argsFilePath]", PathUtil.getCurrentFilename()), "", this.options, "");
    }

    private IComponent keep(String label, String description, IComponent component) {
        components.put(label, new Pair<>(component, description));
        return component;
    }

    @Override
    public IComponent<String> Text(String label, String shortLabel, String description) {
        this.options.addOption(Option.builder(shortLabel).longOpt(label).required().desc(description).argName("value").hasArg().build());
        return this.keep(label, description, new Text(label));
    }

    @Override
    public IComponent<Boolean> Switch(String label, String shortLabel, String description) {
        this.options.addOption(Option.builder(shortLabel).longOpt(label).required(false).desc(String.format(this.DEFAULT_VALUE_TEMPLATE, description, "false")).build());
        return this.keep(label, description, new Switch());
    }

    @Override
    public <V> IComponent<V> Enumerate(String label, String shortLabel, String description, Map<String, V> enums) {
        this.options.addOption(Option.builder(shortLabel).longOpt(label).required().desc(String.format(this.ENUM_VALUE_TEMPLATE, description, enums.keySet())).argName("choice").hasArg().build());
        return this.keep(label, description, new Enumerate<V>(enums));
    }

    @Override
    public IComponent<List<String>> List(String label, String shortLabel, String description) {
        this.options.addOption(Option.builder(shortLabel).longOpt(label).required(false).desc(description).argName("value1,value2,...").hasArg().build());
        return this.keep(label, description, new ListArg());
    }

    @Override
    public IComponent<String> Text(String label, String shortLabel, String description, String defaultValue) {
        this.options.addOption(Option.builder(shortLabel).longOpt(label).required(false).desc(String.format(this.DEFAULT_VALUE_TEMPLATE, description, defaultValue)).argName("value").hasArg().build());
        return this.keep(label, description, new Text(defaultValue));
    }

    @Override
    public <V> IComponent<V> Enumerate(String label, String shortLabel, String description, Map<String, V> enums, String defaultSelected) {
        this.options.addOption(Option.builder(shortLabel).longOpt(label).required(false).desc(String.format(this.DEFAULT_VALUE_TEMPLATE, String.format(this.ENUM_VALUE_TEMPLATE, description, enums.keySet()), defaultSelected)).argName("choice").hasArg().build());
        return this.keep(label, description, new Enumerate(enums, defaultSelected));
    }

    @Override
    public IComponent<Boolean> Switch(String label, String shortLabel, String description, Boolean defaultValue) {
        this.options.addOption(Option.builder(shortLabel).longOpt(label).required(false).desc(String.format(this.DEFAULT_VALUE_TEMPLATE, description, defaultValue ? "true" : "false")).build());
        return this.keep(label, description, new Switch(defaultValue));
    }

    @Override
    public IComponent<List<String>> List(String label, String shortLabel, String description, List defaultValue) {
        this.options.addOption(Option.builder(shortLabel).longOpt(label).required(false).desc(String.format(this.DEFAULT_VALUE_TEMPLATE, description, defaultValue)).argName("value1, value2, ...").hasArg().build());
        return this.keep(label, description, new ListArg(defaultValue));
    }
}