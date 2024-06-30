package frontend.drawables;

import backend.model.Figure;
import javafx.scene.paint.*;

public interface Colorable extends Figure {
    Paint getFill(Color color1, Color color2);
}