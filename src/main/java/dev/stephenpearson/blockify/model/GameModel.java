package dev.stephenpearson.blockify.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import dev.stephenpearson.blockify.constants.GameConstants;
import dev.stephenpearson.blockify.main.Tetromino;
import dev.stephenpearson.blockify.util.ColorUtil;

public class GameModel {
    private int BOARD_WIDTH;
    private int BOARD_HEIGHT;
    private int TILE_SIZE;
    private int gameState;
    private int[][] board;
    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private boolean isPaused;
    private int score;
    private int level;
    private int linesCleared;
    private long startTime;
    private long elapsedTime;
    private boolean ghostPieceEnabled;
    private boolean screenShake;
    private int shakeOffsetX;
    private int shakeOffsetY;
    private Random random;
    private boolean[] flashingLines;
    private boolean flashVisible;
    private boolean gameOverFlashVisible;
    private boolean levelUpFlashVisible;
    private Color[] pieceColors;
    private int[] shapeCount;
    private int selectedMenuOption;
    private int selectedLevelOption;
    private int selectedOptionMenu;
    private double ASPECT_RATIO;
    private int MIN_WINDOW_HEIGHT;
    private int MAX_WINDOW_HEIGHT;
    private boolean currentPieceJustSpawned;
    private List<GameModelObserver> observers;
    private Font retroFont;
    private Font instructionFont;
    
    public GameModel() {
        BOARD_WIDTH = GameConstants.BASE_BOARD_WIDTH;
        BOARD_HEIGHT = GameConstants.BASE_BOARD_HEIGHT + GameConstants.BUFFER_ZONE;
        TILE_SIZE = GameConstants.BASE_TILE_SIZE;
        ASPECT_RATIO = ((double)(GameConstants.BASE_BOARD_WIDTH * 3) * GameConstants.BASE_TILE_SIZE + 40) /
                       (GameConstants.BASE_BOARD_HEIGHT * GameConstants.BASE_TILE_SIZE + 100);
        MIN_WINDOW_HEIGHT = (int)(GameConstants.MIN_WINDOW_WIDTH / ASPECT_RATIO);
        MAX_WINDOW_HEIGHT = (int)(GameConstants.MAX_WINDOW_WIDTH / ASPECT_RATIO);
        random = new Random();
        shapeCount = new int[7];
        observers = new ArrayList<>();
        flashVisible = true;
        gameOverFlashVisible = true;
        levelUpFlashVisible = true;
        selectedMenuOption = 0;
        selectedLevelOption = 0;
        selectedOptionMenu = 0;
        retroFont = new Font("SansSerif", Font.BOLD, GameConstants.BASE_FONT_SIZE);
        instructionFont = new Font("SansSerif", Font.PLAIN, (int)(GameConstants.BASE_FONT_SIZE * 0.8));
        initGame();
    }
    
    public void addObserver(GameModelObserver o) { observers.add(o); }
    public void removeObserver(GameModelObserver o) { observers.remove(o); }
    public void notifyObservers() { for(GameModelObserver o : observers) o.update(); }
    
    public void initGame() {
        board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        currentPiece = new Tetromino(random.nextInt(7), BOARD_WIDTH, GameConstants.BUFFER_ZONE);
        nextPiece = new Tetromino(random.nextInt(7), BOARD_WIDTH, GameConstants.BUFFER_ZONE);
        pieceColors = ColorUtil.initPieceColors();
        startTime = System.currentTimeMillis();
        ghostPieceEnabled = true;
        gameState = GameConstants.MENU_STATE;
        score = 0;
        level = 1;
        linesCleared = 0;
        currentPieceJustSpawned = false;
    }
    
    public double getScaleFactor(int panelWidth, int panelHeight) {
        double scaleX = (double)panelWidth / (BOARD_WIDTH * TILE_SIZE * 3 + 40);
        double scaleY = (double)panelHeight / (GameConstants.BASE_BOARD_HEIGHT * TILE_SIZE + 100);
        double scale = Math.min(scaleX, scaleY);
        scale = Math.max(GameConstants.MIN_SCALE, Math.min(GameConstants.MAX_SCALE, scale));
        return scale;
    }
    
    public double getAspectRatio() { return ASPECT_RATIO;}
    public int[][] getBoard() { return board; }
    public Tetromino getCurrentPiece() { return currentPiece; }
    public Tetromino getNextPiece() { return nextPiece; }
    public int getScore() { return score; }
    public int getLevel() { return level; }
    public int getLinesCleared() { return linesCleared; }
    public int getBOARD_WIDTH() { return BOARD_WIDTH; }
    public int getBOARD_HEIGHT() { return BOARD_HEIGHT; }
    public int getTILE_SIZE() { return TILE_SIZE; }
    public int getGameState() { return gameState; }
    public void setGameState(int state) { gameState = state; notifyObservers(); }
    public int getMinWindowWidth() { return GameConstants.MIN_WINDOW_WIDTH; }
    public int getMinWindowHeight() { return MIN_WINDOW_HEIGHT; }
    public int getMaxWindowWidth() { return GameConstants.MAX_WINDOW_WIDTH; }
    public int getMaxWindowHeight() { return MAX_WINDOW_HEIGHT; }
    public Font getRetroFont() { return retroFont; }
    public void setRetroFont(Font f) { retroFont = f; notifyObservers(); }
    public Font getInstructionFont() { return instructionFont; }
    public void setInstructionFont(Font f) { instructionFont = f; notifyObservers(); }
    public boolean isScreenShake() { return screenShake; }
    public int getShakeOffsetX() { return shakeOffsetX; }
    public int getShakeOffsetY() { return shakeOffsetY; }
    public void setScreenShake(boolean b) { screenShake = b; notifyObservers(); }
    public void setShakeOffsetX(int x) { shakeOffsetX = x; notifyObservers(); }
    public void setShakeOffsetY(int y) { shakeOffsetY = y; notifyObservers(); }
    public boolean isFlashVisible() { return flashVisible; }
    public boolean isGameOverFlashVisible() { return gameOverFlashVisible; }
    public boolean isLevelUpFlashVisible() { return levelUpFlashVisible; }
    public int getSelectedMenuOption() { return selectedMenuOption; }
    public void setSelectedMenuOption(int opt) { selectedMenuOption = opt; notifyObservers(); }
    public String[] getMainMenuOptions() { return new String[]{"Start Game", "Level Selection", "Options", "Exit"}; }
    public int getSelectedLevelOption() { return selectedLevelOption; }
    public String[] getLevelOptions() { return new String[]{"Level 1", "Level 2", "Level 3", "Level 4", "Level 5", "Level 6", "Level 7", "Level 8", "Level 9", "Level 10"}; }
    public int getSelectedOptionMenu() { return selectedOptionMenu; }
    public String[] getOptionMenuOptions() { return new String[]{"Music Volume", "SFX Volume", "Controls", "Back"}; }
    public long getElapsedTime() { elapsedTime = (System.currentTimeMillis()-startTime)/1000; return elapsedTime; }
    public boolean isGhostPieceEnabled() { return ghostPieceEnabled; }
    public Color[] getPieceColors() { return pieceColors; }
    public int[] getShapeCount() { return shapeCount; }
    public boolean[] getFlashingLines() { return flashingLines; }
    public boolean isPaused() { return isPaused; }
    public void setPaused(boolean paused) { isPaused = paused; notifyObservers(); }
    public boolean isCurrentPieceJustSpawned() { return currentPieceJustSpawned; }
    public void setCurrentPieceJustSpawned(boolean value) { currentPieceJustSpawned = value; notifyObservers(); }
    public Random getRandom() { return random; }
    
    public void movePieceDown() {
        if (isValidPosition(currentPiece.getCoordinates(), currentPiece.getPosition().x, currentPiece.getPosition().y+1))
            currentPiece.getPosition().y++;
        else {
            fixPieceToBoard();
            clearLines();
            spawnNextPiece();
        }
        notifyObservers();
    }
    
    private void fixPieceToBoard() {
        boolean gameOver = false;
        for(Point p : currentPiece.getCoordinates()){
            int x = currentPiece.getPosition().x + p.x;
            int y = currentPiece.getPosition().y + p.y;
            if(x>=0 && x<BOARD_WIDTH && y>=0 && y<BOARD_HEIGHT)
                board[y][x] = currentPiece.getType()+1;
            if(y < GameConstants.BUFFER_ZONE)
                gameOver = true;
        }
        shapeCount[currentPiece.getType()]++;
        if(gameOver){
            gameState = GameConstants.GAME_OVER_STATE;
            gameOverFlashVisible = true;
        }
    }
    
    private void clearLines() {
        boolean anyLineFull = false;
        flashingLines = new boolean[BOARD_HEIGHT];
        for(int i = GameConstants.BUFFER_ZONE; i < BOARD_HEIGHT; i++){
            boolean lineFull = true;
            for(int j = 0; j < BOARD_WIDTH; j++){
                if(board[i][j] == 0){ lineFull = false; break; }
            }
            if(lineFull){ flashingLines[i] = true; anyLineFull = true; }
        }
        if(anyLineFull){
            gameState = GameConstants.FLASHING_STATE;
            flashVisible = true;
            removeFlashingLines();
        }
    }
    
    private void removeFlashingLines() {
        for(int i = GameConstants.BUFFER_ZONE; i < BOARD_HEIGHT; i++){
            if(flashingLines[i]){
                linesCleared++;
                int pointsPerLine = ghostPieceEnabled ? 100 : 200;
                score += pointsPerLine;
                for(int k = i; k > 0; k--){
                    System.arraycopy(board[k-1], 0, board[k], 0, BOARD_WIDTH);
                }
                for(int j = 0; j < BOARD_WIDTH; j++){
                    board[0][j] = 0;
                }
            }
        }
        flashingLines = null;
        int newLevel = score/500 + 1;
        if(newLevel > level){ level = newLevel; triggerLevelUp(); }
        notifyObservers();
    }
    
    private void triggerLevelUp() { gameState = GameConstants.LEVEL_UP_STATE; levelUpFlashVisible = true; }
    
    private void spawnNextPiece() {
        currentPiece = nextPiece;
        nextPiece = new Tetromino(random.nextInt(7), BOARD_WIDTH, GameConstants.BUFFER_ZONE);
        currentPieceJustSpawned = true;
        if(!isValidPosition(currentPiece.getCoordinates(), currentPiece.getPosition().x, currentPiece.getPosition().y)){
            gameState = GameConstants.GAME_OVER_STATE;
            gameOverFlashVisible = true;
        }
    }
    
    public void changePieceColors() {
        ColorUtil.changePieceColors(pieceColors);
        notifyObservers();
    }
    
    public void resetGame() {
        board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        currentPiece = new Tetromino(random.nextInt(7), BOARD_WIDTH, GameConstants.BUFFER_ZONE);
        nextPiece = new Tetromino(random.nextInt(7), BOARD_WIDTH, GameConstants.BUFFER_ZONE);
        score = 0;
        linesCleared = 0;
        level = 1;
        startTime = System.currentTimeMillis();
        isPaused = false;
        gameState = GameConstants.GAME_STATE;
        for(int i = 0; i < shapeCount.length; i++){
            shapeCount[i] = 0;
        }
        ghostPieceEnabled = true;
        notifyObservers();
    }
    
    private boolean isValidPosition(Point[] coords, int x, int y) {
        for(Point p : coords){
            int newX = x + p.x;
            int newY = y + p.y;
            if(newX < 0 || newX >= BOARD_WIDTH || newY >= BOARD_HEIGHT)
                return false;
            if(newY >= 0 && board[newY][newX] != 0)
                return false;
        }
        return true;
    }
    
    public void rotateCurrentPiece() {
        currentPiece.rotate();
        if(!isValidPosition(currentPiece.getCoordinates(), currentPiece.getPosition().x, currentPiece.getPosition().y))
            currentPiece.rotateBack();
        notifyObservers();
    }
    
    public void moveCurrentPieceLeft() {
        if(isValidPosition(currentPiece.getCoordinates(), currentPiece.getPosition().x - 1, currentPiece.getPosition().y)){
            currentPiece.getPosition().x--;
            notifyObservers();
        }
    }
    
    public void moveCurrentPieceRight() {
        if(isValidPosition(currentPiece.getCoordinates(), currentPiece.getPosition().x + 1, currentPiece.getPosition().y)){
            currentPiece.getPosition().x++;
            notifyObservers();
        }
    }
    
    public void dropCurrentPiece() {
        while(isValidPosition(currentPiece.getCoordinates(), currentPiece.getPosition().x, currentPiece.getPosition().y + 1)){
            currentPiece.getPosition().y++;
        }
        movePieceDown();
        notifyObservers();
    }
    
    public void toggleGhost() {
        ghostPieceEnabled = !ghostPieceEnabled;
        notifyObservers();
    }
}
