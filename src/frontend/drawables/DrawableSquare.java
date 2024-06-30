package frontend.drawables;

import backend.model.Point;
import backend.model.Square;
import javafx.scene.canvas.GraphicsContext;

public class DrawableSquare extends Square implements LinearlyColored, Drawable {
    public DrawableSquare(Point topLeft, double size) {
        super(topLeft, size);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.fillRect(this.getTopLeft().getX(), this.getTopLeft().getY(),
                Math.abs(this.getTopLeft().getX() - this.getBottomRight().getX()), Math.abs(this.getTopLeft().getY() - this.getBottomRight().getY()));
        gc.strokeRect(this.getTopLeft().getX(), this.getTopLeft().getY(),
                Math.abs(this.getTopLeft().getX() - this.getBottomRight().getX()), Math.abs(this.getTopLeft().getY() - this.getBottomRight().getY()));
    }
}
