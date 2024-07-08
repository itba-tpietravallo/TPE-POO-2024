package frontend.drawables;

import backend.model.Point;
import backend.model.Rectangle;
import frontend.features.FigureFeatures;

public class DrawableRectangle extends Rectangle implements RenderAsRectangle {
    private FigureFeatures features;
    public DrawableRectangle(Point topLeft, Point bottomRight) {
        super(topLeft, bottomRight);
    }

    @Override
    public FigureFeatures getFeatures(){
        return this.features;
    }

    @Override
    public void setFeatures(FigureFeatures features){
        this.features = features;
    }

    public static DrawableRectangle createFromPoints(Point start, Point end) {
        return new DrawableRectangle(start, end);
    }

    @Override
    public Drawable getCopy(){
        return new DrawableRectangle(this.getTopLeft().getCopy(), this.getBottomRight().getCopy());
    }

    @Override
    public Drawable[] split(){
        Point[] points = this.divide();
        return new Drawable[]{createFromPoints(points[0], points[1]), createFromPoints(points[2], points[3])};
    }
}
