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
    private static final int DEFAULT_LAYER = 0;
    private int layersCreated = STARTING_LAYERS;
    private final List<Layer<T>> layers = new ArrayList<>();
    private int currentLayer = DEFAULT_LAYER;

    public CanvasState() {
        for (int i = 1; i <= STARTING_LAYERS; i++) {
            layers.add(new Layer<>(i));
        }
    }

    public void setCurrentLayer(Layer<T> l){
//        todo validar
        this.currentLayer = this.layers.indexOf(l);
    }

    public List<Layer<T>> getLayers() {
        return layers;
    }

    public Layer<T> addLayer() {
        return new Layer<>(++layersCreated);
    }

    // todo make deletelayer behaviour

    public void addFigure(T figure) {
        layers.get(currentLayer).addFigure(figure);
    }

    public void deleteFigure(T figure) {
        layers.get(currentLayer).deleteFigure(figure);
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

    public void hideCurrentLayer() {
        layers.get(currentLayer).setVisible(false);
    }

    public void showCurrentLayer() {
        layers.get(currentLayer).setVisible(true);
    }

    public int nextLayer() {
        return layersCreated++;
    }

}
