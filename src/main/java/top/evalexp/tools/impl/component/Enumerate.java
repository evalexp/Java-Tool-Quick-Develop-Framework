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
        if (this.enums.containsKey(defaultSelected))    this.value = this.enums.get(defaultSelected);
    }

    public boolean setSelected(String key) {
        if (this.enums.containsKey(key)) {
            this.value = this.enums.get(key);
            return true;
        }
        return false;
    }
}
