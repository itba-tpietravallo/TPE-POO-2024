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

    public Layer<T> getCurrentLayer() {
        return this.layers.get(currentLayerId);
    }

    public List<Layer<T>> getLayers() {
        return layers;
    }

    public void addLayer() {
        Layer<T> newLayer = new Layer<>();
        this.layers.add(newLayer);
        this.currentLayerId = this.layers.size() - 1;
    }

    public void deleteLayer(){
        if (currentLayerId >= STARTING_LAYERS) {
            layers.remove(currentLayerId);
            currentLayerId = Math.min(layers.size() - 1, currentLayerId);
        }
    }

    public void addFigure(T figure) {
        Layer<T> layer = this.layers.get(currentLayerId);
        if(layer.isVisible()){
            layer.addFigure(figure);
        }
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
        return layers.stream()
                .sorted(Comparator.reverseOrder())
                .filter(Layer::isVisible)
                .flatMap(l -> l.figures().reversed().stream())
                .filter(f -> f.pointBelongs(location) );
    }

    public Optional<T> intersectsAnyFigure(Point location) {
        return this.intersectingFigures(location).findAny();
    }
}
