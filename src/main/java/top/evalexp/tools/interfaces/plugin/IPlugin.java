package top.evalexp.tools.interfaces.plugin;

public interface IPlugin {
    public void setup(IContext context);
    public void handle(IResult result);
}
