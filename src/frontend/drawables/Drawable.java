package frontend.drawables;

import backend.model.Copiable;
import backend.model.Figure;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public interface Drawable extends Figure, Copiable<Drawable> {
    void draw(GraphicsContext gc);
    Paint getFill(Color color1, Color color2);
}

