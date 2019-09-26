package mo.visualization.process.plugin.model;

public enum Separator {
    FILE(System.getProperty("file.separator")),
    WINDOWS_FILE("\\"),
    REGEX_WINDOW_FILE("\\\\");

    private final String value;

    Separator(String value){
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
