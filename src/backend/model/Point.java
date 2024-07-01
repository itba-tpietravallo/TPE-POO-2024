package backend.model;

public class Point implements Movable, Copiable<Point>{

    private double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void move(double diffX, double diffY) {
        this.moveTo(this.x + diffX, this.y + diffY);
    }

    @Override
    public void moveTo(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Point getCopy(){
        return new Point(x, y);
    }

    @Override
    public String toString() {
        return String.format("{%.2f , %.2f}", x, y);
    }

}

