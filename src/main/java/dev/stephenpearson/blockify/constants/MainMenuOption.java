package dev.stephenpearson.blockify.constants;

public enum MainMenuOption {
    START_GAME("Start Game"),
    LEVEL_SELECTION("Level Selection"),
    OPTIONS("Options"),
    EXIT("Exit");

    private final String displayName;

    MainMenuOption(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
