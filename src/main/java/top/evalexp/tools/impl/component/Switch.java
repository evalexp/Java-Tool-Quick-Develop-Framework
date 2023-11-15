package top.evalexp.tools.impl.component;

import top.evalexp.tools.interfaces.component.IComponent;

public class Switch implements IComponent<Boolean> {
    private Boolean enable = false;
    @Override
    public Boolean get() {
        return this.enable;
    }

    public Switch() {

    }

    public Switch(Boolean defaultValue) {
        this();
        this.enable = defaultValue;
    }

    public void doSwitch() {
        this.enable = !this.enable;
    }
}
