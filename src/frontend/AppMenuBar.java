package frontend;

import javafx.scene.control.*;

import java.util.Optional;

public class AppMenuBar extends MenuBar {

    public AppMenuBar() {
        Menu file = new Menu("Archivo");
        MenuItem exitMenuItem = new MenuItem("Salir");
        bindMenuItemToAlert(exitMenuItem, Alert.AlertType.CONFIRMATION, "Salir", "Salir de la aplicacion", "¿Está seguro que desea salir de la aplicación?", () -> System.exit(0));

        file.getItems().add(exitMenuItem);
        Menu help = new Menu("Ayuda");
        MenuItem aboutMenuItem = new MenuItem("Acerca De");

        bindMenuItemToAlert(aboutMenuItem, Alert.AlertType.INFORMATION, "Acerca De", "Paint", "TPE Final POO Julio 2024", () -> {});

        help.getItems().add(aboutMenuItem);
        getMenus().addAll(file, help);
    }

    private void bindMenuItemToAlert(MenuItem item, Alert.AlertType type, String title, String header, String contextText, Runnable actionOK) {
        item.setOnAction(event -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(contextText);
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent()) {
                if (result.get() == ButtonType.OK) {
                    actionOK.run();
                }
            }
        });
    }

}
