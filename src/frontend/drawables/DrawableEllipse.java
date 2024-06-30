package frontend.drawables;

import backend.model.Ellipse;
import backend.model.Point;
import javafx.scene.canvas.GraphicsContext;

public class DrawableEllipse extends Ellipse implements RadiallyColored {
    public DrawableEllipse(Point centerPoint, double sMayorAxis, double sMinorAxis) {
        super(centerPoint, sMayorAxis, sMinorAxis);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.strokeOval(this.getCenterPoint().getX() - (this.getsMayorAxis() / 2), this.getCenterPoint().getY() - (this.getsMinorAxis() / 2), this.getsMayorAxis(), this.getsMinorAxis());
        gc.fillOval(this.getCenterPoint().getX() - (this.getsMayorAxis() / 2), this.getCenterPoint().getY() - (this.getsMinorAxis() / 2), this.getsMayorAxis(), this.getsMinorAxis());
    }

    public static DrawableEllipse createFromPoints(Point startPoint, Point endPoint) {
        Point centerPoint = new Point(Math.abs(endPoint.getX() + startPoint.getX()) / 2, (Math.abs((endPoint.getY() + startPoint.getY())) / 2));
        double sMayorAxis = Math.abs(endPoint.getX() - startPoint.getX());
        double sMinorAxis = Math.abs(endPoint.getY() - startPoint.getY());
        return new DrawableEllipse(centerPoint, sMayorAxis, sMinorAxis);
    }

    @Override
    public Drawable getCopy(){
        return new DrawableEllipse(super.getCenterPoint(), super.getsMayorAxis(), super.getsMinorAxis());
    }
}
