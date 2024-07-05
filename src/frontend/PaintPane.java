package frontend;

import backend.model.*;
import frontend.drawables.*;
import frontend.features.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class PaintPane extends BorderPane {
	//Canvas dimensions
	final int CANVAS_WIDTH = 800, CANVAS_HEIGHT = 600;

	// Tool width
	private static final int TOOL_MIN_WIDTH = 90;

	// VBox and Hbox features
	private static final int VBOX_SPACING = 10, VBOX_PREF_WIDTH = 100, VBOX_LINE_WIDTH = 1;
	private static final String BOX_BACKGROUND_COLOR = "-fx-background-color: #999";

	// Insets offsets value
	private static final int OFFSETS_VALUE = 5;

	// Stroke dimensions
	private static final int STROKE_MIN = 0, STROKE_MAX = 10;

	// Canvas
	Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
	GraphicsContext gc = canvas.getGraphicsContext2D();

	// Figure buttons and others
	ToggleButton selectionButton = new ToggleButton("Seleccionar");
	ToggleButton rectangleButton = new ToggleButton("Rectángulo");
	ToggleButton circleButton = new ToggleButton("Círculo");
	ToggleButton squareButton = new ToggleButton("Cuadrado");
	ToggleButton ellipseButton = new ToggleButton("Elipse");
	ToggleButton deleteButton = new ToggleButton("Borrar");

	// Shade
	Label shadeLabel = new Label("Sombra");
	ChoiceBox<Shade> shadeOptions = new ChoiceBox<>(FXCollections.observableArrayList(Shade.NOSHADE, Shade.SIMPLE, Shade.COLORED, Shade.SIMPLEINVERTED, Shade.COLOREDINVERTED));

	// Color picker
	Label fillLabel = new Label("Relleno");
	ColorPicker fillColorPicker1 = new ColorPicker();
	ColorPicker fillColorPicker2 = new ColorPicker();

	// Stroke
	Label strokeLabel = new Label("Borde");
	Slider strokeWidth = new Slider();
	ChoiceBox<Stroke> strokeOptions = new ChoiceBox<>(FXCollections.observableArrayList(Stroke.NORMAL, Stroke.SIMPLE, Stroke.COMPLEX));
	// Actions
	Label actionLabel = new Label("Acciones");

	ToggleButton duplicateButton = new ToggleButton("Duplicar");
	ToggleButton divideButton = new ToggleButton("Dividir");
	ToggleButton moveToCenterButton = new ToggleButton("Mov. Centro");
	List<ToggleButton> actionButtons = List.of(duplicateButton, divideButton, moveToCenterButton);

	// Layers
	Label layerLabel = new Label("Capas");
	ObservableList<Layer<Drawable>> layers = FXCollections.observableArrayList();
	ChoiceBox<Layer<Drawable>> layerOptions = new ChoiceBox<>(layers);
	RadioButton showButton = new RadioButton("Mostrar");
	RadioButton hideButton = new RadioButton("Ocultar");
	ToggleButton addLayerButton = new ToggleButton("Agregar Capa");
	ToggleButton deleteLayerButton = new ToggleButton("Eliminar Capa");

	List<ToggleButton> figureButtons =  List.of(
			rectangleButton,
			circleButton,
			squareButton,
			ellipseButton
	);

	public PaintPane() {
		// Side panel UI
		List<ToggleButton> toolsArr = new ArrayList<>();
		toolsArr.add(selectionButton);
		toolsArr.addAll(figureButtons);
		toolsArr.add(deleteButton);

		Stream.of(
				shadeOptions,
				strokeOptions
		).forEach(e -> e.setMinWidth(TOOL_MIN_WIDTH));

		Collection<Node> sideButtons = new ArrayList<>(toolsArr);
		sideButtons.addAll(List.of(shadeLabel, shadeOptions, fillLabel, fillColorPicker1, fillColorPicker2, strokeLabel, strokeWidth, strokeOptions, actionLabel));
		sideButtons.addAll(actionButtons);

		VBox buttonsBox = new VBox(VBOX_SPACING);
		buttonsBox.getChildren().addAll(sideButtons);
		buttonsBox.setPadding(new Insets(OFFSETS_VALUE));
		buttonsBox.setStyle(BOX_BACKGROUND_COLOR);
		buttonsBox.setPrefWidth(VBOX_PREF_WIDTH);

		// Set toggle groups
		ToggleGroup tools = new ToggleGroup();
		ToggleGroup actions = new ToggleGroup();
		toolsArr.forEach(tool -> tool.setToggleGroup(tools));
		actionButtons.forEach(e -> e.setToggleGroup(actions));

		// Set all the minWidth and Cursors
		Stream.of(toolsArr.stream(), actionButtons.stream())
				.flatMap(Function.identity())
				.forEach(tool -> {
					tool.setMinWidth(TOOL_MIN_WIDTH);
					tool.setCursor(Cursor.HAND);
				});

		strokeWidth.setMin(STROKE_MIN);
		strokeWidth.setMax(STROKE_MAX);
		strokeWidth.setShowTickLabels(true);

		gc.setLineWidth(VBOX_LINE_WIDTH);

		// Top Section UI
		Collection<Node> topButtons = new ArrayList<>(List.of(layerLabel, layerOptions, showButton, hideButton, addLayerButton, deleteLayerButton));
		HBox topBox = new HBox(VBOX_SPACING);
		topBox.getChildren().addAll(topButtons);
		topBox.setPadding(new Insets(OFFSETS_VALUE));
		topBox.setStyle(BOX_BACKGROUND_COLOR);
		topBox.setAlignment(Pos.CENTER);

		setTop(topBox);
		setLeft(buttonsBox);
		setRight(canvas);
	}

	void redrawCanvas(Iterable<Drawable> figures, Map<Drawable, FigureFeatures> figureFeaturesMap) {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for(Drawable figure : figures) {
			// Get all the figures features
			FigureFeatures features = figureFeaturesMap.get(figure);

			// Draw the corresponding shade type
			features.getShade().drawShade(gc, figure, features.getColor1() );

			// Set the gradient fill
			gc.setFill(figure.getFill(features.getColor1(), features.getColor2()));

			// Set stroke
			features.getStroke().setStroke(gc, features.getStrokeWidth(), features.isSelected());
			
			// Draw the figure
			figure.draw(gc);
		}
	}
}
