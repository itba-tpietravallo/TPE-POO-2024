package frontend.drawables;

import backend.model.Circle;
import backend.model.Point;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class DrawableCircle extends Circle implements RadiallyColored {
    public DrawableCircle(Point centerPoint, double radius) {
        super(centerPoint, radius);
    }

    public void draw(GraphicsContext gc) {
        double diameter = this.getRadius() * 2;
        gc.fillOval(this.getCenterPoint().getX() - this.getRadius(), this.getCenterPoint().getY() - this.getRadius(), diameter, diameter);
        gc.strokeOval(this.getCenterPoint().getX() - this.getRadius(), this.getCenterPoint().getY() - this.getRadius(), diameter, diameter);
    }

    @Override
    public Paint getFill(Color color1, Color color2) {
        return RadiallyColored.super.getFill(color1, color2);
    }

    public static DrawableCircle createFromPoints(Point start, Point end) {
        double circleRadius = Math.abs(end.getX() - start.getX());
        return new DrawableCircle(start, circleRadius);
    }

    @Override
    public Drawable getCopy(){
        return new DrawableCircle(this.getCenterPoint().getCopy(), this.getRadius());
    }

    @Override
    public Drawable[] split(){
        Point[] centers = this.divideCenter();
        double radius = this.divideAxis()[0];
        return new Drawable[]{new DrawableCircle(centers[0], radius), new DrawableCircle(centers[1], radius)};
    }
}
