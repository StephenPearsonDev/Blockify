package dev.stephenpearson.blockify.util;

import java.awt.Color;
import java.util.Random;

public class ColorUtil {
    private static final Random random = new Random();

 
    public static Color[] initPieceColors() {
        return new Color[]{
            new Color(102, 204, 255), // Light Blue
            new Color(153, 102, 255), // Purple
            new Color(255, 178, 102), // Orange
            new Color(255, 255, 153), // Yellow
            new Color(102, 255, 153), // Green
            new Color(255, 153, 204), // Pink
            new Color(255, 102, 102)  // Red
        };
    }

   
    public static void changePieceColors(Color[] pieceColors) {
        for (int i = 0; i < pieceColors.length; i++) {
            int red = (pieceColors[i].getRed() + random.nextInt(256)) / 2;
            int green = (pieceColors[i].getGreen() + random.nextInt(256)) / 2;
            int blue = (pieceColors[i].getBlue() + random.nextInt(256)) / 2;
            pieceColors[i] = new Color(red, green, blue);
        }
    }
}
