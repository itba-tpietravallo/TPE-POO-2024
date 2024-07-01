package backend;

import backend.model.Figure;
import backend.model.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class CanvasState<T extends Figure> {

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

    public Stream<T> intersectingFigures(Point location) {
        return list.stream().filter(f -> f.pointBelongs(location) );
    }

    public Optional<T> intersectsAnyFigure(Point location) {
        return this.intersectingFigures(location).findAny();
    }
}
