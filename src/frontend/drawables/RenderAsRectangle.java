package frontend.drawables;

import backend.model.Point;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;

public interface RenderAsRectangle extends Drawable {
    Point getTopLeft();
    Point getBottomRight();
    default void drawShape(GraphicsContext gc) {
        gc.fillRect(this.getTopLeft().getX(), this.getTopLeft().getY(),
                Math.abs(this.getTopLeft().getX() - this.getBottomRight().getX()), Math.abs(this.getTopLeft().getY() - this.getBottomRight().getY()));
        gc.strokeRect(this.getTopLeft().getX(), this.getTopLeft().getY(),
                Math.abs(this.getTopLeft().getX() - this.getBottomRight().getX()), Math.abs(this.getTopLeft().getY() - this.getBottomRight().getY()));
    }
    @Override
    default Paint getFill(Color color1, Color color2) {
        return new LinearGradient(0, 0, 1, 0, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, color1),
                new Stop(1, color2)
        );
    }
}
