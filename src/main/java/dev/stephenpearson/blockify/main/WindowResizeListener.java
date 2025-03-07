package dev.stephenpearson.blockify.main;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import dev.stephenpearson.blockify.constants.GameConstants;
import dev.stephenpearson.blockify.model.GameModel;
import dev.stephenpearson.blockify.view.GamePanel;

public class WindowResizeListener extends ComponentAdapter {
    private final GameModel gameModel;
    private final GamePanel gamePanel;

    public WindowResizeListener(GamePanel gamePanel, GameModel gameModel) {
        this.gamePanel = gamePanel;
        this.gameModel = gameModel;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        int newWidth = e.getComponent().getWidth();
        int newHeight = e.getComponent().getHeight();
        double aspectRatio = gameModel.getAspectRatio();

        if (newWidth < gameModel.getMinWindowWidth()) newWidth = gameModel.getMinWindowWidth();
        if (newWidth > gameModel.getMaxWindowWidth()) newWidth = gameModel.getMaxWindowWidth();
        if (newHeight < gameModel.getMinWindowHeight()) newHeight = gameModel.getMinWindowHeight();
        if (newHeight > gameModel.getMaxWindowHeight()) newHeight = gameModel.getMaxWindowHeight();

        double newAspectRatio = (double) newWidth / newHeight;
        if (Math.abs(newAspectRatio - aspectRatio) > GameConstants.ASPECT_RATIO_TOLERANCE) {
            if (newAspectRatio > aspectRatio) {
                newWidth = (int) (newHeight * aspectRatio);
            } else {
                newHeight = (int) (newWidth / aspectRatio);
            }
            e.getComponent().setSize(newWidth, newHeight);
            gamePanel.repaint();
        }
    }
}
