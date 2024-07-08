package frontend.drawables;

import backend.model.Point;
import backend.model.Square;
import frontend.features.FigureFeatures;

public class DrawableSquare extends Square implements RenderAsRectangle {
    private FigureFeatures features;
    public DrawableSquare(Point topLeft, double size) {
        super(topLeft, size);
    }

    @Override
    public FigureFeatures getFeatures(){
        return this.features;
    }

    @Override
    public void setFeatures(FigureFeatures features){
        this.features = features;
    }

    public static DrawableSquare createFromPoints(Point start, Point end) {
        double size = Math.abs(end.getX() - start.getX());
        return new DrawableSquare(start, size);
    }

    @Override
    public Drawable getCopy(){
        return new DrawableSquare(this.getTopLeft().getCopy(), this.sizeX());
    }

    @Override
    public Drawable[] split(){
        Point[] points = this.divide();
        return new Drawable[]{createFromPoints(points[0], points[1]), createFromPoints(points[2], points[3])};
    }
}
