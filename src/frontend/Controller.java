package frontend;

import backend.CanvasState;
import backend.model.Point;
import frontend.drawables.Drawable;
import frontend.features.FigureFeatures;
import javafx.scene.input.MouseEvent;

import java.util.Optional;

public class Controller {
    CanvasState<Drawable> state;
    StatusPane statusPane;
    PaintPane paintPane;
    Optional<Drawable> selectedFigure;
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
        return this.selectedFigure;
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

                    this.paintPane.figureFeaturesMap.put(f, features);
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
        if(this.paintPane.selectionMode()) {
            this.paintPane.selectedFigure = state.intersectingFigures(location).findFirst();
            this.paintPane.showValues(selectedFigure);
            this.paintPane.redrawCanvas();
        }
    }
    private void onMouseDragged(MouseEvent event) {
        if(this.paintPane.selectionMode()) {
            Point eventPoint = this.pointFromEvent(event);
            double diffX = (eventPoint.getX() - startPoint.getX()) / 100;
            double diffY = (eventPoint.getY() - startPoint.getY()) / 100;
            selectedFigure.ifPresent(f -> { f.move(diffX, diffY); this.paintPane.redrawCanvas(); });
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
}
