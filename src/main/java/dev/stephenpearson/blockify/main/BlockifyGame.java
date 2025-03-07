package dev.stephenpearson.blockify.main;

import java.awt.Dimension;
import javax.swing.JFrame;

import dev.stephenpearson.blockify.controller.GameController;
import dev.stephenpearson.blockify.model.GameModel;
import dev.stephenpearson.blockify.view.GamePanel;

public class BlockifyGame extends JFrame {
    private final GamePanel gamePanel;
    private final GameModel gameModel;
    private final GameController gameController;

    public BlockifyGame() {
        setTitle("Blockify");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        
        gameModel = new GameModel();
        gameController = new GameController(gameModel);
        gamePanel = new GamePanel(gameModel, gameController);
        add(gamePanel);
        pack();
        setLocationRelativeTo(null);
        
       
        
        setMinimumSize(new Dimension(gameModel.getMinWindowWidth(), gameModel.getMinWindowHeight()));
        setMaximumSize(new Dimension(gameModel.getMaxWindowWidth(), gameModel.getMaxWindowWidth()));

        WindowResizeListener resizeListener = new WindowResizeListener(gamePanel, gameModel);
        addComponentListener(resizeListener);
    }
}
