package frontend.features;

import frontend.drawables.Drawable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public enum Shade {
    NOSHADE(0, true, "Sin sombra") {
        @Override
        public void drawShade(GraphicsContext gc, Drawable figure, Color color) {}
    },
    SIMPLE(10, true, "Simple"),
    SIMPLEINVERTED(-10, true, "Simple inversa"),
    COLORED(10, false, "Coloreada"),
    COLOREDINVERTED(-10, false, "Coloreada inversa") ;
    private static final Color DEFAULT_COLOR = Color.GRAY;
    private static final Stroke SHADE_STROKE = Stroke.NOSTROKE;
    private final int offset;
    private final boolean useDefaultColor;
    private final String name;

    Shade(int offset, boolean useDefaultColor, String name) {
        this.offset = offset;
        this.useDefaultColor = useDefaultColor;
        this.name = name;
    }

    public void drawShade(GraphicsContext gc, Drawable figure, Color figureColor) {
        gc.setFill( this.useDefaultColor ? DEFAULT_COLOR : figureColor.darker());
        figure.move(this.offset, this.offset);
        SHADE_STROKE.setStroke(gc);
        figure.draw(gc);
        figure.move(-this.offset, -this.offset);
    }
    @Override
    public String toString() {
        return name;
    }
}
