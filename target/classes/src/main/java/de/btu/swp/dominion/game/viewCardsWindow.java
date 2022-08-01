package de.btu.swp.dominion.game;

import java.net.URL;
import java.util.ResourceBundle;
import de.btu.swp.dominion.cards.Cards;
import de.btu.swp.dominion.game.GameCon;
import de.btu.swp.dominion.gameLogic.GameLogic;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.Node;

public class viewCardsWindow extends GameCon implements Initializable {
	@FXML
	private Label titelLabel;

	@FXML
	private HBox cardsHBox;

	@FXML
	private Button cancelBtn;

	@FXML
	private ImageView extBtn;

	@FXML
	void cancelBtnHandler(MouseEvent event) {
		cardsHBox.getChildren().clear();
		playerService.getPlayer().getloserHandCards().clear();
		Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		// close the current Stage
		window.close();
	}

	@FXML
	void updateViewingWindow(MouseEvent event) {
		cardsHBox.getChildren().clear();
		for (Cards card : playerService.getPlayer().getloserHandCards()) {
			createImageView(card);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		GameLogic.setBuerokratLoserActive(false);
		titelLabel.setText("Die Handkarten von " + playerService.getPlayer().getloserName());
		cardsHBox.getChildren().clear();
		for (Cards card : playerService.getPlayer().getloserHandCards()) {
			createImageView(card);
		}
	}

	private void createImageView(Cards card) {
		ImageView imgView = new ImageView(new Image(card.getImagePath()));
		imgView.setFitHeight(200);
		imgView.setFitWidth(100);
		cardsHBox.getChildren().add(imgView);
	}

}