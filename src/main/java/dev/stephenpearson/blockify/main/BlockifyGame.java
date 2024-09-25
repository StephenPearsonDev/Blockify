package dev.stephenpearson.blockify.main;

import java.awt.Dimension;
import javax.swing.JFrame;
import dev.stephenpearson.blockify.view.GamePanel;

public class BlockifyGame extends JFrame {
    private final GamePanel gamePanel;

    public BlockifyGame() {
        setTitle("Blockify");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);

        gamePanel = new GamePanel();
        add(gamePanel);
        pack();
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(gamePanel.getMinWindowWidth(), gamePanel.getMinWindowHeight()));
        setMaximumSize(new Dimension(gamePanel.getMaxWindowWidth(), gamePanel.getMaxWindowHeight()));

        WindowResizeListener resizeListener = new WindowResizeListener(gamePanel);
        addComponentListener(resizeListener);
    }
}
