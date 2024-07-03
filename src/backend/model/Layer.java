package backend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Layer<T> {
    private static int CURRENT_LAYER_ID = 1;
    private final int id = CURRENT_LAYER_ID++;
    private boolean visible = true;
    private final List<T> figures = new ArrayList<>();
    public void addFigure(T figure) {
        figures.add(figure);
    }

    public void deleteFigure(T figure) {
        figures.remove(figure);
    }

    public List<T> figures() {
        return figures;
    }

    public void setVisible(boolean h) {
        this.visible = h;
    }

    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public String toString() {
        return "Capa %d".formatted(this.id);
    }
}
