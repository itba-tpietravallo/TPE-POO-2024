package backend.model;

public class Circle extends Ellipse implements Figure {


    public Circle(Point centerPoint, double radius) {
        super(centerPoint, radius, radius);
    }

    @Override
    public String toString() {
        return String.format("Círculo [Centro: %s, Radio: %.2f]", super.getCenterPoint(), super.getsMayorAxis());
    }
    public double getRadius() {
        return super.getsMayorAxis();
    }

    @Override
    public boolean pointBelongs(Point p) {
        return Math.sqrt(Math.pow(this.getCenterPoint().getX() - p.getX(), 2) +
                Math.pow(this.getCenterPoint().getY() - p.getY(), 2)) < this.getRadius();
    }
}
