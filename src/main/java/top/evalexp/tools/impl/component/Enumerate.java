package top.evalexp.tools.impl.component;

import top.evalexp.tools.interfaces.component.IComponent;
import java.util.Map;

public class Enumerate<V> implements IComponent<V> {
    private V value;
    private Map<String, V> enums;
    @Override
    public V get() {
        return value;
    }

    public Enumerate(Map<String, V> enums) {
        this.enums = enums;
    }

    public Enumerate(Map<String, V> enums, String defaultSelected) {
        this(enums);
        this.setSelected(defaultSelected);
    }

    public void setSelected(String key) {
        this.value = this.enums.get(key);
    }
}
