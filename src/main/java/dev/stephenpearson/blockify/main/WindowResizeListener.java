package dev.stephenpearson.blockify.main;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import dev.stephenpearson.blockify.view.GamePanel;
import dev.stephenpearson.blockify.constants.GameConstants;

public class WindowResizeListener extends ComponentAdapter {
    private final GamePanel gamePanel;

    public WindowResizeListener(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        int newWidth = e.getComponent().getWidth();
        int newHeight = e.getComponent().getHeight();
        double aspectRatio = gamePanel.getAspectRatio();

        if (newWidth < gamePanel.getMinWindowWidth()) newWidth = gamePanel.getMinWindowWidth();
        if (newWidth > gamePanel.getMaxWindowWidth()) newWidth = gamePanel.getMaxWindowWidth();
        if (newHeight < gamePanel.getMinWindowHeight()) newHeight = gamePanel.getMinWindowHeight();
        if (newHeight > gamePanel.getMaxWindowHeight()) newHeight = gamePanel.getMaxWindowHeight();

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
