package top.evalexp.tools.impl.plugin;

import org.apache.commons.cli.ParseException;
import top.evalexp.tools.impl.component.Enumerate;
import top.evalexp.tools.impl.component.ListArg;
import top.evalexp.tools.impl.component.Switch;
import top.evalexp.tools.impl.component.Text;
import top.evalexp.tools.interfaces.component.IComponent;
import top.evalexp.tools.interfaces.plugin.IContext;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class JTestContext implements IContext {

    public static <V> void setValue(Object target, String memberName, V value) throws NoSuchFieldException, IllegalAccessException, ParseException {
        Field field = target.getClass().getDeclaredField(memberName);
        field.setAccessible(true);
        IComponent component = (IComponent) field.get(target);
        if (component instanceof Text && value instanceof String) {
            ((Text) component).setText((String) value);
        } else if (component instanceof ListArg<?> && value instanceof List) {
            ((ListArg<?>) component).setList((List) value);
        } else if (component instanceof Switch && value instanceof Boolean) {
            ((Switch) component).doSwitch();
        } else if (component instanceof Enumerate && value instanceof String) {
            if (!((Enumerate) component).setSelected((String) value))
                throw new ParseException("Invalid selection");
        }
    }

    @Override
    public IComponent<String> Text(String label, String shortLabel, String description) {
        return new Text();
    }


    @Override
    public IComponent<Boolean> Switch(String label, String shortLabel, String description) {
        return new Switch();
    }

    @Override
    public <V> IComponent<V> Enumerate(String label, String shortLabel, String description, Map<String, V> enums) {
        return new Enumerate<>(enums);
    }

    @Override
    public IComponent<List<String>> List(String label, String shortLabel, String description) {
        return new ListArg<>();
    }

    @Override
    public IComponent<String> Text(String label, String shortLabel, String description, String defaultValue) {
        return new Text(defaultValue);
    }

    @Override
    public <V> IComponent<V> Enumerate(String label, String shortLabel, String description, Map<String, V> enums, String defaultSelected) {
        return new Enumerate<>(enums, defaultSelected);
    }

    @Override
    public IComponent<Boolean> Switch(String label, String shortLabel, String description, Boolean defaultValue) {
        return new Switch(defaultValue);
    }

    @Override
    public IComponent<List<String>> List(String label, String shortLabel, String description, List defaultValue) {
        return new ListArg<>(defaultValue);
    }
}
