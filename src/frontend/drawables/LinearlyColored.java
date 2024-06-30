package frontend.drawables;

import javafx.scene.paint.*;

public interface LinearlyColored extends Drawable {
    @Override
    default Paint getFill(Color color1, Color color2) {
        return new LinearGradient(0, 0, 1, 0, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, color1),
                new Stop(1, color2)
        );
    }
}
