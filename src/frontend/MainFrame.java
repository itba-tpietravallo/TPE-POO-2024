package frontend;

import backend.CanvasState;
import frontend.drawables.Drawable;
import javafx.scene.layout.VBox;

public class MainFrame extends VBox {
    public MainFrame(CanvasState<Drawable> canvasState) {
        StatusPane statusPane = new StatusPane();
        PaintPane paintPane = new PaintPane();
        new Controller(canvasState, statusPane, paintPane);

        getChildren().add(new AppMenuBar());
        getChildren().add(paintPane);
        getChildren().add(statusPane);
    }
}
