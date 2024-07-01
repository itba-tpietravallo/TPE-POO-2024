package frontend;

import backend.CanvasState;
import backend.model.*;
import frontend.drawables.*;
import frontend.features.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class PaintPane extends BorderPane {
	//Canvas dimensions
	private static final int CANVAS_WIDTH = 800, CANVAS_HEIGHT = 600;

	//Tool width
	private static final int TOOL_MIN_WIDTH = 90;

	//VBox features
	private static final int VBOX_SPACING = 10, VBOX_PREF_WIDTH = 100, VBOX_LINE_WIDTH = 1;
	private static final String VBOX_BACKGROUND_COLOR = "-fx-background-color: #999";

	//Insets offsets value
	private static final int OFFSETS_VALUE = 5;

	// Stroke dimensions
	private static final int STROKE_MIN = 0, STROKE_MAX = 10;

	// Duplicate offset
	private static final int DUPLICATE_OFFSET = 12;

	// BackEnd
	CanvasState<Drawable> canvasState;

	// Canvas y relacionados
	Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
	GraphicsContext gc = canvas.getGraphicsContext2D();
	Color defaultFillColor1 = Color.CYAN;
	Color defaultFillColor2 = Color.web("ccffcc");

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

	// Actions
	Label actionLabel = new Label("Acciones");
	ToggleButton duplicateButton = new ToggleButton("Duplicar");
	ToggleButton divideButton = new ToggleButton("Dividir");
	ToggleButton moveToCenterButton = new ToggleButton("Mov. Centro");


	// Dibujar una figura
	Point startPoint;

	// Seleccionar una figura
	Optional<Drawable> selectedFigure = Optional.empty();

	// StatusBar
	StatusPane statusPane;

	// Colores de relleno de cada figura
	Map<Figure, FigureFeatures> figureFeaturesMap = new HashMap<>();
	Map<ToggleButton, BiFunction<Point, Point, Drawable>> figureButtons = Map.ofEntries(
			Map.entry(rectangleButton,	DrawableRectangle::createFromPoints),
			Map.entry(circleButton,		DrawableCircle::createFromPoints),
			Map.entry(squareButton, 	DrawableSquare::createFromPoints),
			Map.entry(ellipseButton, 	DrawableEllipse::createFromPoints)
	);

	public PaintPane(CanvasState<Drawable> canvasState, StatusPane statusPane) {
		this.canvasState = canvasState;
		this.statusPane = statusPane;

		List<ToggleButton> toolsArr = new ArrayList<>();
		toolsArr.add(selectionButton);
		toolsArr.addAll(figureButtons.keySet());
		toolsArr.add(deleteButton);

		List<ToggleButton> actionsArr = List.of(duplicateButton, divideButton, moveToCenterButton);

		Map<ChoiceBox<?>, ?> choiceBoxes = Map.ofEntries(
				Map.entry(shadowOptions, Shade.NOSHADE),
				Map.entry(strokeOptions, Stroke.NORMAL)
		);

		// @todo Look into type-safe heterogeneous containers
		shadowOptions.setValue(Shade.NOSHADE);
		strokeOptions.setValue(Stroke.NORMAL);

		ToggleGroup tools = new ToggleGroup();
		ToggleGroup actions = new ToggleGroup();

		Collection<Node> sideButtons = new ArrayList<>(toolsArr);
		sideButtons.addAll(List.of(shadowLabel, shadowOptions, fillLabel, fillColorPicker1, fillColorPicker2, strokeLabel, strokeWidth, strokeOptions, actionLabel));
		sideButtons.addAll(actionsArr);

		toolsArr.forEach(tool -> { tool.setToggleGroup(tools); tool.setMinWidth(TOOL_MIN_WIDTH); tool.setCursor(Cursor.HAND); });
		actionsArr.forEach(action -> { action.setToggleGroup(actions); action.setMinWidth(TOOL_MIN_WIDTH); action.setCursor(Cursor.HAND); });

		for (Map.Entry<ChoiceBox<?>, ?> e : choiceBoxes.entrySet()) {
			e.getKey().setMinWidth(TOOL_MIN_WIDTH);
		}

		VBox buttonsBox = new VBox(VBOX_SPACING);
		buttonsBox.getChildren().addAll(sideButtons);

		strokeWidth.setMin(STROKE_MIN);
		strokeWidth.setMax(STROKE_MAX);
		strokeWidth.setShowTickLabels(true);

		buttonsBox.setPadding(new Insets(OFFSETS_VALUE));
		buttonsBox.setStyle(VBOX_BACKGROUND_COLOR);
		buttonsBox.setPrefWidth(VBOX_PREF_WIDTH);
		gc.setLineWidth(VBOX_LINE_WIDTH);

		canvas.setOnMousePressed(this::onMousePressed);
		canvas.setOnMouseReleased(this::onMouseReleased);
		canvas.setOnMouseMoved(this::onMouseMoved);
		canvas.setOnMouseClicked(this::onMouseClicked);
		canvas.setOnMouseDragged(this::onMouseDragged);

		this.bindComboBox(fillColorPicker1, FigureFeatures::setColor1);
		this.bindComboBox(fillColorPicker2, FigureFeatures::setColor2);
		this.bindChoiceBox(shadowOptions, FigureFeatures::setShade);
		this.bindChoiceBox(strokeOptions, FigureFeatures::setStroke);
		this.bindSlider(strokeWidth, FigureFeatures::setStrokeWidth);

		this.bindButton(deleteButton, f -> {
			canvasState.deleteFigure(f);
			selectedFigure = Optional.empty();
		});

		this.bindButton(duplicateButton, f -> {
			Drawable duplicatedFigure = f.getCopy();
			duplicatedFigure.move(DUPLICATE_OFFSET, DUPLICATE_OFFSET);
			figureFeaturesMap.put(duplicatedFigure, figureFeaturesMap.get(f).getCopy());
			canvasState.addFigure(duplicatedFigure);
		});;

		this.bindButton(divideButton, f ->{
			Drawable[] dividedFigures = f.split();
			for (Drawable newFigure : dividedFigures) {
				figureFeaturesMap.put(newFigure, figureFeaturesMap.get(f).getCopy());
				canvasState.addFigure(newFigure);
			};
			figureFeaturesMap.remove(f);
			canvasState.deleteFigure(f);
		});

		this.bindButton(moveToCenterButton, f -> {
			f.moveTo(CANVAS_WIDTH / 2.0, CANVAS_HEIGHT / 2.0);
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
			features.getStroke().setStroke(gc, features.getStrokeWidth(), selectedFigure.isPresent() && selectedFigure.get().equals(figure) );

			// Draw the figure
			figure.draw(gc);
		}
	}

	private void onMousePressed(MouseEvent event) {
		startPoint = new Point(event.getX(), event.getY());
	}
	private void onMouseReleased(MouseEvent event) {
		Point endPoint = new Point(event.getX(), event.getY());

		if(startPoint == null) {
			return ;
		}

		if(endPoint.getX() < startPoint.getX() || endPoint.getY() < startPoint.getY()) {
			return ;
		}

		Drawable newFigure = null;

		for (Map.Entry<ToggleButton, BiFunction<Point, Point, Drawable>> e : figureButtons.entrySet()) {
			if (e.getKey().isSelected()) {
				newFigure = e.getValue().apply(startPoint, endPoint);
				break;
			}
		}

		if (newFigure == null) return;

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
	}
	private void onMouseMoved(MouseEvent event) {
		checkIntersectingFigures(event);
	}
	private void onMouseClicked(MouseEvent event) {
		if(this.selectionMode()) {
			selectedFigure = checkIntersectingFigures(event);
			redrawCanvas();
		}
	}
	private void onMouseDragged(MouseEvent event) {
		if(this.selectionMode()) {
			Point eventPoint = new Point(event.getX(), event.getY());
			double diffX = (eventPoint.getX() - startPoint.getX()) / 100;
			double diffY = (eventPoint.getY() - startPoint.getY()) / 100;
			selectedFigure.ifPresent(f -> { f.move(diffX, diffY); redrawCanvas(); });
		}
	}

	private boolean selectionMode() {
		return this.selectionButton.isSelected();
	}

	private Optional<Drawable> checkIntersectingFigures(MouseEvent event){
		Point location = new Point(event.getX(), event.getY());
		Optional<Drawable> intersectingFigure = Optional.empty();
		StringBuilder label = new StringBuilder("Se seleccionó: ");
		boolean found = false;

		for (Drawable figure : canvasState.figures()) {
			if(figure.pointBelongs(location)) {
				found = true;
				intersectingFigure = Optional.of(figure);
				label.append(figure.toString());
			}
		}

		if(found) {
			statusPane.updateStatus(label.toString());
		} else {
			statusPane.updateStatus(location.toString());
		}

		return intersectingFigure;
	}

	private <T extends Event> EventHandler<T> runAndRedrawIfSelectedFigurePresent(Consumer<Drawable> figureConsumer ) {
		return (event) -> selectedFigure.ifPresent(f -> {
			figureConsumer.accept(f);
			redrawCanvas();
		});
	}

	private <T> void bindButton(ButtonBase button, Consumer<Drawable> action) {
		button.setOnAction(this.runAndRedrawIfSelectedFigurePresent(action));
	}
	private <T> void bindComboBox(ComboBoxBase<T> box, BiConsumer<FigureFeatures, T> featureSetter) {
		box.setOnAction(this.runAndRedrawIfSelectedFigurePresent(f -> {
			featureSetter.accept(this.figureFeaturesMap.get(f), box.getValue());
		}));
	}

	private <T> void bindChoiceBox(ChoiceBox<T> box, BiConsumer<FigureFeatures, T> featureSetter) {
		box.setOnAction(this.runAndRedrawIfSelectedFigurePresent(f -> {
			featureSetter.accept(this.figureFeaturesMap.get(f), box.getValue());
		}));
	}

	private void bindSlider(Slider slider, BiConsumer<FigureFeatures, Double> featureSetter) {
		slider.setOnMouseDragged(this.runAndRedrawIfSelectedFigurePresent(f -> {
			featureSetter.accept(this.figureFeaturesMap.get(f), slider.getValue());
		}));
	}
}
