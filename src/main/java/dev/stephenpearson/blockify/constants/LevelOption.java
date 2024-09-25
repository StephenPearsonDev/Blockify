package dev.stephenpearson.blockify.constants;

public enum LevelOption {
    LEVEL_1("Level 1"),
    LEVEL_2("Level 2"),
    LEVEL_3("Level 3"),
    LEVEL_4("Level 4"),
    LEVEL_5("Level 5"),
    LEVEL_6("Level 6"),
    LEVEL_7("Level 7"),
    LEVEL_8("Level 8"),
    LEVEL_9("Level 9"),
    LEVEL_10("Level 10");

    private final String displayName;

    LevelOption(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
