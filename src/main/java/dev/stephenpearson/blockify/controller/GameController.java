package dev.stephenpearson.blockify.controller;

import dev.stephenpearson.blockify.constants.GameConstants;
import dev.stephenpearson.blockify.model.GameModel;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class GameController {
    private GameModel model;
    private Timer gameTimer;
    
    public GameController(GameModel model) {
        this.model = model;
        startGameTimer();
    }
    
    private void startGameTimer() {
        int delay = Math.max(100, 500 - (model.getLevel() - 1) * 40);
        gameTimer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(model.getGameState() == GameConstants.GAME_STATE && !model.isPaused()) {
                    if(model.isCurrentPieceJustSpawned())
                        model.setCurrentPieceJustSpawned(false);
                    else
                        model.movePieceDown();
                }
            }
        });
        gameTimer.start();
    }
    
    public void triggerScreenShake() {
        if(model.isScreenShake()) return;
        model.setScreenShake(true);
        Timer shakeTimer = new Timer(GameConstants.SHAKE_INTERVAL, new ActionListener() {
            private int elapsed = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                if(elapsed < GameConstants.SHAKE_DURATION) {
                    int offsetX = model.getRandom().nextInt(2 * GameConstants.SHAKE_INTENSITY + 1) - GameConstants.SHAKE_INTENSITY;
                    int offsetY = model.getRandom().nextInt(2 * GameConstants.SHAKE_INTENSITY + 1) - GameConstants.SHAKE_INTENSITY;
                    model.setShakeOffsetX(offsetX);
                    model.setShakeOffsetY(offsetY);
                    elapsed += GameConstants.SHAKE_INTERVAL;
                } else {
                    model.setScreenShake(false);
                    model.setShakeOffsetX(0);
                    model.setShakeOffsetY(0);
                    ((Timer)e.getSource()).stop();
                }
            }
        });
        shakeTimer.start();
    }
    
    public void processKeyEvent(KeyEvent e) {
        int code = e.getKeyCode();
        if(model.getGameState() == GameConstants.MENU_STATE) {
            if(code == KeyEvent.VK_UP) {
                int opt = model.getSelectedMenuOption();
                model.setSelectedMenuOption((opt - 1 + model.getMainMenuOptions().length) % model.getMainMenuOptions().length);
            } else if(code == KeyEvent.VK_DOWN) {
                int opt = model.getSelectedMenuOption();
                model.setSelectedMenuOption((opt + 1) % model.getMainMenuOptions().length);
            } else if(code == KeyEvent.VK_ENTER) {
                if(model.getSelectedMenuOption() == 0) {
                    model.resetGame();
                    model.setGameState(GameConstants.GAME_STATE);
                } else if(model.getSelectedMenuOption() == 1) {
                    model.setGameState(GameConstants.LEVEL_SELECT_STATE);
                } else if(model.getSelectedMenuOption() == 2) {
                    model.setGameState(GameConstants.OPTIONS_STATE);
                } else if(model.getSelectedMenuOption() == 3) {
                    System.exit(0);
                }
            }
        } else if(model.getGameState() == GameConstants.GAME_STATE) {
            if(code == KeyEvent.VK_LEFT) {
                model.moveCurrentPieceLeft();
            } else if(code == KeyEvent.VK_RIGHT) {
                model.moveCurrentPieceRight();
            } else if(code == KeyEvent.VK_DOWN) {
                model.movePieceDown();
            } else if(code == KeyEvent.VK_UP) {
                model.rotateCurrentPiece();
            } else if(code == KeyEvent.VK_SPACE) {
                model.dropCurrentPiece();
                triggerScreenShake();
            } else if(code == KeyEvent.VK_K) {
                model.changePieceColors();
            } else if(code == KeyEvent.VK_G) {
                model.toggleGhost();
            } else if(code == KeyEvent.VK_ESCAPE) {
                model.setGameState(GameConstants.PAUSE_STATE);
            }
        } else if(model.getGameState() == GameConstants.PAUSE_STATE) {
            if(code == KeyEvent.VK_ESCAPE) {
                model.setGameState(GameConstants.GAME_STATE);
            } else if(code == KeyEvent.VK_SPACE) {
                model.setGameState(GameConstants.MENU_STATE);
            }
        } else if(model.getGameState() == GameConstants.LEVEL_SELECT_STATE) {
            if(code == KeyEvent.VK_UP) {
            } else if(code == KeyEvent.VK_DOWN) {
            } else if(code == KeyEvent.VK_ENTER) {
                model.resetGame();
                model.setGameState(GameConstants.GAME_STATE);
            } else if(code == KeyEvent.VK_ESCAPE) {
                model.setGameState(GameConstants.MENU_STATE);
            }
        } else if(model.getGameState() == GameConstants.OPTIONS_STATE) {
            if(code == KeyEvent.VK_ESCAPE) {
                model.setGameState(GameConstants.MENU_STATE);
            }
        } else if(model.getGameState() == GameConstants.GAME_OVER_STATE) {
            if(code == KeyEvent.VK_ENTER) {
                model.resetGame();
            } else if(code == KeyEvent.VK_ESCAPE) {
                model.setGameState(GameConstants.MENU_STATE);
            }
        }
    }
}
