package backend.model;

public class Rectangle implements Figure, Movable {

    private final Point topLeft, bottomRight;

    public Rectangle(Point topLeft, Point bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    public Point getTopLeft() {
        return topLeft;
    }

    public Point getBottomRight() {
        return bottomRight;
    }

    @Override
    public boolean pointBelongs(Point p) {
        return p.getX() > this.getTopLeft().getX() && p.getX() < this.getBottomRight().getX() &&
                p.getY() > this.getTopLeft().getY() && p.getY() < this.getBottomRight().getY();
    }

    @Override
    public String toString() {
        return String.format("Rectángulo [ %s , %s ]", topLeft, bottomRight);
    }

    @Override
    public void moveTo(double x, double y) {
        double sizeX = sizeX();
        double sizeY = sizeY();
        this.getTopLeft().moveTo(x - sizeX / 2, y - sizeY / 2);
        this.getBottomRight().moveTo(x + sizeX / 2, y + sizeY / 2);
    }

    @Override
    // Returns the X coordinate of the rectangle's center
    public double getX() {
        return this.getTopLeft().getX() + sizeX() / 2;
    }

    // Returns the Y coordinate of the rectangle's center
    public double getY() {
        return this.getTopLeft().getY() + sizeY() / 2;
    }

    public double sizeX() {
        return this.getBottomRight().getX() - this.getTopLeft().getX();
    }

    public double sizeY(){
        return this.getBottomRight().getY() - this.getTopLeft().getY();
    }
}
