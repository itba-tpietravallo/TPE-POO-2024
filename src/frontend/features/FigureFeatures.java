package frontend.features;
import javafx.scene.paint.*;


public class FigureFeatures {

    private Color color1;
    private Color color2;
    private Shade shade;

    public FigureFeatures(Color color1, Color color2, Shade shadeType){
        this.color1 = color1;
        this.color2 = color2;
        this.shade = shadeType;
    }
    public Shade getShade() {
        return shade;
    }
    public Color getColor1() {
        return color1;
    }
    public Color getColor2() {
        return color2;
    }
}
