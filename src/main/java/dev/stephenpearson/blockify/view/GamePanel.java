package dev.stephenpearson.blockify.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

import dev.stephenpearson.blockify.constants.GameConstants;
import dev.stephenpearson.blockify.main.Tetromino;
import dev.stephenpearson.blockify.util.ColorUtil;
import dev.stephenpearson.blockify.util.FontLoader;

public class GamePanel extends JPanel implements KeyListener {
	private double scaleFactor = 1.0;
    private int BOARD_WIDTH;
    private int BOARD_HEIGHT;
    private int TILE_SIZE;
    private Font retroFont;
    private Font instructionFont;
    private int gameState = GameConstants.MENU_STATE;
    private Timer gameTimer;
    private Timer shakeTimer;
    private Timer flashingTimer;
    private Timer gameOverFlashingTimer;
    private Timer levelUpFlashingTimer;
    private int[][] board;
    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private boolean isPaused = false;
    private int score = 0;
    private int level = 1;
    private int linesCleared = 0;
    private long startTime;
    private long elapsedTime;
    private int musicVolume = 5;
    private int sfxVolume = 5;
    private boolean ghostPieceEnabled = true;
    private boolean screenShake = false;
    private int shakeOffsetX = 0;
    private int shakeOffsetY = 0;
    private final Random random;
    private boolean[] flashingLines;
    private boolean flashVisible = true;
    private boolean gameOverFlashVisible = true;
    private boolean levelUpFlashVisible = true;
    private Color[] pieceColors;
    private final int[] shapeCount = new int[7];
    private final String[] mainMenuOptions = {"Start Game", "Level Selection", "Options", "Exit"};
    private final String[] levelOptions = {
        "Level 1", "Level 2", "Level 3", "Level 4", "Level 5",
        "Level 6", "Level 7", "Level 8", "Level 9", "Level 10"
    };
    private final String[] optionMenuOptions = {"Music Volume", "SFX Volume", "Controls", "Back"};
    private int selectedMenuOption = 0;
    private int selectedLevelOption = 0;
    private int selectedOptionMenu = 0;
    
    private final double ASPECT_RATIO;
    private final int MIN_WINDOW_HEIGHT;
    private final int MAX_WINDOW_HEIGHT;

    private boolean currentPieceJustSpawned = false;
    private final FontLoader fontLoader;

    public GamePanel() {
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(this);
        BOARD_WIDTH = GameConstants.BASE_BOARD_WIDTH;
        BOARD_HEIGHT = GameConstants.BASE_BOARD_HEIGHT + GameConstants.BUFFER_ZONE;
        TILE_SIZE = GameConstants.BASE_TILE_SIZE;
        ASPECT_RATIO = ((double) (GameConstants.BASE_BOARD_WIDTH * 3) * GameConstants.BASE_TILE_SIZE + 40) /
                (GameConstants.BASE_BOARD_HEIGHT * GameConstants.BASE_TILE_SIZE + 100);
        MIN_WINDOW_HEIGHT = (int) (GameConstants.MIN_WINDOW_WIDTH / ASPECT_RATIO);
        MAX_WINDOW_HEIGHT = (int) (GameConstants.MAX_WINDOW_WIDTH / ASPECT_RATIO);
        random = new Random();
        fontLoader = new FontLoader(scaleFactor, GameConstants.BASE_FONT_SIZE);
        retroFont = fontLoader.getRetroFont();
        instructionFont = fontLoader.getInstructionFont();
        pieceColors = ColorUtil.initPieceColors();  
        initGame();
        startGameTimer();
        int initialWidth = BOARD_WIDTH * TILE_SIZE * 3 + 40;
        int initialHeight = GameConstants.BASE_BOARD_HEIGHT * TILE_SIZE + 100;
        setPreferredSize(new Dimension(initialWidth, initialHeight));
    }
    
    public void initConstants() {
    	
    }

    

    public double getAspectRatio() {
        return ASPECT_RATIO;
    }

    public int getMinWindowWidth() {
        return GameConstants.MIN_WINDOW_WIDTH;
    }

    public int getMinWindowHeight() {
        return MIN_WINDOW_HEIGHT;
    }

    public int getMaxWindowWidth() {
        return GameConstants.MAX_WINDOW_WIDTH;
    }

    public int getMaxWindowHeight() {
        return MAX_WINDOW_HEIGHT;
    }

    private void initGame() {
        board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        currentPiece = new Tetromino(random.nextInt(7), BOARD_WIDTH, GameConstants.BUFFER_ZONE);
        nextPiece = new Tetromino(random.nextInt(7), BOARD_WIDTH, GameConstants.BUFFER_ZONE);
        pieceColors = ColorUtil.initPieceColors();
        startTime = System.currentTimeMillis();
        ghostPieceEnabled = true;
    }

    private void startGameTimer() {
        int timerDelay = Math.max(100, 500 - (level - 1) * 40);
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
        gameTimer = new Timer(timerDelay, e -> {
            if (gameState == GameConstants.GAME_STATE && !isPaused) {
                if (currentPieceJustSpawned) {
                    currentPieceJustSpawned = false;
                } else {
                    movePieceDown();
                }
            }
        });
        gameTimer.start();
    }
    
    private String formatTime(long totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    //Draw methods TODO: Refactor to Renderer

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
      
        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }
        Graphics2D g2d = (Graphics2D) g.create();
        double scaleX = (double) getWidth() / (BOARD_WIDTH * TILE_SIZE * 3 + 40);
        double scaleY = (double) getHeight() / (GameConstants.BASE_BOARD_HEIGHT * TILE_SIZE + 100);
        scaleFactor = Math.min(scaleX, scaleY);
        scaleFactor = Math.max(GameConstants.MIN_SCALE, Math.min(GameConstants.MAX_SCALE, scaleFactor));
        retroFont = retroFont.deriveFont(Font.BOLD, (float) (GameConstants.BASE_FONT_SIZE * scaleFactor));
        instructionFont = instructionFont.deriveFont(Font.PLAIN, (float) (GameConstants.BASE_FONT_SIZE * 0.8 * scaleFactor));
        g2d.setFont(retroFont);
        g2d.scale(scaleFactor, scaleFactor);
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, (int) (getWidth() / scaleFactor), (int) (getHeight() / scaleFactor));
        if (screenShake) {
            g2d.translate(shakeOffsetX, shakeOffsetY);
        }
        
        
        switch (gameState) {
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
                if (gameState == GameConstants.FLASHING_STATE) {
                    drawFlashingLines(g2d);
                }
                if (gameState == GameConstants.GAME_OVER_STATE) {
                    drawGameOverOverlay(g2d);
                }
                if (gameState == GameConstants.LEVEL_UP_STATE) {
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
        if (screenShake) {
            g2d.translate(-shakeOffsetX, -shakeOffsetY);
        }
        g2d.dispose();
    }

    private void drawMainMenu(Graphics2D g) {
        int panelWidth = (int) (getWidth() / scaleFactor);
        int panelHeight = (int) (getHeight() / scaleFactor);
        int centerX = panelWidth / 2;
        String title = "Blockify";
        int titleFontSize = (int)(retroFont.getSize() * 5);
        Font titleFont = retroFont.deriveFont(Font.BOLD, titleFontSize);
        g.setFont(titleFont);
        int titleWidth = g.getFontMetrics().stringWidth(title);
        int titleX = centerX - titleWidth / 2;
        int titleY = panelHeight / 2 - 100;
        int letterX = titleX;
        for (int i = 0; i < title.length(); i++) {
            char c = title.charAt(i);
            g.setColor(pieceColors[i % pieceColors.length]);
            String letter = String.valueOf(c);
            int letterWidth = g.getFontMetrics().charWidth(c);
            g.drawString(letter, letterX, titleY);
            letterX += letterWidth;
        }
        g.setFont(retroFont);
        g.setColor(Color.white);
        int totalMenuHeight = mainMenuOptions.length * 40;
        int startY = titleY + 75;
        for (int i = 0; i < mainMenuOptions.length; i++) {
            g.setColor(i == selectedMenuOption ? Color.white : Color.gray);
            int textWidth = g.getFontMetrics().stringWidth(mainMenuOptions[i]);
            g.drawString(mainMenuOptions[i], centerX - textWidth / 2, startY + i * 40);
        }
        g.setColor(Color.gray);
        String footerText1 = "Created by Stephen Pearson";
        String footerText2 = "https://github.com/StephenPearsonDev/Blockify";
        int footerY = panelHeight - 60;
        int footerX1 = centerX - g.getFontMetrics().stringWidth(footerText1) / 2;
        int footerX2 = centerX - g.getFontMetrics().stringWidth(footerText2) / 2;
        g.drawString(footerText1, footerX1, footerY);
        g.drawString(footerText2, footerX2, footerY + 40);
    }

    private void drawLevelSelectMenu(Graphics2D g) {
        int panelWidth = (int) (getWidth() / scaleFactor);
        int centerX = panelWidth / 2;
        String title = "Blockify";
        int titleFontSize = (int)(retroFont.getSize() * 4);
        Font titleFont = retroFont.deriveFont(Font.BOLD, titleFontSize);
        g.setFont(titleFont);
        int titleWidth = g.getFontMetrics().stringWidth(title);
        int titleX = centerX - titleWidth / 2;
        int titleY = 100;
        int letterX = titleX;
        for (int i = 0; i < title.length(); i++) {
            char c = title.charAt(i);
            g.setColor(pieceColors[i % pieceColors.length]);
            String letter = String.valueOf(c);
            int letterWidth = g.getFontMetrics().charWidth(c);
            g.drawString(letter, letterX, titleY);
            letterX += letterWidth;
        }
        g.setFont(retroFont);
        g.setColor(Color.white);
        g.drawString("Press ENTER to select", centerX - 80, titleY + 40);
        g.drawString("Press ESC to Back", centerX - 60, titleY + 70);
        int totalLevelsHeight = levelOptions.length * 30;
        int startY = titleY + 100;
        for (int i = 0; i < levelOptions.length; i++) {
            g.setColor(i == selectedLevelOption ? Color.white : Color.gray);
            int textWidth = g.getFontMetrics().stringWidth(levelOptions[i]);
            g.drawString(levelOptions[i], centerX - textWidth / 2, startY + i * 30);
        }
    }

    private void drawOptionsMenu(Graphics2D g) {
        int panelWidth = (int) (getWidth() / scaleFactor);
        int centerX = panelWidth / 2;
        String title = "Blockify";
        int titleFontSize = (int)(retroFont.getSize() * 4);
        Font titleFont = retroFont.deriveFont(Font.BOLD, titleFontSize);
        g.setFont(titleFont);
        int titleWidth = g.getFontMetrics().stringWidth(title);
        int titleX = centerX - titleWidth / 2;
        int titleY = 100;
        int letterX = titleX;
        for (int i = 0; i < title.length(); i++) {
            char c = title.charAt(i);
            g.setColor(pieceColors[i % pieceColors.length]);
            String letter = String.valueOf(c);
            int letterWidth = g.getFontMetrics().charWidth(c);
            g.drawString(letter, letterX, titleY);
            letterX += letterWidth;
        }
        g.setFont(retroFont);
        g.setColor(Color.white);
        int totalOptionsHeight = optionMenuOptions.length * 40;
        int startY = titleY + 70;
        for (int i = 0; i < optionMenuOptions.length; i++) {
            g.setColor(i == selectedOptionMenu ? Color.white : Color.gray);
            int textWidth = g.getFontMetrics().stringWidth(optionMenuOptions[i]);
            g.drawString(optionMenuOptions[i], centerX - textWidth / 2, startY + i * 40);
        }
        g.setColor(Color.white);
        g.drawString("Press ESC to Back", centerX - 60, startY + optionMenuOptions.length * 40 + 30);
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, (int) (getWidth() / scaleFactor), (int) (getHeight() / scaleFactor));
        g.setColor(Color.white);
        g.drawString("TODO: Options not implemented", centerX - 100, (GameConstants.BASE_BOARD_HEIGHT * TILE_SIZE + 100) / 2);
    }

    private void drawGame(Graphics2D g) {
        int panelWidth = (int) (getWidth() / scaleFactor);
        int panelTotalWidth = (BOARD_WIDTH * TILE_SIZE) * 3 + 40;
        int startX = (panelWidth - panelTotalWidth) / 2;
        int leftPanelX = startX;
        int leftPanelY = 50;
        int leftPanelWidth = BOARD_WIDTH * TILE_SIZE;
        int leftPanelHeight = GameConstants.BASE_BOARD_HEIGHT * TILE_SIZE;
        int boardX = leftPanelX + leftPanelWidth + 10;
        int boardY = leftPanelY;
        int boardPixelWidth = BOARD_WIDTH * TILE_SIZE;
        int boardPixelHeight = GameConstants.BASE_BOARD_HEIGHT * TILE_SIZE;
        int rightPanelX = boardX + boardPixelWidth + 10;
        int rightPanelY = boardY;
        int rightPanelWidth = BOARD_WIDTH * TILE_SIZE;
        int rightPanelHeight = GameConstants.BASE_BOARD_HEIGHT * TILE_SIZE;
        int statsTopMargin = 30;
        int statsBottomMargin = 40;
        int nextPieceTopMargin = 30;
        int nextPieceBottomMargin = 30;
        
        g.setFont(instructionFont);
        g.setColor(Color.white);
        int instructionsStartY = rightPanelY + 200;
        int instructionsSpacing = 32;
        g.drawString("Score: " + score, rightPanelX + 10, instructionsStartY);
        g.drawString("Time: " + formatTime(elapsedTime), rightPanelX + 10, instructionsStartY + instructionsSpacing);
        g.drawString("Lines: " + linesCleared, rightPanelX + 10, instructionsStartY + 2 * instructionsSpacing);
        int controlsFontSize = (int)(retroFont.getSize() * .6);
        g.setFont(retroFont.deriveFont(Font.BOLD, controlsFontSize));
        g.drawString("Controls:", rightPanelX + 10, instructionsStartY + 4 * instructionsSpacing);
        g.drawString("Up: Rotate", rightPanelX + 10, instructionsStartY + 5 * instructionsSpacing);
        g.drawString("Down: Move Down", rightPanelX + 10, instructionsStartY + 6 * instructionsSpacing);
        g.drawString("Left/Right: Move", rightPanelX + 10, instructionsStartY + 7 * instructionsSpacing);
        g.drawString("Space: Drop", rightPanelX + 10, instructionsStartY + 8 * instructionsSpacing);
        g.drawString("K: Change Colors", rightPanelX + 10, instructionsStartY + 10 * instructionsSpacing);
        g.drawString("G: Toggle Ghost Piece", rightPanelX + 10, instructionsStartY + 11 * instructionsSpacing);
        g.drawString("ESC: Pause", rightPanelX + 10, instructionsStartY + 12 * instructionsSpacing);
        if (!ghostPieceEnabled) {
            g.setColor(Color.YELLOW);
            g.drawString("Ghost OFF: Double Points!", rightPanelX + 10, instructionsStartY + 13 * instructionsSpacing);
        }
        g.setFont(retroFont);
        g.setColor(Color.white);
        String statsTitle = "Statistics";
        FontMetrics fmStats = g.getFontMetrics();
        int statsTitleWidth = fmStats.stringWidth(statsTitle);
        int statsTitleX = leftPanelX + (leftPanelWidth - statsTitleWidth) / 2;
        int statsTitleY = leftPanelY + statsTopMargin + fmStats.getAscent();
        g.drawString(statsTitle, statsTitleX, statsTitleY);
        int statsContentYStart = statsTitleY + statsBottomMargin;
        g.drawRect(leftPanelX, leftPanelY, leftPanelWidth, leftPanelHeight);
        int statsSpacing = 70;
        for (int i = 0; i < 7; i++) {
            String quantityText = String.valueOf(shapeCount[i]);
            int quantityWidth = g.getFontMetrics().stringWidth(quantityText);
            int quantityX = leftPanelX + (leftPanelWidth - quantityWidth) / 2;
            g.drawString(quantityText, quantityX, statsContentYStart + i * statsSpacing);
            g.setColor(pieceColors[i]);
            Point[] shapeCoords = Tetromino.getShapeCoordinates(i);
            int shapeSize = TILE_SIZE / 2;
            int shapeOffsetX = leftPanelX + (leftPanelWidth - shapeSize * 4) / 2;
            int shapeOffsetY = statsContentYStart + i * statsSpacing + 10;
            for (Point p : shapeCoords) {
                int x = shapeOffsetX + (p.x + 1) * shapeSize;
                int y = shapeOffsetY + (p.y + 1) * shapeSize;
                g.fillRect(x, y, shapeSize, shapeSize);
                g.setColor(Color.darkGray);
                g.drawRect(x, y, shapeSize, shapeSize);
                g.setColor(pieceColors[i]);
            }
            g.setColor(Color.white);
        }
        String nextPieceTitle = "Next Piece";
        FontMetrics fmNext = g.getFontMetrics();
        int nextPieceTitleWidth = fmNext.stringWidth(nextPieceTitle);
        int nextPieceTitleX = rightPanelX + (rightPanelWidth - nextPieceTitleWidth) / 2;
        int nextPieceTitleY = rightPanelY + nextPieceTopMargin + fmNext.getAscent();
        g.drawString(nextPieceTitle, nextPieceTitleX, nextPieceTitleY);
        int nextPieceContentYStart = nextPieceTitleY + nextPieceBottomMargin;
        g.drawRect(rightPanelX, rightPanelY, rightPanelWidth, rightPanelHeight);
        drawNextPiece(g, rightPanelX, nextPieceContentYStart);
        g.setColor(pieceColors[currentPiece.type]);
        g.drawRect(boardX, boardY, boardPixelWidth, boardPixelHeight);
        for (int i = GameConstants.BUFFER_ZONE; i < BOARD_HEIGHT; i++) {
            int yPos = i - GameConstants.BUFFER_ZONE;
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (board[i][j] != 0) {
                    if ((gameState == GameConstants.GAME_OVER_STATE && gameOverFlashVisible) ||
                        (gameState == GameConstants.LEVEL_UP_STATE && levelUpFlashVisible)) {
                        g.setColor(Color.yellow);
                    } else {
                        g.setColor(pieceColors[board[i][j] - 1]);
                    }
                    g.fillRect(boardX + j * TILE_SIZE, boardY + yPos * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    g.setColor(Color.darkGray);
                    g.drawRect(boardX + j * TILE_SIZE, boardY + yPos * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
        if (gameState != GameConstants.GAME_OVER_STATE) {
            drawCurrentPiece(g, boardX, boardY);
            if (ghostPieceEnabled) {
                drawGhostPiece(g, boardX, boardY);
            }
        }
        g.setColor(Color.WHITE);
        g.setFont(retroFont.deriveFont(Font.BOLD, (float) (GameConstants.BASE_FONT_SIZE * 1.2 * scaleFactor)));
        String levelText = "Level: " + level;
        int levelTextWidth = g.getFontMetrics().stringWidth(levelText);
        g.drawString(levelText, panelWidth / 2 - levelTextWidth / 2, 30);
        g.setFont(retroFont);
    }

    private void drawPauseMenu(Graphics2D g) {
        g.setFont(retroFont);
        g.setColor(new Color(0, 0, 0, 150));
        int overlayX = 0;
        int overlayY = 0;
        int overlayWidth = (int) (getWidth() / scaleFactor);
        int overlayHeight = (int) (getHeight() / scaleFactor);
        g.fillRect(overlayX, overlayY, overlayWidth, overlayHeight);
        g.setColor(Color.white);
        String pauseText = "Game Paused";
        int textWidth = g.getFontMetrics().stringWidth(pauseText);
        g.drawString(pauseText, overlayWidth / 2 - textWidth / 2, overlayHeight / 2 - 40);
        String resumeText = "Press ESC to Resume";
        textWidth = g.getFontMetrics().stringWidth(resumeText);
        g.drawString(resumeText, overlayWidth / 2 - textWidth / 2, overlayHeight / 2 - 10);
        String exitText = "Press SPACE to Exit to Main Menu";
        textWidth = g.getFontMetrics().stringWidth(exitText);
        g.drawString(exitText, overlayWidth / 2 - textWidth / 2, overlayHeight / 2 + 20);
    }

    private void drawGameOverOverlay(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        int overlayWidth = (int) (getWidth() / scaleFactor);
        int overlayHeight = (int) (getHeight() / scaleFactor);
        g.fillRect(0, 0, overlayWidth, overlayHeight);
        g.setFont(retroFont);
        g.setColor(Color.white);
        String gameOverText = "Game Over";
        int textWidth = g.getFontMetrics().stringWidth(gameOverText);
        int textX = (overlayWidth - textWidth) / 2;
        int textY = overlayHeight / 2 - 40;
        g.drawString(gameOverText, textX, textY);
        String scoreText = "Score: " + score;
        textWidth = g.getFontMetrics().stringWidth(scoreText);
        textX = (overlayWidth - textWidth) / 2;
        textY += 40;
        g.drawString(scoreText, textX, textY);
        String optionsText1 = "Press ENTER to Restart";
        String optionsText2 = "Press ESC to Exit to Main Menu";
        textWidth = g.getFontMetrics().stringWidth(optionsText1);
        textX = (overlayWidth - textWidth) / 2;
        textY += 40;
        g.drawString(optionsText1, textX, textY);
        textWidth = g.getFontMetrics().stringWidth(optionsText2);
        textX = (overlayWidth - textWidth) / 2;
        textY += 30;
        g.drawString(optionsText2, textX, textY);
    }

    private void drawLevelUpFlash(Graphics2D g) {
        int panelWidth = (int) (getWidth() / scaleFactor);
        int boardX = (panelWidth - (BOARD_WIDTH * TILE_SIZE * 3 + 20)) / 2 + BOARD_WIDTH * TILE_SIZE + 10;
        int boardY = 50;
        int boardPixelWidth = BOARD_WIDTH * TILE_SIZE;
        int boardPixelHeight = GameConstants.BASE_BOARD_HEIGHT * TILE_SIZE;
        if (levelUpFlashVisible) {
            g.setColor(new Color(255, 255, 0, 100));
            g.fillRect(boardX, boardY, boardPixelWidth, boardPixelHeight);
        }
    }

    private void drawFlashingLines(Graphics2D g) {
        if (flashingLines == null) return;
        int panelWidth = (int) (getWidth() / scaleFactor);
        g.setColor(Color.white);
        int boardX = (panelWidth - (BOARD_WIDTH * TILE_SIZE * 3 + 20)) / 2 + BOARD_WIDTH * TILE_SIZE + 10;
        int boardY = 50;
        for (int i = GameConstants.BUFFER_ZONE; i < BOARD_HEIGHT; i++) {
            int yPos = i - GameConstants.BUFFER_ZONE;
            if (flashingLines[i] && flashVisible) {
                for (int j = 0; j < BOARD_WIDTH; j++) {
                    g.fillRect(boardX + j * TILE_SIZE, boardY + yPos * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }

    private void drawCurrentPiece(Graphics2D g, int boardX, int boardY) {
        g.setColor(pieceColors[currentPiece.type]);
        for (Point p : currentPiece.coordinates) {
            int x = currentPiece.position.x + p.x;
            int y = currentPiece.position.y + p.y;
            int yPos = y - GameConstants.BUFFER_ZONE;
            if (y >= 0 && y < BOARD_HEIGHT && yPos >= 0 && yPos < GameConstants.BASE_BOARD_HEIGHT) {
                g.fillRect(boardX + x * TILE_SIZE, boardY + yPos * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                g.setColor(Color.darkGray);
                g.drawRect(boardX + x * TILE_SIZE, boardY + yPos * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                g.setColor(pieceColors[currentPiece.type]);
            }
        }
    }

    private void drawGhostPiece(Graphics2D g, int boardX, int boardY) {
        Point ghostPosition = new Point(currentPiece.position);
        while (isValidPosition(currentPiece.coordinates, ghostPosition.x, ghostPosition.y + 1)) {
            ghostPosition.y++;
        }
        g.setColor(new Color(255, 255, 255, 100));
        for (Point p : currentPiece.coordinates) {
            int x = ghostPosition.x + p.x;
            int y = ghostPosition.y + p.y;
            int yPos = y - GameConstants.BUFFER_ZONE;
            if (y >= 0 && y < BOARD_HEIGHT && yPos >= 0 && yPos < GameConstants.BASE_BOARD_HEIGHT) {
                g.drawRect(boardX + x * TILE_SIZE, boardY + yPos * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    private void drawNextPiece(Graphics2D g, int rightPanelX, int nextPieceY) {
        g.setColor(pieceColors[nextPiece.type]);
        int nextPieceTileSize = (int) (TILE_SIZE * 0.5);
        int offsetX = rightPanelX + (BOARD_WIDTH * TILE_SIZE - nextPieceTileSize * 4) / 2;
        int offsetY = nextPieceY + 20;
        for (Point p : nextPiece.coordinates) {
            int x = p.x;
            int y = p.y;
            g.fillRect(offsetX + x * nextPieceTileSize, offsetY + y * nextPieceTileSize, nextPieceTileSize, nextPieceTileSize);
            g.setColor(Color.darkGray);
            g.drawRect(offsetX + x * nextPieceTileSize, offsetY + y * nextPieceTileSize, nextPieceTileSize, nextPieceTileSize);
            g.setColor(pieceColors[nextPiece.type]);
        }
    }

    
    
    
    
    private boolean isValidPosition(Point[] coords, int x, int y) {
        for (Point p : coords) {
            int newX = x + p.x;
            int newY = y + p.y;
            if (newX < 0 || newX >= BOARD_WIDTH || newY >= BOARD_HEIGHT) {
                return false;
            }
            if (newY >= 0 && board[newY][newX] != 0) {
                return false;
            }
        }
        return true;
    }

    public void movePieceDown() {
        if (isValidPosition(currentPiece.coordinates, currentPiece.position.x, currentPiece.position.y + 1)) {
            currentPiece.position.y++;
        } else {
            fixPieceToBoard();
            clearLines();
            spawnNextPiece();
        }
        repaint();
    }

    private void fixPieceToBoard() {
        boolean gameOver = false;
        for (Point p : currentPiece.coordinates) {
            int x = currentPiece.position.x + p.x;
            int y = currentPiece.position.y + p.y;
            if (x >= 0 && x < BOARD_WIDTH && y >= 0 && y < BOARD_HEIGHT) {
                board[y][x] = currentPiece.type + 1;
            }
            if (y < GameConstants.BUFFER_ZONE) {
                gameOver = true;
            }
        }
        shapeCount[currentPiece.type]++;
        if (gameOver) {
            gameTimer.stop();
            gameState = GameConstants.GAME_OVER_STATE;
            gameOverFlashVisible = true;
            gameOverFlashingTimer = new Timer(GameConstants.FLASH_INTERVAL, new ActionListener() {
                private int elapsed = 0;

                @Override
                public void actionPerformed(ActionEvent e) {
                    gameOverFlashVisible = !gameOverFlashVisible;
                    repaint();
                    elapsed += GameConstants.FLASH_INTERVAL;
                    if (elapsed >= GameConstants.FLASH_DURATION) {
                        gameOverFlashingTimer.stop();
                        gameOverFlashVisible = false;
                        repaint();
                    }
                }
            });
            gameOverFlashingTimer.start();
        }
    }

    
    
    private void clearLines() {
        boolean anyLineFull = false;
        flashingLines = new boolean[BOARD_HEIGHT];
        for (int i = GameConstants.BUFFER_ZONE; i < BOARD_HEIGHT; i++) {
            boolean lineFull = true;
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (board[i][j] == 0) {
                    lineFull = false;
                    break;
                }
            }
            if (lineFull) {
                flashingLines[i] = true;
                anyLineFull = true;
            }
        }
        if (anyLineFull) {
            gameTimer.stop();
            gameState = GameConstants.FLASHING_STATE;
            flashVisible = true;
            flashingTimer = new Timer(GameConstants.FLASH_INTERVAL, new ActionListener() {
                private int elapsed = 0;

                @Override
                public void actionPerformed(ActionEvent e) {
                    flashVisible = !flashVisible;
                    repaint();
                    elapsed += GameConstants.FLASH_INTERVAL;
                    if (elapsed >= GameConstants.FLASH_DURATION) {
                        flashingTimer.stop();
                        removeFlashingLines();
                        if (gameState != GameConstants.GAME_OVER_STATE) {
                            gameState = GameConstants.GAME_STATE;
                            gameTimer.start();
                        }
                    }
                }
            });
            flashingTimer.start();
        }
    }

    private void removeFlashingLines() {
        for (int i = GameConstants.BUFFER_ZONE; i < BOARD_HEIGHT; i++) {
            if (flashingLines[i]) {
                linesCleared++;
                int pointsPerLine = ghostPieceEnabled ? 100 : 200;
                score += pointsPerLine;
                for (int k = i; k > 0; k--) {
                    System.arraycopy(board[k - 1], 0, board[k], 0, BOARD_WIDTH);
                }
                for (int j = 0; j < BOARD_WIDTH; j++) {
                    board[0][j] = 0;
                }
            }
        }
        flashingLines = null;
        int newLevel = score / 500 + 1;
        if (newLevel > level) {
            level = newLevel;
            triggerLevelUp();
        }
        repaint();
    }

    
    
    private void triggerLevelUp() {
        gameTimer.stop();
        gameState = GameConstants.LEVEL_UP_STATE;
        levelUpFlashVisible = true;
        levelUpFlashingTimer = new Timer(GameConstants.FLASH_INTERVAL, new ActionListener() {
            private int elapsed = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                levelUpFlashVisible = !levelUpFlashVisible;
                repaint();
                elapsed += GameConstants.FLASH_INTERVAL;
                if (elapsed >= GameConstants.FLASH_DURATION) {
                    levelUpFlashingTimer.stop();
                    changePieceColors();
                    if (gameState != GameConstants.GAME_OVER_STATE) {
                        gameState = GameConstants.GAME_STATE;
                        startGameTimer();
                        gameTimer.start();
                    }
                }
            }
        });
        levelUpFlashingTimer.start();
    }

    private void triggerScreenShake() {
        if (screenShake) return;
        screenShake = true;
        gameTimer.stop();
        shakeTimer = new Timer(GameConstants.SHAKE_INTERVAL, new ActionListener() {
            private int elapsed = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (elapsed < GameConstants.SHAKE_DURATION) {
                    shakeOffsetX = random.nextInt(2 * GameConstants.SHAKE_INTENSITY + 1) - GameConstants.SHAKE_INTENSITY;
                    shakeOffsetY = random.nextInt(2 * GameConstants.SHAKE_INTENSITY + 1) - GameConstants.SHAKE_INTENSITY;
                    elapsed += GameConstants.SHAKE_INTERVAL;
                    repaint();
                } else {
                    screenShake = false;
                    shakeOffsetX = 0;
                    shakeOffsetY = 0;
                    shakeTimer.stop();
                    if (gameState == GameConstants.GAME_STATE && !isPaused) {
                        gameTimer.start();
                    }
                    repaint();
                }
            }
        });
        shakeTimer.start();
    }

    private void spawnNextPiece() {
        currentPiece = nextPiece;
        nextPiece = new Tetromino(random.nextInt(7), BOARD_WIDTH, GameConstants.BUFFER_ZONE);
        currentPieceJustSpawned = true;
        if (!isValidPosition(currentPiece.coordinates, currentPiece.position.x, currentPiece.position.y)) {
            gameTimer.stop();
            gameState = GameConstants.GAME_OVER_STATE;
            gameOverFlashVisible = true;
            gameOverFlashingTimer = new Timer(GameConstants.FLASH_INTERVAL, new ActionListener() {
                private int elapsed = 0;

                @Override
                public void actionPerformed(ActionEvent e) {
                    gameOverFlashVisible = !gameOverFlashVisible;
                    repaint();
                    elapsed += GameConstants.FLASH_INTERVAL;
                    if (elapsed >= GameConstants.FLASH_DURATION) {
                        gameOverFlashingTimer.stop();
                        gameOverFlashVisible = false;
                        repaint();
                    }
                }
            });
            gameOverFlashingTimer.start();
        }
    }

    private void changePieceColors() {
        ColorUtil.changePieceColors(pieceColors);
        repaint();
    }

    private void resetGame() {
        board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        currentPiece = new Tetromino(random.nextInt(7), BOARD_WIDTH, GameConstants.BUFFER_ZONE);
        nextPiece = new Tetromino(random.nextInt(7), BOARD_WIDTH, GameConstants.BUFFER_ZONE);
        score = 0;
        linesCleared = 0;
        level = 1;
        startTime = System.currentTimeMillis();
        isPaused = false;
        gameState = GameConstants.GAME_STATE;
        for (int i = 0; i < shapeCount.length; i++) {
            shapeCount[i] = 0;
        }
        ghostPieceEnabled = true;
        repaint();
        startGameTimer();
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (gameState) {
            case GameConstants.MENU_STATE:
                handleMenuInput(e);
                break;
            case GameConstants.LEVEL_SELECT_STATE:
                handleLevelSelectInput(e);
                break;
            case GameConstants.OPTIONS_STATE:
                handleOptionsInput(e);
                break;
            case GameConstants.GAME_STATE:
                handleGameInput(e);
                break;
            case GameConstants.PAUSE_STATE:
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    isPaused = false;
                    gameState = GameConstants.GAME_STATE;
                    gameTimer.start();
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    gameState = GameConstants.MENU_STATE;
                    isPaused = false;
                    repaint();
                }
                break;
            case GameConstants.FLASHING_STATE:
            case GameConstants.LEVEL_UP_STATE:
                break;
            case GameConstants.GAME_OVER_STATE:
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    resetGame();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    gameState = GameConstants.MENU_STATE;
                    repaint();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }

    private void handleMenuInput(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            selectedMenuOption = (selectedMenuOption - 1 + mainMenuOptions.length) % mainMenuOptions.length;
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            selectedMenuOption = (selectedMenuOption + 1) % mainMenuOptions.length;
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            switch (selectedMenuOption) {
                case 0:
                    resetGame();
                    gameState = GameConstants.GAME_STATE;
                    repaint();
                    break;
                case 1:
                    gameState = GameConstants.LEVEL_SELECT_STATE;
                    selectedLevelOption = level - 1;
                    repaint();
                    break;
                case 2:
                    gameState = GameConstants.OPTIONS_STATE;
                    selectedOptionMenu = 0;
                    repaint();
                    break;
                case 3:
                    System.exit(0);
                    break;
            }
        }
    }

    private void handleLevelSelectInput(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            selectedLevelOption = (selectedLevelOption - 1 + levelOptions.length) % levelOptions.length;
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            selectedLevelOption = (selectedLevelOption + 1) % levelOptions.length;
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            level = selectedLevelOption + 1;
            startGameTimer();
            resetGame();
            gameState = GameConstants.GAME_STATE;
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            gameState = GameConstants.MENU_STATE;
            repaint();
        }
    }

    private void handleOptionsInput(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            gameState = GameConstants.MENU_STATE;
            repaint();
        }
    }

    private void handleGameInput(KeyEvent e) {
        if (gameState != GameConstants.GAME_STATE || isPaused) return;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (isValidPosition(currentPiece.coordinates, currentPiece.position.x - 1, currentPiece.position.y)) {
                    currentPiece.position.x--;
                    repaint();
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (isValidPosition(currentPiece.coordinates, currentPiece.position.x + 1, currentPiece.position.y)) {
                    currentPiece.position.x++;
                    repaint();
                }
                break;
            case KeyEvent.VK_DOWN:
                movePieceDown();
                break;
            case KeyEvent.VK_UP:
                currentPiece.rotate();
                if (!isValidPosition(currentPiece.coordinates, currentPiece.position.x, currentPiece.position.y)) {
                    currentPiece.rotateBack();
                }
                repaint();
                break;
            case KeyEvent.VK_SPACE:
                while (isValidPosition(currentPiece.coordinates, currentPiece.position.x, currentPiece.position.y + 1)) {
                    currentPiece.position.y++;
                }
                movePieceDown();
                triggerScreenShake();
                break;
            case KeyEvent.VK_K:
                changePieceColors();
                break;
            case KeyEvent.VK_G:
                ghostPieceEnabled = !ghostPieceEnabled;
                repaint();
                break;
            case KeyEvent.VK_ESCAPE:
                isPaused = true;
                gameState = GameConstants.PAUSE_STATE;
                gameTimer.stop();
                repaint();
                break;
            default:
                break;
        }
    }
}
