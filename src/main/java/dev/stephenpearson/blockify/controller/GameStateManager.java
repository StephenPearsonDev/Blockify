package dev.stephenpearson.blockify.controller;

import dev.stephenpearson.blockify.constants.GameConstants;

public class GameStateManager {
    private int gameState = GameConstants.MENU_STATE;

    public int getGameState() {
        return gameState;
    }

    public void setGameState(int gameState) {
        this.gameState = gameState;
    }

    public boolean isMenuState() {
        return gameState == GameConstants.MENU_STATE;
    }

    public boolean isLevelSelectState() {
        return gameState == GameConstants.LEVEL_SELECT_STATE;
    }

    public boolean isOptionsState() {
        return gameState == GameConstants.OPTIONS_STATE;
    }

    public boolean isGameState() {
        return gameState == GameConstants.GAME_STATE;
    }

    public boolean isFlashingState() {
        return gameState == GameConstants.FLASHING_STATE;
    }

    public boolean isGameOverState() {
        return gameState == GameConstants.GAME_OVER_STATE;
    }

    public boolean isLevelUpState() {
        return gameState == GameConstants.LEVEL_UP_STATE;
    }

    public boolean isPauseState() {
        return gameState == GameConstants.PAUSE_STATE;
    }
}
