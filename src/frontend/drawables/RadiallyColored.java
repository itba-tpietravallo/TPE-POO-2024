package frontend.drawables;
import javafx.scene.paint.*;

public interface RadiallyColored extends Colorable {
    @Override
    default Paint getFill(Color color1, Color color2) {
        return new RadialGradient(0, 0, 0.5, 0.5, 0.5, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, color1),
                new Stop(1, color2)
        );
    }
}
