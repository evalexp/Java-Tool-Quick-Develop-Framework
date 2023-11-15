# XuanYuan

> XuanYuan is a framework for Fast Java Tool Development.

## How to use

### Auto scan

```bash
java -jar XuanYuan.jar Plugin_Name [args...]
```

For example, use `Coder` plugin:

```bash
java -jar XuanYuan.jar Coder -a Base64 -i 123 -m Encode
```

### Specify Plugin File

```bash
java -jar XuanYuan.jar @Plugin_Path [args...]
```

For example, use `Coder` plugin which is in `/tmp/coder/`:

```bash
java -jar XuanYuan.jar @/tmp/coder/Coder.jar -a Base64 -i 123 -m Encode
```

### @ Syntax Support

You could use `@` syntax to pass plugin locaiton or arguments.

For example, save content in `/tmp/args.txt`:

```txt
-a Base64
-i 123
-m Encode
```

Then pass it like this :

```bash
java -jar XuanYuan.jar Coder @/tmp/args.txt
```

## How to develop a plugin

### Dependence
In this repo, download the `XuanYuanSDK-$VERSION.zip`, and extra it into your src folder.

### Manifest

Pay attention, you need to write a `manifest.json` to describe your plugin, and here's the structure:

```java
public class Manifest {
    private String name;
    private String type;
    private String entry;
    private String author;
    private String version;
    private String description;
}
```

It means you have to struct your json like this:

```json
{
  "name": "Coder",
  "type": "helper",
  "entry": "top.evalexp.tools.plugins.Coder",
  "author": "Evalexp",
  "version": "1.0.0",
  "description": "A coder plugin, for fast encode or decode."
}
```

### IPlugin Implementation

Then you need to define a class which implement IPlugin, it would be the entry for plugin.

And here's `IPlugin` interface:

```java
package top.evalexp.tools.interfaces.plugin;

public interface IPlugin {
    public void setup(IContext context);
    public void handle(IResult result);
}
```

### Get Input Data

In `setup`, you need to specific your plugin input arguments, there are four support types:

* Text
* Switch
* Enumerate
* List

It is easy for you to get input from user, just use `IContenxt.Text` or other method to create a input component, for example:

```java
public void setup(IContext context) {
    IComponent<Mode> mode = context.Enumerate("Mode","m", "Mode for plugin", new HashMap<>() {{
        put("Encode", Mode.Encode);
        put("Decode", Mode.Decode);
    }});
    IComponent<String> input = context.Text("Input", "i", "Input data");
    IComponent<Boolean> enable = context.Switch("Enable", "e", "Enable debug");
    IComponent<List<String>> fileList = context.List("Files", "fs", "File List");
}
```

> Notice: `Switch` has a default value `false`, `List` has a default value `[]`.

If you want to set the input as optional, please set the default value:

```java
public void setup(IContext context) {
    IComponent<Mode> mode = context.Enumerate("Mode","m", "Mode for plugin", new HashMap<>() {{
        put("Encode", Mode.Encode);
        put("Decode", Mode.Decode);
    }}, "Encode");  // default return Mode.Encode
    IComponent<String> input = context.Text("Input", "i", "Input data", "TestData");
    IComponent<Boolean> disable = context.Switch("Disable", "d", "Disable debug", false);
    IComponent<List<String>> fileList = context.List("Files", "fs", "File List", new ArrayList<>(){{
            add("/tmp/1");
            add("/tmp/2");
        }});
}
```

> Notice: If you set `Switch` default value to be `false`, the argument pass from cmdline `-Disable` would set its value to be `true`

### Handle Task

In `handle`, you need to handle the task and write result via `IResult.write` or `IResult.writeln`.

To get the input value, call `IComponent.get`:

```java
@Override
public void handle(IResult result) {
    result.writeln(new String(mode.get()));
    result.writeln(new String(input.get()));
    result.writeln(new String(enable.get()));
    result.writeln(new String(fileList.get()));
}
```

### UnitTest

Please use `JTestContext` and `JTestResult`, for example:

```java
@org.junit.jupiter.api.Test
public void test() throws NoSuchFieldException, IllegalAccessException {
    JTestContext context = new JTestContext();
    JTestResult result = new JTestResult();
    Coder coder = new Coder();
    coder.setup(context);
    JTestContext.setValue(coder, "mode", "Encode");
    JTestContext.setValue(coder, "input_format", "Raw");
    JTestContext.setValue(coder, "output_format", "Raw");
    JTestContext.setValue(coder, "algorithm", "Base64");
    JTestContext.setValue(coder, "input", "123");
    coder.handle(result);
    System.out.println(result.getResult());
    Assertions.assertEquals(result.getResult(), "MTIz\n");
}
```

### Plugin Example

* [Coder - XuanYuan Plugin](https://github.com/evalexp/Coder-XuanYuanPlugin)