package frontend.features;

import frontend.drawables.Drawable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public enum Shade {

    NOSHADE(0, true) {
        @Override
        public String toString() {
            return "Sin sombra";
        }
        @Override
        public void drawShade(GraphicsContext gc, Drawable figure, Color color) {}
    },
    SIMPLE(+10, true) {
        @Override
        public String toString() {
            return "Simple";
        }
    },
    SIMPLEINVERTED(-10, true){
        @Override
        public String toString() {
            return "Simple inversa";
        }
    },
    COLORED(10, false) {
        @Override
        public String toString() {
            return "Coloreada";
        }
    },
    COLOREDINVERTED(-10, false) {
        @Override
        public String toString() {
            return "Coloreada inversa";
        }
    };

    Shade(int offset, boolean useDefaultColor) {
        this.offset = offset;
        this.useDefaultColor = useDefaultColor;
    }

    private static final Color DEFAULT_COLOR = Color.GRAY;
    private final int offset;
    private final boolean useDefaultColor;

    public void drawShade(GraphicsContext gc, Drawable figure, Color figureColor) {
        gc.setFill( this.useDefaultColor ? DEFAULT_COLOR : figureColor.darker());
        figure.move(this.offset, this.offset);
        figure.draw(gc);
        figure.move(-this.offset, -this.offset);
    }

}
