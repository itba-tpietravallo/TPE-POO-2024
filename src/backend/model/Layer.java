package backend.model;

import java.util.ArrayList;
import java.util.List;

public class Layer<T> implements Comparable<Layer<T>> {
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
    public int getLayerId() { return id; }
    public boolean isVisible() {
        return this.visible;
    }
    @Override
    public String toString() {
        return "Capa %d".formatted(this.id);
    }
    @Override
    public int compareTo(Layer<T> o) {
        return this.id - o.id;
    }
}
