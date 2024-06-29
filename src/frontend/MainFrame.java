package frontend;

import backend.CanvasState;
import frontend.drawables.Drawable;
import javafx.scene.layout.VBox;

public class MainFrame<T extends Drawable> extends VBox {

    public MainFrame(CanvasState<T> canvasState) {
        getChildren().add(new AppMenuBar());
        StatusPane statusPane = new StatusPane();
        getChildren().add(new PaintPane<>(canvasState, statusPane));
        getChildren().add(statusPane);
    }

}
