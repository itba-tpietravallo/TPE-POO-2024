package frontend;

import backend.CanvasState;
import backend.model.Point;
import frontend.drawables.*;
import frontend.features.FigureFeatures;
import frontend.features.Shade;
import frontend.features.Stroke;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Controller {
    CanvasState<Drawable> state;
    StatusPane statusPane;
    PaintPane paintPane;
    // Currently selected figure (maybe null)
    Drawable selectedFigure = null;
    // Features by figure map
    Map<Drawable, FigureFeatures> figureFeaturesMap = new HashMap<>();
    Point startPoint;
    Point endPoint;

    // Default fill colors
    private static final Color DEFAULT_FILL_COLOR_1 = Color.CYAN;
    private static final Color DEFAULT_FILL_COLOR_2 = Color.web("ccffcc");
    // Default shade
    private static final Shade DEFAULT_SHADE = Shade.NOSHADE;
    // Default stroke
    private static final Stroke DEFAULT_STROKE_TYPE = Stroke.NORMAL;
    private static final int DEFAULT_STROKE_WIDTH = 5;
    // Duplicate offset
    private static final int DUPLICATE_OFFSET = 12;
    List<Map.Entry<ToggleButton, BiFunction<Point,Point,Drawable>>> figureButtonsToActions;

    public Controller(CanvasState<Drawable> state, StatusPane statusPane, PaintPane pane) {
        this.state = state;
        this.statusPane = statusPane;
        this.paintPane = pane;

        this.figureButtonsToActions = List.of(
                Map.entry(this.paintPane.rectangleButton,	DrawableRectangle::createFromPoints),
                Map.entry(this.paintPane.circleButton,		DrawableCircle::createFromPoints),
                Map.entry(this.paintPane.squareButton, 	DrawableSquare::createFromPoints),
                Map.entry(this.paintPane.ellipseButton, 	DrawableEllipse::createFromPoints)
        );

        this.paintPane.canvas.setOnMouseClicked(this::onMouseClicked);
        this.paintPane.canvas.setOnMouseMoved(this::onMouseMoved);
        this.paintPane.canvas.setOnMouseDragged(this::onMouseDragged);
        this.paintPane.canvas.setOnMouseReleased(this::onMouseReleased);
        this.paintPane.canvas.setOnMousePressed(this::onMousePressed);

        this.bindComboBoxToSelectedFigure(this.paintPane.fillColorPicker1, FigureFeatures::setColor1);
        this.bindComboBoxToSelectedFigure(this.paintPane.fillColorPicker2, FigureFeatures::setColor2);
        this.bindChoiceBoxToSelectedFigure(this.paintPane.shadeOptions, FigureFeatures::setShade);
        this.bindChoiceBoxToSelectedFigure(this.paintPane.strokeOptions, FigureFeatures::setStroke);
        this.bindSliderToSelectedFigure(this.paintPane.strokeWidth, FigureFeatures::setStrokeWidth);

        this.bindButtonToSelectedFigure(this.paintPane.deleteButton, f -> {
            this.state.deleteFigure(f);
            figureFeaturesMap.remove(f);
            selectedFigure = null;
        });

        this.paintPane.layerOptions.setOnAction( event -> {
            this.state.setCurrentLayer(this.paintPane.layerOptions.getValue());
            setCurrentLayerMode();
        });

        this.paintPane.layers.addAll(state.getLayers());
        this.paintPane.layerOptions.setValue(state.getLayers().getFirst());

        this.bindButtonToSelectedFigure(this.paintPane.duplicateButton, (f) -> {
            Drawable duplicatedFigure = f.getCopy();
            duplicatedFigure.move(DUPLICATE_OFFSET, DUPLICATE_OFFSET);
            this.getFeaturesMap().put(duplicatedFigure, this.getFeaturesMap().get(f).getCopy());
            this.state.addFigure(duplicatedFigure);
            this.setSelectedFigure(duplicatedFigure);
        });

        this.bindButtonToSelectedFigure(this.paintPane.divideButton, (f) -> {
            Drawable[] dividedFigures = f.split();
            for (Drawable newFigure : dividedFigures) {
                this.getFeaturesMap().put(newFigure, this.getFeaturesMap().get(f).getCopy());
                this.getState().addFigure(newFigure);
            }
            this.getFeaturesMap().remove(f);
            this.getState().deleteFigure(f);
            this.setSelectedFigure(dividedFigures[dividedFigures.length - 1]);
        });

        this.bindButtonToSelectedFigure(this.paintPane.moveToCenterButton, f -> f.moveTo(this.paintPane.CANVAS_WIDTH / 2.0, this.paintPane.CANVAS_HEIGHT / 2.0));

        this.bindButtonToRedraw(this.paintPane.showButton, () -> setCurrentLayerMode(true));
        this.bindButtonToRedraw(this.paintPane.hideButton, () -> setCurrentLayerMode(false));
        this.bindButtonToLayerAction(this.paintPane.addLayerButton, this.state::addLayer);
        this.bindButtonToLayerActionAndRedraw(this.paintPane.deleteLayerButton, this.state::deleteLayer);

        setCurrentLayerMode();
        assignDefaultValues();
    }
    public CanvasState<Drawable> getState() {
        return this.state;
    }
    public Map<Drawable, FigureFeatures> getFeaturesMap() {
        return this.figureFeaturesMap;
    }
    public Optional<Drawable> getSelectedFigure() {
        return Optional.ofNullable(this.selectedFigure);
    }
    public void setSelectedFigure(Drawable f) {
        this.getSelectedFigure().ifPresent(c -> {
            if (figureFeaturesMap.get(c) != null) {
                figureFeaturesMap.get(c).setSelected(false);
            }
        });
        this.selectedFigure = f;
        if (f != null) {
            figureFeaturesMap.get(f).setSelected(true);
        }
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

        figureButtonsToActions.stream().filter(e -> e.getKey().isSelected())
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
                    this.paintPane.redrawCanvas(state.figures(), figureFeaturesMap);
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
            Optional<Drawable> aux = this.getSelectedFigure();
            aux.ifPresent(drawable -> figureFeaturesMap.get(drawable).setSelected(false));
            this.setSelectedFigure(state.intersectingFigures(location).findFirst().orElse(null));
            this.showValues();
            this.paintPane.redrawCanvas(state.figures(), figureFeaturesMap);
        }
    }
    private void onMouseDragged(MouseEvent event) {
        if(this.selectionMode()) {
            Point eventPoint = this.pointFromEvent(event);
            double diffX = (eventPoint.getX() - startPoint.getX()) / 100;
            double diffY = (eventPoint.getY() - startPoint.getY()) / 100;
            getSelectedFigure().ifPresent(f -> { f.move(diffX, diffY); this.paintPane.redrawCanvas(state.figures(), figureFeaturesMap); });
        }
    }
    private void updateStatusLabel(Point location) {
        this.updateStatusLabel(location, location.toString());
    }
    private void updateStatusLabel(Point location, String defaultText) {
        state.intersectsAnyFigure(location).ifPresentOrElse(x -> statusPane.updateStatus(
                state.intersectingFigures(location)
                        .map(Drawable::toString)
                        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString()
        ), () -> statusPane.updateStatus(defaultText));
    }
    boolean selectionMode() {
        return this.paintPane.selectionButton.isSelected();
    }
    private void setCurrentLayerMode(){
        setCurrentLayerMode(this.state.getCurrentLayer().isVisible());
    }
    private void setCurrentLayerMode(boolean visible) {
        this.state.getCurrentLayer().setVisible(visible);
        this.paintPane.showButton.setSelected(visible);
        this.paintPane.hideButton.setSelected(!visible);
    }
    private <T extends Event> EventHandler<T> runAndRedrawIfSelectedFigurePresent(Consumer<Drawable> figureConsumer ) {
        return (event) -> getSelectedFigure().ifPresent(f -> {
            figureConsumer.accept(f);
            this.paintPane.redrawCanvas(state.figures(), figureFeaturesMap);
        });
    }
    private void bindButtonToLayerAction(ToggleButton button, Runnable action) {
        button.setOnAction(x -> {
            action.run();
            this.paintPane.layers.setAll(this.state.getLayers());
            this.paintPane.layerOptions.setValue(this.state.getCurrentLayer());
            setCurrentLayerMode();
            button.setSelected(false);
        });
    }
    private void bindButtonToLayerActionAndRedraw(ToggleButton button, Runnable action) {
        this.bindButtonToLayerAction(button, () -> { action.run(); this.paintPane.redrawCanvas(state.figures(), figureFeaturesMap); });
    }
    private void bindButtonToRedraw(ButtonBase button, Runnable action) {
        button.setOnAction((x) -> { action.run(); this.paintPane.redrawCanvas(state.figures(), figureFeaturesMap); });
    }
    private void bindButtonToSelectedFigure(ButtonBase button, Consumer<Drawable> action) {
        button.setOnAction(this.runAndRedrawIfSelectedFigurePresent(action));
    }
    private <T> void bindComboBoxToSelectedFigure(ComboBoxBase<T> box, BiConsumer<FigureFeatures, T> featureSetter) {
        box.setOnAction(this.runAndRedrawIfSelectedFigurePresent(f -> featureSetter.accept(this.figureFeaturesMap.get(f), box.getValue())));
    }
    private <T> void bindChoiceBoxToSelectedFigure(ChoiceBox<T> box, BiConsumer<FigureFeatures, T> featureSetter) {
        box.setOnAction(this.runAndRedrawIfSelectedFigurePresent(f -> featureSetter.accept(this.figureFeaturesMap.get(f), box.getValue())));
    }
    private void bindSliderToSelectedFigure(Slider slider, BiConsumer<FigureFeatures, Double> featureSetter) {
        slider.setOnMouseDragged(this.runAndRedrawIfSelectedFigurePresent(f -> featureSetter.accept(this.figureFeaturesMap.get(f), slider.getValue())));
    }
    void showValues(){
        getSelectedFigure().ifPresentOrElse(f -> {
            FigureFeatures features = figureFeaturesMap.get(f);
            assignValues(features.getShade(), features.getColor1(), features.getColor2(), features.getStrokeWidth(), features.getStroke());
        }, this::assignDefaultValues);
    }
    private void assignValues(Shade shade, Color color1, Color color2, double width, Stroke stroke) {
        this.paintPane.shadeOptions.setValue(shade);
        this.paintPane.fillColorPicker1.setValue(color1);
        this.paintPane.fillColorPicker2.setValue(color2);
        this.paintPane.strokeWidth.setValue(width);
        this.paintPane.strokeOptions.setValue(stroke);
        this.paintPane.showButton.setSelected(true);
    }
    private void assignDefaultValues(){
        assignValues(DEFAULT_SHADE, DEFAULT_FILL_COLOR_1, DEFAULT_FILL_COLOR_2, DEFAULT_STROKE_WIDTH, DEFAULT_STROKE_TYPE);
    }
}
