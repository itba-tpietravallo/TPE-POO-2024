package frontend;

import backend.CanvasState;
import backend.model.*;
import frontend.drawables.*;
import frontend.features.*;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
	private static final int STROKE_MIN = 0, STROKE_MAX = 10, DEFAULT_STROKE_WIDTH = 5;

	// Duplicate offset
	private static final int DUPLICATE_OFFSET = 12;

	// Default fill colors
	private static final Color DEFAULT_FILL_COLOR_1 = Color.CYAN;
	private static final Color DEFAULT_FILL_COLOR_2 = Color.web("ccffcc");

	// Default shade
	private static final Shade DEFAULT_SHADE = Shade.NOSHADE;

	// Default stroke
	private static final Stroke DEFAULT_STROKE_TYPE = Stroke.NORMAL;

	// BackEnd
	CanvasState<Drawable> canvasState;

	// Canvas y relacionados
	Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
	GraphicsContext gc = canvas.getGraphicsContext2D();

	// Botones Barra Izquierda
	ToggleButton selectionButton = new ToggleButton("Seleccionar");
	ToggleButton rectangleButton = new ToggleButton("Rectángulo");
	ToggleButton circleButton = new ToggleButton("Círculo");
	ToggleButton squareButton = new ToggleButton("Cuadrado");
	ToggleButton ellipseButton = new ToggleButton("Elipse");
	ToggleButton deleteButton = new ToggleButton("Borrar");

	// Shade
	Label shadeLabel = new Label("Sombra");
	ChoiceBox<Shade> shadeOptions = new ChoiceBox<>(FXCollections.observableArrayList(Shade.NOSHADE, Shade.SIMPLE, Shade.COLORED, Shade.SIMPLEINVERTED, Shade.COLOREDINVERTED));

	// Selector de color de relleno
	Label fillLabel = new Label("Relleno");
	ColorPicker fillColorPicker1 = new ColorPicker();
	ColorPicker fillColorPicker2 = new ColorPicker();

	// Border
	Label strokeLabel = new Label("Borde");
	Slider strokeWidth = new Slider();
	ChoiceBox<Stroke> strokeOptions = new ChoiceBox<>(FXCollections.observableArrayList(Stroke.NORMAL, Stroke.SIMPLE, Stroke.COMPLEX));

	// Actions
	Label actionLabel = new Label("Acciones");
	ToggleButton duplicateButton = new ToggleButton("Duplicar");
	ToggleButton divideButton = new ToggleButton("Dividir");
	ToggleButton moveToCenterButton = new ToggleButton("Mov. Centro");

	// Layers
	Label layerLabel = new Label("Capas");
	// todo Cambiar a <Layer>
	ChoiceBox<String> layerOptions = new ChoiceBox<>(FXCollections.observableArrayList("Capa 1", "Capa 2", "Capa 3"));
	RadioButton showButton = new RadioButton("Mostrar");
	RadioButton hideButton = new RadioButton("Ocultar");
	ToggleButton addLayerButton = new ToggleButton("Agregar Capa");
	ToggleButton deleteLayerButton = new ToggleButton("Eliminar Capa");

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
				Map.entry(shadeOptions, Shade.NOSHADE),
				Map.entry(strokeOptions, Stroke.NORMAL)
		);

		// @todo Look into type-safe heterogeneous containers
		assignDefaultValues();

		ToggleGroup tools = new ToggleGroup();
		ToggleGroup actions = new ToggleGroup();

		Collection<Node> sideButtons = new ArrayList<>(toolsArr);
		sideButtons.addAll(List.of(shadeLabel, shadeOptions, fillLabel, fillColorPicker1, fillColorPicker2, strokeLabel, strokeWidth, strokeOptions, actionLabel));
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

		Collection<Node> topButtons = new ArrayList<>(List.of(layerLabel, showButton, hideButton, addLayerButton, deleteLayerButton));

		HBox topBox = new HBox(VBOX_SPACING);
		topBox.getChildren().addAll(topButtons);
		topBox.setPadding(new Insets(OFFSETS_VALUE));
		topBox.setStyle(VBOX_BACKGROUND_COLOR);
		topBox.setAlignment(Pos.CENTER);

		canvas.setOnMousePressed(this::onMousePressed);
		canvas.setOnMouseReleased(this::onMouseReleased);
		canvas.setOnMouseMoved(this::onMouseMoved);
		canvas.setOnMouseClicked(this::onMouseClicked);
		canvas.setOnMouseDragged(this::onMouseDragged);

		this.bindComboBox(fillColorPicker1, FigureFeatures::setColor1);
		this.bindComboBox(fillColorPicker2, FigureFeatures::setColor2);
		this.bindChoiceBox(shadeOptions, FigureFeatures::setShade);
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

		setTop(topBox);
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
		startPoint = this.pointFromEvent(event);
	}
	private void onMouseReleased(MouseEvent event) {
		Point endPoint = this.pointFromEvent(event);

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
				shadeOptions.getValue(),
				strokeWidth.getValue(),
				strokeOptions.getValue()
		);

		figureFeaturesMap.put(newFigure, features);
		canvasState.addFigure(newFigure);
		startPoint = null;
		redrawCanvas();
	}

	private Point pointFromEvent(MouseEvent event) {
		return new Point(event.getX(), event.getY());
	}
	private void onMouseMoved(MouseEvent event) {
		updateStatusLabel(pointFromEvent(event));
	}
	private void onMouseClicked(MouseEvent event) {
		Point location = this.pointFromEvent(event);
		this.updateStatusLabel(location, "Ninguna figura encontrada");
		if(this.selectionMode()) {
			selectedFigure = canvasState.intersectingFigures(location).findFirst();
			showValues(selectedFigure);
			redrawCanvas();
		}
	}
	private void onMouseDragged(MouseEvent event) {
		if(this.selectionMode()) {
			Point eventPoint = this.pointFromEvent(event);
			double diffX = (eventPoint.getX() - startPoint.getX()) / 100;
			double diffY = (eventPoint.getY() - startPoint.getY()) / 100;
			selectedFigure.ifPresent(f -> { f.move(diffX, diffY); redrawCanvas(); });
		}
	}

	private boolean selectionMode() {
		return this.selectionButton.isSelected();
	}
	private void updateStatusLabel(Point location) {
		this.updateStatusLabel(location, location.toString());
	}
	private void updateStatusLabel(Point location, String defaultText) {
		canvasState.intersectsAnyFigure(location).ifPresentOrElse(x -> {
			statusPane.updateStatus(
					canvasState.intersectingFigures(location)
							.map(Drawable::toString)
							.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString()
			);
		}, () -> { statusPane.updateStatus(defaultText); });
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

	private void showValues(Optional<Drawable> selectedFigure){
		selectedFigure.ifPresentOrElse(f -> {
			FigureFeatures features = figureFeaturesMap.get(f);
			assignValues(features.getShade(), features.getColor1(), features.getColor2(), features.getStrokeWidth(), features.getStroke());
		}, this::assignDefaultValues);
	}

	private void assignValues(Shade shade, Color color1, Color color2, double width, Stroke stroke){
		shadeOptions.setValue(shade);
		fillColorPicker1.setValue(color1);
		fillColorPicker2.setValue(color2);
		strokeWidth.setValue(width);
		strokeOptions.setValue(stroke);
	}

	private void assignDefaultValues(){
		assignValues(DEFAULT_SHADE, DEFAULT_FILL_COLOR_1, DEFAULT_FILL_COLOR_2, DEFAULT_STROKE_WIDTH, DEFAULT_STROKE_TYPE);
	}
}
