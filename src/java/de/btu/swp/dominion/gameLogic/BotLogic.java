package de.btu.swp.dominion.gameLogic;

import de.btu.swp.dominion.cards.*;
import de.btu.swp.dominion.network.Packets.*;
import java.util.Collections;
import java.util.LinkedList;

public class BotLogic {
    private GameLogic logic;
    private Bot bot;
    private boolean botwaiting = false;
    private LinkedList<Cards> newhand = new LinkedList<>();

    public BotLogic(Bot bot, GameLogic logic) {
        this.bot = bot;
        this.logic = logic;
    }

    /** buy the choosen card from buyingMechanism */
    public void buyCard(){
        Cards card = buyingMechanism();
        bot.setBuys(-1);
        bot.setMoney(-card.getCost());
        if (card.getCardType().equals("Punkte")) {
            bot.setPoints(card.getPoints());
        }
        bot.getDiscardDeck().add(card);
        playMoneyOnBoard(card.getCost());

        //sends for Players
        sendDiscard();
        sendHand();
        sendBuyedCard(card.getName());
        sendToLog(card.getName(), "buyCard");
    }

    /** choose the card that the Bots want */
    public Cards buyingMechanism() {
        Cards card;
        System.out.println(bot.getMoney() + "");
        if (bot.getMoney() <= 1) {
            card = new Kupfer();
        } else if (bot.getMoney() == 2) {
            int randomNumber = (int)(Math.random() * getBuyableCards(2).size());
            card = getBuyableCards(2).get(randomNumber);
        } else if (bot.getMoney() == 3) {
            int randomNumber = (int)(Math.random() * getBuyableCards(3).size());
            card = getBuyableCards(3).get(randomNumber);
        } else if (bot.getMoney() == 4 && getBuyableCards(4).size() != 0) {
            int randomNumber = (int)(Math.random() * getBuyableCards(4).size());
            card = getBuyableCards(4).get(randomNumber);
        } else if (bot.getMoney() == 5) {
            int randomNumber = (int)(Math.random() * getBuyableCards(5).size());
            card = getBuyableCards(5).get(randomNumber);
        } else if (bot.getMoney() <= 7) {
            int randomNumber = (int)(Math.random() * getBuyableCards(6).size());
            card = getBuyableCards(6).get(randomNumber);
        } else if (bot.getMoney() >= 8) {
            card = new Provinz();
        } else {
            int randomNumber = (int)(Math.random() * getBuyableCards(3).size());
            card = getBuyableCards(3).get(randomNumber);
        }
        
        System.out.println(card.getName() + "");
        return card;
    }

    public LinkedList<Cards> getBuyableCards(int cost) {
        LinkedList<Cards> copyList = new LinkedList<>();
        for (int i = 0; i < GameLogic.getCardspool().size(); i++) {
            if (GameLogic.getCardspool().get(i).getCost() == cost && GameLogic.getCardnumber().get(i) > 0) {
                copyList.add(GameLogic.getCardspool().get(i));
            }
        }
        return copyList;
    }

    /** starts the flow of the Turn */
    public void startTurn() {
        System.out.println(bot.getPlayers().size() + "");
        sendHand();
        for (int i = 0; i < bot.getAction(); i++) {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println(e);
            }
            layActionCards();
        }

        for (int i = 0; i < bot.getBuys(); i++) {
            buyCard();
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        endTurn();
        sendDiscard();
        sendHand();
    }

    public void sendCardFromHandToDiscard(int index) {
        bot.getDiscardDeck().add(bot.getHand().get(index));
        bot.getHand().remove(index);
    }


    /** cardsLeft says when the deck is empty (for Dieb z.B. when 1 card in Deck) */
    public void emptyDeck(int cardsLeft) {
        if (bot.getDeck().size() <= cardsLeft) {
            while (bot.getDiscardDeck().size() > 0) {
                bot.getDeck().add(bot.getDiscardDeck().get(0));
                bot.getDiscardDeck().remove(0);
            }
            Collections.shuffle(bot.getDeck());
        }
    }

    public void draw5Cards() {
        for (int i = 0; i < 5; i++) {
            drawACard();
        }
        sendHand();
    }

    public void drawACard() {
        emptyDeck(0);
        bot.getHand().add(bot.getDeck().get(0));
        if (bot.getDeck().get(0).getCardType().equals("Geld")) {
            bot.setMoney(bot.getDeck().get(0).getPoints());
        }
        bot.getDeck().remove(0);
    }

    public void layActionCards() {
        LinkedList<Cards> cardsList = copyActionCardsFromHand();

        if (bot.getAction() > 0 && cardsList.size() != 0) {
            int randomNumber = (int)(Math.random() * cardsList.size());
            bot.getPlayedCardsList().add(cardsList.get(randomNumber));
            cardsList.remove(randomNumber);
            for (Cards card : cardsList) {
                bot.getHand().add(card);
            }
            sendToLog(bot.getPlayedCardsList().getLast().getName(), "playCard");
            //send to Player for GUI
            Packet10RenderSpecator zug = new Packet10RenderSpecator();
            zug.cardName = bot.getPlayedCardsList().getLast().getName();
            bot.getClient().getClient().sendTCP(zug);
            launchCardsEffects();
        }
        sendHand();
    }

    public LinkedList<Cards> copyActionCardsFromHand () {
        LinkedList<Cards> cardsList = new LinkedList<>();
        for (int i = 0; i < bot.getHand().size(); i++) {
            if (bot.getHand().get(i).getCardType().equals("Aktion")) {
                cardsList.add(bot.getHand().get(i));
                bot.getHand().remove(i);
            }
        }
        return cardsList;
    }

    /**
     * well take a string and publish it to all player in the log list used a
     * trigger to decied the kind of the log message
     */
    public void sendToLog(String cardName, String trigger) {
        Packet15log log = new Packet15log();
        String playerName = "[" + bot.getPlayerName() + "]:";

        if (trigger.equals("playCard")) {
            log.logMessage = playerName + " spielte " + cardName + ".";
        } else if (trigger.equals("endTurn")) {
            log.logMessage = playerName + " beendete seinen Zug.";
        } else if (trigger.equals("buyCard")) {
            log.logMessage = playerName + " kaufte " + cardName + ".";
        } else if (trigger.equals("takeCard")) {
            log.logMessage = playerName + " nahm sich " + cardName + ".";
        }
        bot.getClient().getClient().sendTCP(log);
    }

    /** plays money cards from hand */
    public void playMoneyOnBoard(int price) {
        int anzahlKupfer = 0;
        if (bot.getPlayedCardsList().size() > 0) {
            for (int a = bot.getPlayedCardsList().size() - 1; a >= 0; a--) {
                if (bot.getCardNamePlayed(a).equals("Kupfer")) {
                    anzahlKupfer++;
                }
            }
        }

        for (int k = bot.getHand().size() - 1; k >= 0; k--) {
            if (anzahlKupfer >= price) {
                break;
            } else {
                if (bot.getCardName(k) == "Kupfer") {
                    bot.getPlayedCardsList().add(bot.getHand().get(k));
                    anzahlKupfer++;
                    bot.getHand().remove(k);
                    newhand = bot.getHand();
                    bot.setHand(newhand);
                } else if (bot.getCardName(k) == "Silber") {
                    bot.getPlayedCardsList().add(bot.getHand().get(k));
                    anzahlKupfer = anzahlKupfer + 2;
                    bot.getHand().remove(k);
                    newhand = bot.getHand();
                    bot.setHand(newhand);
                } else if (bot.getCardName(k) == "Gold") {
                    bot.getPlayedCardsList().add(bot.getHand().get(k));
                    anzahlKupfer = anzahlKupfer + 3;
                    bot.getHand().remove(k);
                    newhand = bot.getHand();
                    bot.setHand(newhand);
                }
            }
        }
    }

    public void endTurn() {
        cleanBord();
        checkIfGameEnds();
        bot.getStartValue();
        draw5Cards();
    
        // send to Players for GUI
        sendToLog("null", "endTurn");
        sendHand();
        Packet06EndTurn player = new Packet06EndTurn();
        player.name = bot.getPlayerName();
        bot.getClient().getClient().sendTCP(player);
    }

    /** remove all card from hand and playground */
    public void cleanBord() {
        cleanPlayedCards();
        cleanHandCards();
        sendDiscard();
    }
    
    public void cleanPlayedCards () {
        for (int i = 0; i < bot.getPlayedCardsList().size(); i++) {
            bot.getDiscardDeck().add(bot.getPlayedCardsList().getLast());
            bot.getPlayedCardsList().remove(bot.getPlayedCardsList().getLast());
        }
    }

    public void cleanHandCards() {
        bot.getDiscardDeck().addAll(bot.getHand());
        bot.setHand(new LinkedList<Cards>());
    }

    /** send number of cards in hand */
    public void sendHand() {
        Packet09HandCardNumber card2 = new Packet09HandCardNumber();
        card2.NumberOfCardInHand = bot.getHand().size();
        card2.playerName = bot.getPlayerName();
        bot.getClient().getClient().sendTCP(card2);
    }

    /** send last Discard Card to Player for GUI */
    public void sendDiscard () {
        if (bot.getDiscardDeck().size() > 0) {
            Packet08DiscardDeck card = new Packet08DiscardDeck();
            card.cardName = bot.getDiscardDeck().getLast().getImagePath();
            card.playerName = bot.getPlayerName();
            bot.getClient().getClient().sendTCP(card);
        } else {
            Packet08DiscardDeck card = new Packet08DiscardDeck();
            card.cardName = "/src/main/resources/backgrounds/Blanko.png";
            card.playerName = bot.getPlayerName();
            bot.getClient().getClient().sendTCP(card);
        }
    }

    public void launchCardsEffects() {
        Cards card = bot.getPlayedCardsList().getLast();
        System.out.println(card.getName()+ " : " + bot.getPlayerName());
        for (int i = 0; i < card.getEffect().size(); i++) {
            if (card.getEffect().get(i) == "addCard") {
                drawACard();
            } else if (card.getEffect().get(i) == "addAction") {
                bot.setAction(1);
            } else if (card.getEffect().get(i) == "addBuy") {
                bot.setBuys(1);
            } else if (card.getEffect().get(i) == "addMoney") {
                bot.setMoney(1);
            } else if (card.getEffect().get(i) == "addKapelleEffect") {
                activateKapelle();
            } else if (card.getEffect().get(i) == "addWerkstattEffect") {
                activateWerkstatt();
            } else if (card.getEffect().get(i) == "addFestmahlEffect") {
                activateFestmahl();
            } else if (card.getEffect().get(i) == "addGeldverleiherEffect") {
                activateGeldverleiher();
            } else if (card.getEffect().get(i) == "addKanzlerEffekt") {
                activateKanzler();
            } else if (card.getEffect().get(i) == "addRatsEffekt") {
                activateRatsversammlung();
            } else if (card.getEffect().get(i) == "addThronsaalEffect") {
                activateThronsaal();
            } else if (card.getEffect().get(i) == "addHexeEffect") {
                activateHexe();
            } else if (card.getEffect().get(i) == "addUmbauEffect") {
                activateUmbau();
            } else if (card.getEffect().get(i) == "addSpionEffect") {
                activateSpion();
            } else if (card.getEffect().get(i) == "addMilizEffekt") {
                activateMiliz();
            } else if (card.getEffect().get(i) == "addBuerokratEffekt") {
                activateBuerokrat();
            } else if (card.getEffect().get(i) == "addAdventure") {
                activateAbenteurer();
            } else if (card.getEffect().get(i) == "addCardLibrary") {
                activateBibliothek();
            } else if (card.getEffect().get(i) == "addDiebEffekt") {
                activateDieb();
            } else if (card.getEffect().get(i) == "addKellerEffect") {
                activateKeller();
            } else if (card.getEffect().get(i) == "addMineEffekt") {
                activateMine();
            }

            bot.setAction(-1);
            sendHand();
        }
    }

    /** send to global trash and delete card from hand */
    public void sendCardToTrash(int index) {
        Packet20GlobalTrash globalTrashCard = new Packet20GlobalTrash();
        globalTrashCard.cardName = bot.getHand().get(index).getName();
        bot.getClient().getClient().sendTCP(globalTrashCard);
        if (bot.getHand().get(index).getCardType().equals("Geld")) {
            bot.setMoney(-bot.getHand().get(index).getPoints());
        } else if (bot.getHand().get(index).getCardType().equals("Punkte")) {
            bot.setPoints(-bot.getHand().get(index).getPoints());
        }
        bot.getHand().remove(index);
    }

    public void takeCard(Cards card) {
        bot.getDiscardDeck().add(card);
        if (card.getCardType() == "Punkte") {
            bot.setPoints(card.getPoints());
        }

        sendBuyedCard(card.getName());
        sendToLog(card.getName(), "takeCard");
    }

    public void activateKapelle() {
        int randomNumber = (int)(Math.random() * bot.getHand().size());
        sendCardToTrash(randomNumber);
    }
    
    public void activateWerkstatt() {
        LinkedList<Cards> copyList = logic.getTheRightCards(4);
        int randomNumber = (int)(Math.random() * copyList.size());
        takeCard(copyList.get(randomNumber));
    }
    
    public void activateFestmahl() {
        LinkedList<Cards> copyList = logic.getTheRightCards(5);
        int randomNumber = (int)(Math.random() * copyList.size());
        takeCard(copyList.get(randomNumber));
        bot.getHand().add(bot.getPlayedCardsList().getLast());
        bot.getPlayedCardsList().removeLast();
        sendCardToTrash(bot.getHand().size() - 1);
    }

    public void activateGeldverleiher() {
        for (int i = 0; i < bot.getHand().size(); i++) {
            if (bot.getHand().get(i).getName().equals("Kupfer")) {
                sendCardToTrash(i);
                bot.setMoney(3);
            }
        }
    }

    public void activateKanzler() {
        bot.setMoney(2);
        if (bot.getDeck().size() > 0) {
            bot.getDiscardDeck().addAll(bot.getDeck());
            bot.setDeck(new LinkedList<Cards>());
        }
    }

    public void activateRatsversammlung() {
        Packet17RatsEffect effect = new Packet17RatsEffect();
        effect.name = bot.getPlayerName();
        bot.getClient().getClient().sendTCP(effect);
    }

    public void activateThronsaal() {
        if (copyActionCardsFromHand().size() != 0) {
            bot.setAction(1);
            layActionCards();
            launchCardsEffects();
        }
    }

    public void activateHexe() {
        Packet10HexeEffect hexeCard = new Packet10HexeEffect();
        hexeCard.ownerName = bot.getPlayerName();
        bot.getClient().getClient().sendTCP(hexeCard);
    }

    public void activateUmbau() {
        int randomNumber = (int)(Math.random() * bot.getHand().size());
        LinkedList<Cards> list = logic.getTheRightCards(bot.getHand().get(randomNumber).getCost() + 2);
        sendCardToTrash(randomNumber);
        int newRandomNumber = (int)(Math.random() * list.size());
        takeCard(list.get(newRandomNumber));
    }

    public void activateSpion() {
        Packet24SpionEffect spionEffect = new Packet24SpionEffect();
        spionEffect.playername = bot.getPlayerName();
        bot.getClient().getClient().sendTCP(spionEffect);
        botwaiting = true;
        while (botwaiting) {
            try {
                Thread.sleep(50);
            } catch (Exception e) {
                System.out.println(e);
            }
            int j = 0;
            for (int i = 0; i < bot.getPlayers().size(); i++) {
                if (bot.getPlayers().get(i).getFirstDeckCard() != null) {
                    j++;
                }
            }
            if (bot.getPlayers().size() == j) {
                botwaiting = false;
            }
        }
        for (int i = 0; i < bot.getPlayers().size() + 1; i++) {
            int randomNumber = (int)(Math.random() * 2);
            if (i == 0 && randomNumber == 0) {
                sendCardFromDeckToDiscard();
            } else if (randomNumber == 0 && i != 0) {
                OtherPlayers player = bot.getPlayers().get(i - 1);
                Packet28RemoveFirstDeck deletcard = new Packet28RemoveFirstDeck();
                deletcard.playername = player.getPlayerName();
                bot.getClient().getClient().sendTCP(deletcard);
            }
        }
        for (int i = 0; i < bot.getPlayers().size(); i++) {
            bot.getPlayers().get(i).setFirstDeckCard(null);
        }
    }

    public void sendCardFromDeckToDiscard() {
        bot.getDiscardDeck().add(bot.getDeck().get(0));
        bot.getDeck().remove(0);
    }

    public void activateMiliz() {
        Packet11MilizEffekt effect = new Packet11MilizEffekt();
        effect.playPlayer = bot.getPlayerName();
        bot.getClient().getClient().sendTCP(effect);
    }
    public void reactToMiliz() {
        while (bot.getHand().size() > 3) {
            int randomNumber = (int)(Math.random() * bot.getHand().size());
            bot.getDiscardDeck().add(bot.getHand().get(randomNumber));
            bot.getHand().remove(randomNumber);
        }
        sendHand();
        sendDiscard();
    }

    public void activateBuerokrat() {
        Silber silber = new Silber();
        bot.getDeck().addFirst(silber);
        sendBuyedCard(silber.getName());
        Packet29Buerokrat BuerokratPacket = new Packet29Buerokrat();
        BuerokratPacket.ownerName = bot.getPlayerName();
        bot.getClient().getClient().sendTCP(BuerokratPacket);
    }

    public void reactToBuerokrat(int haspoints) {
        bot.getDeck().addFirst(bot.getHand().get(haspoints));
        bot.getHand().remove(haspoints);
        sendHand();
    }

    public void activateAbenteurer() {
        int numberOfMoneyCards = 0;
        while (numberOfMoneyCards < 2) {
            if (bot.getDeck().size() == 0) {
                emptyDeck(0);
                if (bot.getDeck().size() == 0) {
                    return;
                }
            } else {
                if (bot.getDeck().get(0).getCardType().equals("Geld")) {
                    numberOfMoneyCards++;
                    drawACard();
                } else {
                    bot.getDiscardDeck().add(bot.getDeck().get(0));
                    bot.getDeck().remove(0);
                }
            }
        }
    }

    public void activateBibliothek() {
        while (bot.getHand().size() < 7) {
            if (bot.getDeck().size() > 0) {
                if (bot.getDeck().get(0).getCardType().equals("Aktion")) {
                    int randomNumber = (int)(Math.random() * 2);
                    if (randomNumber == 0) {
                        sendCardFromDeckToDiscard();
                    } else {
                        drawACard();
                    }
                } else {
                    drawACard();
                }
            } else {
                drawACard();
            }
        }
    }

    public void activateKeller() {
        int randomNumber = (int)(Math.random() * bot.getHand().size());
        for (int j = 0; j < randomNumber; j++) {
            int discardCard = (int)(Math.random() * bot.getHand().size());
            sendCardFromHandToDiscard(discardCard);
        }
        for (int j = 0; j < randomNumber; j++) {
            drawACard();
        }
    }

    public void activateMine() {
        int j = -1;
        for (int i = 0; i < bot.getHand().size(); i++) {
            if (bot.getHand().get(i).getCardType().equals("Geld")) {
                int randomNumber = (int)(Math.random() * 2);
                j = i;
                if (randomNumber == 0) {
                    break;
                }

            }
        }
    
        if (j != -1) {
            if (bot.getHand().get(j).getName().equals("Kupfer")) {
                sendCardToTrash(j);
                bot.getHand().add(j, new Silber());
                sendBuyedCard("Silber");
                bot.setMoney(2);
            } else {
                sendCardToTrash(j);
                bot.getHand().set(j, new Gold());
                sendBuyedCard("Gold");
                bot.setMoney(3);
            }
        }
    }

    public void activateDieb() {
        Packet23DiebEffekt effekt = new Packet23DiebEffekt();
        effekt.playername = bot.getPlayerName();
        bot.getClient().getClient().sendTCP(effekt);
        botwaiting = true;
        while (botwaiting) {
            try {
                Thread.sleep(50);
            } catch (Exception e) {
                System.out.println(e);
            }
            int j = 0;
            for (int i = 0; i < bot.getPlayers().size(); i++) {
                if (bot.getPlayers().get(i).getFirstDeckCard() != null && bot.getPlayers().get(i).getSecondDeckCard() != null) {
                    j++;
                }
            }
            if (bot.getPlayers().size() == j) {
                botwaiting = false;
            }
        }
        for (int i = 0; i < bot.getPlayers().size(); i++) {
            if (bot.getPlayers().get(i).getFirstDeckCard().getCardType().equals("Geld")) {
                int randomNumber = (int)(Math.random() * 2);
                if (randomNumber == 0 || !bot.getPlayers().get(i).getSecondDeckCard().getCardType().equals("Geld")) {
                    Packet26DeleteFirstDeck deletePacket = new Packet26DeleteFirstDeck();
                    deletePacket.playername = bot.getPlayers().get(i).getPlayerName();
                    bot.getClient().getClient().sendTCP(deletePacket);
                    bot.getDiscardDeck().add(bot.getPlayers().get(i).getFirstDeckCard());
                } else {
                    Packet27DeleteSecondDeck deletePacket = new Packet27DeleteSecondDeck();
                    deletePacket.playername = bot.getPlayers().get(i).getPlayerName();
                    bot.getClient().getClient().sendTCP(deletePacket);
                    bot.getDiscardDeck().add(bot.getPlayers().get(i).getSecondDeckCard());
                }
            } else if (bot.getPlayers().get(i).getSecondDeckCard().getCardType().equals("Geld")) {
                Packet27DeleteSecondDeck deletePacket = new Packet27DeleteSecondDeck();
                deletePacket.playername = bot.getPlayers().get(i).getPlayerName();
                bot.getClient().getClient().sendTCP(deletePacket);
                bot.getDiscardDeck().add(bot.getPlayers().get(i).getSecondDeckCard());
            }
        }
        sendDiscard();
    }

    public int getNumberOfGardens() {
        int sum = 0;
        for (int i = 0; i < bot.getDeck().size(); i++) {
            if (bot.getDeck().get(i).getName().equals("Gaerten")) {
                sum++;
            }
        }
        for (int i = 0; i < bot.getDiscardDeck().size(); i++) {
            if (bot.getDiscardDeck().get(i).getName() == "Gaerten") {
                sum++;
            }
        }
        return sum;
    }

    public void sendpoints() {
        int counter1 = bot.getDeck().size();
        int counter2 = bot.getDiscardDeck().size();
        int counter = (counter1 + counter2) / 10;
        Packet13Points points = new Packet13Points();
        int sumGarden = getNumberOfGardens();
        if (sumGarden != 0) {
            points.points = bot.getPoints() + (sumGarden * counter);
            points.name = bot.getPlayerName();
        } else {
            points.points = bot.getPoints();
            points.name = bot.getPlayerName();
        }
        bot.getClient().getClient().sendTCP(points);
    }

    public void checkIfGameEnds() {
        int j = 0;
        for (int i = 0; i < GameLogic.getCardnumber().size(); i++) {
            if (GameLogic.getCardnumber().get(i) == 0) {
                j++;
            }
        }

        if (j == 3 || GameLogic.getCardnumber().get(12) == 0) {
            Packet12GameEnd end = new Packet12GameEnd();
            end.end = true;
            bot.getClient().getClient().sendTCP(end);

            sendpoints();
        }
    }

    public void sendBuyedCard(String card){
        Packet14Card2 boughtCard = new Packet14Card2();
        boughtCard.name = card;
        bot.getClient().getClient().sendTCP(boughtCard);
    }
}
