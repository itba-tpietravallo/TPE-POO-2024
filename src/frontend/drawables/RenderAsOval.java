package frontend.drawables;
import backend.model.Point;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;

public interface RenderAsOval extends Drawable {
    Point getCenterPoint();
    double getsMayorAxis();
    double getsMinorAxis();

    default void drawShape(GraphicsContext gc) {
        gc.strokeOval(this.getCenterPoint().getX() - (this.getsMayorAxis() / 2), this.getCenterPoint().getY() - (this.getsMinorAxis() / 2), this.getsMayorAxis(), this.getsMinorAxis());
        gc.fillOval(this.getCenterPoint().getX() - (this.getsMayorAxis() / 2), this.getCenterPoint().getY() - (this.getsMinorAxis() / 2), this.getsMayorAxis(), this.getsMinorAxis());
    }
    @Override
    default Paint getFill(Color color1, Color color2) {
        return new RadialGradient(0, 0, 0.5, 0.5, 0.5, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, color1),
                new Stop(1, color2)
        );
    }
}
