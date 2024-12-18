package frontend.drawables;

import backend.model.Ellipse;
import backend.model.Point;
import frontend.features.FigureFeatures;

public class DrawableEllipse extends Ellipse implements RenderAsOval {
    private FigureFeatures features;
    public DrawableEllipse(Point centerPoint, double sMayorAxis, double sMinorAxis) {
        super(centerPoint, sMayorAxis, sMinorAxis);
    }

    @Override
    public FigureFeatures getFeatures(){
        return this.features;
    }

    @Override
    public void setFeatures(FigureFeatures features){
        this.features = features;
    }

    public static DrawableEllipse createFromPoints(Point startPoint, Point endPoint) {
        Point centerPoint = new Point(Math.abs(endPoint.getX() + startPoint.getX()) / 2, (Math.abs((endPoint.getY() + startPoint.getY())) / 2));
        double sMayorAxis = Math.abs(endPoint.getX() - startPoint.getX());
        double sMinorAxis = Math.abs(endPoint.getY() - startPoint.getY());
        return new DrawableEllipse(centerPoint, sMayorAxis, sMinorAxis);
    }

    @Override
    public Drawable getCopy(){
        return new DrawableEllipse(this.getCenterPoint().getCopy(), this.getsMayorAxis(), this.getsMinorAxis());
    }

    @Override
    public Drawable[] split(){
        Point[] centers = this.divideCenter();
        double[] axis = this.divideAxis();
        double mayorAxis = axis[0], minorAxis = axis[1];
        return new Drawable[]{new DrawableEllipse(centers[0], mayorAxis, minorAxis), new DrawableEllipse(centers[1], mayorAxis, minorAxis)};
    }
}
