package frontend.drawables;

import backend.model.Copiable;
import backend.model.Figure;
import frontend.features.FigureFeatures;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public interface Drawable extends Figure, Copiable<Drawable>, Splitable {
    default void draw(GraphicsContext gc){
        // Get all the figures features
        FigureFeatures features = this.getFeatures();
        // Draw the corresponding shade type
        features.getShade().drawShade(gc, this, features.getColor1() );
        // Set the gradient fill
        gc.setFill(this.getFill(features.getColor1(), features.getColor2()));
        // Set stroke
        features.getStroke().setStroke(gc, features.getStrokeWidth(), features.isSelected());
        // Draw the figure
        this.drawShape(gc);
    }
    void drawShape(GraphicsContext gc);
    Paint getFill(Color color1, Color color2);
    void setFeatures(FigureFeatures features);
    //cambiar para que haya un draw
    FigureFeatures getFeatures();
}
