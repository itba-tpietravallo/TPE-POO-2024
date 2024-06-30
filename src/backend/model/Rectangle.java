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
    public void move(double diffX, double diffY) {
        this.getTopLeft().move(diffX, diffY);
        this.getBottomRight().move(diffX, diffY);
    }

    @Override
    public void moveTo(double x, double y) {
        double sizeX = sizeX();
        double sizeY = sizeY();
        this.getTopLeft().moveTo(x - sizeX / 2, y - sizeY / 2);
        this.getBottomRight().moveTo(x + sizeX / 2, y + sizeY / 2);
    }

    private double sizeX() {
        return this.getBottomRight().getX() - this.getTopLeft().getX();
    }

    private double sizeY(){
        return this.getBottomRight().getY() - this.getTopLeft().getY();
    }
}
