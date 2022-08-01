package de.btu.swp.dominion.game;

import com.esotericsoftware.kryonet.Connection;
import de.btu.swp.dominion.cards.*;
import de.btu.swp.dominion.game.LobbyCon;
import de.btu.swp.dominion.gameLogic.PlayerService;
import de.btu.swp.dominion.gameLogic.GameLogic;
import de.btu.swp.dominion.gameLogic.GuiDesigner;
import de.btu.swp.dominion.network.Packets.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CardMenuCon extends LobbyCon implements Initializable {
    // BackButtons
    private PlayerService playerService = getPlayerService();
    private ArrayList<Cards> kartenpoolList = new ArrayList<>();
    /** autoComplete List */
    private ArrayList<Cards> autoCompleteList = new ArrayList<>();
    // Objects
    private Cards cardDorf = new Dorf();
    private Cards cardSchmiede = new Schmiede();
    private Cards cardHolzfaeller = new Holzfaeller();
    private Cards cardKanzler = new Kanzler();
    private Cards cardAbenteurer = new Abenteurer();
    private Cards cardLaboratorium = new Laboratorium();
    private Cards cardBibliothek = new Bibliothek();
    private Cards cardJahrmarkt = new Jahrmarkt();
    private Cards cardMarkt = new Markt();
    private Cards cardKapelle = new Kapelle();
    private Cards cardMiliz = new Miliz();
    private Cards cardBurggraben = new Burggraben();
    private Cards cardThronsaal = new Thronsaal();
    private Cards cardHexe = new Hexe();
    private Cards cardRatsversammlung = new Ratsversammlung();
    private Cards cardDieb = new Dieb();
    private Cards cardWerkstatt = new Werkstatt();
    private Cards cardUmbau = new Umbau();
    private Cards cardSpion = new Spion();
    private Cards cardGarten = new Gaerten();
    private Cards cardFestmahl = new Festmahl();
    private Cards cardGeldverleiher = new Geldverleiher();
    private Cards cardBuerokrat = new Buerokrat();
    private Cards cardKeller = new Keller();
    private Cards cardMine = new Mine();

    @FXML
    private VBox vorVBox;

    /** vack arrow to the Lobby */
    @FXML
    private ImageView backArrow;
    @FXML
    private TextField userNameForSInglePlayer;
    // Save Buttons
    /** save Btn from Vorgewaehlte Karten */
    @FXML
    private ImageView erstesSpielBtn;
    @FXML
    private ImageView grossesGeldBtn;
    @FXML
    private Button SaveBtnSelbstGe;
    // Zeiger::
    @FXML
    private ImageView zufalligKartenZeiger;
    @FXML
    private ImageView vorgefertigtZeiger;
    @FXML
    private ImageView selbgewKartenZeiger;
    // Karten
    @FXML
    private ImageView dorfKarte;
    @FXML
    private ImageView schmiedeKarte;
    @FXML
    private ImageView holzfaellerKarte;
    @FXML
    private ImageView kanzlerKarte;
    @FXML
    private ImageView abenteurerKarte;
    @FXML
    private ImageView laboratoriumKarte;
    @FXML
    private ImageView libKarte;
    @FXML
    private ImageView jahrmarktKarte;
    @FXML
    private ImageView marktKarte;
    @FXML
    private ImageView kapelleKarte;
    @FXML
    private ImageView thronsaalKarte;
    @FXML
    private ImageView hexeKarte;
    @FXML
    private ImageView milizKarte;
    @FXML
    private ImageView burggrabenKarte;
    @FXML
    private ImageView ratsversammlungKarte;
    @FXML
    private ImageView diebKarte;
    @FXML
    private ImageView werkstattKarte;
    @FXML
    private ImageView umbauKarte;
    @FXML
    private ImageView spionKarte;
    @FXML
    private ImageView gaertenKarte;
    @FXML
    private ImageView festmahlKarte;
    @FXML
    private ImageView geldverleiherKarte;
    @FXML
    private ImageView buerokratKarte;
    @FXML
    private ImageView kellerKarte;
    @FXML
    private ImageView mineKarte;

    // Ranking
    /** used for the Ranking */
    private int playerOneRanking = 1;
    private int playerTwoRanking = 2;
    private int playerThreeRanking = 3;
    private int playerFourRanking = 4;

    /** Reihenfolge der Spieler */

    @FXML
    private BorderPane mainBorderS;
    @FXML
    private BorderPane mainBorderC;
    @FXML
    private BorderPane mainBorderVor;
    @FXML
    private BorderPane mainBorderRan;
    @FXML
    private BorderPane mainBorderSel;
    @FXML
    private HBox HostHBox;
    @FXML
    private Label HostLabel;
    @FXML
    private MenuButton rankHost;
    @FXML
    private MenuItem hostRankOne;
    @FXML
    private MenuItem hostRankTwo;
    @FXML
    private MenuItem hostRankThree;
    @FXML
    private MenuItem hostRankFour;
    @FXML
    private HBox PlayerTwoHBox;
    @FXML
    private Label PlayerTwoLabel;
    @FXML
    private MenuButton rankPlayerTwo;
    @FXML
    private MenuItem playerTwoRankOne;
    @FXML
    private MenuItem playerTwoRankTwo;
    @FXML
    private MenuItem playerTwoRankThree;
    @FXML
    private MenuItem playerTwoRankFour;
    @FXML
    private HBox PlayerThreeHBox;
    @FXML
    private Label PlayerThreeLabel;
    @FXML
    private MenuButton rankPlayerThree;
    @FXML
    private MenuItem playerThreeRankOne;
    @FXML
    private MenuItem playerThreeRankTwo;
    @FXML
    private MenuItem playerThreeRankThree;
    @FXML
    private MenuItem playerThreeRankFour;
    @FXML
    private HBox PlayerFourHBox;
    @FXML
    private Label PlayerFourLabel;
    @FXML
    private MenuButton rankPlayerFour;
    @FXML
    private MenuItem playerFourRankOne;
    @FXML
    private MenuItem playerFourRankTwo;
    @FXML
    private MenuItem playerFourRankThree;
    @FXML
    private MenuItem playerFourRankFour;
    /** zufällige Kartensatz */
    @FXML
    private HBox randomCards1;
    @FXML
    private HBox randomCards2;
    private int cardHeight = 220; // old 308
    private int cardWidth = 150; // old 200
    @FXML
    private ImageView greatImageView;

    // Action Handler
    @FXML
    void backArrowSettingClicked(MouseEvent event) {
        Parent root;
        // back to lobby from CardMenu
        if (event.getTarget() == backArrow) {
            try {
                playerService.getTrigger().setIsInLobbySetting(false);
                playerService.getTrigger().setIsInLobby(true);
                root = FXMLLoader.load(getClass().getResource("/src/main/resources/FXML/Lobby.fxml"));
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                if (window.getScene() == null) {
                    Scene sc = new Scene(root, GuiDesigner.getWidth(), GuiDesigner.getHeight());
                    window.setScene(sc);
                } else {
                    window.getScene().setRoot(root);
                }
            } catch (IOException e) {
                System.err.println("err CardMenu 01: BackArrow Menu");
            }
        } else {
            // back to LobbySettings (Card Menu from XX..Kartensatz Menus)
            playerService.getTrigger().reset();
            try {
                if (playerService.getTrigger().getSource() == false) {
                    playerService.getTrigger().setCardMenuState(3);
                    playerService.getTrigger().setIsInLobbySetting(true);
                    root = FXMLLoader.load(getClass().getResource("/src/main/resources/FXML/LobbySettings.fxml"));
                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    if (window.getScene() == null) {
                        Scene sc = new Scene(root, GuiDesigner.getWidth(), GuiDesigner.getHeight());
                        window.setScene(sc);
                    } else {
                        window.getScene().setRoot(root);
                    }
                } else {
                    playerService.getTrigger().setCardMenuState(4);
                    root = FXMLLoader.load(getClass().getResource("/src/main/resources/FXML/einzelSpieler.fxml"));
                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    if (window.getScene() == null) {
                        Scene sc = new Scene(root, GuiDesigner.getWidth(), GuiDesigner.getHeight());
                        window.setScene(sc);
                    } else {
                        window.getScene().setRoot(root);
                    }
                }
            } catch (IOException e) {
                System.err.println("err CardMenu 02");
            }
        }
    }

    /** from Settings to Cards Menu */
    @FXML
    void selbstgewClicked(MouseEvent event) {
        // for SinglePlayer rememeber the username before going to menues
        if (playerService.getTrigger().getSource() == true) {
            if (!userNameForSInglePlayer.getText().isEmpty()) {
                playerService.getPlayer().setPlayerName(userNameForSInglePlayer.getText());
            }
        }

        if (isListSelected(event)) {
            // trigger zeiger
            playerService.getTrigger().reset();
            playerService.getTrigger().setTriggerChosen(true);
            playerService.getTrigger().setTriggerCardSets(false);
            playerService.getTrigger().setTriggerRandom(false);
            playerService.getTrigger().setIsInLobbySetting(false);
            playerService.getTrigger().setCardMenuState(2);

            changeState(event);
        }
    }

    /** from Settings to Cards Menu */
    @FXML
    void vorgefertigtKartenClicked(MouseEvent event) {
        // for SinglePlayer rememeber the username before going to menues
        if (playerService.getTrigger().getSource() == true) {
            if (!userNameForSInglePlayer.getText().isEmpty()) {
                playerService.getPlayer().setPlayerName(userNameForSInglePlayer.getText());
            }
        }
        if (isListSelected(event)) {
            // trigger zeiger
            playerService.getTrigger().reset();
            playerService.getTrigger().setTriggerChosen(false);
            playerService.getTrigger().setTriggerCardSets(true);
            playerService.getTrigger().setTriggerRandom(false);
            playerService.getTrigger().setIsInLobbySetting(false);
            playerService.getTrigger().setCardMenuState(0);

            changeState(event);
        }
    }

    /** from Settings to Cards Menu */
    @FXML
    void zufalligKartenClicked(MouseEvent event) {
        // for SinglePlayer rememeber the username before going to menues
        if (playerService.getTrigger().getSource() == true) {
            if (!userNameForSInglePlayer.getText().isEmpty()) {
                playerService.getPlayer().setPlayerName(userNameForSInglePlayer.getText());
            }
        }

        if (isListSelected(event)) {
            // trigger zeiger
            playerService.getTrigger().reset();
            playerService.getTrigger().setTriggerChosen(false);
            playerService.getTrigger().setTriggerCardSets(false);
            playerService.getTrigger().setTriggerRandom(true);
            playerService.getTrigger().setIsInLobbySetting(false);
            playerService.getTrigger().setCardMenuState(1);

            changeState(event);
        }
    }

    /** check if Cardpool is empty and sent Alert */
    public boolean isListSelected(MouseEvent event) {
        if (GameLogic.getCardspool().size() != 0) {
            // alert to reSelect the Cards if the kartenpool list already full
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Bestätigung");
            musicBox.alertStyler(alert);
            alert.setContentText("Sie haben bereits eine Liste gewählt. Wollen Sie eine neue Liste wählen?");
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            alert.initOwner(window);
            // if agreed to leave
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void changeState(MouseEvent event) {
        try {
            Parent root;
            if (playerService.getTrigger().getCardMenuState() == 1) {
                root = FXMLLoader.load(getClass().getResource("/src/main/resources/FXML/ZufalligKartensatz.fxml"));
            } else if (playerService.getTrigger().getCardMenuState() == 2) {
                root = FXMLLoader
                        .load(getClass().getResource("/src/main/resources/FXML/SelbstGewaehlterKartensatz.fxml"));
            } else {
                root = FXMLLoader
                        .load(getClass().getResource("/src/main/resources/FXML/VorgefertigterKartensatz.fxml"));
            }
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            if (window.getScene() == null) {
                Scene sc = new Scene(root, GuiDesigner.getWidth(), GuiDesigner.getHeight());
                window.setScene(sc);
            } else {
                window.getScene().setRoot(root);
            }
        } catch (IOException e) {
            System.err.println("err CardMenu 05: change State");
            e.printStackTrace();
        }
    }

    @FXML
    void changeToGreen(MouseEvent event) {
        VBox vBox = (VBox) event.getSource();
        vBox.setBackground(new Background(new BackgroundFill(Color.valueOf("#DFDDCA"), null, null)));
    }

    /** Multiplayer save Cards after selection in different Menus */
    @FXML
    void SaveCards(MouseEvent event) {
        // selbstgewählte
        if (playerService.getTrigger().getCardMenuState() == 2) {
            // add Cards to CardList in PlayerServices
            autoComplete();
        }
        // go at the end to lobbySettings
        playerService.getTrigger().setIsInLobbySetting(true);
        // save kartenpoolList into the kartenpool in playerService
        GameLogic.setCardspool(kartenpoolList);
        if (!playerService.getTrigger().getSource()) {
            playerService.getServerServices().getServerProgram().setCardspool(kartenpoolList);
        }
        Parent root;
        try {
            if (playerService.getTrigger().getSource() == false) {
                playerService.getTrigger().setCardMenuState(3);
                root = FXMLLoader.load(getClass().getResource("/src/main/resources/FXML/LobbySettings.fxml"));
            } else {
                playerService.getTrigger().setCardMenuState(4);
                root = FXMLLoader.load(getClass().getResource("/src/main/resources/FXML/einzelSpieler.fxml"));
            }
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            if (window.getScene() == null) {
                Scene sc = new Scene(root, GuiDesigner.getWidth(), GuiDesigner.getHeight());
                window.setScene(sc);
            } else {
                window.getScene().setRoot(root);
            }
        } catch (IOException e) {
            System.err.println("err CardMenu 06");
        }
    }

    // Anmiations
    // Zoom ActionHandler

    /** if the Mouse on the Card then zoom out to the Card */
    public void zoomIn(MouseEvent event) {
        greatImageView.setImage(((ImageView) event.getSource()).getImage());
    }

    /** select& DeSelect Cards for selbstgewählte Kartensatz */
    @FXML
    void choose(MouseEvent event) {
        HBox cardHBox = ((HBox) ((ImageView) event.getSource()).getParent());
        if (cardHBox.backgroundProperty().getValue() == null) {
            if (kartenpoolList.size() < 10) {
                changeToGreen(cardHBox);
                selectCard(event);
            } else {
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                alert("Fehler", "Sie dürfen nicht mehr als 10 Karten auswählen.", window);
            }
        } else {
            changeToNormal(cardHBox);
            deselectCard(event);
        }
    }

    public void deselectCard(MouseEvent event) {
        if (event.getTarget() == dorfKarte) {
            kartenpoolList.remove(cardDorf);
        } else if (event.getTarget() == schmiedeKarte) {
            kartenpoolList.remove(cardSchmiede);
        } else if (event.getTarget() == holzfaellerKarte) {
            kartenpoolList.remove(cardHolzfaeller);
        } else if (event.getTarget() == kanzlerKarte) {
            kartenpoolList.remove(cardKanzler);
        } else if (event.getTarget() == abenteurerKarte) {
            kartenpoolList.remove(cardAbenteurer);
        } else if (event.getTarget() == laboratoriumKarte) {
            kartenpoolList.remove(cardLaboratorium);
        } else if (event.getTarget() == libKarte) {
            kartenpoolList.remove(cardBibliothek);
        } else if (event.getTarget() == jahrmarktKarte) {
            kartenpoolList.remove(cardJahrmarkt);
        } else if (event.getTarget() == marktKarte) {
            kartenpoolList.remove(cardMarkt);
        } else if (event.getTarget() == kapelleKarte) {
            kartenpoolList.remove(cardKapelle);
        } else if (event.getTarget() == thronsaalKarte) {
            kartenpoolList.remove(cardThronsaal);
        } else if (event.getTarget() == hexeKarte) {
            kartenpoolList.remove(cardHexe);
        } else if (event.getTarget() == milizKarte) {
            kartenpoolList.remove(cardMiliz);
        } else if (event.getTarget() == burggrabenKarte) {
            kartenpoolList.remove(cardBurggraben);
        } else if (event.getTarget() == ratsversammlungKarte) {
            kartenpoolList.remove(cardRatsversammlung);
        //} else if (event.getTarget() == diebKarte) {
            //kartenpoolList.remove(cardDieb);
        } else if (event.getTarget() == werkstattKarte) {
            kartenpoolList.remove(cardWerkstatt);
        } else if (event.getTarget() == umbauKarte) {
            kartenpoolList.remove(cardUmbau);
        } else if (event.getTarget() == spionKarte) {
            kartenpoolList.remove(cardSpion);
        } else if (event.getTarget() == gaertenKarte) {
            kartenpoolList.remove(cardGarten);
        } else if (event.getTarget() == festmahlKarte) {
            kartenpoolList.remove(cardFestmahl);
        } else if (event.getTarget() == geldverleiherKarte) {
            kartenpoolList.remove(cardGeldverleiher);
        } else if (event.getTarget() == buerokratKarte) {
            kartenpoolList.remove(cardBuerokrat);
        } else if (event.getTarget() == kellerKarte) {
            kartenpoolList.remove(cardKeller);
        } else if (event.getTarget() == mineKarte) {
            kartenpoolList.remove(cardMine);
        }
    }

    public void selectCard(MouseEvent event) {
        if (event.getTarget() == dorfKarte) {
            kartenpoolList.add(cardDorf);
        } else if (event.getTarget() == schmiedeKarte) {
            kartenpoolList.add(cardSchmiede);
        } else if (event.getTarget() == holzfaellerKarte) {
            kartenpoolList.add(cardHolzfaeller);
        } else if (event.getTarget() == kanzlerKarte) {
            kartenpoolList.add(cardKanzler);
        } else if (event.getTarget() == abenteurerKarte) {
            kartenpoolList.add(cardAbenteurer);
        } else if (event.getTarget() == laboratoriumKarte) {
            kartenpoolList.add(cardLaboratorium);
        } else if (event.getTarget() == libKarte) {
            kartenpoolList.add(cardBibliothek);
        } else if (event.getTarget() == jahrmarktKarte) {
            kartenpoolList.add(cardJahrmarkt);
        } else if (event.getTarget() == marktKarte) {
            kartenpoolList.add(cardMarkt);
        } else if (event.getTarget() == kapelleKarte) {
            kartenpoolList.add(cardKapelle);
        } else if (event.getTarget() == thronsaalKarte) {
            kartenpoolList.add(cardThronsaal);
        } else if (event.getTarget() == hexeKarte) {
            kartenpoolList.add(cardHexe);
        } else if (event.getTarget() == milizKarte) {
            kartenpoolList.add(cardMiliz);
        } else if (event.getTarget() == burggrabenKarte) {
            kartenpoolList.add(cardBurggraben);
        } else if (event.getTarget() == ratsversammlungKarte) {
            kartenpoolList.add(cardRatsversammlung);
        //} else if (event.getTarget() == diebKarte) {
            //kartenpoolList.add(cardDieb);
        } else if (event.getTarget() == werkstattKarte) {
            kartenpoolList.add(cardWerkstatt);
        } else if (event.getTarget() == umbauKarte) {
            kartenpoolList.add(cardUmbau);
        } else if (event.getTarget() == spionKarte) {
            kartenpoolList.add(cardSpion);
        } else if (event.getTarget() == gaertenKarte) {
            kartenpoolList.add(cardGarten);
        } else if (event.getTarget() == festmahlKarte) {
            kartenpoolList.add(cardFestmahl);
        } else if (event.getTarget() == geldverleiherKarte) {
            kartenpoolList.add(cardGeldverleiher);
        } else if (event.getTarget() == buerokratKarte) {
            kartenpoolList.add(cardBuerokrat);
        } else if (event.getTarget() == kellerKarte) {
            kartenpoolList.add(cardKeller);
        } else if (event.getTarget() == mineKarte) {
            kartenpoolList.add(cardMine);
        }
    }

    /**
     * change the selected cardsbackground to green
     */
    private void changeToGreen(HBox bg) {
        bg.setBackground(new Background(new BackgroundFill(Color.valueOf("#DFDDCA"), null, null)));
    }

    /** return to normal background color */
    private void changeToNormal(HBox bg) {
        //bg.setBackground(new Background(new BackgroundFill(null, null, null)));
        bg.setBackground(null);
    }
    
    @FXML
    void chooseVor(MouseEvent event) {
        VBox vBox = (VBox) event.getSource();
        VBox parent = (VBox) vBox.getParent();
        ((Label) vBox.getChildren().get(0)).setTextFill(Color.web("#000000"));
        vBox.setBackground(new Background(new BackgroundFill(Color.valueOf("#DFDDCA"), null, null)));
        if (kartenpoolList.size() < 10) {
            vorVBox = vBox;
        } else {
            vorVBox.setBackground(null);
            ((Label) vorVBox.getChildren().get(0)).setTextFill(Color.web("#DFDDCA"));
            vorVBox = vBox;
            kartenpoolList.clear();
        }
        for (int i = 0; i < parent.getChildren().size(); i++) {
            if (parent.getChildren().get(i) == vorVBox) {
                vorSelectCards(i);
            }
        }
    }

    public void vorSelectCards(int index) {
        switch (index) {
            case 0:
                kartenpoolList.add(cardBurggraben);
                kartenpoolList.add(cardDorf);
                kartenpoolList.add(cardHolzfaeller);
                kartenpoolList.add(cardKeller);
                kartenpoolList.add(cardMarkt);
                kartenpoolList.add(cardMiliz);
                kartenpoolList.add(cardSchmiede);
                kartenpoolList.add(cardUmbau);
                kartenpoolList.add(cardWerkstatt);
                kartenpoolList.add(cardMine);
                break;
            case 1:
                kartenpoolList.add(cardAbenteurer);
                kartenpoolList.add(cardBuerokrat);
                kartenpoolList.add(cardFestmahl);
                kartenpoolList.add(cardGeldverleiher);
                kartenpoolList.add(cardKanzler);
                kartenpoolList.add(cardKapelle);
                kartenpoolList.add(cardLaboratorium);
                kartenpoolList.add(cardMarkt);
                kartenpoolList.add(cardMine);
                kartenpoolList.add(cardThronsaal);
                break;
            case 2:
                kartenpoolList.add(cardBibliothek);
                kartenpoolList.add(cardBurggraben);
                kartenpoolList.add(cardBuerokrat);
                kartenpoolList.add(cardKeller);
                kartenpoolList.add(cardDorf);
                kartenpoolList.add(cardJahrmarkt);
                kartenpoolList.add(cardKanzler);
                kartenpoolList.add(cardMiliz);
                kartenpoolList.add(cardRatsversammlung);
                kartenpoolList.add(cardSpion);
                break;
            case 3:
                kartenpoolList.add(cardMine);
                kartenpoolList.add(cardDorf);
                kartenpoolList.add(cardFestmahl);
                kartenpoolList.add(cardGarten);
                kartenpoolList.add(cardHexe);
                kartenpoolList.add(cardHolzfaeller);
                kartenpoolList.add(cardKapelle);
                kartenpoolList.add(cardKeller);
                kartenpoolList.add(cardLaboratorium);
                kartenpoolList.add(cardWerkstatt);
                break;
            case 4:
                kartenpoolList.add(cardBibliothek);
                kartenpoolList.add(cardBuerokrat);
                kartenpoolList.add(cardDorf);
                kartenpoolList.add(cardHolzfaeller);
                kartenpoolList.add(cardJahrmarkt);
                kartenpoolList.add(cardKeller);
                kartenpoolList.add(cardMarkt);
                kartenpoolList.add(cardSchmiede);
                kartenpoolList.add(cardThronsaal);
                kartenpoolList.add(cardUmbau);
                break;
        }
    }

    /**
     * Save Reihenfolge Btn is in LobbySetting
     */
    @FXML
    void saveInLobbySettingsHandler(MouseEvent event) {
        // save Reihenfolge
        setupRanking();
        // send True for the First One in the Connectionlist
        playerService.getTrigger().seIsRankingSorted(true);
        Packet04NextPlayer turn = new Packet04NextPlayer();
        turn.turn = true;
        turn.playerN = playerService.getServerServices().getAllPlayers().get(0).toString();
        playerService.getClient().getClient().sendTCP(turn);
        // go back to Lobby
        try {
            playerService.getTrigger().setIsInLobbySetting(false);
              playerService.getTrigger().setIsInLobby(true);
            Parent root = FXMLLoader.load(getClass().getResource("/src/main/resources/FXML/Lobby.fxml"));
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            if (window.getScene() == null) {
                Scene sc = new Scene(root, GuiDesigner.getWidth(), GuiDesigner.getHeight());
                window.setScene(sc);
            } else {
                window.getScene().setRoot(root);
            }
        } catch (IOException e) {
            System.err.println("err CardMenu 07");
        }
    }

    /** setup the Reihenfolge von Spielern */
    private void setupRanking() {
        try {
            int limit = playerService.getServerServices().getAllPlayers().size();
            Connection[] list = new Connection[limit];
            list[0] = playerService.getServerServices().getAllPlayers().get(playerOneRanking - 1);
            if (limit == 2) {
                list[1] = playerService.getServerServices().getAllPlayers().get(playerTwoRanking - 1);
            } else if (limit == 3) {
                list[1] = playerService.getServerServices().getAllPlayers().get(playerTwoRanking - 1);
                list[2] = playerService.getServerServices().getAllPlayers().get(playerThreeRanking - 1);
            } else if (limit == 4) {
                list[1] = playerService.getServerServices().getAllPlayers().get(playerTwoRanking - 1);
                list[2] = playerService.getServerServices().getAllPlayers().get(playerThreeRanking - 1);
                list[3] = playerService.getServerServices().getAllPlayers().get(playerFourRanking - 1);
            }

            playerService.getServerServices().getAllPlayers().clear();

            for (int i = 0; i < limit; i++) {
                playerService.getServerServices().getAllPlayers().add(list[i]);
            }
        } catch (Exception e) {
            System.err.println("err CardMenu 08: fxml refresh exception aber nicht entscheident!");
        }
    }

    /**
     * work with the Treger Class to show a zeiger of the selected set of cards in
     * the lobby Settings
     */
    public void showZeiger() {
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
    void rank(ActionEvent event) {
        SwitchPlayerOne(event);
        SwitchPlayerTwo(event);
        SwitchPlayerThree(event);
        SwitchPlayerFour(event);
    }

    /** einstellung für die Reihenfolge für Player 1 */
    private void SwitchPlayerOne(ActionEvent event) {
        if (event.getTarget() == hostRankOne) {
            swap("1", rankHost.getText());
            rankHost.setText("1");
            playerOneRanking = 1;
        } else if (event.getTarget() == hostRankTwo) {
            swap("2", rankHost.getText());
            rankHost.setText("2");
            playerOneRanking = 2;
        } else if (event.getTarget() == hostRankThree) {
            swap("3", rankHost.getText());
            rankHost.setText("3");
            playerOneRanking = 3;
        } else if (event.getTarget() == hostRankFour) {
            swap("4", rankHost.getText());
            rankHost.setText("4");
            playerOneRanking = 4;
        }
    }

    /** einstellung für die Reihenfolge für Player 2 */
    private void SwitchPlayerTwo(ActionEvent event) {
        if (event.getTarget() == playerTwoRankOne) {
            swap("1", rankPlayerTwo.getText());
            rankPlayerTwo.setText("1");
            playerTwoRanking = 1;
        } else if (event.getTarget() == playerTwoRankTwo) {
            swap("2", rankPlayerTwo.getText());
            rankPlayerTwo.setText("2");
            playerTwoRanking = 2;
        } else if (event.getTarget() == playerTwoRankThree) {
            swap("3", rankPlayerTwo.getText());
            rankPlayerTwo.setText("3");
            playerTwoRanking = 3;
        } else if (event.getTarget() == playerTwoRankFour) {
            swap("4", rankPlayerTwo.getText());
            rankPlayerTwo.setText("4");
            playerTwoRanking = 4;
        }
    }

    /** einstellung für die Reihenfolge für Player 3 */
    private void SwitchPlayerThree(ActionEvent event) {
        if (event.getTarget() == playerThreeRankOne) {
            swap("1", rankPlayerThree.getText());
            rankPlayerThree.setText("1");
            playerThreeRanking = 1;
        } else if (event.getTarget() == playerThreeRankTwo) {
            swap("2", rankPlayerThree.getText());
            rankPlayerThree.setText("2");
            playerThreeRanking = 2;
        } else if (event.getTarget() == playerThreeRankThree) {
            swap("3", rankPlayerThree.getText());
            rankPlayerThree.setText("3");
            playerThreeRanking = 3;
        } else if (event.getTarget() == playerThreeRankFour) {
            swap("4", rankPlayerThree.getText());
            rankPlayerThree.setText("4");
            playerThreeRanking = 4;
        }
    }

    /** einstellung für die Reihenfolge für Player 4 */
    private void SwitchPlayerFour(ActionEvent event) {
        if (event.getTarget() == playerFourRankOne) {
            swap("1", rankPlayerFour.getText());
            rankPlayerFour.setText("1");
            playerFourRanking = 1;
        } else if (event.getTarget() == playerFourRankTwo) {
            swap("2", rankPlayerFour.getText());
            rankPlayerFour.setText("2");
            playerFourRanking = 2;
        } else if (event.getTarget() == playerFourRankThree) {
            swap("3", rankPlayerFour.getText());
            rankPlayerFour.setText("3");
            playerFourRanking = 3;
        } else if (event.getTarget() == playerFourRankFour) {
            swap("4", rankPlayerFour.getText());
            rankPlayerFour.setText("4");
            playerFourRanking = 4;
        }
    }

    /**
     * swap will swap a two ranking with each other
     * 
     * @return
     */
    private void swap(String oldPos, String newPos) {
        if (rankHost.getText().equals(oldPos)) {
            rankHost.setText(newPos);
            playerOneRanking = Integer.parseInt(newPos);
        } else if (rankPlayerTwo.getText().equals(oldPos)) {
            rankPlayerTwo.setText(newPos);
            playerTwoRanking = Integer.parseInt(newPos);
        } else if (rankPlayerThree.getText().equals(oldPos)) {
            rankPlayerThree.setText(newPos);
            playerThreeRanking = Integer.parseInt(newPos);
        } else if (rankPlayerFour.getText().equals(oldPos)) {
            playerFourRanking = Integer.parseInt(newPos);
            rankPlayerFour.setText(newPos);
        }

    }

    /**
     * show the avalible Players in the Ranking. Well take the limit of the Server
     * and show HBoxs as much as the limit
     */
    private void showAvaliblePlayers() {
        showPlayerOne();
        if (playerService.getServerServices().getAllPlayers().size() == 2) {
            showPlayerTwo();
        } else if (playerService.getServerServices().getAllPlayers().size() == 3) {
            showPlayerTwo();
            showPlayerThree();
        } else if (playerService.getServerServices().getAllPlayers().size() == 4) {
            showPlayerTwo();
            showPlayerThree();
            showPlayerFour();
        }
    }

    private void showPlayerOne() {
        HostHBox.setVisible(true);
        HostLabel.setText(playerService.getPlayer().getPlayerName());
    }

    private void showPlayerTwo() {
        PlayerTwoHBox.setVisible(true);
        hostRankTwo.setVisible(true);
        PlayerTwoLabel.setText(playerService.getServerServices().getAllPlayers().get(1).toString());
    }

    private void showPlayerThree() {
        PlayerThreeHBox.setVisible(true);
        hostRankTwo.setVisible(true);
        hostRankThree.setVisible(true);

        playerTwoRankThree.setVisible(true);
        PlayerThreeLabel.setText(playerService.getServerServices().getAllPlayers().get(2).toString());
    }

    private void showPlayerFour() {
        PlayerFourHBox.setVisible(true);
        hostRankTwo.setVisible(true);
        hostRankThree.setVisible(true);
        hostRankFour.setVisible(true);

        playerTwoRankThree.setVisible(true);
        playerTwoRankFour.setVisible(true);
        playerThreeRankFour.setVisible(true);
        PlayerFourLabel.setText(playerService.getServerServices().getAllPlayers().get(3).toString());
    }

    /** live changings */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (playerService.getTrigger().getIsInLobbySetting()) {
            showZeiger();
            showAvaliblePlayers();
        }
        if (playerService.getTrigger().getCardMenuState() == 0) {
            GuiDesigner.scaling(mainBorderVor);
            GameLogic.getCardspool().clear();
            kartenpoolList.clear();
        } else if (playerService.getTrigger().getCardMenuState() == 1) {
            autoComplete();
            for (int i = 0; i < 5; i++) {
                ImageView image = new ImageView(kartenpoolList.get(i).getImagePath());
                image.setFitHeight(cardHeight);
                image.setFitWidth(cardWidth);
                randomCards1.getChildren().add(image);
            }
            for (int i = 5; i < 10; i++) {
                ImageView image = new ImageView(kartenpoolList.get(i).getImagePath());
                image.setFitHeight(cardHeight);
                image.setFitWidth(cardWidth);
                randomCards2.getChildren().add(image);
            }

            GuiDesigner.scaling(mainBorderRan);
            GameLogic.getCardspool().clear();
            kartenpoolList.clear();
        } else if (playerService.getTrigger().getCardMenuState() == 2) {
            GuiDesigner.scaling(mainBorderSel);
            GameLogic.getCardspool().clear();
            kartenpoolList.clear();
        } else {
            GuiDesigner.scaling(mainBorderC);
        }

    }

    //////// Adding Cards

    /** bei selbstgewählte Karten autocomplete von Karten */
    private void autoComplete() {
        if (autoCompleteList.size() <= 0) {
            setupAutoCompleteList();
        }
        while (kartenpoolList.size() < 10) {
            Random random = new Random();
            int randomCard = random.nextInt(autoCompleteList.size());
            if (!kartenpoolList.contains(autoCompleteList.get(randomCard))) {
                kartenpoolList.add(autoCompleteList.get(randomCard));
            }
        }
    }

    /** autoComplete List */
    private void setupAutoCompleteList() {
        autoCompleteList.add(cardDorf);
        autoCompleteList.add(cardSchmiede);
        autoCompleteList.add(cardHolzfaeller);
        autoCompleteList.add(cardKanzler);
        autoCompleteList.add(cardKapelle);
        autoCompleteList.add(cardAbenteurer);
        autoCompleteList.add(cardLaboratorium);
        autoCompleteList.add(cardBibliothek);
        autoCompleteList.add(cardJahrmarkt);
        autoCompleteList.add(cardMarkt);
        autoCompleteList.add(cardKapelle);
        autoCompleteList.add(cardMiliz);
        autoCompleteList.add(cardBurggraben);
        autoCompleteList.add(cardThronsaal);
        autoCompleteList.add(cardHexe);
        autoCompleteList.add(cardRatsversammlung);
        //autoCompleteList.add(cardDieb);
        autoCompleteList.add(cardWerkstatt);
        autoCompleteList.add(cardUmbau);
        autoCompleteList.add(cardSpion);
        autoCompleteList.add(cardGarten);
        autoCompleteList.add(cardFestmahl);
        autoCompleteList.add(cardGeldverleiher);
        autoCompleteList.add(cardBuerokrat);
        autoCompleteList.add(cardKeller);
        autoCompleteList.add(cardMine);
    }

    /**
     * get an autoCompleted List : used in the default value of kartenpool
     * 
     * @return
     */
    public ArrayList<Cards> getAutoCompleteList() {
        autoComplete();
        return this.kartenpoolList;
    }
}