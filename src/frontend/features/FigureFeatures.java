package frontend.features;
import backend.model.Copiable;
import javafx.scene.paint.*;


public class FigureFeatures implements Copiable<FigureFeatures> {

    private Color color1;
    private Color color2;
    private Shade shade;
    private double strokeWidth;
    private Stroke stroke;
    private boolean selected;

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

    public boolean isSelected(){
        return selected;
    }

    public void setColor1(Color color1) {
        this.color1 = color1;
    }

    public void setColor2(Color color2) {
        this.color2 = color2;
    }

    public void setShade(Shade shade) {
        this.shade = shade;
    }

    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    public void setSelected(boolean selected){
        this.selected = selected;
    }

    @Override
    public FigureFeatures getCopy(){
        return new FigureFeatures(color1, color2, shade, strokeWidth, stroke);
    }
}
