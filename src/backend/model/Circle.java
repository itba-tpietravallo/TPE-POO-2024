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

}
