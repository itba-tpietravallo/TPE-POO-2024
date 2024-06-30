package frontend.features;
import javafx.scene.paint.*;


public class FigureFeatures {

    private Color color1;
    private Color color2;
    private Shade shade;
    private double strokeWidth;
    private Stroke stroke;

    public FigureFeatures(Color color1, Color color2, Shade shadeType, double strokeWidth, Stroke stroke){
        this.color1 = color1;
        this.color2 = color2;
        this.shade = shadeType;
        this.strokeWidth = strokeWidth;
        this.stroke = stroke;
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
    public double getStrokeWidth(){
        return strokeWidth;
    }
    public Stroke getStroke(){
        return stroke;
    }
}
