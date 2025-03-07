package dev.stephenpearson.blockify.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

import dev.stephenpearson.blockify.constants.GameConstants;
import dev.stephenpearson.blockify.controller.GameController;
import dev.stephenpearson.blockify.main.Tetromino;
import dev.stephenpearson.blockify.model.GameModel;
import dev.stephenpearson.blockify.model.GameModelObserver;

public class GamePanel extends JPanel implements KeyListener, GameModelObserver {
    private GameModel model;
    private GameController controller;
    
    public GamePanel(GameModel model, GameController controller) {
        this.model = model;
        this.controller = controller;
        model.addObserver(this);
        setFocusable(true);
        setDoubleBuffered(true);
        addKeyListener(this);
        int initialWidth = model.getBOARD_WIDTH() * model.getTILE_SIZE() * 3 + 40;
        int initialHeight = GameConstants.BASE_BOARD_HEIGHT * model.getTILE_SIZE() + 100;
        setPreferredSize(new Dimension(initialWidth, initialHeight));
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        controller.processKeyEvent(e);
    }
    
    @Override
    public void keyReleased(KeyEvent e) { }
    
    @Override
    public void keyTyped(KeyEvent e) { }
    
    @Override
    public void update() {
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        double scaleFactor = model.getScaleFactor(getWidth(), getHeight());
        g2d.scale(scaleFactor, scaleFactor);
        g2d.setColor(java.awt.Color.black);
        g2d.fillRect(0, 0, (int)(getWidth()/scaleFactor), (int)(getHeight()/scaleFactor));
        if(model.isScreenShake()){
            g2d.translate(model.getShakeOffsetX(), model.getShakeOffsetY());
        }
        switch(model.getGameState()){
            case GameConstants.MENU_STATE:
                drawMainMenu(g2d);
                break;
            case GameConstants.LEVEL_SELECT_STATE:
                drawLevelSelectMenu(g2d);
                break;
            case GameConstants.OPTIONS_STATE:
                drawOptionsMenu(g2d);
                break;
            case GameConstants.GAME_STATE:
            case GameConstants.FLASHING_STATE:
            case GameConstants.GAME_OVER_STATE:
            case GameConstants.LEVEL_UP_STATE:
                drawGame(g2d);
                if(model.getGameState() == GameConstants.FLASHING_STATE){
                    drawFlashingLines(g2d);
                }
                if(model.getGameState() == GameConstants.GAME_OVER_STATE){
                    drawGameOverOverlay(g2d);
                }
                if(model.getGameState() == GameConstants.LEVEL_UP_STATE){
                    drawLevelUpFlash(g2d);
                }
                break;
            case GameConstants.PAUSE_STATE:
                drawGame(g2d);
                drawPauseMenu(g2d);
                break;
            default:
                break;
        }
        if(model.isScreenShake()){
            g2d.translate(-model.getShakeOffsetX(), -model.getShakeOffsetY());
        }
        g2d.dispose();
    }
    
    private void drawMainMenu(Graphics2D g) {
        int panelWidth = (int)(getWidth()/model.getScaleFactor(getWidth(), getHeight()));
        int panelHeight = (int)(getHeight()/model.getScaleFactor(getWidth(), getHeight()));
        int centerX = panelWidth/2;
        String title = "Blockify";
        int titleFontSize = (int)(model.getRetroFont().getSize()*5);
        Font titleFont = model.getRetroFont().deriveFont(java.awt.Font.BOLD, titleFontSize);
        g.setFont(titleFont);
        int titleWidth = g.getFontMetrics().stringWidth(title);
        int titleX = centerX - titleWidth/2;
        int titleY = panelHeight/2 - 100;
        int letterX = titleX;
        for(int i=0;i<title.length();i++){
            char c = title.charAt(i);
            g.setColor(model.getPieceColors()[i % model.getPieceColors().length]);
            String letter = String.valueOf(c);
            int letterWidth = g.getFontMetrics().charWidth(c);
            g.drawString(letter, letterX, titleY);
            letterX += letterWidth;
        }
        g.setFont(model.getRetroFont());
        g.setColor(java.awt.Color.white);
        String[] options = model.getMainMenuOptions();
        int startY = titleY + 75;
        for(int i=0;i<options.length;i++){
            g.setColor(i==model.getSelectedMenuOption()?java.awt.Color.white:java.awt.Color.gray);
            int textWidth = g.getFontMetrics().stringWidth(options[i]);
            g.drawString(options[i], centerX - textWidth/2, startY + i*40);
        }
        g.setColor(java.awt.Color.gray);
        String footerText1 = "Created by Stephen Pearson";
        String footerText2 = "https://github.com/StephenPearsonDev/Blockify";
        int footerY = panelHeight - 60;
        int footerX1 = centerX - g.getFontMetrics().stringWidth(footerText1)/2;
        int footerX2 = centerX - g.getFontMetrics().stringWidth(footerText2)/2;
        g.drawString(footerText1, footerX1, footerY);
        g.drawString(footerText2, footerX2, footerY+40);
    }
    
    private void drawLevelSelectMenu(Graphics2D g) {
        int panelWidth = (int)(getWidth()/model.getScaleFactor(getWidth(), getHeight()));
        int centerX = panelWidth/2;
        String title = "Blockify";
        int titleFontSize = (int)(model.getRetroFont().getSize()*4);
        Font titleFont = model.getRetroFont().deriveFont(java.awt.Font.BOLD, titleFontSize);
        g.setFont(titleFont);
        int titleWidth = g.getFontMetrics().stringWidth(title);
        int titleX = centerX - titleWidth/2;
        int titleY = 100;
        int letterX = titleX;
        for(int i=0;i<title.length();i++){
            char c = title.charAt(i);
            g.setColor(model.getPieceColors()[i % model.getPieceColors().length]);
            String letter = String.valueOf(c);
            int letterWidth = g.getFontMetrics().charWidth(c);
            g.drawString(letter, letterX, titleY);
            letterX += letterWidth;
        }
        g.setFont(model.getRetroFont());
        g.setColor(java.awt.Color.white);
        g.drawString("Press ENTER to select", centerX-80, titleY+40);
        g.drawString("Press ESC to Back", centerX-60, titleY+70);
        String[] levOptions = model.getLevelOptions();
        int startY = titleY+100;
        for(int i=0;i<levOptions.length;i++){
            g.setColor(i==model.getSelectedLevelOption()?java.awt.Color.white:java.awt.Color.gray);
            int textWidth = g.getFontMetrics().stringWidth(levOptions[i]);
            g.drawString(levOptions[i], centerX-textWidth/2, startY+i*30);
        }
    }
    
    private void drawOptionsMenu(Graphics2D g) {
        int panelWidth = (int)(getWidth()/model.getScaleFactor(getWidth(), getHeight()));
        int centerX = panelWidth/2;
        String title = "Blockify";
        int titleFontSize = (int)(model.getRetroFont().getSize()*4);
        Font titleFont = model.getRetroFont().deriveFont(java.awt.Font.BOLD, titleFontSize);
        g.setFont(titleFont);
        int titleWidth = g.getFontMetrics().stringWidth(title);
        int titleX = centerX-titleWidth/2;
        int titleY = 100;
        int letterX = titleX;
        for(int i=0;i<title.length();i++){
            char c = title.charAt(i);
            g.setColor(model.getPieceColors()[i % model.getPieceColors().length]);
            String letter = String.valueOf(c);
            int letterWidth = g.getFontMetrics().charWidth(c);
            g.drawString(letter, letterX, titleY);
            letterX += letterWidth;
        }
        g.setFont(model.getRetroFont());
        g.setColor(java.awt.Color.white);
        String[] options = model.getOptionMenuOptions();
        int startY = titleY+70;
        for(int i=0;i<options.length;i++){
            g.setColor(i==model.getSelectedOptionMenu()?java.awt.Color.white:java.awt.Color.gray);
            int textWidth = g.getFontMetrics().stringWidth(options[i]);
            g.drawString(options[i], centerX-textWidth/2, startY+i*40);
        }
        g.setColor(java.awt.Color.white);
        g.drawString("Press ESC to Back", centerX-60, startY+options.length*40+30);
        g.setColor(new java.awt.Color(0,0,0,150));
        g.fillRect(0,0,(int)(getWidth()/model.getScaleFactor(getWidth(), getHeight())),(int)(getHeight()/model.getScaleFactor(getWidth(), getHeight())));
        g.setColor(java.awt.Color.white);
        g.drawString("TODO: Options not implemented", centerX-100, (GameConstants.BASE_BOARD_HEIGHT*model.getTILE_SIZE()+100)/2);
    }
    
    private void drawGame(Graphics2D g) {
        int panelWidth = (int)(getWidth()/model.getScaleFactor(getWidth(), getHeight()));
        int panelTotalWidth = (model.getBOARD_WIDTH()*model.getTILE_SIZE())*3+40;
        int startX = (panelWidth-panelTotalWidth)/2;
        int leftPanelX = startX;
        int leftPanelY = 50;
        int leftPanelWidth = model.getBOARD_WIDTH()*model.getTILE_SIZE();
        int leftPanelHeight = GameConstants.BASE_BOARD_HEIGHT*model.getTILE_SIZE();
        int boardX = leftPanelX+leftPanelWidth+10;
        int boardY = leftPanelY;
        int boardPixelWidth = model.getBOARD_WIDTH()*model.getTILE_SIZE();
        int boardPixelHeight = GameConstants.BASE_BOARD_HEIGHT*model.getTILE_SIZE();
        int rightPanelX = boardX+boardPixelWidth+10;
        int rightPanelY = boardY;
        int rightPanelWidth = model.getBOARD_WIDTH()*model.getTILE_SIZE();
        int rightPanelHeight = GameConstants.BASE_BOARD_HEIGHT*model.getTILE_SIZE();
        int statsTopMargin = 30;
        int statsBottomMargin = 40;
        int nextPieceTopMargin = 30;
        int nextPieceBottomMargin = 30;
        g.setFont(model.getInstructionFont());
        g.setColor(java.awt.Color.white);
        int instructionsStartY = rightPanelY+200;
        int instructionsSpacing = 32;
        g.drawString("Score: " + model.getScore(), rightPanelX+10, instructionsStartY);
        g.drawString("Time: " + model.getElapsedTime(), rightPanelX+10, instructionsStartY+instructionsSpacing);
        g.drawString("Lines: " + model.getLinesCleared(), rightPanelX+10, instructionsStartY+2*instructionsSpacing);
        int controlsFontSize = (int)(model.getRetroFont().getSize()*0.6);
        g.setFont(model.getRetroFont().deriveFont(java.awt.Font.BOLD, controlsFontSize));
        g.drawString("Controls:", rightPanelX+10, instructionsStartY+4*instructionsSpacing);
        g.drawString("Up: Rotate", rightPanelX+10, instructionsStartY+5*instructionsSpacing);
        g.drawString("Down: Move Down", rightPanelX+10, instructionsStartY+6*instructionsSpacing);
        g.drawString("Left/Right: Move", rightPanelX+10, instructionsStartY+7*instructionsSpacing);
        g.drawString("Space: Drop", rightPanelX+10, instructionsStartY+8*instructionsSpacing);
        g.drawString("K: Change Colors", rightPanelX+10, instructionsStartY+10*instructionsSpacing);
        g.drawString("G: Toggle Ghost Piece", rightPanelX+10, instructionsStartY+11*instructionsSpacing);
        g.drawString("ESC: Pause", rightPanelX+10, instructionsStartY+12*instructionsSpacing);
        if(!model.isGhostPieceEnabled()){
            g.setColor(java.awt.Color.YELLOW);
            g.drawString("Ghost OFF: Double Points!", rightPanelX+10, instructionsStartY+13*instructionsSpacing);
        }
        g.setFont(model.getRetroFont());
        g.setColor(java.awt.Color.white);
        String statsTitle = "Statistics";
        FontMetrics fmStats = g.getFontMetrics();
        int statsTitleWidth = fmStats.stringWidth(statsTitle);
        int statsTitleX = leftPanelX+(leftPanelWidth-statsTitleWidth)/2;
        int statsTitleY = leftPanelY+statsTopMargin+fmStats.getAscent();
        g.drawString(statsTitle, statsTitleX, statsTitleY);
        int statsContentYStart = statsTitleY+statsBottomMargin;
        g.drawRect(leftPanelX, leftPanelY, leftPanelWidth, leftPanelHeight);
        int statsSpacing = 70;
        for(int i=0;i<7;i++){
            String quantityText = String.valueOf(model.getShapeCount()[i]);
            int quantityWidth = g.getFontMetrics().stringWidth(quantityText);
            int quantityX = leftPanelX+(leftPanelWidth-quantityWidth)/2;
            g.drawString(quantityText, quantityX, statsContentYStart+i*statsSpacing);
            g.setColor(model.getPieceColors()[i]);
            Point[] shapeCoords = Tetromino.getShapeCoordinates(i);
            int shapeSize = model.getTILE_SIZE()/2;
            int shapeOffsetX = leftPanelX+(leftPanelWidth-shapeSize*4)/2;
            int shapeOffsetY = statsContentYStart+i*statsSpacing+10;
            for(Point p : shapeCoords){
                int x = shapeOffsetX+(p.x+1)*shapeSize;
                int y = shapeOffsetY+(p.y+1)*shapeSize;
                g.fillRect(x, y, shapeSize, shapeSize);
                g.setColor(java.awt.Color.darkGray);
                g.drawRect(x, y, shapeSize, shapeSize);
                g.setColor(model.getPieceColors()[i]);
            }
            g.setColor(java.awt.Color.white);
        }
        String nextPieceTitle = "Next Piece";
        FontMetrics fmNext = g.getFontMetrics();
        int nextPieceTitleWidth = fmNext.stringWidth(nextPieceTitle);
        int nextPieceTitleX = rightPanelX+(rightPanelWidth-nextPieceTitleWidth)/2;
        int nextPieceTitleY = rightPanelY+nextPieceTopMargin+fmNext.getAscent();
        g.drawString(nextPieceTitle, nextPieceTitleX, nextPieceTitleY);
        int nextPieceContentYStart = nextPieceTitleY+nextPieceBottomMargin;
        g.drawRect(rightPanelX, rightPanelY, rightPanelWidth, rightPanelHeight);
        drawNextPiece(g, rightPanelX, nextPieceContentYStart);
        g.setColor(model.getPieceColors()[model.getCurrentPiece().getType()]);
        g.drawRect(boardX, boardY, boardPixelWidth, boardPixelHeight);
        for(int i = GameConstants.BUFFER_ZONE;i<model.getBOARD_HEIGHT();i++){
            int yPos = i-GameConstants.BUFFER_ZONE;
            for(int j = 0;j<model.getBOARD_WIDTH();j++){
                if(model.getBoard()[i][j]!=0){
                    if((model.getGameState()==GameConstants.GAME_OVER_STATE && model.isGameOverFlashVisible()) ||
                       (model.getGameState()==GameConstants.LEVEL_UP_STATE && model.isLevelUpFlashVisible()))
                        g.setColor(java.awt.Color.yellow);
                    else
                        g.setColor(model.getPieceColors()[model.getBoard()[i][j]-1]);
                    g.fillRect(boardX+j*model.getTILE_SIZE(), boardY+yPos*model.getTILE_SIZE(), model.getTILE_SIZE(), model.getTILE_SIZE());
                    g.setColor(java.awt.Color.darkGray);
                    g.drawRect(boardX+j*model.getTILE_SIZE(), boardY+yPos*model.getTILE_SIZE(), model.getTILE_SIZE(), model.getTILE_SIZE());
                }
            }
        }
        if(model.getGameState()!=GameConstants.GAME_OVER_STATE){
            drawCurrentPiece(g, boardX, boardY);
            if(model.isGhostPieceEnabled()){
                drawGhostPiece(g, boardX, boardY);
            }
        }
        g.setColor(java.awt.Color.WHITE);
        g.setFont(model.getRetroFont().deriveFont(java.awt.Font.BOLD, (float)(GameConstants.BASE_FONT_SIZE*1.2*model.getScaleFactor(getWidth(), getHeight()))));
        String levelText = "Level: " + model.getLevel();
        int levelTextWidth = g.getFontMetrics().stringWidth(levelText);
        g.drawString(levelText, panelWidth/2-levelTextWidth/2, 30);
        g.setFont(model.getRetroFont());
    }
    
    private void drawPauseMenu(Graphics2D g) {
        g.setFont(model.getRetroFont());
        g.setColor(new java.awt.Color(0,0,0,150));
        int overlayX = 0;
        int overlayY = 0;
        int overlayWidth = (int)(getWidth()/model.getScaleFactor(getWidth(), getHeight()));
        int overlayHeight = (int)(getHeight()/model.getScaleFactor(getWidth(), getHeight()));
        g.fillRect(overlayX, overlayY, overlayWidth, overlayHeight);
        g.setColor(java.awt.Color.white);
        String pauseText = "Game Paused";
        int textWidth = g.getFontMetrics().stringWidth(pauseText);
        g.drawString(pauseText, overlayWidth/2-textWidth/2, overlayHeight/2-40);
        String resumeText = "Press ESC to Resume";
        textWidth = g.getFontMetrics().stringWidth(resumeText);
        g.drawString(resumeText, overlayWidth/2-textWidth/2, overlayHeight/2-10);
        String exitText = "Press SPACE to Exit to Main Menu";
        textWidth = g.getFontMetrics().stringWidth(exitText);
        g.drawString(exitText, overlayWidth/2-textWidth/2, overlayHeight/2+20);
    }
    
    private void drawGameOverOverlay(Graphics2D g) {
        g.setColor(new java.awt.Color(0,0,0,150));
        int overlayWidth = (int)(getWidth()/model.getScaleFactor(getWidth(), getHeight()));
        int overlayHeight = (int)(getHeight()/model.getScaleFactor(getWidth(), getHeight()));
        g.fillRect(0,0,overlayWidth,overlayHeight);
        g.setFont(model.getRetroFont());
        g.setColor(java.awt.Color.white);
        String gameOverText = "Game Over";
        int textWidth = g.getFontMetrics().stringWidth(gameOverText);
        int textX = (overlayWidth-textWidth)/2;
        int textY = overlayHeight/2-40;
        g.drawString(gameOverText, textX, textY);
        String scoreText = "Score: " + model.getScore();
        textWidth = g.getFontMetrics().stringWidth(scoreText);
        textX = (overlayWidth-textWidth)/2;
        textY += 40;
        g.drawString(scoreText, textX, textY);
        String optionsText1 = "Press ENTER to Restart";
        String optionsText2 = "Press ESC to Exit to Main Menu";
        textWidth = g.getFontMetrics().stringWidth(optionsText1);
        textX = (overlayWidth-textWidth)/2;
        textY += 40;
        g.drawString(optionsText1, textX, textY);
        textWidth = g.getFontMetrics().stringWidth(optionsText2);
        textX = (overlayWidth-textWidth)/2;
        textY += 30;
        g.drawString(optionsText2, textX, textY);
    }
    
    private void drawLevelUpFlash(Graphics2D g) {
        int panelWidth = (int)(getWidth()/model.getScaleFactor(getWidth(), getHeight()));
        int boardX = (panelWidth - (model.getBOARD_WIDTH()*model.getTILE_SIZE()*3+20))/2 + model.getBOARD_WIDTH()*model.getTILE_SIZE() + 10;
        int boardY = 50;
        int boardPixelWidth = model.getBOARD_WIDTH()*model.getTILE_SIZE();
        int boardPixelHeight = GameConstants.BASE_BOARD_HEIGHT*model.getTILE_SIZE();
        if(model.isLevelUpFlashVisible()){
            g.setColor(new java.awt.Color(255,255,0,100));
            g.fillRect(boardX, boardY, boardPixelWidth, boardPixelHeight);
        }
    }
    
    private void drawFlashingLines(Graphics2D g) {
        if(model.getFlashingLines()==null)return;
        int panelWidth = (int)(getWidth()/model.getScaleFactor(getWidth(), getHeight()));
        g.setColor(java.awt.Color.white);
        int boardX = (panelWidth - (model.getBOARD_WIDTH()*model.getTILE_SIZE()*3+20))/2 + model.getBOARD_WIDTH()*model.getTILE_SIZE() + 10;
        int boardY = 50;
        for(int i = GameConstants.BUFFER_ZONE;i<model.getBOARD_HEIGHT();i++){
            int yPos = i-GameConstants.BUFFER_ZONE;
            if(model.getFlashingLines()[i] && model.isFlashVisible()){
                for(int j=0;j<model.getBOARD_WIDTH();j++){
                    g.fillRect(boardX+j*model.getTILE_SIZE(), boardY+yPos*model.getTILE_SIZE(), model.getTILE_SIZE(), model.getTILE_SIZE());
                }
            }
        }
    }
    
    private void drawCurrentPiece(Graphics2D g, int boardX, int boardY) {
        g.setColor(model.getPieceColors()[model.getCurrentPiece().getType()]);
        for(Point p : model.getCurrentPiece().getCoordinates()){
            int x = model.getCurrentPiece().getPosition().x + p.x;
            int y = model.getCurrentPiece().getPosition().y + p.y;
            int yPos = y - GameConstants.BUFFER_ZONE;
            if(y>=0 && y<model.getBOARD_HEIGHT() && yPos>=0 && yPos<GameConstants.BASE_BOARD_HEIGHT){
                g.fillRect(boardX+x*model.getTILE_SIZE(), boardY+yPos*model.getTILE_SIZE(), model.getTILE_SIZE(), model.getTILE_SIZE());
                g.setColor(java.awt.Color.darkGray);
                g.drawRect(boardX+x*model.getTILE_SIZE(), boardY+yPos*model.getTILE_SIZE(), model.getTILE_SIZE(), model.getTILE_SIZE());
                g.setColor(model.getPieceColors()[model.getCurrentPiece().getType()]);
            }
        }
    }
    
    private void drawGhostPiece(Graphics2D g, int boardX, int boardY) {
        Point ghostPosition = new Point(model.getCurrentPiece().getPosition());
        while(isValidPosition(model.getCurrentPiece().getCoordinates(), ghostPosition.x, ghostPosition.y+1)){
            ghostPosition.y++;
        }
        g.setColor(new java.awt.Color(255,255,255,100));
        for(Point p : model.getCurrentPiece().getCoordinates()){
            int x = ghostPosition.x + p.x;
            int y = ghostPosition.y + p.y;
            int yPos = y - GameConstants.BUFFER_ZONE;
            if(y>=0 && y<model.getBOARD_HEIGHT() && yPos>=0 && yPos<GameConstants.BASE_BOARD_HEIGHT){
                g.drawRect(boardX+x*model.getTILE_SIZE(), boardY+yPos*model.getTILE_SIZE(), model.getTILE_SIZE(), model.getTILE_SIZE());
            }
        }
    }
    
    private void drawNextPiece(Graphics2D g, int rightPanelX, int nextPieceY) {
        g.setColor(model.getPieceColors()[model.getNextPiece().getType()]);
        int nextPieceTileSize = (int)(model.getTILE_SIZE()*0.5);
        int offsetX = rightPanelX+(model.getBOARD_WIDTH()*model.getTILE_SIZE()-nextPieceTileSize*4)/2;
        int offsetY = nextPieceY+20;
        for(Point p : model.getNextPiece().getCoordinates()){
            int x = p.x;
            int y = p.y;
            g.fillRect(offsetX+x*nextPieceTileSize, offsetY+y*nextPieceTileSize, nextPieceTileSize, nextPieceTileSize);
            g.setColor(java.awt.Color.darkGray);
            g.drawRect(offsetX+x*nextPieceTileSize, offsetY+y*nextPieceTileSize, nextPieceTileSize, nextPieceTileSize);
            g.setColor(model.getPieceColors()[model.getNextPiece().getType()]);
        }
    }
    
    private boolean isValidPosition(Point[] coords, int x, int y) {
        for(Point p : coords){
            int newX = x+p.x;
            int newY = y+p.y;
            if(newX < 0 || newX >= model.getBOARD_WIDTH() || newY >= model.getBOARD_HEIGHT())
                return false;
            if(newY >= 0 && model.getBoard()[newY][newX] != 0)
                return false;
        }
        return true;
    }
}
