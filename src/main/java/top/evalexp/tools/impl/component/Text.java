package top.evalexp.tools.impl.component;

import top.evalexp.tools.interfaces.component.IComponent;

public class Text implements IComponent<String> {
    private String text;
    @Override
    public String get() {
        return this.text;
    }

    public Text() {

    }

    public Text(String defaultValue) {
        this();
        this.text = defaultValue;
    }

    public void setText(String text) {
        this.text = text;
    }
}
