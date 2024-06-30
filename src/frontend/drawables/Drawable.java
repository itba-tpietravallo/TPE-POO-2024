package frontend.drawables;

import javafx.scene.canvas.GraphicsContext;

public interface Drawable extends Colorable {
    void draw(GraphicsContext gc);
}

