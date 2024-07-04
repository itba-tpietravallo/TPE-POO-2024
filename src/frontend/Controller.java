package frontend;

import backend.CanvasState;
import backend.model.Figure;
import backend.model.Point;
import frontend.drawables.Drawable;
import frontend.features.FigureFeatures;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Controller {
    CanvasState<Drawable> state;
    StatusPane statusPane;
    PaintPane paintPane;
    // Currently selected figure (may be null)
    Drawable selectedFigure = null;
    // Features by figure map
    Map<Figure, FigureFeatures> figureFeaturesMap = new HashMap<>();
    Point startPoint;
    Point endPoint;

    public Controller(CanvasState<Drawable> state, StatusPane statusPane, PaintPane pane) {
        this.state = state;
        this.statusPane = statusPane;
        this.paintPane = pane;

        this.paintPane.canvas.setOnMouseClicked(this::onMouseClicked);
        this.paintPane.canvas.setOnMouseMoved(this::onMouseMoved);
        this.paintPane.canvas.setOnMouseDragged(this::onMouseDragged);
        this.paintPane.canvas.setOnMouseReleased(this::onMouseReleased);
        this.paintPane.canvas.setOnMousePressed(this::onMousePressed);

    }
    public CanvasState<Drawable> getState() {
        return this.state;
    }
    public Optional<Drawable> getSelectedFigure() {
        return Optional.ofNullable(this.selectedFigure);
    }
    private Point pointFromEvent(MouseEvent event) {
        return new Point(event.getX(), event.getY());
    }
    private void onMouseReleased(MouseEvent event) {
        Point endPoint = this.pointFromEvent(event);

        if(startPoint == null) {
            return ;
        }

        if(endPoint.getX() < startPoint.getX() || endPoint.getY() < startPoint.getY()) {
            return ;
        }

        this.paintPane.figureButtons
                .stream()
                .filter(e -> e.getKey().isSelected())
                .map(e -> e.getValue().apply(startPoint, endPoint))
                .findFirst()
                .ifPresent(f -> {
                    FigureFeatures features = new FigureFeatures(
                            this.paintPane.fillColorPicker1.getValue(),
                            this.paintPane.fillColorPicker2.getValue(),
                            this.paintPane.shadeOptions.getValue(),
                            this.paintPane.strokeWidth.getValue(),
                            this.paintPane.strokeOptions.getValue()
                    );

                    figureFeaturesMap.put(f, features);
                    state.addFigure(f);
                    startPoint = null;
                    this.paintPane.redrawCanvas();
                });
    }
    private void onMousePressed(MouseEvent event) {
        startPoint = this.pointFromEvent(event);
    }
    private void onMouseMoved(MouseEvent event) {
        updateStatusLabel(pointFromEvent(event));
    }
    private void onMouseClicked(MouseEvent event) {
        Point location = this.pointFromEvent(event);
        this.updateStatusLabel(location, "Ninguna figura encontrada");
        if(this.selectionMode()) {
            this.selectedFigure = state.intersectingFigures(location).findFirst().orElse(null);
            this.paintPane.showValues(getSelectedFigure());
            this.paintPane.redrawCanvas();
        }
    }
    private void onMouseDragged(MouseEvent event) {
        if(this.selectionMode()) {
            Point eventPoint = this.pointFromEvent(event);
            double diffX = (eventPoint.getX() - startPoint.getX()) / 100;
            double diffY = (eventPoint.getY() - startPoint.getY()) / 100;
            getSelectedFigure().ifPresent(f -> { f.move(diffX, diffY); this.paintPane.redrawCanvas(); });
        }
    }
    private void updateStatusLabel(Point location) {
        this.updateStatusLabel(location, location.toString());
    }
    private void updateStatusLabel(Point location, String defaultText) {
        state.intersectsAnyFigure(location).ifPresentOrElse(x -> {
            statusPane.updateStatus(
                    state.intersectingFigures(location)
                            .map(Drawable::toString)
                            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString()
            );
        }, () -> { statusPane.updateStatus(defaultText); });
    }
    boolean selectionMode() {
        return this.paintPane.selectionButton.isSelected();
    }
    private void setCurrentLayerMode(){
        setCurrentLayerMode(this.state.getCurrentLayer().isVisible());
    }
    private void setCurrentLayerMode(boolean visible){
        this.state.getCurrentLayer().setVisible(visible);
        this.paintPane.showButton.setSelected(visible);
        this.paintPane.hideButton.setSelected(!visible);
    }
    private <T extends Event> EventHandler<T> runAndRedrawIfSelectedFigurePresent(Consumer<Drawable> figureConsumer ) {
        return (event) -> getSelectedFigure().ifPresent(f -> {
            figureConsumer.accept(f);
            this.paintPane.redrawCanvas();
        });
    }
    private void bindButtonToLayerAction(ToggleButton button, Runnable action) {
        button.setOnAction(x -> {
            action.run();
            layers.setAll(this.state.getLayers());
            layerOptions.setValue(this.state.getCurrentLayer());
            setCurrentLayerMode();
            button.setSelected(false);
        });
    }
    private void bindButtonToLayerActionAndRedraw(ToggleButton button, Runnable action) {
        this.bindButtonToLayerAction(button, () -> { action.run(); this.paintPane.redrawCanvas(); });
    }
    private void bindButtonToRedraw(ButtonBase button, Runnable action) {
        button.setOnAction((x) -> { action.run(); this.paintPane.redrawCanvas(); });
    }
    private void bindButtonToSelectedFigure(ButtonBase button, Consumer<Drawable> action) {
        button.setOnAction(this.runAndRedrawIfSelectedFigurePresent(action));
    }
    private <T> void bindComboBoxToSelectedFigure(ComboBoxBase<T> box, BiConsumer<FigureFeatures, T> featureSetter) {
        box.setOnAction(this.runAndRedrawIfSelectedFigurePresent(f -> {
            featureSetter.accept(this.figureFeaturesMap.get(f), box.getValue());
        }));
    }
    private <T> void bindChoiceBoxToSelectedFigure(ChoiceBox<T> box, BiConsumer<FigureFeatures, T> featureSetter) {
        box.setOnAction(this.runAndRedrawIfSelectedFigurePresent(f -> {
            featureSetter.accept(this.figureFeaturesMap.get(f), box.getValue());
        }));
    }
    private void bindSliderToSelectedFigure(Slider slider, BiConsumer<FigureFeatures, Double> featureSetter) {
        slider.setOnMouseDragged(this.runAndRedrawIfSelectedFigurePresent(f -> {
            featureSetter.accept(this.figureFeaturesMap.get(f), slider.getValue());
        }));
    }
}
