package de.btu.swp.dominion.game;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import de.btu.swp.dominion.gameLogic.Bot;
import de.btu.swp.dominion.gameLogic.GameLogic;
import de.btu.swp.dominion.gameLogic.GuiDesigner;
import de.btu.swp.dominion.gameLogic.PlayerService;
import de.btu.swp.dominion.network.Packets.Packet05Players;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SingleplayerCon extends CardMenuCon implements Initializable {

    private PlayerService playerService = MainMenuCon.playerService;
    private Boolean easy = true;
    private int numberOfPlayers = 2;
    @FXML
    private MenuButton menuButtonSchw;
    @FXML
    private MenuButton menuButtonAnzSpieler;
    @FXML
    private BorderPane mainBorderS;
    @FXML
    private TextField userNameForSInglePlayer;
    @FXML
    private ImageView vorgefertigtZeiger;
    @FXML
    private ImageView zufalligKartenZeiger;
    @FXML
    private ImageView selbgewKartenZeiger;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showZeigerSingle();
        if (menuButtonAnzSpieler != null) {
            menuButtonAnzSpieler.setText(Integer.toString(playerService.getTrigger().getBotCounterInSinglePlayer()));
            if (!playerService.getTrigger().getBotLevelrInSinglePlayer()) {
                menuButtonSchw.setText("Mittel");
            }
        }
        GuiDesigner.scaling(mainBorderS);
        if (!(playerService.getPlayer().getPlayerName() == null)) {
            userNameForSInglePlayer.setText(playerService.getPlayer().getPlayerName());
        } else {
            userNameForSInglePlayer.setText(metaData.getPlayerNameMeta());
        }
    }

    public void showZeigerSingle() {
        if (playerService.getTrigger().getTriggerChosen()) {
            selbgewKartenZeiger.setVisible(true);
            vorgefertigtZeiger.setVisible(false);
            zufalligKartenZeiger.setVisible(false);

        } else if (playerService.getTrigger().getTriggerCardSets()) {
            vorgefertigtZeiger.setVisible(true);
            selbgewKartenZeiger.setVisible(false);
            zufalligKartenZeiger.setVisible(false);

        } else if (playerService.getTrigger().getTriggerRandom()) {
            zufalligKartenZeiger.setVisible(true);
            selbgewKartenZeiger.setVisible(false);
            vorgefertigtZeiger.setVisible(false);
        }
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
            System.err.println("err Single 01: Backarrow");
            e.printStackTrace();
        }
    }

    @FXML
    public void menuButtonsClicked(ActionEvent event) {
        switch (((MenuItem) event.getSource()).getText()) {
        case "2":
            menuButtonAnzSpieler.setText("2");
            this.numberOfPlayers = 2;
            playerService.getTrigger().setBotCounterInSinglePlayer(2);
            return;
        case "3":
            menuButtonAnzSpieler.setText("3");
            this.numberOfPlayers = 3;
            playerService.getTrigger().setBotCounterInSinglePlayer(3);
            return;
        case "4":
            menuButtonAnzSpieler.setText("4");
            this.numberOfPlayers = 4;
            playerService.getTrigger().setBotCounterInSinglePlayer(4);
            return;
        case "Leicht":
            menuButtonSchw.setText("Leicht");
            this.easy = true;
            playerService.getTrigger().setBotLevelrInSinglePlayer(true);
            return;
        case "Mittel":
            menuButtonSchw.setText("Mittel");
            this.easy = false;
              playerService.getTrigger().setBotLevelrInSinglePlayer(false);
            return;
        }
    }

    /** open Spielfeld from einzelspieler.fxml */
    @FXML
    void moveToSpielFeldBtnClicked(MouseEvent event) {
        if (userNameForSInglePlayer.getText().isEmpty()) {
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            alert("Fehler", "Erst den Namen eingeben!", window);
        } else {
            // save his name in meta
            metaData.setPlayerNameMeta(userNameForSInglePlayer.getText());
            playerService.getPlayer().setPlayerName(userNameForSInglePlayer.getText());
            playerService.getPlayer().setHost(true);

            // check if the card list is empty
            if (GameLogic.getCardspool().size() == 0) {
                CardMenuCon cards = new CardMenuCon();
                GameLogic.setCardspool(cards.getAutoCompleteList());
            }

            // give the Host true if he in single Player
            playerService.getPlayer().setYourTurn(true);
            try {
                playerService.initServer(numberOfPlayers);
                playerService.initConnection();
                playerService.getTrigger().reset();
                playerService.getTrigger().setIsInLobbySetting(false);

                boolean l = true;
                while (l) {
                    System.out.print("");
                    if (playerService.getServerServices().getAllPlayers().size() > 0) {
                        l = false;
                    }
                }
                if (!playerService.getServerServices().getAllPlayers().get(0).toString()
                        .equals(playerService.getPlayer().getPlayerName())) {
                    // put his name in playerlist
                    playerService.getServerServices().getAllPlayers().get(0)
                            .setName(playerService.getPlayer().getPlayerName());
                }
                // Bots hinzufügen
                if (easy) {
                    for (int i = 1; i < numberOfPlayers; i++) {
                        System.out.println("werde bearbeitet");
                        Bot bot = new Bot("Bot" + i, playerService);
                        Packet05Players pn = new Packet05Players();
                        pn.playerName = bot.getPlayerName();
                        bot.getClient().getClient().sendTCP(pn);
                    }
                } else {// TODO Mittlere Bots hier erzeugen
                    for (int i = 1; i < numberOfPlayers; i++) {
                        Bot bot = new Bot("Bot" + i, playerService);
                        Packet05Players pn = new Packet05Players();
                        pn.playerName = bot.getPlayerName();
                        bot.getClient().getClient().sendTCP(pn);
                    }
                }
                playerService.getTrigger().setIsInGame(true);
                Parent root = FXMLLoader.load(getClass().getResource("/src/main/resources/FXML/game.fxml"));
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                if (window.getScene() == null) {
                    Scene sc = new Scene(root, GuiDesigner.getWidth(), GuiDesigner.getHeight());
                    window.setScene(sc);
                } else {
                    window.getScene().setRoot(root);
                }
            } catch (IOException e) {
                System.err.println("err Single 09: Fehler beim Laden des Games");
                e.printStackTrace();
            }
        }
    }
}