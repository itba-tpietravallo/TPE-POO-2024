package frontend;

import backend.CanvasState;
import backend.model.*;
import frontend.drawables.*;
import frontend.features.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class PaintPane extends BorderPane {
	//Canvas dimensions
	private static final int CANVAS_HEIGHT = 800, CANVAS_WIDTH = 600;

	//Tool width
	private static final int TOOL_MIN_WIDTH = 90;

	//VBox features
	private static final int VBOX_SPACING = 10, VBOX_PREF_WIDTH = 100, VBOX_LINE_WIDTH = 1;
	private static final String VBOX_BACKGROUND_COLOR = "-fx-background-color: #999";

	//Insets offsets value
	private static final int OFFSETS_VALUE = 5;

	// Stroke dimensions
	private static final int STROKE_MIN = 0, STROKE_MAX = 10;

	// BackEnd
	CanvasState<Drawable> canvasState;

	// Canvas y relacionados
	Canvas canvas = new Canvas(CANVAS_HEIGHT, CANVAS_WIDTH);
	GraphicsContext gc = canvas.getGraphicsContext2D();
	Color defaultFillColor1 = Color.CYAN;
	Color defaultFillColor2 = Color.MAGENTA;

	// Botones Barra Izquierda
	ToggleButton selectionButton = new ToggleButton("Seleccionar");
	ToggleButton rectangleButton = new ToggleButton("Rectángulo");
	ToggleButton circleButton = new ToggleButton("Círculo");
	ToggleButton squareButton = new ToggleButton("Cuadrado");
	ToggleButton ellipseButton = new ToggleButton("Elipse");
	ToggleButton deleteButton = new ToggleButton("Borrar");

	// Shadow
	Label shadowLabel = new Label("Sombra");
	ChoiceBox<Shade> shadowOptions = new ChoiceBox<>(FXCollections.observableArrayList(Shade.NOSHADE, Shade.SIMPLE, Shade.COLORED, Shade.SIMPLEINVERTED, Shade.COLOREDINVERTED));

	// Selector de color de relleno
	Label fillLabel = new Label("Relleno");
	ColorPicker fillColorPicker1 = new ColorPicker(defaultFillColor1);
	ColorPicker fillColorPicker2 = new ColorPicker(defaultFillColor2);

	// Border
	Label strokeLabel = new Label("Borde");
	Slider strokeWidth = new Slider();
	ChoiceBox<Stroke> strokeOptions = new ChoiceBox<>(FXCollections.observableArrayList(Stroke.NORMAL, Stroke.SIMPLE, Stroke.COMPLEX));

	Label actionLabel = new Label("Acciones");

	ToggleButton duplicateButton = new ToggleButton("Duplicar");

	ToggleButton divideButton = new ToggleButton("Dividir");

	ToggleButton moveToCenterButton = new ToggleButton("Mov. Centro");


	// Dibujar una figura
	Point startPoint;

	// Seleccionar una figura
	Drawable selectedFigure;

	// StatusBar
	StatusPane statusPane;

	// Colores de relleno de cada figura
	Map<Figure, FigureFeatures> figureFeaturesMap = new HashMap<>();

	public PaintPane(CanvasState<Drawable> canvasState, StatusPane statusPane) {
		this.canvasState = canvasState;
		this.statusPane = statusPane;
		ToggleButton[] toolsArr = {selectionButton, rectangleButton, circleButton, squareButton, ellipseButton, deleteButton};
		ToggleGroup tools = new ToggleGroup();
		for (ToggleButton tool : toolsArr) {
			tool.setMinWidth(TOOL_MIN_WIDTH);
			tool.setToggleGroup(tools);
			tool.setCursor(Cursor.HAND);
		}

		VBox buttonsBox = new VBox(VBOX_SPACING);
		buttonsBox.getChildren().addAll(toolsArr);

		buttonsBox.getChildren().add(shadowLabel);
		shadowOptions.setMinWidth(TOOL_MIN_WIDTH);
		shadowOptions.setValue(Shade.NOSHADE);
		buttonsBox.getChildren().add(shadowOptions);

		buttonsBox.getChildren().add(fillLabel);
		buttonsBox.getChildren().add(fillColorPicker1);
		buttonsBox.getChildren().add(fillColorPicker2);

		buttonsBox.getChildren().add(strokeLabel);
		strokeWidth.setMin(STROKE_MIN);
		strokeWidth.setMax(STROKE_MAX);
		strokeWidth.setShowTickLabels(true);
		buttonsBox.getChildren().add(strokeWidth);
		strokeOptions.setMinWidth(TOOL_MIN_WIDTH);
		strokeOptions.setValue(Stroke.NORMAL);
		buttonsBox.getChildren().add(strokeOptions);

		buttonsBox.getChildren().add(actionLabel);
		ToggleButton[] actionsArr = {duplicateButton, divideButton, moveToCenterButton};
		ToggleGroup actions = new ToggleGroup();
		for (ToggleButton actionButton : actionsArr) {
			actionButton.setMinWidth(TOOL_MIN_WIDTH);
			actionButton.setToggleGroup(actions);
			actionButton.setCursor(Cursor.HAND);
		}
		buttonsBox.getChildren().addAll(actionsArr);

		buttonsBox.setPadding(new Insets(OFFSETS_VALUE));
		buttonsBox.setStyle(VBOX_BACKGROUND_COLOR);
		buttonsBox.setPrefWidth(VBOX_PREF_WIDTH);
		gc.setLineWidth(VBOX_LINE_WIDTH);

		canvas.setOnMousePressed(event -> {
			startPoint = new Point(event.getX(), event.getY());
		});

		canvas.setOnMouseReleased(event -> {
			Point endPoint = new Point(event.getX(), event.getY());
			if(startPoint == null) {
				return ;
			}
			if(endPoint.getX() < startPoint.getX() || endPoint.getY() < startPoint.getY()) {
				return ;
			}
			Drawable newFigure = null;
			if(rectangleButton.isSelected()) {
				newFigure = new DrawableRectangle(startPoint, endPoint);
			}
			else if(circleButton.isSelected()) {
				double circleRadius = Math.abs(endPoint.getX() - startPoint.getX());
				newFigure = new DrawableCircle(startPoint, circleRadius);
			} else if(squareButton.isSelected()) {
				double size = Math.abs(endPoint.getX() - startPoint.getX());
				newFigure = new DrawableSquare(startPoint, size);
			} else if(ellipseButton.isSelected()) {
				Point centerPoint = new Point(Math.abs(endPoint.getX() + startPoint.getX()) / 2, (Math.abs((endPoint.getY() + startPoint.getY())) / 2));
				double sMayorAxis = Math.abs(endPoint.getX() - startPoint.getX());
				double sMinorAxis = Math.abs(endPoint.getY() - startPoint.getY());
				newFigure = new DrawableEllipse(centerPoint, sMayorAxis, sMinorAxis);
			} else {
				return ;
			}

			FigureFeatures features = new FigureFeatures(
					fillColorPicker1.getValue(),
					fillColorPicker2.getValue(),
					shadowOptions.getValue(),
					strokeWidth.getValue(),
					strokeOptions.getValue()
			);

			figureFeaturesMap.put(newFigure, features);
			canvasState.addFigure(newFigure);
			startPoint = null;
			redrawCanvas();
		});

		canvas.setOnMouseMoved(event -> {
			Point eventPoint = new Point(event.getX(), event.getY());
			boolean found = false;
			StringBuilder label = new StringBuilder();
			for(Drawable figure : canvasState.figures()) {
				if(figure.pointBelongs(eventPoint)) {
					found = true;
					label.append(figure.toString());
				}
			}
			if(found) {
				statusPane.updateStatus(label.toString());
			} else {
				statusPane.updateStatus(eventPoint.toString());
			}
		});

		canvas.setOnMouseClicked(event -> {
			if(selectionButton.isSelected()) {
				Point eventPoint = new Point(event.getX(), event.getY());
				boolean found = false;
				StringBuilder label = new StringBuilder("Se seleccionó: ");
				for (Drawable figure : canvasState.figures()) {
					if(figure.pointBelongs(eventPoint)) {
						found = true;
						selectedFigure = figure;
						label.append(figure.toString());
					}
				}
				if (found) {
					statusPane.updateStatus(label.toString());
				} else {
					selectedFigure = null;
					statusPane.updateStatus("Ninguna figura encontrada");
				}
				redrawCanvas();
			}
		});

		canvas.setOnMouseDragged(event -> {
			if(selectionButton.isSelected()) {
				Point eventPoint = new Point(event.getX(), event.getY());
				double diffX = (eventPoint.getX() - startPoint.getX()) / 100;
				double diffY = (eventPoint.getY() - startPoint.getY()) / 100;
				if (selectedFigure != null) {
					selectedFigure.move(diffX, diffY);
					redrawCanvas();
				}
			}
		});

		deleteButton.setOnAction(event -> {
			if (selectedFigure != null) {
				canvasState.deleteFigure(selectedFigure);
				selectedFigure = null;
				redrawCanvas();
			}
		});

		fillColorPicker1.setOnAction(event ->{
			if (selectedFigure != null) {
				figureFeaturesMap.get(selectedFigure).setColor1(fillColorPicker1.getValue());
			}
			redrawCanvas();
		});
		// todo se podrá hacer una sola función y llamarla dos veces ?????
		fillColorPicker2.setOnAction(event ->{
			if (selectedFigure != null) {
				figureFeaturesMap.get(selectedFigure).setColor2(fillColorPicker2.getValue());
			}
			redrawCanvas();
		});
		shadowOptions.setOnAction(event -> {
			if (selectedFigure != null) {
				figureFeaturesMap.get(selectedFigure).setShade(shadowOptions.getValue());
			}
			redrawCanvas();
		});
		strokeOptions.setOnAction(event -> {
			if (selectedFigure != null) {
				figureFeaturesMap.get(selectedFigure).setStroke(strokeOptions.getValue());
			}
			redrawCanvas();
		});
		strokeWidth.setOnMouseDragged(event -> {
			if (selectedFigure != null) {
				figureFeaturesMap.get(selectedFigure).setStrokeWidth(strokeWidth.getValue());
			}
			redrawCanvas();
		});





		setLeft(buttonsBox);
		setRight(canvas);
	}

	void redrawCanvas() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for(Drawable figure : canvasState.figures()) {
			// Get all the figures features
			FigureFeatures features = figureFeaturesMap.get(figure);

			// Draw the corresponding shade type
			features.getShade().drawShade(gc, figure, features.getColor1() );

			// Set the gradient fill
			gc.setFill(figure.getFill(features.getColor1(), features.getColor2()));

			// Set stroke
			features.getStroke().setStroke(gc, features.getStrokeWidth(), figure == selectedFigure);

			// Draw the figure
			figure.draw(gc);
		}
	}

}
