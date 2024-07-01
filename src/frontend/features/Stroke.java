package frontend.features;

import javafx.scene.canvas.GraphicsContext;

import javafx.scene.paint.Color;

public enum Stroke {
    NOSTROKE(new double[]{}, "Ninguna"){
        @Override
        public Color getColor(boolean isSelected){
            return Color.TRANSPARENT;
        }
    },
    NORMAL(new double[]{0}, "Normal"),
    SIMPLE(new double[]{10}, "P. Simple"),
    COMPLEX(new double[]{30, 10, 15, 10}, "P. Coloreada");

    private static final Color SELECTED_COLOR = Color.RED, DEFAULT_COLOR = Color.BLACK;
    private final double[] spacing;
    private final String name;
    Stroke(double[] spacing, String name){
        this.spacing = spacing;
        this.name = name;
    }

    public void setStroke(GraphicsContext gc, double strokeWidth, boolean isSelected) {
        gc.setLineDashes(this.spacing);
        gc.setLineWidth(strokeWidth);
        setSafeStrokeWidth(gc, strokeWidth, isSelected);
    }

    public void setStroke(GraphicsContext gc){
        setStroke(gc, 0, false);
    }

    public Color getColor(boolean isSelected){
        return isSelected ? SELECTED_COLOR : DEFAULT_COLOR;
    }
    @Override
    public String toString() {
        return name;
    }

    private void setSafeStrokeWidth(GraphicsContext gc, double strokeWidth, boolean isSelected) {
        if (strokeWidth == 0) {
            gc.setStroke(Stroke.NOSTROKE.getColor(isSelected));
        } else {
            gc.setStroke(getColor(isSelected));
        }
    }
}
