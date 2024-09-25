package dev.stephenpearson.blockify.view.enums;

public enum OptionsMenuOption {
    MUSIC_VOLUME("Music Volume"),
    SFX_VOLUME("SFX Volume"),
    CONTROLS("Controls"),
    BACK("Back");

    private final String displayName;

    OptionsMenuOption(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
