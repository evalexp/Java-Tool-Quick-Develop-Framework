package top.evalexp.tools.interfaces.plugin;

import top.evalexp.tools.interfaces.component.IComponent;

import java.util.List;
import java.util.Map;

public interface IContext {
    public IComponent<String> Text(String label, String shortLabel, String description);
    public IComponent<Boolean> Switch(String label, String shortLabel, String description);
    public <V> IComponent<V> Enumerate(String label, String shortLabel, String description, Map<String, V> enums);
    public IComponent<List<String>> List(String label, String shortLabel, String description);
    public IComponent<String> Text(String label, String shortLabel, String description, String defaultValue);
    public <V> IComponent<V> Enumerate(String label, String shortLabel, String description, Map<String, V> enums, String defaultSelected);
    public IComponent<Boolean> Switch(String label, String shortLabel, String description, Boolean defaultValue);
    public IComponent<List<String>> List(String label, String shortLabel, String description, List defaultValue);
}
