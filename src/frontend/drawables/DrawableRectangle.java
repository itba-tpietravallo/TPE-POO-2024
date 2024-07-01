package frontend.drawables;

import backend.model.Point;
import backend.model.Rectangle;
import javafx.scene.canvas.GraphicsContext;

public class DrawableRectangle extends Rectangle implements LinearlyColored {
    public DrawableRectangle(Point topLeft, Point bottomRight) {
        super(topLeft, bottomRight);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.fillRect(this.getTopLeft().getX(), this.getTopLeft().getY(),
                Math.abs(this.getTopLeft().getX() - this.getBottomRight().getX()), Math.abs(this.getTopLeft().getY() - this.getBottomRight().getY()));
        gc.strokeRect(this.getTopLeft().getX(), this.getTopLeft().getY(),
                Math.abs(this.getTopLeft().getX() - this.getBottomRight().getX()), Math.abs(this.getTopLeft().getY() - this.getBottomRight().getY()));
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
