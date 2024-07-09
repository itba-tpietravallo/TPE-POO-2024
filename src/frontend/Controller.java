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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Controller {
    // Default fill colors
    private static final Color DEFAULT_FILL_COLOR_1 = Color.CYAN;
    private static final Color DEFAULT_FILL_COLOR_2 = Color.PURPLE;

    // Default shade
    private static final Shade DEFAULT_SHADE = Shade.NOSHADE;

    // Default stroke
    private static final Stroke DEFAULT_STROKE_TYPE = Stroke.NORMAL;
    private static final int DEFAULT_STROKE_WIDTH = 5;

    // Duplicate offset
    private static final int DUPLICATE_OFFSET = 12;

    // No figure message
    private static final String FIGURE_NOT_FOUND_MESSAGE = "Ninguna figura encontrada";

    final CanvasState<Drawable> state;
    final StatusPane statusPane;
    final PaintPane paintPane;

    // Currently selected figure (may be null)
    Drawable selectedFigure = null;
    Point startPoint;
    final List<Map.Entry<ToggleButton, BiFunction<Point,Point,Drawable>>> figureButtonsToActions;

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
            this.getState().deleteFigure(f, f.getFeatures().getLayerIdx());
            this.selectedFigure = null;
        });

        this.paintPane.layerOptions.setOnAction( event -> {
            this.getState().setCurrentLayer(this.paintPane.layerOptions.getValue());
            this.setCurrentLayerMode();
        });

        this.paintPane.layers.addAll(this.getState().getLayers());
        this.paintPane.layerOptions.setValue(this.getState().getLayers().getFirst());

        this.bindButtonToSelectedFigure(this.paintPane.duplicateButton, (f) -> {
            Drawable duplicatedFigure = f.getCopy();
            duplicatedFigure.move(DUPLICATE_OFFSET, DUPLICATE_OFFSET);
            duplicatedFigure.setFeatures(f.getFeatures().getCopy());
            duplicatedFigure.getFeatures().setLayerIdx(this.getState().getCurrentLayer().getLayerId());
            this.getState().addFigure(duplicatedFigure);
            this.setSelectedFigure(duplicatedFigure);
        });

        this.bindButtonToSelectedFigure(this.paintPane.divideButton, (f) -> {
            Drawable[] dividedFigures = f.split();
            for (Drawable newFigure : dividedFigures) {
                newFigure.setFeatures(f.getFeatures().getCopy());
                newFigure.getFeatures().setLayerIdx(this.getState().getCurrentLayer().getLayerId());
                this.getState().addFigure(newFigure);
            }
            this.getState().deleteFigure(f, f.getFeatures().getLayerIdx());
            this.setSelectedFigure(dividedFigures[dividedFigures.length - 1]);
        });

        this.bindButtonToSelectedFigure(this.paintPane.moveToCenterButton, f -> f.moveTo(this.paintPane.CANVAS_WIDTH / 2.0, this.paintPane.CANVAS_HEIGHT / 2.0));

        this.bindButtonToRedraw(this.paintPane.showButton, () -> setCurrentLayerMode(true));
        this.bindButtonToRedraw(this.paintPane.hideButton, () -> {
            this.paintPane.selectionButton.setSelected(false);
            this.deselectFigure();
            this.assignDefaultValues();
            setCurrentLayerMode(false);
            this.paintPane.redrawCanvas(state.figures());
        });
        this.bindButtonToLayerAction(this.paintPane.addLayerButton, this.state::addLayer);
        this.bindButtonToLayerActionAndRedraw(this.paintPane.deleteLayerButton, this.state::deleteLayer);
        this.bindFigureButtonstoDeselection();

        this.setCurrentLayerMode();
        this.assignDefaultValues();
    }
    public CanvasState<Drawable> getState() {
        return this.state;
    }
    public Optional<Drawable> getSelectedFigure() {
        return Optional.ofNullable(this.selectedFigure);
    }
    public void setSelectedFigure(Drawable f) {
        this.getSelectedFigure().ifPresent(c -> c.getFeatures().setSelected(false));
        this.selectedFigure = f;
        if (f != null) {
            f.getFeatures().setSelected(true);
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
                            this.paintPane.strokeOptions.getValue(),
                            this.getState().getCurrentLayer().getLayerId()
                    );
                    f.setFeatures(features);
                    this.state.addFigure(f);
                    this.startPoint = null;
                    this.paintPane.redrawCanvas(state.figures());
                });
    }
    private void onMousePressed(MouseEvent event) {
        this.startPoint = this.pointFromEvent(event);
    }
    private void onMouseMoved(MouseEvent event) {
        this.updateStatusLabel(pointFromEvent(event));
    }
    private void onMouseClicked(MouseEvent event) {
        Point location = this.pointFromEvent(event);
        this.updateStatusLabel(location, FIGURE_NOT_FOUND_MESSAGE);
        if(this.selectionMode()) {
            Optional<Drawable> aux = this.getSelectedFigure();
            aux.ifPresent(drawable -> drawable.getFeatures().setSelected(false));
            this.setSelectedFigure(state.intersectingFigures(location).findFirst().orElse(null));
            this.showValues();
            this.paintPane.redrawCanvas(state.figures());
        }
    }
    private void onMouseDragged(MouseEvent event) {
        if(this.selectionMode()) {
            Point eventPoint = this.pointFromEvent(event);
            double diffX = (eventPoint.getX() - startPoint.getX()) / 100;
            double diffY = (eventPoint.getY() - startPoint.getY()) / 100;
            this.getSelectedFigure().ifPresent(f -> { f.move(diffX, diffY); this.paintPane.redrawCanvas(state.figures()); });
        }
    }
    private void updateStatusLabel(Point location) {
        this.updateStatusLabel(location, location.toString());
    }
    private void updateStatusLabel(Point location, String defaultText) {
        this.state.intersectsAnyFigure(location).ifPresentOrElse(x -> statusPane.updateStatus(
                state.intersectingFigures(location)
                        .map(Drawable::toString)
                        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString()
        ), () -> statusPane.updateStatus(defaultText));
    }
    boolean selectionMode() {
        return this.paintPane.selectionButton.isSelected();
    }
    private void setCurrentLayerMode(){
        this.setCurrentLayerMode(this.state.getCurrentLayer().isVisible());
    }
    private void setCurrentLayerMode(boolean visible) {
        this.state.getCurrentLayer().setVisible(visible);
        this.paintPane.showButton.setSelected(visible);
        this.paintPane.hideButton.setSelected(!visible);
    }
    private <T extends Event> EventHandler<T> runAndRedrawIfSelectedFigurePresent(Consumer<Drawable> figureConsumer ) {
        return (event) -> this.getSelectedFigure().ifPresent(f -> {
            figureConsumer.accept(f);
            this.paintPane.redrawCanvas(state.figures());
        });
    }
    private void bindButtonToLayerAction(ToggleButton button, Runnable action) {
        button.setOnAction(x -> {
            action.run();
            this.paintPane.layers.setAll(this.state.getLayers());
            this.paintPane.layerOptions.setValue(this.state.getCurrentLayer());
            this.setCurrentLayerMode();
            button.setSelected(false);
        });
    }
    private void bindButtonToLayerActionAndRedraw(ToggleButton button, Runnable action) {
        this.bindButtonToLayerAction(button, () -> { action.run(); this.paintPane.redrawCanvas(state.figures()); });
    }
    private void bindButtonToRedraw(ButtonBase button, Runnable action) {
        button.setOnAction((x) -> { action.run(); this.paintPane.redrawCanvas(state.figures()); });
    }
    private void bindButtonToSelectedFigure(ToggleButton button, Consumer<Drawable> action) {
        button.setOnAction(evt -> {
            this.runAndRedrawIfSelectedFigurePresent(action).handle(evt);
            button.setSelected(false);
        });
    }
    private <T> void bindComboBoxToSelectedFigure(ComboBoxBase<T> box, BiConsumer<FigureFeatures, T> featureSetter) {
        box.setOnAction(this.runAndRedrawIfSelectedFigurePresent(f -> featureSetter.accept(f.getFeatures(), box.getValue())));
    }
    private <T> void bindChoiceBoxToSelectedFigure(ChoiceBox<T> box, BiConsumer<FigureFeatures, T> featureSetter) {
        box.setOnAction(this.runAndRedrawIfSelectedFigurePresent(f -> featureSetter.accept(f.getFeatures(), box.getValue())));
    }
    private void bindSliderToSelectedFigure(Slider slider, BiConsumer<FigureFeatures, Double> featureSetter) {
        slider.setOnMouseDragged(this.runAndRedrawIfSelectedFigurePresent(f -> featureSetter.accept(f.getFeatures(), slider.getValue())));
    }
    private void bindFigureButtonstoDeselection() {
        for (ToggleButton button : this.paintPane.figureButtons) {
            button.setOnAction( event -> {
                this.deselectFigure();
                this.paintPane.redrawCanvas(state.figures());
            });
        }
    }
    void showValues(){
        this.getSelectedFigure().ifPresentOrElse(f -> {
            FigureFeatures features = f.getFeatures();
            this.assignValues(features.getShade(), features.getColor1(), features.getColor2(), features.getStrokeWidth(), features.getStroke());
        }, this::assignDefaultValues);
    }
    private void assignValues(Shade shade, Color color1, Color color2, double width, Stroke stroke) {
        this.paintPane.shadeOptions.setValue(shade);
        this.paintPane.fillColorPicker1.setValue(color1);
        this.paintPane.fillColorPicker2.setValue(color2);
        this.paintPane.strokeWidth.setValue(width);
        this.paintPane.strokeOptions.setValue(stroke);
        this.setCurrentLayerMode();
    }
    private void assignDefaultValues(){
        this.assignValues(DEFAULT_SHADE, DEFAULT_FILL_COLOR_1, DEFAULT_FILL_COLOR_2, DEFAULT_STROKE_WIDTH, DEFAULT_STROKE_TYPE);
    }

    private void deselectFigure() {
        this.getSelectedFigure().ifPresent(drawable -> drawable.getFeatures().setSelected(false));
        this.selectedFigure = null;
    }
}
