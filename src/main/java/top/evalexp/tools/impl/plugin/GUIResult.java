package top.evalexp.tools.impl.plugin;

import top.evalexp.tools.interfaces.plugin.IResult;

import javax.swing.*;

public class GUIResult implements IResult {
    private JTextArea component;
    private final StringBuffer stringBuffer = new StringBuffer();
    public void setOutputComponent(JTextArea component) {
        this.component = component;
    }

    /**
     * write output
     * @param s output string
     */
    @Override
    public void write(String s) {
        this.stringBuffer.append(s);
        this.component.setText(this.stringBuffer.toString());
    }

    /**
     * write output
     * @param s output string
     */
    @Override
    public void writeln(String s) {
        this.stringBuffer.append(s);
        this.stringBuffer.append("\n");
        this.component.setText(this.stringBuffer.toString());
    }
}
