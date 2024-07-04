package frontend;

import backend.CanvasState;
import frontend.drawables.Drawable;
import javafx.scene.layout.VBox;

public class MainFrame<T extends Drawable> extends VBox {

    public MainFrame(CanvasState<Drawable> canvasState) {
        StatusPane statusPane = new StatusPane();
        PaintPane paintPane = new PaintPane();
        Controller controller = new Controller(canvasState, statusPane, paintPane);

        getChildren().add(new AppMenuBar());
        getChildren().add(paintPane);
        getChildren().add(statusPane);
    }

}
