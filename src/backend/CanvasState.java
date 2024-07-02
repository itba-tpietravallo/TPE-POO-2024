package backend;

import backend.model.Figure;
import backend.model.Layer;
import backend.model.Point;

import java.util.*;
import java.util.stream.Stream;

public class CanvasState<T extends Figure> {
    private static final int STARTING_LAYERS = 3;
    private static final int DEFAULT_LAYER = 0;
    private final List<Layer<T>> layers = new ArrayList<>();
    private int currentLayerId;

    public CanvasState() {
        for (int i = 0; i < STARTING_LAYERS; i++) {
            layers.add(new Layer<>());
        }

        currentLayerId = DEFAULT_LAYER;
    }

    public void setCurrentLayer(Layer<T> l){
        int newIdx = this.layers.indexOf(l);
        // Ensure the layer belongs to this CanvasState
        this.currentLayerId = newIdx < 0 ? this.currentLayerId : newIdx;
    }

    public List<Layer<T>> getLayers() {
        return layers;
    }

    public Layer<T> addLayer() {
        Layer<T> newLayer = new Layer<>();
        this.layers.add(newLayer);
        return newLayer;
    }

    public void addFigure(T figure) {
        this.layers.get(currentLayerId).addFigure(figure);
    }

    public void deleteFigure(T figure) {
        this.layers.get(currentLayerId).deleteFigure(figure);
    }

    public Iterable<T> figures() {
        return layers
                .stream()
                .filter(Layer::isVisible)
                .flatMap(l -> l.figures().stream())
                .toList();
    }

    public Stream<T> intersectingFigures(Point location) {
        return layers.stream().flatMap(l -> l.figures().stream()).filter(f -> f.pointBelongs(location) );
    }

    public Optional<T> intersectsAnyFigure(Point location) {
        return this.intersectingFigures(location).findAny();
    }

}
