package frontend.features;

import frontend.drawables.Drawable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Optional;
import java.util.function.Supplier;

public enum ShadeType {

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

    ShadeType(int offset, boolean useDefaultColor) {
        this.offset = offset;
        this.defaultColor = useDefaultColor;
    }

    private final int offset;
    private final boolean defaultColor;

    public void drawShade(GraphicsContext gc, Drawable figure, Color color) {
        gc.setFill(color);
        figure.move(this.offset, this.offset);
        figure.draw(gc);
        figure.move(-this.offset, -this.offset);
    }

    public boolean usesDefaultColor() {
        return this.defaultColor;
    };
}
