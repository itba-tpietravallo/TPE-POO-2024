package frontend.drawables;

import backend.model.Figure;
import javafx.scene.canvas.GraphicsContext;

public interface Drawable extends Figure {
    void draw(GraphicsContext gc);
}
