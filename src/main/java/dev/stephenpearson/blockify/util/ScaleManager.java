package dev.stephenpearson.blockify.util;

public class ScaleManager {
    private double scaleFactor = 1.0;

    public double getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public double calculateScaleFactor(int panelWidth, int panelHeight, int baseWidth, int baseHeight, double minScale, double maxScale) {
        double scaleX = (double) panelWidth / baseWidth;
        double scaleY = (double) panelHeight / baseHeight;
        scaleFactor = Math.min(scaleX, scaleY);
        return Math.max(minScale, Math.min(maxScale, scaleFactor));
    }
}
