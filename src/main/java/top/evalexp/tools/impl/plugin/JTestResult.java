package top.evalexp.tools.impl.plugin;

import top.evalexp.tools.interfaces.plugin.IResult;

public class JTestResult implements IResult {
    StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void write(String s) {
        this.stringBuilder.append(s);
    }

    @Override
    public void writeln(String s) {
        this.stringBuilder.append(s);
        this.stringBuilder.append("\n");
    }

    public String getResult() {
        return this.stringBuilder.toString();
    }
}
