package de.btu.swp.dominion.gameLogic;

import de.btu.swp.dominion.gameLogic.PlayerService;
import de.btu.swp.dominion.network.Packets;
import de.btu.swp.dominion.network.Packets.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import de.btu.swp.dominion.cards.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class GameLogic {
    private PlayerService playerService;
    private LinkedList<Cards> newhand = new LinkedList<>();
    private LinkedList<Cards> newlist = new LinkedList<Cards>();
    /** well Triger if a card is played */
    private boolean kapelleActive = false;
    private boolean thronsaalActive = false;
    private Boolean kellerActive = false;
    private static boolean milizActive = false;
    private static boolean diebActive = false;
    private static boolean diebActive2 = false;
    private static boolean buerokratActive = false;
    private static boolean buerokratLoserActive = false; // if a player dont have any points then should show his cards
    private boolean umbauActive = false;
    private boolean umbauActive2 = false;
    private boolean werkstattActive = false;
    private boolean mineActive = false;
    private boolean spionActive = false;
    private boolean festmahlActive = false;
    private GuiDesigner musicBox = new GuiDesigner();
    private Alert alert;
    private boolean inCardLaunch = false;

    @FXML
    private HBox kartenInHand;
    Scanner scanner = new Scanner(System.in);
    private static LinkedList<Integer> cardnumber = new LinkedList<Integer>();
    /**
     * kartenpool wird vom Host gewählt, liegen im Vorrat und von der wird gekauft
     */
    private static ArrayList<Cards> cardpool = new ArrayList<>();

    private static LinkedList<Cards> trashCards = new LinkedList<>();

    public GameLogic(PlayerService newplayerservice) {
        this.playerService = newplayerservice;
    }

    /**
     * plays money cards from hand and play them TODO: change so we can get the
     * money for all cards and to accept silver and gold as well as money
     */
    public void playMoneyOnBoard(int price) {
        int anzahlKupfer = 0;
        if (playerService.getPlayer().getPlayedCardsList().size() > 0) {
            for (int a = playerService.getPlayer().getPlayedCardsList().size() - 1; a >= 0; a--) {
                if (playerService.getPlayer().getCardNamePlayed(a) == "Kupfer") {
                    anzahlKupfer++;
                }
            }
        }

        for (int k = playerService.getPlayer().getHand().size() - 1; k >= 0; k--) {
            if (anzahlKupfer >= price) {
                break;
            } else {
                if (playerService.getPlayer().getCardName(k) == "Kupfer") {
                    playerService.getPlayer().getPlayedCardsList().add(playerService.getPlayer().getHand().get(k));
                    anzahlKupfer++;
                    playerService.getPlayer().getHand().remove(k);
                    newhand = playerService.getPlayer().getHand();
                    playerService.getPlayer().setHand(newhand);
                } else if (playerService.getPlayer().getCardName(k) == "Silber") {
                    playerService.getPlayer().getPlayedCardsList().add(playerService.getPlayer().getHand().get(k));
                    anzahlKupfer = anzahlKupfer + 2;
                    playerService.getPlayer().getHand().remove(k);
                    newhand = playerService.getPlayer().getHand();
                    playerService.getPlayer().setHand(newhand);
                } else if (playerService.getPlayer().getCardName(k) == "Gold") {
                    playerService.getPlayer().getPlayedCardsList().add(playerService.getPlayer().getHand().get(k));
                    anzahlKupfer = anzahlKupfer + 3;
                    playerService.getPlayer().getHand().remove(k);
                    newhand = playerService.getPlayer().getHand();
                    playerService.getPlayer().setHand(newhand);
                }
            }
        }
    }

    public boolean getInCardLaunch() {
        return inCardLaunch;
    }

    public static LinkedList<Integer> getCardnumber() {
        return cardnumber;
    }

    public static ArrayList<Cards> getCardspool() {
        return cardpool;
    }

    public static void setCardspool(ArrayList<Cards> cardspool) {
        cardpool = cardspool;
    }

    public LinkedList<Cards> getTrashCardsList() {
        return trashCards;
    }

    /** if the deck have only i cards in the deck get refilled and shuffeled */
    public void refillDeck(int i) {
        if (playerService.getPlayer().getDeck().size() <= i) {
            while (playerService.getPlayer().getDiscardDeck().size() > 0) {
                playerService.getPlayer().getDeck().add(playerService.getPlayer().getDiscardDeck().get(0));
                playerService.getPlayer().getDiscardDeck().remove(0);
            }
            Collections.shuffle(playerService.getPlayer().getDeck());
        }
    }

    public boolean activatedEffectWindow() {
        return milizActive || diebActive || diebActive2 || kapelleActive 
                || buerokratActive || buerokratLoserActive || umbauActive || kellerActive
                || umbauActive2 || werkstattActive || spionActive || festmahlActive || mineActive;
    }

    public void launchEffect(MouseEvent event) {
        Cards card = playerService.getPlayer().getPlayedCardsList().getLast();
        // führt die Effekte aus
        for (int i = 0; i < card.getEffect().size(); i++) {
            if (card.getEffect().get(i) == "addCard") {
                playerService.drawCard();
            } else if (card.getEffect().get(i) == "addAction") {
                playerService.getPlayer().setAction(1);
            } else if (card.getEffect().get(i) == "addBuy") {
                playerService.getPlayer().setBuys(1);
            } else if (card.getEffect().get(i) == "addMoney") {
                playerService.getPlayer().setMoney(1);
            } else if (card.getEffect().get(i) == "addKanzlerEffekt") {
                inCardLaunch = true;
                if (playerService.getPlayer().getDeck().size() > 0) {
                    ButtonType effect = new ButtonType("auf Ablagestapel");
                    ButtonType nothing = new ButtonType("auf Nachziehstapel lassen");
                    alert = new Alert(AlertType.CONFIRMATION,
                            "Wollen Sie ihren Nachziehstapel auf den Ablagestapel legen?", effect, nothing);
                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    alert.initOwner(window);
                    alert.setTitle("Kanzlereffekt");
                    // add style to the alert
                    musicBox.alertStyler(alert);
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == effect) {
                        playerService.getPlayer().getDiscardDeck().addAll(playerService.getPlayer().getDeck());
                        playerService.getPlayer().setRightList(new LinkedList<Cards>());
                    }
                }
                inCardLaunch = false;
            } else if (card.getEffect().get(i) == "addKapelleEffect") {
                setkapelleActive(true);
            } else if (card.getEffect().get(i) == "addThronsaalEffect") {
                setThronsaalActive(true);
                playerService.getTrigger().setThronsaalWaiting(playerService.getTrigger().getThronsaalWaiting()-1);
            } else if (card.getEffect().get(i) == "addAdventure") {
                int numberOfMoneyCards = 0;
                while (numberOfMoneyCards < 2) {
                    if (playerService.getPlayer().getDeck().size() == 0) {
                        playerService.getPlayer().setRightList(playerService.getPlayer().getDiscardDeck());
                        playerService.getPlayer().setDiscardDeck(new LinkedList<Cards>());
                        if (playerService.getPlayer().getDeck().size() == 0) {
                            return;
                        }
                    } else {
                        if (playerService.getPlayer().getDeck().get(0).getCardType() == "Geld") {
                            numberOfMoneyCards++;
                            playerService.drawCard();
                        } else {
                            playerService.getPlayer().getDiscardDeck().add(playerService.getPlayer().getDeck().get(0));
                            playerService.getPlayer().getDeck().remove(0);
                        }
                    }
                }
            } else if (card.getEffect().get(i) == "addCardLibrary") {
                inCardLaunch = true;
                while (playerService.getPlayer().getHand().size() < 7) {
                    if (playerService.getPlayer().getDeck().size() > 0) {
                        if (playerService.getPlayer().getDeck().get(0).getCardType() == "Aktion") {
                            ButtonType behalten = new ButtonType("behalten");
                            ButtonType ablegen = new ButtonType("ablegen");
                            Alert alert = new Alert(AlertType.CONFIRMATION, "Wollen Sie die Karte " + playerService.getPlayer().getDeck().get(0).getName() + " ablegen?", behalten, ablegen);
                            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            alert.initOwner(window);
                            alert.setTitle(playerService.getPlayer().getDeck().get(0).getName());
                            // add style to the alert
                            musicBox.alertStyler(alert);
                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.get() == ablegen) {
                                discardCardfromDeck();
                            } else {
                                playerService.drawCard();
                            }
                        } else {
                            playerService.drawCard();
                        }
                    } else {
                        playerService.drawCard();
                    }
                }
                inCardLaunch = false;
            } else if (card.getEffect().get(i) == "addMilizEffekt") {
                Packet11MilizEffekt effect = new Packet11MilizEffekt();
                effect.playPlayer = playerService.getPlayer().getPlayerName();
                playerService.getClient().getClient().sendTCP(effect);
            } else if (card.getEffect().get(i) == "addHexeEffect") {
                // send fluch to the clients
                Packet10HexeEffect hexeCard = new Packet10HexeEffect();
                hexeCard.ownerName = playerService.getPlayer().getPlayerName();
                playerService.getClient().getClient().sendTCP(hexeCard);
            } else if (card.getEffect().get(i) == "addRatsEffekt") {
                Packet17RatsEffect effect = new Packet17RatsEffect();
                effect.name = playerService.getPlayer().getPlayerName();
                playerService.getClient().getClient().sendTCP(effect);
            } else if (card.getEffect().get(i) == "addDiebEffekt") {
                Packet23DiebEffekt effekt = new Packet23DiebEffekt();
                effekt.playername = playerService.getPlayer().getPlayerName();
                playerService.getClient().getClient().sendTCP(effekt);
                setDiebActive(true);
            } else if (card.getEffect().get(i) == "addWerkstattEffect") {
                setWerkstattActive(true);
            } else if (card.getEffect().get(i) == "addUmbauEffect") {
                setUmbauActive(true);
            } else if (card.getEffect().get(i) == "addSpionEffect") {
                Packet24SpionEffect spionEffect = new Packet24SpionEffect();
                spionEffect.playername = playerService.getPlayer().getPlayerName();
                playerService.getClient().getClient().sendTCP(spionEffect);
                setSpionActive(true);
            } else if (card.getEffect().get(i) == "addFestmahlEffect") {
                if (!getThronsaalActive()) {
                    playerService.getPlayer().getHand().add(playerService.getPlayer().getPlayedCardsList().getLast());
                    sendCardToTrash(playerService.getPlayer().getHand().size() - 1);
                    playerService.getPlayer().getPlayedCardsList().removeLast();
                }
                setFestmahlActive(true);
            } else if (card.getEffect().get(i) == "addGeldverleiherEffect") {
                for (int j = 0; j < playerService.getPlayer().getHand().size(); j++) {
                    if (playerService.getPlayer().getHand().get(j).getName().equals("Kupfer")) {
                        sendCardToTrash(j);
                        playerService.getPlayer().setMoney(3);
                        break;
                    }
                }
            } else if (card.getEffect().get(i) == "addBuerokratEffekt") {
                // add a silber to the left deck without buying it + Network
                Silber silber = new Silber();
                Packet14Card2 boughtCard = new Packet14Card2();
                boughtCard.name = silber.getName();
                playerService.getClient().getClient().sendTCP(boughtCard);
                sendToLog("Silber", "takeCard");
                playerService.getPlayer().getDeck().addFirst(silber);

                // send the effect of to Buerokrat all players
                Packet29Buerokrat BuerokratPacket = new Packet29Buerokrat();
                BuerokratPacket.ownerName = playerService.getPlayer().getPlayerName();
                playerService.getClient().getClient().sendTCP(BuerokratPacket);
            } else if (card.getEffect().get(i) == "addKellerEffect") {
                setKellerActive(true);
            } else if (card.getEffect().get(i) == "addMineEffekt") {
                setMineActive(true);
            }
        }
        playerService.getPlayer().setAction(-1);
        if (!activatedEffectWindow() && getThronsaalActive() && !card.getName().equals("Thronsaal")) {
            setThronsaalActive(false);
            launchEffect(event);
        } else if (card.getName().equals("Thronsaal") && getThronsaalActive() && playerService.getTrigger().getThronsaalWaiting() != -1) {
            playerService.getTrigger().setThronsaalWaiting(playerService.getTrigger().getThronsaalWaiting()+3);
            playerService.getPlayer().setAction(-1);
        } else if (playerService.getTrigger().getThronsaalWaiting() > 0) {
            setThronsaalActive(true);
            playerService.getTrigger().setThronsaalWaiting(playerService.getTrigger().getThronsaalWaiting()-1);
        }
    }

    public void sendCardToDiscard(int index) {
        Cards card = playerService.getPlayer().getDeck().get(index);
        playerService.getPlayer().getDiscardDeck().add(card);
        playerService.getPlayer().getDeck().remove(index);
    }

    public void sendCardToDiscardFromHand(int index) {
        Cards card = playerService.getPlayer().getHand().get(index);
        playerService.getPlayer().getDiscardDeck().add(card);
        playerService.getPlayer().getHand().remove(index);
    }

    /**
     * well take a string and publish it to all player in the log list used a
     * trigger to decied the kind of the log message
     */
    public void sendToLog(String cardName, String trigger) {
        Packet15log log = new Packets.Packet15log();
        String playerName = "[" + playerService.getPlayer().getPlayerName() + "]:";

        if (trigger.equals("playCard")) {
            log.logMessage = playerName + " spielte " + cardName + ".";
        } else if (trigger.equals("endTurn")) {
            log.logMessage = playerName + " beendete seinen Zug.";
        } else if (trigger.equals("buyCard")) {
            log.logMessage = playerName + " kaufte " + cardName + ".";
        } else if (trigger.equals("takeCard")) {
            log.logMessage = playerName + " nahm sich " + cardName + ".";
        }
        playerService.getClient().getClient().sendTCP(log);
    }

    public void playClickedCard(int index, MouseEvent event) {
        sendToLog(playerService.getPlayer().getHand().get(index).getName(), "playCard");
        playerService.getPlayer().getPlayedCardsList().add(playerService.getPlayer().getHand().get(index));
        playerService.getPlayer().getHand().remove(index);
        /** start every effect + Network */
        Packet10RenderSpecator zug = new Packets.Packet10RenderSpecator();
        zug.cardName = playerService.getPlayer().getPlayedCardsList().getLast().getName();
        playerService.getClient().getClient().sendTCP(zug);
        launchEffect(event);
    }

    public void removeFromHand(Cards card, MouseEvent event) {
        loop : for (int j = 0; j < playerService.getPlayer().getHand().size(); j++) {
                    if (playerService.getPlayer().getHand().get(j).getName().equals(card.getName())) {
                        playerService.getPlayer().getHand().remove(j);
                        break loop;
                    }
                }

        launchEffect(event);
    }

    /** throw the card at index from the hand to the discardDeck */
    public void discardCardfromHand(int index) {
        playerService.getPlayer().getDiscardDeck().add(playerService.getPlayer().getHand().get(index));
        playerService.getPlayer().getHand().remove(index);
    }

    /** throw the first card of the deck to the discardDeck */
    public void discardCardfromDeck() {
        playerService.getPlayer().getDiscardDeck().add(playerService.getPlayer().getDeck().get(0));
        playerService.getPlayer().getDeck().remove(0);
    }

    /** maybe unite money and points cards to two methods */
    public void buyCard(Cards card) {
        Packet14Card2 boughtCard = new Packet14Card2();
        boughtCard.name = card.getName();
        playerService.getClient().getClient().sendTCP(boughtCard);

        sendToLog(card.getName(), "buyCard");
        playerService.getPlayer().getDiscardDeck().add(card);
        playerService.getPlayer().setMoney(-card.getCost());
        playerService.getPlayer().setBuys(-1);
        if (card.getCardType() == "Punkte") {
            playerService.getPlayer().setPoints(card.getPoints());
        }
    }

    public void takeCard(Cards card) {
        Packet14Card2 boughtCard = new Packet14Card2();
        boughtCard.name = card.getName();
        playerService.getClient().getClient().sendTCP(boughtCard);

        sendToLog(card.getName(), "takeCard");
        playerService.getPlayer().getDiscardDeck().add(card);
        if (card.getCardType() == "Punkte") {
            playerService.getPlayer().setPoints(card.getPoints());
        }
    }

    /**returns the number of equal Cards in played Cards */
    public int numberOfCards(String path) {
        int value = 1;
        for (int i = playerService.getPlayer().getPlayedCardsList().size() - 1; i >= 0; i--) {
            String namea = playerService.getPlayer().getPlayedCardImage(i);
            if (namea == path) {
                value++;
            }
        }
        return value;
    }

    /**returns the number of equal Cards in Hand */
    public int numberOfCardsinHand(String name) {
        int value = 0;
        for (int i = 0; i < playerService.getPlayer().getHand().size(); i++) {
            String nameHand = playerService.getPlayer().getHand().get(i).getName();
            if (nameHand.equals(name)) {
                value++;
            }
        }
        return value;
    }

    /** draws Cards and shows them in Hand */
    public void initHand() {
        Collections.shuffle(playerService.getPlayer().getDeck());
        // draw 5 Cards
        playerService.drawCard();
        playerService.drawCard();
        playerService.drawCard();
        playerService.drawCard();
        playerService.drawCard();
    }

    /** well send a card with an index to trash */
    public void sendCardToTrash(int index) {
        /** send to the glbal Trash */
        Packet20GlobalTrash globalTrashCard = new Packet20GlobalTrash();
        globalTrashCard.cardName = playerService.getPlayer().getHand().get(index).getName();
        playerService.getClient().getClient().sendTCP(globalTrashCard);
        /** local Trash */
        if (playerService.getPlayer().getHand().get(index).getCardType() == "Geld") {
            playerService.getPlayer().setMoney(-playerService.getPlayer().getHand().get(index).getPoints());
        }

        playerService.getPlayer().getHand().remove(index); // in the feuture finde alternative ways (glitch expected)
    }

    /** get the Index of all aktion cards from the hand */
    public ArrayList<Integer> getCardsFromHand(String CardType) {
        ArrayList<Integer> typeCardsList = new ArrayList<Integer>();

        for (int i = 0; i < playerService.getPlayer().getHand().size(); i++) {
            if (playerService.getPlayer().getHand().get(i).getCardType().equals(CardType)) {
                typeCardsList.add(i);
            }
        }
        return typeCardsList;
    }

    /** check if a card is played */
    public Boolean checkIfPlayed(String cardName) {
        for (int i = 0; i < playerService.getPlayer().getPlayedCardsList().size(); i++) {
            if (playerService.getPlayer().getPlayedCardsList().get(i).getName().equals(cardName)) {
                return true;
            }
        }
        return false;
    }

    /** check if a card is in spec window */
    public Boolean checkIfSpec(String cardName) {
        for (int i = 0; i < playerService.getPlayer().getSpecCards().size(); i++) {
            if (playerService.getPlayer().getSpecCards().get(i).getName().equals(cardName)) {
                return true;
            }
        }
        return false;
    }

    /** sends the first card of the players deck, so it can be removed */
    public void sendFirstCard(int i) {
        Packet26DeleteFirstDeck deletcard = new Packet26DeleteFirstDeck();
        deletcard.cardname = playerService.getPlayers().get(i).getFirstDeckCard().getName();
        deletcard.playername = playerService.getPlayers().get(i).getPlayerName();
        playerService.getClient().getClient().sendTCP(deletcard);
    }

    /** sends the second card of the players deck, so it can be removed */
    public void sendSecondCard(int i) {
        Packet27DeleteSecondDeck deletcard = new Packet27DeleteSecondDeck();
        deletcard.cardname = playerService.getPlayers().get(i).getSecondDeckCard().getName();
        deletcard.playername = playerService.getPlayers().get(i).getPlayerName();
        playerService.getClient().getClient().sendTCP(deletcard);
    }

    public void sendFirstCardtoTrash(int i) {
        /** send to the glbal Trash */
        Packet20GlobalTrash globalTrashCard = new Packet20GlobalTrash();
        globalTrashCard.cardName = playerService.getPlayers().get(i).getFirstDeckCard().getName();
        playerService.getClient().getClient().sendTCP(globalTrashCard);
    }

    public void sendsecondCardtoTrash(int i) {
        /** send to the glbal Trash */
        Packet20GlobalTrash globalTrashCard = new Packet20GlobalTrash();
        globalTrashCard.cardName = playerService.getPlayers().get(i).getSecondDeckCard().getName();
        playerService.getClient().getClient().sendTCP(globalTrashCard);
    }

    /**
     * well create an image from an index in the handlist
     * 
     * @param Index
     * @return
     */
    public Image createImageFromHand(int Index) {
        return new Image(playerService.getPlayer().getHand().get(Index).getImagePath());
    }

    /**
     * if the card Effect is active
     * 
     * @return
     */
    public Boolean getkapelleActive() {
        return this.kapelleActive;
    }

    /**
     * if the card Effect is avtive
     */
    public Boolean getUmbauActive() {
        return this.umbauActive;
    }

    /**
     * if the card Effect is avtive
     */
    public Boolean getUmbauActive2() {
        return this.umbauActive2;
    }

    public Boolean getWerkstattActive() {
        return this.werkstattActive;
    }

    public boolean getSpionActive() {
        return this.spionActive;
    }

    public Boolean getFestmahlActive() {
        return this.festmahlActive;
    }

    public Boolean getMineActive() {
        return this.mineActive;
    }

    /**
     * if the card Effect is active
     * 
     * @return
     */
    public Boolean getThronsaalActive() {
        return this.thronsaalActive;
    }

    /**
     * if the card Effect is active
     * 
     * @return
     */
    public void setThronsaalActive(Boolean thronsaalActive) {
        this.thronsaalActive = thronsaalActive;
    }

    /**
     * if the card Effect is active
     * 
     * @return
     */
    public void setkapelleActive(Boolean kapelleActive) {
        this.kapelleActive = kapelleActive;
    }

    public static void setMilizActive(Boolean newMilizActive) {
        milizActive = newMilizActive;
    }

    public static void setBuerokratActive(Boolean newBuerokratActive) {
        buerokratActive = newBuerokratActive;
    }

    public static void setBuerokratLoserActive(Boolean newBuerokratActive) {
        buerokratLoserActive = newBuerokratActive;
    }

    public Boolean getMilizActive() {
        return milizActive;
    }

    public Boolean getBuerokratActive() {
        return buerokratActive;
    }

    public Boolean getBuerokratLoserActive() {
        return buerokratLoserActive;
    }

    public static void setDiebActive(Boolean newDiebActive) {
        diebActive = newDiebActive;
    }

    public Boolean getDiebActive() {
        return diebActive;
    }

    public static void setDiebActive2(Boolean newDiebActive2) {
        diebActive2 = newDiebActive2;
    }

    public Boolean getDiebActive2() {
        return diebActive2;
    }

    public void setWerkstattActive(Boolean werkstattActive) {
        this.werkstattActive = werkstattActive;
    }

    public void setUmbauActive(Boolean umbauActive) {
        this.umbauActive = umbauActive;
    }

    public void setUmbauActive2(Boolean umbauActive2) {
        this.umbauActive2 = umbauActive2;
    }

    public void setSpionActive(Boolean spionActive) {
        this.spionActive = spionActive;
    }

    public void setFestmahlActive(Boolean festmahlActive) {
        this.festmahlActive = festmahlActive;
    }

    public void setKellerActive(Boolean active) {
        this.kellerActive = active;
    }

    public Boolean getKellerActive() {
        return this.kellerActive;
    }

    public void setMineActive(Boolean mineActive) {
        this.mineActive = mineActive;
    }

    public void sendToDiscardDeck(int index) {
        playerService.getPlayer().getDiscardDeck().add(playerService.getPlayers().get(index).getFirstDeckCard());
    }

    public LinkedList<Cards> getTheRightCards(String cardtype) {
        LinkedList<Cards> list = new LinkedList<Cards>();
        for (int i = 0; i < playerService.getPlayer().getHand().size(); i++) {
            if (playerService.getPlayer().getHand().get(i).getCardType().equals(cardtype)) {
                if (mineActive) {
                    if (!playerService.getPlayer().getHand().get(i).getName().equals("Gold")) {
                        list.add(playerService.getPlayer().getHand().get(i));
                    }
                } else {
                    list.add(playerService.getPlayer().getHand().get(i));
                }
            }
        }
        return list;
    }

    public LinkedList<Cards> getTheRightCards() {
        LinkedList<Cards> list = new LinkedList<Cards>();
        for (int i = 0; i < playerService.getPlayer().getHand().size(); i++) {
            list.add(playerService.getPlayer().getHand().get(i));
        }
        return list;
    }

    public LinkedList<Cards> getTheRightCards(int limit) {
        LinkedList<Cards> list = new LinkedList<Cards>();
        for (int i = 0; i < cardpool.size(); i++) {
            if (cardpool.get(i).getCost() <= limit) {
                list.add(cardpool.get(i));
            }
        }
        return list;
    }

    public void sendpoints() {
        int counter1 = playerService.getPlayer().getDeck().size();
        int counter2 = playerService.getPlayer().getHand().size();
        int counter3 = playerService.getPlayer().getDiscardDeck().size();
        int counter = (counter1 + counter2 + counter3) / 10;
        Packet13Points points = new Packet13Points();

        if (checkIfGardenInDeck()) {
            points.points = playerService.getPlayer().getPoints() + (getNumberOfGardens() * counter);
            points.name = playerService.getPlayer().getPlayerName();
        } else {
            points.points = playerService.getPlayer().getPoints();
            points.name = playerService.getPlayer().getPlayerName();
        }
        playerService.getClient().getClient().sendTCP(points);
    }

    public int getNumberOfGardens() {
        int sum = 0;
        for (int i = 0; i < playerService.getPlayer().getDeck().size(); i++) {
            if (playerService.getPlayer().getDeck().get(i).getName() == "Gaerten") {
                sum += 1;
            }
        }
        for (int i = 0; i < playerService.getPlayer().getHand().size(); i++) {
            if (playerService.getPlayer().getHand().get(i).getName() == "Gaerten") {
                sum += 1;
            }
        }
        return sum;
    }

    public boolean checkIfGardenInDeck() {
        for (int i = 0; i < playerService.getPlayer().getDeck().size(); i++) {
            if (playerService.getPlayer().getDeck().get(i).getName() == "Gaerten") {
                return true;
            }
        }
        for (int i = 0; i < playerService.getPlayer().getHand().size(); i++) {
            if (playerService.getPlayer().getHand().get(i).getName() == "Gaerten") {
                return true;
            }
        }
        for (int i = 0; i < playerService.getPlayer().getDiscardDeck().size(); i++) {
            if (playerService.getPlayer().getDiscardDeck().get(i).getName() == "Gaerten") {
                return true;
            }
        }
        return false;
    }

    public void changeNumb(String cardname) {
        if (cardname.equals("Fluch")) {
            cardnumber.set(16, cardnumber.get(16) - 1);
        } else {
            for (int i = 0; i < cardpool.size(); i++) {
                if (cardpool.get(i).getName().equals(cardname)) {
                    if (cardnumber.get(i) > 0) {
                        cardnumber.set(i, cardnumber.get(i) - 1);
                    }
                }
            }
        }
    }

    public LinkedList<Cards> updateList() {
        LinkedList<String> newlist2 = new LinkedList<String>();
        newlist.clear();
        for (Cards card : playerService.getPlayer().getPlayedCardsList()) {
            newlist.add(card);
        }
        for (int i = newlist.size() - 1; i >= 0; i--) {
            String path = newlist.get(i).getName();
            newlist2.add(path);
        }
        Set<String> set = new LinkedHashSet<String>(newlist2);
        newlist2 = new LinkedList<String>(set);
        newlist.clear();
        for (int k = newlist2.size() - 1; k >= 0; k--) {
            Cards card = GameLogic.stringtoCards(newlist2.get(k));
            newlist.add(card);
        }
        return newlist;
    }

    public LinkedList<Cards> updateList2() {
        LinkedList<String> list2 = new LinkedList<String>();
        LinkedList<Cards> list = new LinkedList<Cards>();
        for (Cards card : playerService.getPlayer().getHand()) {
            list2.add(card.getName());
        }
        Set<String> set = new LinkedHashSet<String>(list2);
        list2 = new LinkedList<String>(set);
        Collections.sort(list2);
        for (String name : list2) {
            list.add(GameLogic.stringtoCards(name));
        }
        return list;
    }

    public void checkIfGameEnds() {
        int j = 0;
        for (int i = 0; i < cardnumber.size(); i++) {
            if (cardnumber.get(i) == 0) {
                j++;
            }
        }

        if (j == 3) {
            Packet12GameEnd end = new Packet12GameEnd();
            end.end = true;
            playerService.getClient().getClient().sendTCP(end);
        }
    }

    /**
     * This methode search the player after the actual player (is static because it
     * must be accessed by ClientNetworkListerner)
     */
    public void changeTurn(String name) {
        for (int i = 1; i <= playerService.getServerServices().getAllPlayers().size(); i++) {
            // searched which one the actual player in list
            if (playerService.getServerServices().getAllPlayers().get(i - 1).toString().equals(name)) {
                Packet04NextPlayer turnPacket = new Packet04NextPlayer();
                turnPacket.turn = true;
                // the first player of the list get his turn, when the last player clicked the
                // button
                if (i == playerService.getServerServices().numberOfPLayerConected()) {
                    turnPacket.playerN = playerService.getServerServices().getAllPlayers().getFirst().toString();
                } else {
                    turnPacket.playerN = playerService.getServerServices().getAllPlayers().get(i).toString();
                }
                playerService.getClient().getClient().sendTCP(turnPacket);
                break;
            }
        }
    }

    public static Cards stringtoCards(String name) {
        Cards card;
        switch (name) {
        case "Dorf":
            card = new Dorf();
            return card;
        case "Schmiede":
            card = new Schmiede();
            return card;
        case "Holzfaeller":
            card = new Holzfaeller();
            return card;
        case "Laboratorium":
            card = new Laboratorium();
            return card;
        case "Kanzler":
            card = new Kanzler();
            return card;
        case "Abenteurer":
            card = new Abenteurer();
            return card;
        case "Bibliothek":
            card = new Bibliothek();
            return card;
        case "Kupfer":
            card = new Kupfer();
            return card;
        case "Silber":
            card = new Silber();
            return card;
        case "Gold":
            card = new Gold();
            return card;
        case "Anwesen":
            card = new Anwesen();
            return card;
        case "Herzogtum":
            card = new Herzogtum();
            return card;
        case "Provinz":
            card = new Provinz();
            return card;
        case "Blanko":
            card = new Blanko();
            return card;
        case "Jahrmarkt":
            card = new Jahrmarkt();
            return card;
        case "Markt":
            card = new Markt();
            return card;
        case "Kapelle":
            card = new Kapelle();
            return card;
        case "Miliz":
            card = new Miliz();
            return card;
        case "Burggraben":
            card = new Burggraben();
            return card;
        case "Thronsaal":
            card = new Thronsaal();
            return card;
        case "Hexe":
            card = new Hexe();
            return card;
        case "Fluch":
            card = new Fluch();
            return card;
        case "Ratsversammlung":
            card = new Ratsversammlung();
            return card;
        case "Dieb":
            card = new Dieb();
            return card;
        case "Werkstatt":
            card = new Werkstatt();
            return card;
        case "Umbau":
            card = new Umbau();
            return card;
        case "Spion":
            card = new Spion();
            return card;
        case "Gaerten":
            card = new Gaerten();
            return card;
        case "Geldverleiher":
            card = new Geldverleiher();
            return card;
        case "Buerokrat":
            card = new Buerokrat();
            return card;
        case "Festmahl":
            card = new Festmahl();
            return card;
        case "Keller":
            card = new Keller();
            return card;
        case "Mine":
            card = new Mine();
            return card;
        default:
            card = new Dorf();
            return card;
        }
    }
}
