package de.btu.swp.dominion.game;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import de.btu.swp.dominion.gameLogic.GuiDesigner;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class InstructionCon extends MainMenuCon implements Initializable {

    @FXML
    private ImageView backArrow;

    @FXML
    private BorderPane mainBorder;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GuiDesigner.scaling(mainBorder);
    }

    @FXML
    void backArrowClicked(MouseEvent event) {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("/src/main/resources/FXML/HauptMenu.fxml"));

            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            if (window.getScene() == null) {
                Scene sc = new Scene(root, GuiDesigner.getWidth(), GuiDesigner.getHeight());
                window.setScene(sc);
            } else {
                window.getScene().setRoot(root);
            }
        } catch (IOException e) {
            System.err.println("err Setting 01: Fehler beim Laden des Hauptmenü");
        }
    }
}
