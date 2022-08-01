package de.btu.swp.dominion.game;

import de.btu.swp.dominion.gameLogic.GuiDesigner;
import de.btu.swp.dominion.gameLogic.OtherPlayers;
import de.btu.swp.dominion.network.Packets.*;
import de.btu.swp.dominion.gameLogic.PlayerService;
import de.btu.swp.dominion.gameLogic.Bot;
import de.btu.swp.dominion.gameLogic.GameLogic;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LobbyCon extends MainMenuCon implements Initializable {

    private PlayerService playerService = MainMenuCon.playerService;
    private static ObservableList<String> chatmessages = FXCollections.observableArrayList();
    @FXML
    private BorderPane mainBorder;
    @FXML
    private ListView<String> lobbyChatList = new ListView<String>(chatmessages);
    @FXML
    private TextField lobbyTextField;
    @FXML
    private Button lobbySendBtn;
    @FXML
    private ImageView LobbyBackBtn;
    @FXML
    private Button lobbyReadyBtn;
    @FXML
    private ImageView HostSettingsBtn;
    @FXML
    private ImageView generalSettings;
    @FXML
    private HBox SettingsBtnHBox;
    @FXML
    private VBox playerVBox;
    @FXML
    private VBox addBotVBox;
    @FXML
    private Button switchSceneTrigger;
    private boolean ready;
    private Timer lobbyTimer = new Timer();
    private static int botIndex = 0;

    public PlayerService getPlayerService() {
        return this.playerService;
    }

    public static void setBotIndex(int botIndex) {
        LobbyCon.botIndex = LobbyCon.botIndex - botIndex;
    }

    @FXML
    public void EventHandler(KeyEvent e) throws Exception {
        if (e.getCode() == KeyCode.ENTER) {
            Packet01Message MessagePacket = new Packet01Message();
            MessagePacket.message = lobbyTextField.getText();
            MessagePacket.clientname = playerService.getPlayer().getPlayerName();
            MessagePacket.stage = 1;
            playerService.getClient().getClient().sendTCP(MessagePacket);
            lobbyTextField.setText("");
        }
    }

    private void updateLobby() {
        if (playerService.getClient().getClient().isConnected() && (playerService.getPlayers().size() != 0 || playerService.getPlayer().getHost())) {
            for (int i = 0; i < playerService.getPlayers().size() + 1; i++) {
                Group group = (Group) playerVBox.getChildren().get(i);
                Label playerLabel = (Label) ((HBox) group.getChildren().get(0)).getChildren().get(0);
                ImageView playerArrow = (ImageView) ((HBox) group.getChildren().get(0)).getChildren().get(1);
                group.setVisible(true);
                if (i == 0 && playerService.getPlayer().getHost()) {
                    playerLabel.setText(playerService.getPlayer().getPlayerName());
                } else if (i == 0) {
                    OtherPlayers other = playerService.getPlayers().get(i);
                    playerLabel.setText(other.getPlayerName());
                    Group group1 = (Group) playerVBox.getChildren().get(i + 1);
                    Label playerLabel1 = (Label) ((HBox) group1.getChildren().get(0)).getChildren().get(0);
                    group1.setVisible(true);
                    playerLabel1.setText(playerService.getPlayer().getPlayerName());
                    ((ImageView) ((HBox) group1.getChildren().get(0)).getChildren().get(1)).setVisible(ready);
                    i++;
                } else {
                    OtherPlayers other = playerService.getPlayers().get(i - 1);
                    playerLabel.setText(other.getPlayerName());
                    try {
                        playerArrow.setVisible(other.getReady());
                    } catch (Exception e) {
                    }
                    if (playerService.getPlayer().getHost()) {
                        group.getChildren().get(1).setVisible(true);
                    }
                }
            }

            if (playerOn() > playerService.getPlayers().size()) {
                for (int i = 1; i < 4; i++) {
                    playerVBox.getChildren().get(i).setVisible(false);
                }
            }

            if (playerService.getPlayer().getHost()) {
                for (int i = 0; i < 3; i++) {
                    Boolean b1 = i == playerService.getPlayers().size();
                    Boolean b2 = playerService.getServerServices().getServerProgram().getServerLimit() > i + 1;
                    addBotVBox.getChildren().get(i).setVisible(b1 && b2);
                }
            }
        }
    }

    public int playerOn() {
        int j = 0;
        for (int i = 1; i < 4; i++) {
            Group group = (Group) playerVBox.getChildren().get(i);
            if (group.isVisible()) {
                j++;
            }
        }
        return j;
    }

    @FXML
    public void xButtonClicked(MouseEvent event) {
        for (int i = 0; i < playerService.getPlayers().size(); i++) {
            ImageView xButton = (ImageView) ((Group) playerVBox.getChildren().get(i + 1)).getChildren().get(1);
            if (event.getSource() == xButton) {
                System.out.println(playerService.getPlayers().get(i).getPlayerName() + " is kicked");
                Packet31KickPlayer kickPlayer = new Packet31KickPlayer();
                kickPlayer.playerName = playerService.getPlayers().get(i).getPlayerName();
                playerService.getClient().getClient().sendTCP(kickPlayer);
            }
        }

    }

    @FXML
    public void ActionHandler(MouseEvent event) throws Exception {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        // Action for Back to Menu Btn
        if (event.getTarget() == LobbyBackBtn) {
            // alert to leave the game
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Bestätigung ");
            // add style to the alert
            musicBox.alertStyler(alert);

            alert.setContentText("Wollen Sie wirklich die Party verlassen?");
            alert.initOwner(window);
            Optional<ButtonType> result = alert.showAndWait();
            // if agreed to leave
            if (result.get() == ButtonType.OK) {
                playerService.getTrigger().reset();
                playerService.getTrigger().setIsInLobbySetting(false);
                // close the Server if you are a host
                if (playerService.getPlayer().getHost()) {
                    playerService.getServerServices().stopServer();
                }
                // leave the Server
                playerService.getClient().getClient().close();
                // change to mainMenu
                try {
                    playerService.getTrigger().setIsInLobby(false);
                    Parent root = FXMLLoader.load(getClass().getResource("/src/main/resources/FXML/HauptMenu.fxml"));

                    if (window.getScene() == null) {
                        Scene sc = new Scene(root, GuiDesigner.getWidth(), GuiDesigner.getHeight());
                        window.setScene(sc);
                    } else {
                        window.getScene().setRoot(root);
                    }
                } catch (IOException e) {
                    System.err.println("err GameCon 100 ");
                }
            }
        }
        // send btn to the Chat
        if (event.getTarget() == lobbySendBtn) {
            if (!lobbyTextField.getText().equals("")) {
                Packet01Message messagePacket = new Packet01Message();
                messagePacket.message = lobbyTextField.getText();
                messagePacket.clientname = playerService.getPlayer().getPlayerName();
                messagePacket.stage = 1;
                playerService.getClient().getClient().sendTCP(messagePacket);
                lobbyTextField.setText("");
            }
        }
    }

    @FXML
    void readyBtnClicked(MouseEvent event) {
        if (playerService.getPlayer().getHost()) {
            if (playerService.allReady()) {
                if (GameLogic.getCardspool().size() == 0) {
                    CardMenuCon cards = new CardMenuCon();
                    GameLogic.setCardspool(cards.getAutoCompleteList());
                }

                lobbyReadyBtn.setText("Spiel wird gestartet..");
                playerService.getServerServices().sendCardpool(GameLogic.getCardspool());

                // send to all players
                Packet01Message messagePacket = new Packet01Message();
                messagePacket.message = " hat das Spiel gestartet!! ";
                messagePacket.clientname = playerService.getPlayer().getPlayerName();
                playerService.getClient().getClient().sendTCP(messagePacket);

                // send game launching to all Clients
                Packet03GameLaunch launchPacket = new Packet03GameLaunch();
                launchPacket.start = true;
                playerService.getClient().getClient().sendTCP(launchPacket);
            } else {
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                alert("Warnung", "Es sind noch nicht alle Spieler bereit.", window);
            }
        } else {
            Packet16ReadyCheck check = new Packet16ReadyCheck();
            check.playerName = playerService.getPlayer().getPlayerName();
            if (lobbyReadyBtn.getText().equals("Bereit")) {
                ready = true;
                check.ready = true;
                lobbyReadyBtn.setText("warte auf Host");
            } else {
                ready = false;
                check.ready = false;
                lobbyReadyBtn.setText("Bereit");
            }
            playerService.getClient().getClient().sendTCP(check);
        }
    }

    /** connect to Game */
    public void goToGameLaunch(ActionEvent event) {
        if (playerService.getPlayer().getReady() == true) {
            playerService.getTrigger().reset();
            playerService.getTrigger().setIsInLobbySetting(false);
            /** give the host YourTurn if no Ranking is Selected */
            if ((playerService.getPlayer().getHost()) && (!playerService.getTrigger().getIsRankingSorted())) {
                playerService.getPlayer().setYourTurn(true);
            }
            try {
                lobbyTimer.cancel();
                playerService.getTrigger().setIsInGame(true);
                playerService.getTrigger().setIsInLobby(false);
                Parent root = FXMLLoader.load(getClass().getResource("/src/main/resources/FXML/game.fxml"));
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                if (window.getScene() == null) {
                    Scene sc = new Scene(root, GuiDesigner.getWidth(), GuiDesigner.getHeight());
                    window.setScene(sc);
                } else {
                    window.getScene().setRoot(root);
                }
            } catch (IOException e) {
                System.out.println("err Lobby 01: Fehler beim Laden von Game");
                e.printStackTrace();
            }
        }
        /** close if he is not connected */
        if (!playerService.getClient().getClient().isConnected()) {
            try {
                lobbyTimer.cancel();
                playerService.getTrigger().setIsInLobby(false);
                Parent root = FXMLLoader.load(getClass().getResource("/src/main/resources/FXML/HauptMenu.fxml"));
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                if (window.getScene() == null) {
                    Scene sc = new Scene(root, GuiDesigner.getWidth(), GuiDesigner.getHeight());
                    window.setScene(sc);
                } else {
                    window.getScene().setRoot(root);
                }
                window.sizeToScene();
            } catch (IOException e) {
                System.err.println("err GameCon 100 ");
            }
        }
    }

    public void addTextToList(String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatmessages.add(message);
            }
        });
    }

    /**
     * The Action for the Settings Btn in the Loppy go to Chose Card
     * 
     * @param event
     */
    @FXML
    void SettingsBtnClicked(MouseEvent event) {
        Parent root;
        playerService.getTrigger().setIsInLobbySetting(true);
        playerService.getTrigger().setSource(false);
        playerService.getTrigger().setCardMenuState(3);
        try {
            lobbyTimer.cancel();
            playerService.getTrigger().setIsInLobby(false);
            root = FXMLLoader.load(getClass().getResource("/src/main/resources/FXML/LobbySettings.fxml"));
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            if (window.getScene() == null) {
                Scene sc = new Scene(root, GuiDesigner.getWidth(), GuiDesigner.getHeight());
                window.setScene(sc);
            } else {
                window.getScene().setRoot(root);
            }
        } catch (IOException e) {
            System.out.println("err Lobby 02: Fehler beim Laden von Lobby Settings");
            e.printStackTrace();
        }
    }

    private void autoLauchGame() {
        if (playerService.getTrigger().getIsInLobby()) {
            if (!playerService.getTrigger().getIsInGame()) {
                /** press a button */
                switchSceneTrigger.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        goToGameLaunch(event);
                    }
                });
                switchSceneTrigger.fire();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // settup slider value
        GuiDesigner.scaling(mainBorder);
        Timer lobbyTimer = new Timer();
        lobbyTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (playerService.getTrigger().getIsInLobby()) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            updateLobby();
                            autoLauchGame();
                        }
                    });
                }
            }
        }, 0, 500);

        // dont show Setting for Clients
        if (!playerService.getPlayer().getHost()) {
            SettingsBtnHBox.setVisible(false);
            // send name of the player to host
            Packet05Players pn = new Packet05Players();
            pn.playerName = playerService.getPlayer().getPlayerName();
            playerService.getClient().getClient().sendTCP(pn);
            playerService.getTrigger().setSource(false);
        } else {
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
                playerService.getTrigger().setSource(false);
            }

            lobbyReadyBtn.setText("Spiel starten!");
        }

        /** Chat setup in Lobby */
        lobbyChatList.setItems(chatmessages);
        lobbyChatList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> lb, String old_val, String new_val) {
            }
        });

    }

    /** generalSetting */
    @FXML
    void generalSettingHandler(MouseEvent event) {
        Packet16ReadyCheck check = new Packet16ReadyCheck();
        check.playerName = playerService.getPlayer().getPlayerName();
        ready = false;
        check.ready = false;
        playerService.getClient().getClient().sendTCP(check);
        playerService.getTrigger().setIsInLobbySetting(true);
        lobbyTimer.cancel();
        try {
            playerService.getTrigger().setIsInLobby(false);
            Parent root = FXMLLoader.load(getClass().getResource("/src/main/resources/FXML/Setting.fxml"));
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            if (window.getScene() == null) {
                Scene sc = new Scene(root, GuiDesigner.getWidth(), GuiDesigner.getHeight());
                window.setScene(sc);
            } else {
                window.getScene().setRoot(root);
            }
        } catch (IOException e) {
            System.out.println("err Lobby 555: Fehler beim Laden von Settings");
            e.printStackTrace();
        }
    }

    @FXML
    void addBot(MouseEvent event) {
        ButtonType easy = new ButtonType("leicht");
        ButtonType medium = new ButtonType("mittel");
        Alert addingBotConfirmation = new Alert(AlertType.CONFIRMATION,
                "Welchen Schwierigkeitsgrad soll der Bot besitzen?", easy, medium);
        addingBotConfirmation.setTitle("Schwierigkeitsgrad");
        // add style to the alert
        musicBox.alertStyler(addingBotConfirmation);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        addingBotConfirmation.initOwner(window);
        botIndex++;
        Bot bot;
        Optional<ButtonType> result = addingBotConfirmation.showAndWait();
        if (result.get() == easy) {
            bot = new Bot("Bot" + botIndex + " (Leicht)", playerService);
            playerService.getPlayers().add(bot);
            Packet05Players pn = new Packet05Players();
            pn.playerName = bot.getPlayerName();
            bot.getClient().getClient().sendTCP(pn);
        } else {
            bot = new Bot("Bot" + botIndex + " (Mittel)", playerService);
            playerService.getPlayers().add(bot);
            Packet05Players pn = new Packet05Players();
            pn.playerName = bot.getPlayerName();
            bot.getClient().getClient().sendTCP(pn);
        }
        Packet16ReadyCheck botready = new Packet16ReadyCheck();
        botready.playerName = bot.getPlayerName();
        botready.ready = true;
        bot.getClient().getClient().sendTCP(botready);
        System.out.println(botready.ready + "");
    }
}