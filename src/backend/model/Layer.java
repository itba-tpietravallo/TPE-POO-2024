package backend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Layer<T> {

    private final int id;
    private boolean visible;
    private final List<T> figures = new ArrayList<>();

    public Layer(int id) {
        this.id = id;
        this.visible = true;
    }

    public int getId(){
        return this.id;
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

    public void setVisible(boolean h) {
        this.visible = h;
    }

    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Layer<?> l &&
                this.id == (l.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, figures);
    }

    @Override
    public String toString() {
        return "Capa %d".formatted(this.id);
    }
}
