package top.evalexp.tools.impl.component;

import top.evalexp.tools.interfaces.component.IComponent;

import java.util.ArrayList;
import java.util.List;

public class ListArg<K> implements IComponent<List<K>> {
    private List<K> list = new ArrayList<>();
    @Override
    public List<K> get() {
        return this.list;
    }

    public ListArg() {

    }

    public ListArg(List<K> defaultValue) {
        this();
        this.list = defaultValue;
    }

    public void setList(List<K> list) {
        this.list = list;
    }
}
