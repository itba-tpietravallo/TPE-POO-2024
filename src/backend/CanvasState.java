package backend;

import backend.model.Figure;
import backend.model.Layer;
import backend.model.Point;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CanvasState<T extends Figure> {
    private static final int STARTING_LAYERS = 3;
    private int layersCreated = STARTING_LAYERS;
    private final List<Layer<T>> layers = new ArrayList<>();

    public void addFigure(T figure, Layer<T> layer) {
        layer.addFigure(figure);
    }

    public void deleteFigure(T figure, Layer<T> layer) {
        layer.deleteFigure(figure);
    }

    public Iterable<T> figures() {
        return layers
                .stream()
//                .filter(layer -> !layer.isHidden())
                .flatMap(l -> l.figures().stream())
                .collect(Collectors.toList());
    }

    public Stream<T> intersectingFigures(Point location) {
        return layers.stream().flatMap(l -> l.figures().stream()).filter(f -> f.pointBelongs(location) );
    }

    public Optional<T> intersectsAnyFigure(Point location) {
        return this.intersectingFigures(location).findAny();
    }

    public int nextLayer() {
        return ++layersCreated;
    }

}
