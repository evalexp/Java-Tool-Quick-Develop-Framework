package top.evalexp.tools.entity.plugin;

/**
 * Entity for plugin manifest
 */
public class Manifest {
    private String name;
    private String type;
    private String entry;
    private String author;
    private String version;
    private String description;
    private String path;

    public String getByIndex(int index) {
        switch (index) {
            case 0:
                return name;
            case 1:
                return author;
            case 2:
                return version;
            case 3:
                return description;
            default:
                return null;
        }
    }

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
