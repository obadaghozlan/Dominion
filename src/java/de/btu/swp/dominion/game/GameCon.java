package de.btu.swp.dominion.game;

import de.btu.swp.dominion.cards.*;
import de.btu.swp.dominion.network.Packets.*;
import de.btu.swp.dominion.gameLogic.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.layout.BorderPane;

public class GameCon extends LobbyCon implements Initializable {

    public PlayerService playerService = getPlayerService();
    private GameLogic gamelogic = new GameLogic(getPlayerService());
    private LinkedList<Cards> choosenList = new LinkedList<Cards>();
    private int oldValue = 0;

    /** when state is true the player is in game, otherwise he is in spectator */
    private boolean state;
    @FXML
    private Button switchGameSceneTrigger;

    /** log */
    private static ObservableList<String> logMessages = FXCollections.observableArrayList();
    @FXML
    public ListView<String> logListView = new ListView<String>(logMessages);
    @FXML
    private BorderPane mainBorder;
    /** game */
    @FXML
    private VBox playground1;

    /** spectator */
    @FXML
    private VBox playground2;
    @FXML
    private HBox cardpool;
    @FXML
    private HBox kartenInHand;
    @FXML
    private HBox playedCards;
    @FXML
    private HBox trashImageViews;
    @FXML
    private Pane TrashWindow;
    @FXML
    private Slider TrashSlider;
    @FXML
    private HBox poolHBox1;
    @FXML
    private HBox poolHBox2;
    @FXML
    private VBox poolVBox;
    @FXML
    private ImageView anwesenKarte;
    @FXML
    private ImageView herzogtumKarte;
    @FXML
    private ImageView provinzKarte;
    @FXML
    private ImageView goldKarte;
    @FXML
    private ImageView silberKarte;
    @FXML
    private ImageView kupferKarte;
    @FXML
    private ImageView ablage;
    @FXML
    private ImageView greatImageView;
    @FXML
    private Label GeldLabel;
    @FXML
    private Label AttackLabel;
    @FXML
    private Label KaufLabel;
    @FXML
    private ImageView specCard1;
    @FXML
    private ImageView specCard2;
    @FXML
    private ImageView specCard3;
    @FXML
    private ImageView specCard4;
    @FXML
    private ImageView specCard5;
    @FXML
    private ImageView specCard6;
    @FXML
    private ImageView popUpCard1;
    @FXML
    private ImageView popUpCard2;
    @FXML
    private ImageView popUpCard3;
    @FXML
    private ImageView popUpCard4;
    @FXML
    private ImageView popUpCard5;
    @FXML
    private ImageView popUpCard6;

    @FXML
    private VBox leftCorner;
    // popupWindow
    @FXML
    private Pane effectWindow;
    @FXML
    private Label popupMessageLabel;
    @FXML
    private ImageView volumeBtn;
    @FXML
    private Label popUpCardNames1;
    @FXML
    private Label popUpCardNames2;
    @FXML
    private Label popUpCardNames3;
    @FXML
    private Label popUpCardNames4;
    @FXML
    private Label popUpCardNames5;
    @FXML
    private HBox popUpHandCards;
    private double opacity = 0.7;
    Timer timer = new Timer();
    Runnable runUpdate;
    LinkedList<Integer> popUpWindowIndexlist = new LinkedList<Integer>();
    ArrayList<ImageView> effectImages = new ArrayList<ImageView>();

    public PlayerService getPlayerService2() {
        return this.playerService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // send to all client if its your Turn to show that you have the turn by the
        // others in the obber left corner
        if (playerService.getPlayer().getYourTurn()) {
            Packet19TurnZeiger turnZeiger = new Packet19TurnZeiger();
            turnZeiger.playerName = playerService.getPlayer().getPlayerName();
            playerService.getClient().getClient().sendTCP(turnZeiger);
        }

        // log
        logListView.setItems(logMessages);
        logListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> lb, String old_val, String new_val) {
            }
        });

        // for the Volum Btn
        if (!metaData.getSoundOnMeta()) {
            volumeBtn.setImage(musicBox.getVolumOffImg());
        } else {
            volumeBtn.setImage(musicBox.getVolumOnImg());
        }

        GuiDesigner.scaling(mainBorder);

        effectImages.add(popUpCard1);
        effectImages.add(popUpCard2);
        effectImages.add(popUpCard3);
        effectImages.add(popUpCard4);
        effectImages.add(popUpCard5);
        effectImages.add(popUpCard6);

        GameLogic.getCardspool().add(new Anwesen());
        GameLogic.getCardspool().add(new Herzogtum());
        GameLogic.getCardspool().add(new Provinz());
        GameLogic.getCardspool().add(new Gold());
        GameLogic.getCardspool().add(new Silber());
        GameLogic.getCardspool().add(new Kupfer());

        playerService.getPlayer().getStartValue();
        for (int i = 1; i <= 7; i++) {
            Cards kupfer = new Kupfer();
            playerService.getPlayer().getDeck().add(kupfer);
        }
        for (int i = 1; i <= 3; i++) {
            Cards anwesen = new Anwesen();
            playerService.getPlayer().getDeck().add(anwesen);
            playerService.getPlayer().setPoints(1);
        }
        gamelogic.initHand();
        initPool();
        setNameGame();
        initSliderListener();

        if (playerService.getPlayer().getYourTurn() == true) {
            startGame();
        } else {
            startSpectator();
        }

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (playerService.getTrigger().getIsInGame()) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            update();
                        }
                    });
                }
            }
        }, 1000, 200);
    }

    /** shows pool Cards from list */
    public void initPool() {
        Image image;
        // set every image in variable pool
        for (int i = 0; i < 10; i++) {
            image = new Image(GameLogic.getCardspool().get(i).getImagePath());
            if (i < 5) {
                ((ImageView) ((Group) poolHBox1.getChildren().get(i)).getChildren().get(0)).setImage(image);
            } else {
                ((ImageView) ((Group) poolHBox2.getChildren().get(i - 5)).getChildren().get(0)).setImage(image);
            }
        }

        GameLogic.getCardnumber().clear();
        for (int i = 0; i < 10; i++) {
            GameLogic.getCardnumber().add(10); // 1 - 10 Poolnumbers
        }
        GameLogic.getCardnumber().add(8); // anwesen
        GameLogic.getCardnumber().add(8); // herzogtum
        GameLogic.getCardnumber().add(8); // provinz
        GameLogic.getCardnumber().add(30); // gold
        GameLogic.getCardnumber().add(40); // silber
        GameLogic.getCardnumber().add(46); // kupfer
        GameLogic.getCardnumber().add(30); // Flüche

        // check the player and put in the right cardnumbers for the playersize
        int i = playerService.getPlayers().size() + 1;
        if (i == 2) {
            GameLogic.getCardnumber().set(10, 18);
            GameLogic.getCardnumber().set(11, 8);
            GameLogic.getCardnumber().set(12, 8);
            GameLogic.getCardnumber().set(15, 46);
            GameLogic.getCardnumber().set(16, 10);
        } else if (i == 3) {
            GameLogic.getCardnumber().set(10, 15);
            GameLogic.getCardnumber().set(11, 12);
            GameLogic.getCardnumber().set(12, 12);
            GameLogic.getCardnumber().set(15, 39);
            GameLogic.getCardnumber().set(16, 20);
        } else if (i == 4) {
            GameLogic.getCardnumber().set(10, 12);
            GameLogic.getCardnumber().set(11, 12);
            GameLogic.getCardnumber().set(12, 12);
            GameLogic.getCardnumber().set(15, 32);
            GameLogic.getCardnumber().set(16, 30);
        }
    }

    @FXML
    void hover(MouseEvent event) {
        Image image;
        if (event.getTarget() == anwesenKarte) {
            image = new Image("/src/main/resources/basis/Anwesen.png");
        } else if (event.getTarget() == herzogtumKarte) {
            image = new Image("/src/main/resources/basis/Herzogtum.png");
        } else if (event.getTarget() == provinzKarte) {
            image = new Image("/src/main/resources/basis/Provinz.png");
        } else if (event.getTarget() == kupferKarte) {
            image = new Image("/src/main/resources/basis/Kupfer.png");
        } else if (event.getTarget() == silberKarte) {
            image = new Image("/src/main/resources/basis/Silber.png");
        } else if (event.getTarget() == goldKarte) {
            image = new Image("/src/main/resources/basis/Gold.png");
        } else {
            image = ((ImageView) event.getSource()).getImage();
        }
        greatImageView.setImage(image);
        greatImageView.setVisible(true);
    }

    @FXML
    void dehover(MouseEvent event) {
        greatImageView.setVisible(false);
    }

    @FXML
    void initSliderListener() {
        TrashSlider.valueProperty().addListener(new ChangeListener<Number>() {

            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                TrashSlider.setValue(newValue.intValue());
                for (int i = newValue.intValue(); i < newValue.intValue() + 5; i++) {
                    Node newImageView = trashImageViews.getChildren().get(i - newValue.intValue());
                    if (newImageView instanceof ImageView) {
                        ((ImageView) newImageView)
                                .setImage(new Image(gamelogic.getTrashCardsList().get(i).getImagePath()));
                    }
                }
            }
        });
    }

    /** calls all update methods */
    public void update() {
        if (!(effectWindow.isVisible() || gamelogic.getInCardLaunch())) {
            updateEffectWindow();
        }
        updateNewSpectator();
        updatePlayedCards();
        if (playerService.getPlayer().getHand().size() < 8) {
            updateHand();
        } else {
            updateHand2();
        }
        updateCountersLabels();
        updateDiscardDeck();
        updatePool();
        updateLeftCorner();
        updateBuyable();
        updateScene();
        autoChangeScene();
        updatelog();

    }

    private void updatelog() {
        // always scroll to the end
        int NewLogIndex = logMessages.size();
        if (playerService.getTrigger().getHasNewLogMessage()) {
            logListView.scrollTo(NewLogIndex);
            playerService.getTrigger().setHasNewLogMessage(false);
            ;
        }
    }

    private void updateScene() {
        if (!playerService.getPlayer().getEnd()) {
            if (!gamelogic.getMilizActive() || !gamelogic.getBuerokratActive()) {
                if (playerService.getPlayer().getYourTurn()) {
                    startGame();
                    playerService.getPlayer().setYourTurn(false);
                }
            }
        }
    }

    private void updateNewSpectator() {
        if (playerService.getPlayer().getSpecCards().size() > 5) {
            specCard6.setImage(new Image(playerService.getPlayer().getSpecCards().get(4).getImagePath()));
            specCard6.setVisible(true);
        }
        if (playerService.getPlayer().getSpecCards().size() > 4) {
            specCard5.setImage(new Image(playerService.getPlayer().getSpecCards().get(4).getImagePath()));
            specCard5.setVisible(true);
        }
        if (playerService.getPlayer().getSpecCards().size() > 3) {
            specCard4.setImage(new Image(playerService.getPlayer().getSpecCards().get(3).getImagePath()));
            specCard4.setVisible(true);
        }
        if (playerService.getPlayer().getSpecCards().size() > 2) {
            specCard3.setImage(new Image(playerService.getPlayer().getSpecCards().get(2).getImagePath()));
            specCard3.setVisible(true);
        }
        if (playerService.getPlayer().getSpecCards().size() > 1) {
            specCard2.setImage(new Image(playerService.getPlayer().getSpecCards().get(1).getImagePath()));
            specCard2.setVisible(true);
        }
        if (playerService.getPlayer().getSpecCards().size() > 0) {
            specCard1.setImage(new Image(playerService.getPlayer().getSpecCards().get(0).getImagePath()));
            specCard1.setVisible(true);
        }
        if (playerService.getPlayer().getSpecCards().size() == 0) {
            specCard1.setVisible(false);
            specCard2.setVisible(false);
            specCard3.setVisible(false);
            specCard4.setVisible(false);
            specCard5.setVisible(false);
            specCard6.setVisible(false);
        }
    }

    public void updateBuyable() {
        ColorAdjust grey = new ColorAdjust();
        ColorAdjust normal = new ColorAdjust();
        grey.setSaturation(-1);
        for (int i = 0; i < GameLogic.getCardspool().size(); i++) {
            if (playerService.getPlayer().getMoney() < GameLogic.getCardspool().get(i).getCost()
                    || GameLogic.getCardnumber().get(i) == 0 || playerService.getPlayer().getBuys() < 1) {
                if (i < 5) {
                    ((ImageView) ((Group) poolHBox1.getChildren().get(i)).getChildren().get(0)).setEffect(grey);
                } else if (i < 10) {
                    ((ImageView) ((Group) poolHBox2.getChildren().get(i - 5)).getChildren().get(0)).setEffect(grey);
                } else {
                    ((ImageView) ((Group) poolVBox.getChildren().get(i - 10)).getChildren().get(0)).setEffect(grey);
                }
            } else {
                if (i < 5) {
                    ((ImageView) ((Group) poolHBox1.getChildren().get(i)).getChildren().get(0)).setEffect(normal);
                } else if (i < 10) {
                    ((ImageView) ((Group) poolHBox2.getChildren().get(i - 5)).getChildren().get(0)).setEffect(normal);
                } else {
                    ((ImageView) ((Group) poolVBox.getChildren().get(i - 10)).getChildren().get(0)).setEffect(normal);
                }
            }
        }
    }

    /** setup Labels for the Attack/Geld/Kauf Counters in Game */
    private void updateCountersLabels() {
        if (playerService.getPlayer().getMoney() >= 10) {
            GeldLabel.setFont(new Font(3.0));
        } else {
            GeldLabel.setFont(new Font(20.0));
        }
        GeldLabel.setText("G " + playerService.getPlayer().getMoney());
        AttackLabel.setText("A " + playerService.getPlayer().getAction());
        KaufLabel.setText("K " + playerService.getPlayer().getBuys());
    }

    public void createCard(int i) {
        Image url = new Image(playerService.getPlayer().getCardImage(i));
        ImageView newImage = new ImageView(url);
        newImage.setFitHeight(208);
        newImage.setFitWidth(120);

        newImage.setOnMouseEntered((e) -> {
            greatImageView.setVisible(true);
            greatImageView.setImage(url);
        });

        newImage.setOnMouseExited((e) -> {
            greatImageView.setVisible(false);
        });

        newImage.setOnMouseClicked((e) -> {

            if (state == true && playerService.getPlayer().getAction() >= 1) {
                for (int x = 0; x < kartenInHand.getChildren().size(); x++) {
                    if (kartenInHand.getChildren().get(x) == ((ImageView) e.getSource())) {
                        if (playerService.getPlayer().getHand().get(x).getCardType().equals("Aktion")) {
                            gamelogic.playClickedCard(x, e);
                        }
                    }
                }
            }
        });

        kartenInHand.getChildren().add(newImage);
    }

    /** updates the handcards on Gui */
    public void updateHand() {
        while (playerService.getPlayer().getHand().size() < kartenInHand.getChildren().size()) {
            kartenInHand.getChildren().clear();
        }

        for (int i = 0; i < playerService.getPlayer().getHand().size(); i++) {
            try {
                if (kartenInHand.getChildren().get(0) instanceof Group) {
                    kartenInHand.getChildren().clear();
                }
                ImageView imageView = (ImageView) kartenInHand.getChildren().get(i);
                Image newImage = new Image(playerService.getPlayer().getCardImage(i));
                imageView.setImage(newImage);
            } catch (IndexOutOfBoundsException e) {
                createCard(i);
            }
        }

        Packet09HandCardNumber card = new Packet09HandCardNumber();
        card.NumberOfCardInHand = playerService.getPlayer().getHand().size();
        card.playerName = playerService.getPlayer().getPlayerName();
        playerService.getClient().getClient().sendTCP(card);
    }

    public Group createCard(int i, LinkedList<Cards> list) {
        Image url = new Image(list.get(i).getImagePath());
        ImageView newImage = new ImageView(url);
        Group group = new Group();
        StackPane stack = new StackPane();
        Rectangle rect = new Rectangle();
        Label label = new Label();
        newImage.setFitHeight(208);
        newImage.setFitWidth(120);
        newImage.setOnMouseEntered((e) -> {
            greatImageView.setVisible(true);
            greatImageView.setImage(url);
        });
        newImage.setOnMouseExited((e) -> {
            greatImageView.setVisible(false);
        });
        newImage.setOnMouseClicked((e) -> {
            if (state == true && playerService.getPlayer().getAction() >= 1) {
                for (int x = 0; x < kartenInHand.getChildren().size(); x++) {
                    ImageView imgv = ((ImageView) ((Group) kartenInHand.getChildren().get(x)).getChildren().get(0));
                    // hier geht er nicht rein
                    if (imgv == ((ImageView) e.getSource())) {
                        if (list.get(x).getCardType().equals("Aktion")) {
                            playerService.getPlayer().getPlayedCardsList().add(list.get(x));
                            gamelogic.sendToLog(list.get(x).getName(), "playCard");
                            gamelogic.removeFromHand(list.get(x), e);
                            list.remove(x);
                        }
                    }
                }
            }
        });
        stack.setPrefHeight(40.0);
        stack.setPrefWidth(49.0);
        rect.setWidth(25.0);
        rect.setHeight(26.0);
        rect.setFill(Color.RED);
        label.setPrefHeight(25.6);
        label.setPrefWidth(19.2);
        String name = list.get(i).getName();
        label.setText(Integer.toString(gamelogic.numberOfCardsinHand(name)));
        label.setTextFill(Color.WHITE);
        label.setFont(new Font(16.0));
        stack.getChildren().add(rect);
        stack.getChildren().add(label);
        group.getChildren().add(newImage);
        group.getChildren().add(stack);
        return group;
    }

    public void updateHand2() {
        LinkedList<Cards> list = new LinkedList<Cards>(gamelogic.updateList2());
        if (list.size() < kartenInHand.getChildren().size()) {
            kartenInHand.getChildren().clear();
        }
        for (int i = 0; i < list.size(); i++) {
            try {
                if (kartenInHand.getChildren().get(0) instanceof ImageView) {
                    kartenInHand.getChildren().clear();
                }
                Group group = (Group) kartenInHand.getChildren().get(i);
                Image newImage = new Image(list.get(i).getImagePath());
                ImageView imageView = (ImageView) group.getChildren().get(0);
                String updatedText = Integer.toString(gamelogic.numberOfCardsinHand(list.get(i).getName()));
                Label label = (Label) ((StackPane) group.getChildren().get(1)).getChildren().get(1);
                label.setText(updatedText);
                imageView.setImage(newImage);
            } catch (IndexOutOfBoundsException e) {
                kartenInHand.getChildren().add(createCard(i, list));
            }
        }

        Packet09HandCardNumber card = new Packet09HandCardNumber();
        card.NumberOfCardInHand = playerService.getPlayer().getHand().size();
        card.playerName = playerService.getPlayer().getPlayerName();
        playerService.getClient().getClient().sendTCP(card);
    }

    /** updates played Cards in Gui */
    public void updatePlayedCards() {
        playedCards.getChildren().clear();
        LinkedList<Cards> list = new LinkedList<Cards>(gamelogic.updateList());
        for (int i = 0; i < list.size(); i++) {
            Image url = new Image(list.get(i).getImagePath());
            ImageView newImage = new ImageView(url);
            Group group = new Group();
            StackPane stack = new StackPane();
            Rectangle rect = new Rectangle();
            Label label = new Label();
            newImage.setFitHeight(208);
            newImage.setFitWidth(120);
            stack.setPrefHeight(40.0);
            stack.setPrefHeight(49.0);
            rect.setWidth(25.0);
            rect.setHeight(26.0);
            rect.setFill(Color.RED);
            label.setPrefHeight(25.6);
            label.setPrefWidth(19.2);
            String path = list.get(i).getImagePath();
            label.setText(Integer.toString(gamelogic.numberOfCards(path) - 1));
            label.setTextFill(Color.WHITE);
            label.setFont(new Font(16.0));
            stack.getChildren().add(rect);
            stack.getChildren().add(label);
            group.getChildren().add(newImage);
            group.getChildren().add(stack);
            playedCards.getChildren().add(group);
        }
    }

    public void updateDiscardDeck() {
        int lastcard = playerService.getPlayer().getDiscardDeck().size() - 1;
        if (playerService.getPlayer().getDiscardDeck().size() > 0) {
            ablage.setImage(new Image(playerService.getPlayer().getDiscardCardImage(lastcard)));
            Packet08DiscardDeck card = new Packet08DiscardDeck();
            card.cardName = playerService.getPlayer().getDiscardDeck().getLast().getImagePath();
            card.playerName = playerService.getPlayer().getPlayerName();
            playerService.getClient().getClient().sendTCP(card);
        }
        if (playerService.getPlayer().getDiscardDeck().size() == 0) {
            ablage.setImage(new Image("/src/main/resources/backgrounds/Blanko.png"));
            Packet08DiscardDeck card = new Packet08DiscardDeck();
            card.cardName = "/src/main/resources/backgrounds/Blanko.png";
            card.playerName = playerService.getPlayer().getPlayerName();
            playerService.getClient().getClient().sendTCP(card);
        }
    }

    public void updateLeftCorner() {
        String currentPlayer = playerService.getTrigger().getCurrentPlayerName();
        for (int i = 0; i < playerService.getPlayers().size(); i++) {
            OtherPlayers other = playerService.getPlayers().get(i);
            Group group = (Group) leftCorner.getChildren().get(i);
            // updated number of Cards in Hand
            Label otherHand = (Label) ((StackPane) group.getChildren().get(2)).getChildren().get(1);
            otherHand.setText(other.getNumberCardsInHand() + "");
            // updated discard Card
            ImageView discard = (ImageView) ((HBox) ((VBox) group.getChildren().get(1)).getChildren().get(0))
                    .getChildren().get(0);
            discard.setImage(new Image(other.getFirstDiscardCard()));
            // updated boxes
            group.getChildren().get(0).setVisible(other.getPlayerName().equals(currentPlayer));
        }
    }

    public void updatePool() {
        StackPane pane;
        for (int i = 0; i < GameLogic.getCardspool().size(); i++) {
            if (i < 5) {
                pane = ((StackPane) ((Group) poolHBox1.getChildren().get(i)).getChildren().get(1));
            } else if (i < 10) {
                pane = ((StackPane) ((Group) poolHBox2.getChildren().get(i - 5)).getChildren().get(1));
            } else {
                pane = ((StackPane) ((Group) poolVBox.getChildren().get(i - 10)).getChildren().get(1));
            }
            Label label = (Label) pane.getChildren().get(1);
            label.setText(GameLogic.getCardnumber().get(i).toString());
        }
    }

    /**
     * when the button is clicked the player end his turn and the spectatorscene
     * appear
     */
    @FXML
    void btnCleaningPhaseClicked(MouseEvent event) throws IOException {
        if (!effectWindow.isVisible()) {
            kartenInHand.getChildren().clear();
            // log
            gamelogic.sendToLog("null", "endTurn");
            // close all popups Window
            playerService.getTrigger().setThronsaalWaiting(0);
            TrashWindow.setVisible(false);
            playerService.getPlayer().getStartValue();
            // füge hand zum Ablagestapel
            playerService.getPlayer().addDiscardDeck(playerService.getPlayer().getHand());
            playerService.getPlayer().setHand(new LinkedList<Cards>());
            // füge gespielte Karten zum Ablagestapel
            playerService.getPlayer().addDiscardDeck(playerService.getPlayer().getPlayedCardsList());
            playerService.getPlayer().setPlayedCardsList(new LinkedList<Cards>());
            playerService.drawCard();
            playerService.drawCard();
            playerService.drawCard();
            playerService.drawCard();
            playerService.drawCard();
            playedCards.getChildren().clear();

            playerService.getPlayer().getSpecCards().clear();
            // when the player is host, then he can change the list in serverProgram
            if (playerService.getPlayer().getHost() == true) {
                gamelogic.changeTurn(playerService.getPlayer().getPlayerName());

            } else {
                // otherwise the player send the next player to the host
                Packet06EndTurn player = new Packet06EndTurn();
                player.name = playerService.getPlayer().getPlayerName();
                playerService.getClient().getClient().sendTCP(player);
            }
            startSpectator();
        }
    }

    /** in develop setName method */

    private void autoChangeScene() {
        switchGameSceneTrigger.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                changeScene(event);
            }
        });
        switchGameSceneTrigger.fire();
    }

    public void changeScene(ActionEvent event) {
        if (playerService.getPlayer().getEnd() == true) {
            musicBox.getBackgroundAudio().stop();
            gamelogic.sendpoints();
            timer.cancel();
            playerService.getTrigger().setIsInGame(false);
            try {

                Parent root = FXMLLoader.load(getClass().getResource("/src/main/resources/FXML/GameEnd.fxml"));
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                if (window.getScene() == null) {
                    Scene sc = new Scene(root, GuiDesigner.getWidth(), GuiDesigner.getHeight());
                    window.setScene(sc);
                } else {
                    window.getScene().setRoot(root);
                }
            } catch (IOException e) {
                System.err.println("err GameCon 02: Fehler bei Siegerbild");
                e.printStackTrace();
            }
        } else {
            if (!gamelogic.getMilizActive() || !gamelogic.getBuerokratActive()) {
                if (playerService.getPlayer().getYourTurn()) {
                    startGame();
                    playerService.getPlayer().setYourTurn(false);
                }
            }
            if (gamelogic.getBuerokratLoserActive()) {

                if (effectWindow.isVisible() && popUpWindowIndexlist.size() == 0) {
                    effectWindow.setVisible(false);
                }
                try {
                    Parent root = FXMLLoader
                            .load(getClass().getResource("/src/main/resources/FXML/viewCardsWindow.fxml"));
                    Stage chat = new Stage();
                    chat.setScene(new Scene(root));
                    chat.initStyle(StageStyle.UNDECORATED);
                    chat.show();
                } catch (IOException e) {
                    System.out.println("err Game 4711: Fehler beim Laden vom ViewingWindow");
                    e.printStackTrace();
                }
            }
        }
        /** close if he is not connected */
        if (!playerService.getClient().getClient().isConnected()) {
            try {
                Runtime.getRuntime().exec("cmd /c mvn exec:java");
            } catch (IOException e) {
                System.out.println("stating präsentation relaunch");
            }
            try {
                Runtime.getRuntime().exec("java -jar dominion-0.0.1-shaded.jar");
            } catch (IOException e) {
                System.out.println("faild");
            }
            System.exit(0);
        }
    }

    public void startGame() {
        state = true;
        playground2.setVisible(false);
        playground1.setVisible(true);
        playground1.getChildren().get(1).setVisible(true);
        cardpool.setVisible(true);
    }

    public void startSpectator() {
        state = false;
        playground2.setVisible(true);
        playground1.setVisible(false);
        cardpool.setVisible(false);
    }

    @FXML
    void btnChatClicked(MouseEvent event) throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/src/main/resources/FXML/Chat.fxml"));
            Stage chat = new Stage();
            chat.setScene(new Scene(root));
            // make the stage movable
            musicBox.makeStageMovable(root, chat);
            chat.initStyle(StageStyle.UNDECORATED);
            chat.show();
        } catch (IOException e) {
            System.out.println("err Game 11: Fehler beim Laden vom Chat");
            e.printStackTrace();
        }
    }

    @FXML
    void btnExitClicked(MouseEvent event) throws IOException {
        ButtonType exit = new ButtonType("verlassen");
        ButtonType stay = new ButtonType("bleiben");
        Alert closeConfirmation = new Alert(AlertType.CONFIRMATION, "Sind Sie sicher, dass Sie das Spiel verlassen?",
                exit, stay);
        closeConfirmation.setTitle("Bestätigung");
        // add style to the alert
        musicBox.alertStyler(closeConfirmation);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        closeConfirmation.initOwner(window);
        Optional<ButtonType> result = closeConfirmation.showAndWait();
        if (result.get() == exit) {
            if (playerService.getPlayer().getHost()) {
                playerService.getPlayer().setShutDownServer(true);
                playerService.getServerServices().stopServer();
            } else {
                playerService.getClient().getClient().close();
            }
        }
    }

    @FXML
    void btnTrashClicked(MouseEvent event) {
        if (TrashWindow.isVisible()) {
            TrashWindow.setVisible(false);
            if (playground2.isVisible()) {
                playground1.getChildren().get(1).setVisible(true);
                playground1.setVisible(false);
            }
        } else {
            TrashWindow.setVisible(true);
            if (playground2.isVisible()) {
                playground2.setVisible(true);
                playground1.setVisible(true);
                playground1.getChildren().get(1).setVisible(false);
            }

            if (gamelogic.getTrashCardsList().size() < 6) {
                for (int i = 0; i < gamelogic.getTrashCardsList().size(); i++) {
                    Node newImageView = trashImageViews.getChildren().get(i);
                    if (newImageView instanceof ImageView) {
                        ((ImageView) newImageView)
                                .setImage(new Image(gamelogic.getTrashCardsList().get(i).getImagePath()));
                    }
                }
                TrashSlider.setVisible(false);
            }
            if (gamelogic.getTrashCardsList().size() > 5) {
                for (int i = 0; i < 5; i++) {
                    Node newImageView = trashImageViews.getChildren().get(i);
                    if (newImageView instanceof ImageView) {
                        ((ImageView) newImageView)
                                .setImage(new Image(gamelogic.getTrashCardsList().get(i).getImagePath()));
                    }
                }
                double maxVisiblecards = 5;
                TrashSlider.setVisible(true);
                TrashSlider.setMin(0);
                TrashSlider.setMax(((double) gamelogic.getTrashCardsList().size()) - maxVisiblecards);
                TrashSlider.setValue(0);
            }
        }
    }

    /**
     * here we have to ask which card is on the pool, for set the
     * image(ablagestapel) and get the price(playMoneyonBoard) TODO: when we can buy
     * other cards we can do the playerservice lines in gamelogic with parameters
     */
    @FXML
    void btnCardClicked(MouseEvent event) {
        playerService.getPlayer().setAction(-playerService.getPlayer().getAction());
        Cards card = new Blanko();
        ImageView clickedCard = ((ImageView) event.getSource());
        for (int i = 0; i < GameLogic.getCardspool().size(); i++) {
            if (i < 5) {
                if (clickedCard == ((ImageView) ((Group) poolHBox1.getChildren().get(i)).getChildren().get(0))) {
                    card = GameLogic.getCardspool().get(i);
                }
            } else if (i < 10) {
                if (clickedCard == ((ImageView) ((Group) poolHBox2.getChildren().get(i - 5)).getChildren().get(0))) {
                    card = GameLogic.getCardspool().get(i);
                }
            } else {
                if (clickedCard == ((ImageView) ((Group) poolVBox.getChildren().get(i - 10)).getChildren().get(0))) {
                    card = GameLogic.getCardspool().get(i);
                }
            }
        }

        Boolean buy = (playerService.getPlayer().getMoney() >= card.getCost())
                && (playerService.getPlayer().getBuys() >= 1);

        if (buy) {
            // change the number of Cards in pool
            StackPane redRect = (StackPane) ((Group) clickedCard.getParent()).getChildren().get(1);
            Label label = ((Label) redRect.getChildren().get(1));
            if (label.getText().equals("1")) {
                clickedCard.setDisable(true);
                ColorAdjust grey = new ColorAdjust();
                grey.setSaturation(-1);
                clickedCard.setEffect(grey);
                // if Provinz is empty the game ends
                if (card.getName().equals("Provinz")) {
                    Packet12GameEnd end = new Packet12GameEnd();
                    end.end = true;
                    playerService.getClient().getClient().sendTCP(end);
                }
            } else if (label.getText().equals("0")) {
                clickedCard.setDisable(true);
                return;
            }
            // buy Card
            gamelogic.buyCard(card);
            // set image on discard Deck
            ablage.setImage(new Image(card.getImagePath()));
            // play Money and updates everything
            gamelogic.playMoneyOnBoard(card.getCost());
            playerService.getPlayer().setAction(-playerService.getPlayer().getAction());
            gamelogic.checkIfGameEnds();
        }
        updateHand();
    }

    /**
     * shows the name of the player under his cards and when the prev player end his
     * turn and you move your Mouse the scene changes
     */
    public void setNameGame() {
        for (int i = 0; i < playerService.getPlayers().size(); i++) {
            OtherPlayers other = playerService.getPlayers().get(i);
            Group group = (Group) leftCorner.getChildren().get(i);
            Label playerLabel = (Label) ((VBox) group.getChildren().get(1)).getChildren().get(1);
            group.setVisible(true);
            playerLabel.setText(other.getPlayerName());
        }

        Packet08DiscardDeck card1 = new Packet08DiscardDeck();
        card1.cardName = "/src/main/resources/backgrounds/Blanko.png";
        card1.playerName = playerService.getPlayer().getPlayerName();
        playerService.getClient().getClient().sendTCP(card1);

        Packet09HandCardNumber card2 = new Packet09HandCardNumber();
        card2.NumberOfCardInHand = kartenInHand.getChildren().size();
        card2.playerName = playerService.getPlayer().getPlayerName();
        playerService.getClient().getClient().sendTCP(card2);

        Packet21FirstDeckCard carddeck = new Packet21FirstDeckCard();
        carddeck.cardname = playerService.getPlayer().getDeck().get(0).getName();
        carddeck.playername = playerService.getPlayer().getPlayerName();
        playerService.getClient().getClient().sendTCP(carddeck);
    }

    /** music */
    @FXML
    void volumeHandler(MouseEvent event) {
        if (musicBox.getBackgroundAudio().isPlaying()) {
            musicBox.getBackgroundAudio().stop();
            metaData.setSoundOnMeta(false);
            volumeBtn.setImage(musicBox.getVolumOffImg());
        } else {
            metaData.setSoundOnMeta(true);
            volumeBtn.setImage(musicBox.getVolumOnImg());
            musicBox.getBackgroundAudio().setVolume(metaData.getVolumMeta());
            musicBox.getBackgroundAudio().play();
        }
    }

    /** log */
    public void addTextToLogList(String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                playerService.getTrigger().setHasNewLogMessage(true);
                logMessages.add(message);
            }
        });
    }

    /////////////////////////// Effectwindow ///////////////////////

    /** well handle the confirm Btn of the Popupwindow */
    @FXML
    void confirmPopUPHandler(MouseEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (popUpWindowIndexlist.size() != 0) {
            if (gamelogic.getkapelleActive()) {
                // sort the list of the deleted cards (avoid exceptions)
                Collections.sort(popUpWindowIndexlist, Collections.reverseOrder());
                for (Integer index : popUpWindowIndexlist) {
                    gamelogic.sendCardToTrash(index);
                }
                popUpWindowIndexlist.clear();
                gamelogic.setkapelleActive(false);
                closeEffectWindow();
                if (gamelogic.getThronsaalActive()) {
                    gamelogic.setThronsaalActive(false);
                    gamelogic.launchEffect(event);
                }

            } else if (gamelogic.getSpionActive()) {
                addCardsSpion();
                closeEffectWindow();
                gamelogic.setSpionActive(false);
                resetLabels();
                if (gamelogic.getThronsaalActive()) {
                    gamelogic.setThronsaalActive(false);
                    gamelogic.launchEffect(event);
                }

            } else if (gamelogic.getMilizActive()) {
                if (playerService.getPlayer().getHand().size() == popUpWindowIndexlist.size() + 3) {
                    Collections.sort(popUpWindowIndexlist);
                    for (int i = popUpWindowIndexlist.size() - 1; i >= 0; i--) {
                        int index = popUpWindowIndexlist.get(i);
                        Cards card = playerService.getPlayer().getHand().get(index);
                        if (card.getCardType() == "Geld") {
                            playerService.getPlayer().setMoney(-card.getPoints());
                        }
                        gamelogic.discardCardfromHand(index);
                    }
                    playground2.setVisible(true);
                    playground1.getChildren().get(1).setVisible(true);
                    playground1.setVisible(false);
                    GameLogic.setMilizActive(false);
                    closeEffectWindow();
                    if (gamelogic.getThronsaalActive()) {
                        gamelogic.setThronsaalActive(false);
                        gamelogic.launchEffect(event);
                    }

                } else {
                    alert("Warnung", "Es wurden zu wenig Karten ausgewählt!", window);
                }
            } else if (gamelogic.getDiebActive()) {
                if (noMoneyCards()) {
                    GameLogic.setDiebActive(false);
                    closeEffectWindow();
                    popUpWindowIndexlist.clear();
                } else {
                    GameLogic.setDiebActive(false);
                    showAllPopUpCards2();
                    clearAllPopUpCards();
                    effectWindow.setVisible(false);
                    GameLogic.setDiebActive2(true);
                    updateEffectWindow();
                    setDisablePopUp2();
                    popUpWindowIndexlist.clear();
                }
            } else if (gamelogic.getDiebActive2()) {
                addCardsDieb();
                trashCards();
                GameLogic.setDiebActive2(false);
                closeEffectWindow();
                setImagesVisible2();
                resetLabels();
                if (gamelogic.getThronsaalActive()) {
                    gamelogic.setThronsaalActive(false);
                    gamelogic.launchEffect(event);
                }

            } else if (gamelogic.getUmbauActive()) {
                int index = popUpWindowIndexlist.getFirst();
                oldValue = playerService.getPlayer().getHand().get(index).getCost();
                gamelogic.sendCardToTrash(index);
                gamelogic.setUmbauActive(false);
                closeEffectWindow();
                gamelogic.setUmbauActive2(true);

            } else if (gamelogic.getKellerActive()) {
                Collections.sort(popUpWindowIndexlist);
                for (int i = popUpWindowIndexlist.size() - 1; i >= 0; i--) {
                    int index = popUpWindowIndexlist.get(i);
                    Cards card = playerService.getPlayer().getHand().get(index);
                    if (card.getCardType() == "Geld") {
                        playerService.getPlayer().setMoney(-card.getPoints());
                    }
                    gamelogic.discardCardfromHand(index);
                    playerService.drawCard();
                }
                gamelogic.setKellerActive(false);
                closeEffectWindow();
                if (gamelogic.getThronsaalActive()) {
                    gamelogic.setThronsaalActive(false);
                    gamelogic.launchEffect(event);
                }

            } else if (gamelogic.getUmbauActive2()) {
                int choosenCard = popUpWindowIndexlist.getFirst();
                gamelogic.takeCard(choosenList.get(choosenCard));
                gamelogic.setUmbauActive2(false);
                closeEffectWindow();
                if (gamelogic.getThronsaalActive()) {
                    gamelogic.setThronsaalActive(false);
                    gamelogic.launchEffect(event);
                }

            } else if (gamelogic.getWerkstattActive()) {
                int choosenCard = popUpWindowIndexlist.getFirst();
                gamelogic.takeCard(choosenList.get(choosenCard));
                gamelogic.setWerkstattActive(false);
                closeEffectWindow();
                if (gamelogic.getThronsaalActive()) {
                    gamelogic.setThronsaalActive(false);
                    gamelogic.launchEffect(event);
                }

            } else if (gamelogic.getFestmahlActive()) {
                int choosenCard = popUpWindowIndexlist.getFirst();
                gamelogic.takeCard(choosenList.get(choosenCard));
                gamelogic.setFestmahlActive(false);
                closeEffectWindow();
                if (gamelogic.getThronsaalActive()) {
                    gamelogic.setThronsaalActive(false);
                    gamelogic.launchEffect(event);
                }

            } else if (gamelogic.getBuerokratActive()) {
                // send the chosen card to the deck
                int j = -1;
                for (int i = 0; i < playerService.getPlayer().getHand().size(); i++) {
                    if (playerService.getPlayer().getHand().get(i).getCardType().equals("Punkte")) {
                        j++;
                        if (j == popUpWindowIndexlist.getFirst()) {
                            j = i;
                            break;
                        }
                    }
                }
                playerService.getPlayer().getDeck().addFirst(playerService.getPlayer().getHand().get(j));
                // delete this card from hand
                playerService.getPlayer().getHand().remove(j);
                // reset
                GameLogic.setBuerokratActive(false);
                closeEffectWindow();
                if (playerService.getTrigger().getBuerokratWaiting()) {
                    Boolean hasPoints = false;
                    for (Cards handCard : playerService.getPlayer().getHand()) {
                        if (handCard.getCardType().equals("Punkte")) {
                            hasPoints = true;
                        }
                    }
                    // if the player have points cards
                    if (hasPoints) {
                        GameLogic.setBuerokratActive(true);
                    } else {
                        // the player have no Points (show his cards)
                        Packet30BuerokratShowTheCards showMyCards = new Packet30BuerokratShowTheCards();
                        for (Cards card : playerService.getPlayer().getHand()) {
                            showMyCards.loserHand = card.getName();
                            showMyCards.loserName = playerService.getPlayer().getPlayerName();
                            playerService.getClient().getClient().sendTCP(showMyCards);
                        }
                    }
                }

            } else if (gamelogic.getMineActive()) {
                int j = -1;
                for (int i = 0; i < playerService.getPlayer().getHand().size(); i++) {
                    if (playerService.getPlayer().getHand().get(i).getCardType().equals("Geld")) {
                        j++;
                        if (j == popUpWindowIndexlist.getFirst()) {
                            j = i;
                            break;
                        }
                    }
                }
                gamelogic.setMineActive(false);
                closeEffectWindow();
                if (playerService.getPlayer().getHand().get(j).getName().equals("Kupfer")) {
                    gamelogic.sendCardToTrash(j);
                    playerService.getPlayer().getHand().add(j, new Silber());
                    Packet14Card2 boughtCard = new Packet14Card2();
                    boughtCard.name = "Silber";
                    playerService.getClient().getClient().sendTCP(boughtCard);
                    playerService.getPlayer().setMoney(2);
                } else {
                    gamelogic.sendCardToTrash(j);
                    playerService.getPlayer().getHand().set(j, new Gold());
                    Packet14Card2 boughtCard = new Packet14Card2();
                    boughtCard.name = "Gold";
                    playerService.getClient().getClient().sendTCP(boughtCard);
                    playerService.getPlayer().setMoney(3);
                }
                if (gamelogic.getThronsaalActive()) {
                    gamelogic.setThronsaalActive(false);
                    gamelogic.launchEffect(event);
                }
                update();

            } else if (gamelogic.getThronsaalActive()) {
                int j = -1;
                for (int i = 0; i < playerService.getPlayer().getHand().size(); i++) {
                    if (playerService.getPlayer().getHand().get(i).getCardType().equals("Aktion")) {
                        j++;
                        if (j == popUpWindowIndexlist.getFirst()) {
                            j = i;
                            break;
                        }
                    }
                }
                closeEffectWindow();
                playerService.getPlayer().setAction(2);
                gamelogic.playClickedCard(j, event);

            }
        } else {
            // allow no selection in kapelle
            if (gamelogic.getkapelleActive()) {
                gamelogic.setkapelleActive(false);
                closeEffectWindow();
            } else {
                alert("Warnung", "Es wurden zu wenig Karten ausgewählt!", window);
            }
        }
    }

    public Cards getCardFromPool(String url) {
        for (int i = 0; i < GameLogic.getCardspool().size(); i++) {
            if (url.equals(GameLogic.getCardspool().get(i).getImagePath())) {
                return GameLogic.getCardspool().get(i);
            }
        }

        return null;
    }

    /** clear Btn in the effect window */
    @FXML
    void clearBtnHandler(MouseEvent event) {
        showAllPopUpCards();
        popUpWindowIndexlist.clear();
        if (gamelogic.getDiebActive()) {
            updateDieb();
        } else {
            setDisablePopUp();
        }
    }

    /**
     * well close the effect window, reset images in the window and deactive all
     * effects of the cards
     */
    private void closeEffectWindow() {
        showAllPopUpCards();
        effectWindow.setVisible(false);
        popUpWindowIndexlist.clear();
        choosenList.clear();
    }

    private void showAllPopUpCards() {
        for (int i = 0; i < popUpHandCards.getChildren().size(); i++) {
            popUpHandCards.getChildren().get(i).setOpacity(1);
        }
    }

    private void showAllPopUpCards2() {
        for (ImageView card : effectImages) {
            card.setOpacity(1);
        }
    }

    private void clearAllPopUpCards() {
        for (ImageView card : effectImages) {
            card.setImage(null);
        }
    }

    private Boolean noMoneyCards() {
        int numbofmoney = 0;
        for (int i = 0; i < playerService.getPlayers().size(); i++) {
            if (playerService.getPlayers().get(i).getFirstDeckCard().getCardType().equals("Geld")
                    || playerService.getPlayers().get(i).getSecondDeckCard().getCardType().equals("Geld")) {
                numbofmoney++;
            }
        }
        if (numbofmoney > 0) {
            return false;
        } else {
            return true;
        }
    }

    /** well active when any of the card in popup window is clicked */
    @FXML
    void popUpCardsHandler(MouseEvent event) {
        HBox hbox = (HBox) ((ImageView) event.getSource()).getParent();
        for (int i = 0; i < hbox.getChildren().size(); i++) {
            if (((ImageView) hbox.getChildren().get(i)) == event.getTarget()) {
                // kapelle
                if (gamelogic.getkapelleActive()) {
                    if (!popUpWindowIndexlist.contains(i)) {
                        popUpWindowIndexlist.add(i);
                    }
                } // Werkstatt
                else if (gamelogic.getWerkstattActive()) {
                    chooseOneCard(i, hbox);
                } // umbau
                else if (gamelogic.getUmbauActive()) {
                    chooseOneCard(i, hbox);
                } else if (gamelogic.getUmbauActive2()) {
                    chooseOneCard(i, hbox);
                } // Miliz
                else if (gamelogic.getMilizActive()) {
                    int j = playerService.getPlayer().getHand().size() - 3;
                    if (popUpWindowIndexlist.size() < j && !popUpWindowIndexlist.contains(i)) {
                        popUpWindowIndexlist.add(i);
                    } else if (popUpWindowIndexlist.size() >= j) {
                        ((ImageView) hbox.getChildren().get(popUpWindowIndexlist.getFirst())).setOpacity(1);
                        popUpWindowIndexlist.removeFirst();
                        popUpWindowIndexlist.add(i);
                    }
                } else if (gamelogic.getSpionActive()) {
                    if (!popUpWindowIndexlist.contains(i)) {
                        popUpWindowIndexlist.add(i);
                    }
                }
                // Dieb
                else if (gamelogic.getDiebActive()) {
                    popUpWindowIndexlist.add(i);
                    if (popUpWindowIndexlist.contains(0)) {
                        popUpHandCards.getChildren().get(1).setDisable(true);
                    }
                    if (popUpWindowIndexlist.contains(1)) {
                        popUpHandCards.getChildren().get(0).setDisable(true);
                    }
                    if (popUpWindowIndexlist.contains(2)) {
                        popUpHandCards.getChildren().get(3).setDisable(true);
                    }
                    if (popUpWindowIndexlist.contains(3)) {
                        popUpHandCards.getChildren().get(2).setDisable(true);
                    }
                    if (popUpWindowIndexlist.contains(4)) {
                        popUpHandCards.getChildren().get(5).setDisable(true);
                    }
                    if (popUpWindowIndexlist.contains(5)) {
                        popUpHandCards.getChildren().get(4).setDisable(true);
                    }
                } else if (gamelogic.getDiebActive2()) {
                    popUpWindowIndexlist.add(i);
                } else if (gamelogic.getFestmahlActive()) {
                    chooseOneCard(i, hbox);
                } // Buerokrat
                else if (gamelogic.getBuerokratActive()) {
                    chooseOneCard(i, hbox);
                } // Mine
                else if (gamelogic.getMineActive()) {
                    chooseOneCard(i, hbox);
                } // Keller
                else if (gamelogic.getKellerActive()) {
                    popUpWindowIndexlist.add(i);
                } // Thronsaal
                else if (gamelogic.getThronsaalActive()) {
                    chooseOneCard(i, hbox);
                }

                ((ImageView) event.getSource()).setOpacity(opacity);
            }
        }
    }

    public void chooseOneCard(int index, HBox hbox) {
        if (popUpWindowIndexlist.size() == 0) {
            popUpWindowIndexlist.add(index);
        } else {
            ((ImageView) hbox.getChildren().get(popUpWindowIndexlist.getFirst())).setOpacity(1);
            popUpWindowIndexlist.removeFirst();
            popUpWindowIndexlist.add(index);
        }
    }

    public void updateEffectWindow() {
        if (gamelogic.activatedEffectWindow() || gamelogic.getThronsaalActive()) {
            if (gamelogic.getFestmahlActive()) {
                popupMessageLabel.setText("Festmahl :: Wähle eine Karte aus deiner Hand.");
                choosenList = gamelogic.getTheRightCards(5);
                setupEffectWindow(choosenList);

            } else if (gamelogic.getKellerActive()) {
                popupMessageLabel.setText("Keller :: Lege beliebig viele Karten aus deiner Hand ab.");
                choosenList = gamelogic.getTheRightCards();
                setupEffectWindow(choosenList);

            } else if (gamelogic.getkapelleActive()) {
                popupMessageLabel.setText("Kapelle :: Wähle bis zu vier Karten zum entsorgen.");
                choosenList = gamelogic.getTheRightCards();
                setupEffectWindow(choosenList);

            } else if (gamelogic.getSpionActive()) {
                popupMessageLabel.setText(
                        "Spion :: Entscheide ob die Karten auf den Ablagestapel oder zurück auf den Nachziehstapel soll!");
                setPlayersName2();
                updateSpion();

            } else if (gamelogic.getDiebActive()) {
                popupMessageLabel.setText("Dieb :: Wähle die Geldkarten aus");
                setPlayersName();
                updateDieb();

            } else if (gamelogic.getDiebActive2()) {
                popupMessageLabel.setText("Dieb :: Wähle die Geldkarten welche du behalten willst.");
                setPlayersName();
                updateDieb2();

            } else if (gamelogic.getMilizActive()) {
                popupMessageLabel.setText("Miliz :: wähle Karten aus bis du nur noch 3 hast");
                playground2.setVisible(true);
                playground1.setVisible(true);
                playground1.getChildren().get(1).setVisible(false);
                choosenList = gamelogic.getTheRightCards();
                setupEffectWindow(choosenList);

            } else if (gamelogic.getBuerokratActive()) {
                popupMessageLabel.setText("Buerokrat :: Wähle eine PunktKarte aus.");
                playground2.setVisible(true);
                playground1.setVisible(true);
                playground1.getChildren().get(1).setVisible(false);
                choosenList = gamelogic.getTheRightCards("Punkte");
                setupEffectWindow(choosenList);

            } else if (gamelogic.getUmbauActive()) {
                popupMessageLabel.setText("Umbau :: Wähle eine Karte aus deine Hand und entsorge sie.");
                choosenList = gamelogic.getTheRightCards();
                setupEffectWindow(choosenList);

            } else if (gamelogic.getUmbauActive2()) {
                popupMessageLabel.setText("Umbau :: Wähle eine neue Karte");
                choosenList = gamelogic.getTheRightCards(oldValue + 2);
                setupEffectWindow(choosenList);

            } else if (gamelogic.getWerkstattActive()) {
                popupMessageLabel.setText("Werkstatt :: Wähle eine Karte aus deiner Hand.");
                choosenList = gamelogic.getTheRightCards(4);
                setupEffectWindow(choosenList);

            } else if (gamelogic.getMineActive()) {
                popupMessageLabel.setText("Mine :: Wähle eine Geldkarte aus die entsorgt werden soll.");
                choosenList = gamelogic.getTheRightCards("Geld");
                setupEffectWindow(choosenList);

            } else if (gamelogic.getThronsaalActive()) {
                if (gamelogic.getCardsFromHand("Aktion").size() != 0) {
                    popupMessageLabel.setText("Thronsaal :: wähle eine Aktionkarte");
                    choosenList = gamelogic.getTheRightCards("Aktion");
                    setupEffectWindow(choosenList);
                } else {
                    gamelogic.setThronsaalActive(false);
                    playerService.getTrigger().setThronsaalWaiting(0);
                    return;
                }
            }

            effectWindow.setVisible(true);
        }
    }

    public void createEffectWindowCard(int i) {
        Image url = new Image(choosenList.get(i).getImagePath());
        ImageView newImage = new ImageView(url);
        newImage.setFitHeight(200);
        newImage.setFitWidth(120);
        newImage.setOnMouseClicked((e) -> {
            popUpCardsHandler(e);
        });
        popUpHandCards.getChildren().add(newImage);
    }

    /** takes a explicit type of the handcards and put it in the effectwindow */
    public void setupEffectWindow(LinkedList<Cards> list) {
        while (list.size() < popUpHandCards.getChildren().size()) {
            popUpHandCards.getChildren().remove(popUpHandCards.getChildren().size() - 1);
        }

        for (int i = 0; i < list.size(); i++) {
            try {
                ImageView imageView = (ImageView) popUpHandCards.getChildren().get(i);
                Image newImage = new Image(list.get(i).getImagePath());
                imageView.setImage(newImage);
            } catch (IndexOutOfBoundsException e) {
                createEffectWindowCard(i);
            }
        }
    }

    public void updateSpion() {
        gamelogic.refillDeck(0);
        popUpHandCards.getChildren().clear();
        for (int i = 0; i < playerService.getPlayers().size() + 1; i++) {
            Image url;
            if (i == 0) { // the player
                url = new Image(playerService.getPlayer().getDeck().get(0).getImagePath());
            } else { // the enemies
                url = new Image(playerService.getPlayers().get(i - 1).getFirstDeckCard().getImagePath());
            }
            ImageView newImage = new ImageView(url);
            newImage.setFitHeight(200);
            newImage.setFitWidth(120);
            newImage.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    popUpCardsHandler(e);
                }
            });
            popUpHandCards.getChildren().add(newImage);
        }
    }

    /** take up to six cards in the popup for dieb */
    public void updateDieb() {
        int numberofPlayers = playerService.getPlayers().size();
        if (numberofPlayers > 0) {
            popUpCard1.setImage(new Image(playerService.getPlayers().get(0).getFirstDeckCard().getImagePath()));
            popUpCard2.setImage(new Image(playerService.getPlayers().get(0).getSecondDeckCard().getImagePath()));
            if (!playerService.getPlayers().get(0).getFirstDeckCard().getCardType().equals("Geld")) {
                popUpCard1.setDisable(true);
            }
            if (!playerService.getPlayers().get(0).getSecondDeckCard().getCardType().equals("Geld")) {
                popUpCard2.setDisable(true);
            }
        }
        if (numberofPlayers > 1) {
            popUpCard3.setImage(new Image(playerService.getPlayers().get(1).getFirstDeckCard().getImagePath()));
            popUpCard4.setImage(new Image(playerService.getPlayers().get(1).getSecondDeckCard().getImagePath()));
            if (!playerService.getPlayers().get(1).getFirstDeckCard().getCardType().equals("Geld")) {
                popUpCard3.setDisable(true);
            }
            if (!playerService.getPlayers().get(1).getSecondDeckCard().getCardType().equals("Geld")) {
                popUpCard4.setDisable(true);
            }
        }
        if (numberofPlayers > 2) {
            popUpCard5.setImage(new Image(playerService.getPlayers().get(2).getFirstDeckCard().getImagePath()));
            popUpCard6.setImage(new Image(playerService.getPlayers().get(2).getSecondDeckCard().getImagePath()));
            if (!playerService.getPlayers().get(2).getFirstDeckCard().getCardType().equals("Geld")) {
                popUpCard5.setDisable(true);
            }
            if (!playerService.getPlayers().get(2).getSecondDeckCard().getCardType().equals("Geld")) {
                popUpCard6.setDisable(true);
            }
        }
    }

    /** shows the selected cards of dieb */
    public void updateDieb2() {
        if (popUpWindowIndexlist.contains(0)) {
            popUpCard1.setImage(new Image(playerService.getPlayers().get(0).getFirstDeckCard().getImagePath()));
        } else {
            popUpCard1.setVisible(false);
        }
        if (popUpWindowIndexlist.contains(1)) {
            popUpCard2.setImage(new Image(playerService.getPlayers().get(0).getSecondDeckCard().getImagePath()));
        } else {
            popUpCard2.setVisible(false);
        }
        if (popUpWindowIndexlist.contains(2)) {
            popUpCard3.setImage(new Image(playerService.getPlayers().get(1).getFirstDeckCard().getImagePath()));
        } else {
            popUpCard3.setVisible(false);
        }
        if (popUpWindowIndexlist.contains(3)) {
            popUpCard4.setImage(new Image(playerService.getPlayers().get(1).getSecondDeckCard().getImagePath()));
        } else {
            popUpCard4.setVisible(false);
        }
        if (popUpWindowIndexlist.contains(4)) {
            popUpCard5.setImage(new Image(playerService.getPlayers().get(2).getFirstDeckCard().getImagePath()));
        } else {
            popUpCard5.setVisible(false);
        }
        if (popUpWindowIndexlist.contains(5)) {
            popUpCard6.setImage(new Image(playerService.getPlayers().get(2).getSecondDeckCard().getImagePath()));
        } else {
            popUpCard6.setVisible(false);
        }
    }

    private void resetLabels() {
        popUpCardNames1.setText(" ");
        popUpCardNames2.setText(" ");
        popUpCardNames3.setText(" ");
        popUpCardNames4.setText(" ");
        popUpCardNames5.setText(" ");
    }

    public void setPlayersName() {
        int playersNumber = playerService.getPlayers().size();
        if (playersNumber == 1) {
            popUpCardNames1.setVisible(true);
            popUpCardNames1.setText(playerService.getPlayers().get(0).getPlayerName());
        }
        if (playersNumber == 2) {
            popUpCardNames1.setVisible(true);
            popUpCardNames3.setVisible(true);
            popUpCardNames1.setText(playerService.getPlayers().get(0).getPlayerName());
            popUpCardNames3.setText(playerService.getPlayers().get(1).getPlayerName());
        }
        if (playersNumber == 3) {
            popUpCardNames1.setVisible(true);
            popUpCardNames3.setVisible(true);
            popUpCardNames5.setVisible(true);
            popUpCardNames1.setText(playerService.getPlayers().get(0).getPlayerName());
            popUpCardNames3.setText(playerService.getPlayers().get(1).getPlayerName());
            popUpCardNames5.setText(playerService.getPlayers().get(2).getPlayerName());
        }
    }

    public void setPlayersName2() {
        int playersNumber = playerService.getPlayers().size();
        if (playersNumber == 1) {
            popUpCardNames2.setVisible(true);
            popUpCardNames3.setVisible(true);
            popUpCardNames2.setText(playerService.getPlayer().getPlayerName());
            popUpCardNames3.setText(playerService.getPlayers().get(0).getPlayerName());
        }
        if (playersNumber == 2) {
            popUpCardNames2.setVisible(true);
            popUpCardNames3.setVisible(true);
            popUpCardNames4.setVisible(true);
            popUpCardNames2.setText(playerService.getPlayer().getPlayerName());
            popUpCardNames3.setText(playerService.getPlayers().get(0).getPlayerName());
            popUpCardNames4.setText(playerService.getPlayers().get(1).getPlayerName());
        }
        if (playersNumber == 3) {
            popUpCardNames2.setVisible(true);
            popUpCardNames3.setVisible(true);
            popUpCardNames4.setVisible(true);
            popUpCardNames5.setVisible(true);
            popUpCardNames2.setText(playerService.getPlayer().getPlayerName());
            popUpCardNames3.setText(playerService.getPlayers().get(0).getPlayerName());
            popUpCardNames4.setText(playerService.getPlayers().get(1).getPlayerName());
            popUpCardNames5.setText(playerService.getPlayers().get(2).getPlayerName());
        }
    }

    public void addCardsSpion() {
        for (int i = 0; i < popUpWindowIndexlist.size(); i++) {
            if (popUpWindowIndexlist.get(i) == 0) {
                gamelogic.sendCardToDiscard(0);
            } else {
                int index = popUpWindowIndexlist.get(i) - 1;
                OtherPlayers player = playerService.getPlayers().get(index);
                Packet28RemoveFirstDeck deletcard = new Packet28RemoveFirstDeck();
                deletcard.playername = player.getPlayerName();
                playerService.getClient().getClient().sendTCP(deletcard);
            }
        }
    }

    /** set all popupcards to not disabled */
    public void setDisablePopUp() {
        for (int i = 0; i < popUpHandCards.getChildren().size(); i++) {
            popUpHandCards.getChildren().get(i).setDisable(false);
        }
    }

    /** set all popupcards visible */
    public void setImagesVisible() {
        for (int i = 0; i < popUpHandCards.getChildren().size(); i++) {
            popUpHandCards.getChildren().get(i).setVisible(true);
        }
    }

    /** set all popupcards to not disabled */
    public void setDisablePopUp2() {
        popUpCard1.setDisable(false);
        popUpCard2.setDisable(false);
        popUpCard3.setDisable(false);
        popUpCard4.setDisable(false);
        popUpCard5.setDisable(false);
        popUpCard6.setDisable(false);
    }

    /** set all popupcards visible */
    public void setImagesVisible2() {
        popUpCard1.setVisible(true);
        popUpCard2.setVisible(true);
        popUpCard3.setVisible(true);
        popUpCard4.setVisible(true);
        popUpCard5.setVisible(true);
        popUpCard6.setVisible(true);
    }

    /** adds the choosen cards of dieb to the discarddeck of the player */
    public void addCardsDieb() {
        int numberofPlayers = playerService.getPlayers().size();
        if (numberofPlayers > 0) {
            if (popUpWindowIndexlist.contains(0)) {
                playerService.getPlayer().getDiscardDeck().add(playerService.getPlayers().get(0).getFirstDeckCard());
                gamelogic.sendFirstCard(0);
            }
            if (popUpWindowIndexlist.contains(1)) {
                playerService.getPlayer().getDiscardDeck().add(playerService.getPlayers().get(0).getSecondDeckCard());
                gamelogic.sendSecondCard(0);
            }
        }
        if (numberofPlayers > 1) {
            if (popUpWindowIndexlist.contains(2)) {
                playerService.getPlayer().getDiscardDeck().add(playerService.getPlayers().get(1).getFirstDeckCard());
                gamelogic.sendFirstCard(1);
            }
            if (popUpWindowIndexlist.contains(3)) {
                playerService.getPlayer().getDiscardDeck().add(playerService.getPlayers().get(1).getSecondDeckCard());
                gamelogic.sendSecondCard(1);
            }
        }
        if (numberofPlayers > 2) {
            if (popUpWindowIndexlist.contains(4)) {
                playerService.getPlayer().getDiscardDeck().add(playerService.getPlayers().get(2).getFirstDeckCard());
                gamelogic.sendFirstCard(2);
            }
            if (popUpWindowIndexlist.contains(5)) {
                playerService.getPlayer().getDiscardDeck().add(playerService.getPlayers().get(2).getSecondDeckCard());
                gamelogic.sendSecondCard(2);
            }
        }
    }

    /** adds the not choosen cards of dieb to the trash */
    public void trashCards() {
        int numberofPlayers = playerService.getPlayers().size();
        if (numberofPlayers > 0) {
            if (!popUpWindowIndexlist.contains(0)
                    && playerService.getPlayers().get(0).getFirstDeckCard().getCardType() == "Geld"
                    && popUpCard1.isVisible()) {
                gamelogic.sendFirstCardtoTrash(0);
                gamelogic.sendFirstCard(0);
            }
            if (!popUpWindowIndexlist.contains(1)
                    && playerService.getPlayers().get(0).getSecondDeckCard().getCardType() == "Geld"
                    && popUpCard2.isVisible()) {
                gamelogic.sendsecondCardtoTrash(0);
                gamelogic.sendSecondCard(0);
            }
        }
        if (numberofPlayers > 1) {
            if (!popUpWindowIndexlist.contains(2)
                    && playerService.getPlayers().get(1).getFirstDeckCard().getCardType() == "Geld"
                    && popUpCard3.isVisible()) {
                gamelogic.sendFirstCardtoTrash(1);
                gamelogic.sendFirstCard(1);
            }
            if (!popUpWindowIndexlist.contains(3)
                    && playerService.getPlayers().get(1).getSecondDeckCard().getCardType() == "Geld"
                    && popUpCard4.isVisible()) {
                gamelogic.sendsecondCardtoTrash(1);
                gamelogic.sendSecondCard(1);
            }
        }
        if (numberofPlayers > 2) {
            if (!popUpWindowIndexlist.contains(4)
                    && playerService.getPlayers().get(2).getFirstDeckCard().getCardType() == "Geld"
                    && popUpCard5.isVisible()) {
                gamelogic.sendFirstCardtoTrash(2);
                gamelogic.sendFirstCard(2);
            }
            if (!popUpWindowIndexlist.contains(5)
                    && playerService.getPlayers().get(2).getSecondDeckCard().getCardType() == "Geld"
                    && popUpCard6.isVisible()) {
                gamelogic.sendFirstCardtoTrash(2);
                gamelogic.sendSecondCard(2);
            }
        }
        popUpWindowIndexlist.clear();
    }
}