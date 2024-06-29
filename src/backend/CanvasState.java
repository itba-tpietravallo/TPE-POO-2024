package backend;

import backend.model.Figure;

import java.util.ArrayList;
import java.util.List;

public class CanvasState<T> {

    private final List<T> list = new ArrayList<>();

    public void addFigure(T figure) {
        list.add(figure);
    }

    public void deleteFigure(T figure) {
        list.remove(figure);
    }

    public Iterable<T> figures() {
        return new ArrayList<>(list);
    }

}
