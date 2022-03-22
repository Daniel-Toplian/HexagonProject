package board;

import java.awt.*;

public class Hexagon extends Polygon {
    private static final long serialVersionUID = 1L;
    public static final int SIDES = 6;
    public static final int EMPTY = 0;
    public static final int PLAYER1 = 1;
    public static final int PLAYER2 = 2;
    public static final int COMPUTER = PLAYER2;
    private Point[] points = new Point[SIDES];
    private Point center = new Point(0, 0);
    private int radius;
    private int rotation = 90;
    private int player = EMPTY;
    private int storageCol;
    private int storageRow;
    private Color lineColor = Color.black;

    public Hexagon(Point center, int radius, int storageCol, int storageRow) {
        npoints = SIDES;
        xpoints = new int[SIDES];
        ypoints = new int[SIDES];
        this.center = center;
        this.radius = radius;
        this.storageCol = storageCol;
        this.storageRow = storageRow;
        updatePoints();
    }

    public Hexagon(int centerX, int centerY, int radius, int storageCol, int storageRow) {
        this(new Point(centerX, centerY), radius, storageCol, storageRow);
    }

    public int getPlayer() {
        return this.player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }


    public int getStorageCol() {
        return storageCol;
    }

    public int getStorageRow() {
        return storageRow;
    }

    public Point getCenter() {
        return center;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    protected void updatePoints() {
        for (int p = 0; p < SIDES; p++) {
            double angle = findAngle((double) p / SIDES);
            Point point = findPoint(angle);
            xpoints[p] = point.x;
            ypoints[p] = point.y;
            points[p] = point;
        }
    }

    private double findAngle(double fraction) {
        return fraction * Math.PI * 2 + Math.toRadians((rotation + 180) % 360);
    }

    private Point findPoint(double angle) {
        int x = (int) (center.x + Math.cos(angle) * radius);
        int y = (int) (center.y + Math.sin(angle) * radius);
        return new Point(x, y);
    }

    public void paint(Graphics g, int lineThickness, boolean filled) {
        Color color = Color.black;
        if (filled) {
            switch (this.player) {
                case PLAYER1:
                    color = Color.red;
                    break;
                case PLAYER2:
                    color = Color.blue;
                    break;
                default:
                    color = Color.white;
                    break;
            }
        }
        g.setColor(color);
        ((Graphics2D) g).setStroke(new BasicStroke(lineThickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        if (filled)
            g.fillPolygon(xpoints, ypoints, npoints);
        else {
            g.setColor(lineColor);
            g.drawPolygon(xpoints, ypoints, npoints);
        }
    }
}
