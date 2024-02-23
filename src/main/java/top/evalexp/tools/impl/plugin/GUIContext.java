package top.evalexp.tools.impl.plugin;

import net.miginfocom.swing.MigLayout;
import top.evalexp.tools.common.util.Pair;
import top.evalexp.tools.impl.component.Enumerate;
import top.evalexp.tools.impl.component.ListArg;
import top.evalexp.tools.impl.component.Switch;
import top.evalexp.tools.impl.component.Text;
import top.evalexp.tools.interfaces.component.IComponent;
import top.evalexp.tools.interfaces.plugin.IContext;
import top.evalexp.tools.interfaces.plugin.IPlugin;
import top.evalexp.tools.interfaces.plugin.IResult;
import top.evalexp.tools.ui.GUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GUIContext implements IContext {
    interface Callback {
        void exec(JTextArea area);
    }

    private final JPanel panel;
    private int maxEnumerateLength = -1;    // for combo box length
    private int maxLabelLength = -1;    // for label length
    private final List<Pair<String, Pair<JComponent, String>>> enumerates = new ArrayList<>();

    private final List<Pair<String, Pair<JComponent, String>>> listArgs = new ArrayList<>();
    private final List<Pair<String, Pair<JComponent, String>>> switches = new ArrayList<>();
    private final List<Pair<String, Pair<JComponent, String>>> texts = new ArrayList<>();
    private final String separator = "--------------------------------------------------";

    private final JButton action = new JButton("Exec");
    private final JTextArea output = new JTextArea();
    private final JScrollPane outputPane = new JScrollPane(output);
    public GUIContext(JPanel panel) {
        this.panel = panel;
        this.panel.setLayout(new MigLayout("nogrid"));
        Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        this.panel.setBorder(border);
        this.output.setEditable(false);
        this.output.setLineWrap(true);
    }

    /**
     * bind result receiver with gui context
     * @param plugin plugin object
     * @param result result receiver
     */
    public void prepareForHandle(IPlugin plugin, IResult result) {
        if (result instanceof GUIResult) {
            ((GUIResult) result).setOutputComponent(output);
        }
        result.write(String.format("Plugin Output: \n%s\n", separator));
        this.action.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (GUIContext.this.action.isEnabled()) {
                    GUI.getExecutorService().submit(() -> {
                        // show result for human like format
                        GUIContext.this.action.setEnabled(false);
                        try {
                            plugin.handle(result);
                            String output = GUIContext.this.output.getText();
                            if (!output.endsWith("\n")) result.writeln("");
                            result.writeln(separator);
                        } catch (Exception exception) {
                            // output exception trace to plugin output
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            PrintStream printStream = new PrintStream(baos);
                            exception.printStackTrace(printStream);
                            String trace = new String(baos.toByteArray());
                            printStream.close();
                            try {
                                baos.close();
                                result.writeln(String.format("Plugin exception: \n%s", trace));
                                result.writeln(separator);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        GUIContext.this.action.setEnabled(true);
                    });
                }
            }
        });
    }

    /**
     * update max label length
     * @param label label text
     */
    private void updateMaxLabelLength(String label) {
        if (label.length() > this.maxLabelLength) this.maxLabelLength = label.length();
    }

    /**
     * get label with preferred size base on max label length
     * @param text label text
     * @param isDesc if label is for description
     * @return JLabel object
     */
    private JLabel getLabel(String text, boolean isDesc) {
        JLabel label = new JLabel(text);
        if (isDesc)
            label.setForeground(Color.GRAY);
        else
            label.setPreferredSize(new Dimension(this.maxLabelLength * 9, 0));
        return label;
    }

    /**
     * render should call after plugin setup
     * and this method would render the actual ui for it
     */
    public void render() {
        // render enumerates
        for (Pair<String, Pair<JComponent, String>> enumerate : this.enumerates) {
            this.panel.add(this.getLabel(enumerate.key() + " : ", false));
            enumerate.value().key().setPreferredSize(new Dimension(this.maxEnumerateLength * 12, 0));
            this.panel.add(enumerate.value().key());
            this.panel.add(this.getLabel("> " + enumerate.value().value(), true), "wrap");
        }
        // render list args
        for (Pair<String, Pair<JComponent, String>> listArg : this.listArgs) {
            this.panel.add(this.getLabel(listArg.key() + " : ", false));
            JScrollPane component_pane = new JScrollPane(listArg.value().key());
            component_pane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 27 * 2));
            this.panel.add(component_pane, "spanx 2, wrap, growx, pushx");
        }
        // render switches
        for (Pair<String, Pair<JComponent, String>> s : this.switches) {
            this.panel.add(this.getLabel(s.key() + " : ", false));
            this.panel.add(s.value().key());
            this.panel.add(this.getLabel("> " + s.value().value(), true), "wrap");
        }
        // render texts
        for(Pair<String, Pair<JComponent, String>> text: this.texts) {
            this.panel.add(this.getLabel(text.key() + " : ", false));
            JScrollPane component_pane = new JScrollPane(text.value().key());
            component_pane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 27 * 2));
            this.panel.add(component_pane, "spanx 2, wrap, growx, pushx");
        }
        // render additional notice, run button and output text area
        JLabel notice = this.getLabel("Once you have specified your input, just CLICK THIS BUTTON =====>", false);
        notice.setForeground(Color.GRAY);
        this.panel.add(notice, "growx, al right");
        this.panel.add(this.action, "wrap");
        this.adjustOutputHeight();
        this.panel.add(this.outputPane, "spanx 3, grow, wrap");
    }

    /**
     * for adjusting plugin output textarea size
     */
    public void adjustOutputHeight() {
        int component_height = (this.enumerates.size() + this.texts.size() * 2 + this.switches.size() + this.listArgs.size() * 2 + 1) * 30 + 115;
        int base_height = GUI.getHeight() - component_height;
        this.outputPane.setPreferredSize(new Dimension(-1, Math.max(base_height, 150)));
    }

    /**
     *
     * @param callback for binding properties
     * @param description tooltip text
     * @return JTextArea with content change callback binding
     */
    private JTextArea makeJTextArea(Callback callback, String description) {
        JTextArea area = new JTextArea();
        area.setToolTipText(description);
        area.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                callback.exec(area);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                callback.exec(area);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        area.setLineWrap(true);
        area.setRows(2);
        return area;
    }

    @Override
    public IComponent<String> Text(String label, String shortLabel, String description) {
        return this.Text(label, shortLabel, description, "");
    }

    @Override
    public IComponent<Boolean> Switch(String label, String shortLabel, String description) {
        return this.Switch(label, shortLabel, description, false);
    }

    @Override
    public <V> IComponent<V> Enumerate(String label, String shortLabel, String description, Map<String, V> enums) {
        return this.Enumerate(label, shortLabel, description, enums, enums.entrySet().iterator().next().getKey());
    }

    @Override
    public IComponent<List<String>> List(String label, String shortLabel, String description) {
        return this.List(label, shortLabel, description, new ArrayList<String>());
    }

    @Override
    public IComponent<String> Text(String label, String shortLabel, String description, String defaultValue) {
        this.updateMaxLabelLength(label);
        Text text = new Text();
        text.setText(defaultValue);
        JTextArea area = this.makeJTextArea(f -> text.setText(f.getText()), description);
        this.texts.add(new Pair<>(label, new Pair<>(area, description)));
        area.setText(defaultValue);
        return text;
    }

    @Override
    public <V> IComponent<V> Enumerate(String label, String shortLabel, String description, Map<String, V> enums, String defaultSelected) {
        this.updateMaxLabelLength(label);
        Enumerate<V> enumerate = new Enumerate<>(enums);
        List<String> options = new ArrayList<>();
        for (Map.Entry<String, V> entry : enums.entrySet()) {
            options.add(entry.getKey());
            if (entry.getKey().length() > this.maxEnumerateLength) this.maxEnumerateLength = entry.getKey().length();
        }
        JComboBox<?> comboBox = new JComboBox<>(options.toArray());
        comboBox.setEditable(false);
        comboBox.addActionListener(e -> {
            enumerate.setSelected((String) comboBox.getSelectedItem());
        });
        comboBox.setSelectedItem(defaultSelected);
        this.enumerates.add(new Pair<>(label, new Pair<>(comboBox, description)));
        return enumerate;
    }

    @Override
    public IComponent<Boolean> Switch(String label, String shortLabel, String description, Boolean defaultValue) {
        this.updateMaxLabelLength(label);
        Switch s = new Switch(defaultValue);
        List<String> options = new ArrayList<String>() {{ add("Enable"); add("Disable"); }};
        JComboBox<?> comboBox = new JComboBox<>(options.toArray());
        comboBox.setEditable(false);
        comboBox.addActionListener(e -> {
            if (comboBox.getSelectedItem().equals("Enable") && !s.get()) {
                s.doSwitch();
            } else if (comboBox.getSelectedItem().equals("Disable") && s.get()) {
                s.doSwitch();
            }
        });
        this.switches.add(new Pair<>(label, new Pair<>(comboBox, description)));
        if (!defaultValue)   comboBox.setSelectedItem("Disable");
        return s;
    }

    @Override
    public IComponent<List<String>> List(String label, String shortLabel, String description, List defaultValue) {
        this.updateMaxLabelLength(label);
        ListArg<String> args = new ListArg<>();
        JTextArea area = this.makeJTextArea(f -> {
            try {
                args.setList(Arrays.asList(f.getText().split(",")));
            } catch (Exception ignored) {}
        }, String.format("%s > use ','(comma) as separator", description));
        this.listArgs.add(new Pair<>(label, new Pair<>(area, description)));
        args.setList(defaultValue);
        return args;
    }
}
