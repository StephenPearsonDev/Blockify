package dev.stephenpearson.blockify.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

public class FontLoader {
    private Font retroFont;
    private Font instructionFont;

    public FontLoader(double scaleFactor, int baseFontSize) {
        loadCustomFont(scaleFactor, baseFontSize);
    }

    private void loadCustomFont(double scaleFactor, int baseFontSize) {
        try {
            InputStream is = getClass().getResourceAsStream("/PressStart2P-Regular.ttf");
            if (is == null) {
                throw new IllegalArgumentException("Font file not found in resources.");
            }
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            retroFont = customFont.deriveFont(Font.BOLD, baseFontSize * (float) scaleFactor);
            instructionFont = retroFont.deriveFont(Font.PLAIN, (float) (baseFontSize * 0.8 * scaleFactor));
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            retroFont = new Font("Monospaced", Font.BOLD, baseFontSize);
            instructionFont = retroFont.deriveFont(Font.PLAIN, (float) (baseFontSize * 0.8));
        }
    }

    public Font getRetroFont() {
        return retroFont;
    }

    public Font getInstructionFont() {
        return instructionFont;
    }
}
