package top.evalexp.tools.impl.plugin;

import top.evalexp.tools.interfaces.plugin.IResult;

public class CommandResult implements IResult {
    @Override
    public void write(String s) {
        System.out.print(s);
    }

    @Override
    public void writeln(String s) {
        System.out.println(s);
    }
}
