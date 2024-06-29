package frontend.features;
import javafx.scene.paint.Color;

import java.util.Optional;


public class FigureFeatures {

    private Color color;

    private ShadeType shade;

    public FigureFeatures(Color color, ShadeType shadeType){
        this.color = color;
        this.shade = shadeType;
    }
    public ShadeType getShade() {
        return shade;
    }
    public Color getColor() {
        return color;
    }
}
