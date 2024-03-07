package top.evalexp.tools.impl.plugin;

import top.evalexp.tools.interfaces.plugin.IResult;

import javax.swing.*;

public class GUIResult implements IResult {
    private JTextArea component;
    public void setOutputComponent(JTextArea component) {
        this.component = component;
    }

    /**
     * write output
     * @param s output string
     */
    @Override
    public void write(String s) {
        this.component.append(s);
    }

    /**
     * write output
     * @param s output string
     */
    @Override
    public void writeln(String s) {
        this.component.append(s + "\n");
    }
}
