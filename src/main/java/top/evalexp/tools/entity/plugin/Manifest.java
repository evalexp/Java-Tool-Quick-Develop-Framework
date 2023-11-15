package top.evalexp.tools.entity.plugin;

public class Manifest {
    private String name;
    private String type;
    private String entry;
    private String author;
    private String version;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Manifest{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", entry='" + entry + '\'' +
                ", author='" + author + '\'' +
                ", version='" + version + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
