package backend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Layer<T> {

    private final String layer;
    private boolean isHidden;
    private final List<T> figures;

    public Layer(String layer) {
        this.layer = layer;
        this.isHidden = false;
        this.figures = new ArrayList<>();
    }

    public void addFigure(T figure) {
        figures.add(figure);
    }

    public void deleteFigure(T figure) {
        figures.remove(figure);
    }

    public List<T> figures() {
        return figures;
    }

    public void setHidden(boolean h) {
        this.isHidden = h;
    }

    public boolean isHidden() {
        return this.isHidden;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Layer<?> l &&
                this.layer.equals(l.layer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(layer, figures);
    }

    @Override
    public String toString() {
        return this.layer;
    }
}
