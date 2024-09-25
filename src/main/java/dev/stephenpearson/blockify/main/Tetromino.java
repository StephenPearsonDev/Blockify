package dev.stephenpearson.blockify.main;

import java.awt.Point;

public class Tetromino {
    public Point[] coordinates;
    public int type;
    public Point position;

    public Tetromino(int type, int boardWidth, int bufferZone) {
        this.type = type;
        setShape(type);
        int minY = getMinY();
        position = new Point(boardWidth / 2 - 1, bufferZone - minY);
    }

    private void setShape(int type) {
        coordinates = getShapeCoordinates(type);
    }

    public static Point[] getShapeCoordinates(int type) {
        switch (type) {
            case 0:
                return new Point[]{
                    new Point(-1, 0), new Point(0, 0),
                    new Point(1, 0), new Point(2, 0)
                };
            case 1:
                return new Point[]{
                    new Point(-1, -1), new Point(-1, 0),
                    new Point(0, 0), new Point(1, 0)
                };
            case 2:
                return new Point[]{
                    new Point(-1, 0), new Point(0, 0),
                    new Point(1, 0), new Point(1, -1)
                };
            case 3:
                return new Point[]{
                    new Point(0, 0), new Point(1, 0),
                    new Point(0, -1), new Point(1, -1)
                };
            case 4:
                return new Point[]{
                    new Point(-1, 0), new Point(0, 0),
                    new Point(0, -1), new Point(1, -1)
                };
            case 5:
                return new Point[]{
                    new Point(-1, 0), new Point(0, 0),
                    new Point(1, 0), new Point(0, -1)
                };
            case 6:
                return new Point[]{
                    new Point(-1, -1), new Point(0, -1),
                    new Point(0, 0), new Point(1, 0)
                };
            default:
                return new Point[]{};
        }
    }

    public int getMinY() {
        int minY = Integer.MAX_VALUE;
        for (Point p : coordinates) {
            if (p.y < minY) {
                minY = p.y;
            }
        }
        return minY;
    }

    public void rotate() {
        if (type == 3) return; //
        for (Point p : coordinates) {
            int x = p.x;
            int y = p.y;
            p.x = -y;
            p.y = x;
        }
    }

    public void rotateBack() {
        if (type == 3) return; 
        for (Point p : coordinates) {
            int x = p.x;
            int y = p.y;
            p.x = y;
            p.y = -x;
        }
    }
    
    public int getType() {
        return type;
    }
}
