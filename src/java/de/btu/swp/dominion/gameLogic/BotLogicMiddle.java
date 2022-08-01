package de.btu.swp.dominion.gameLogic;

import de.btu.swp.dominion.cards.*;
import de.btu.swp.dominion.network.Packets;
import de.btu.swp.dominion.network.Packets.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class BotLogicMiddle {
    private Random random = new Random();
    private GameLogic logic;
    private PlayerService playerService;
    private Bot bot;
    private static LinkedList<Cards> trashCards = new LinkedList<>();
    private static LinkedList<Integer> cardnumber = new LinkedList<Integer>();
    private static ArrayList<Cards> cardpool = new ArrayList<>();
    private LinkedList<Cards> newhand = new LinkedList<>();

    public BotLogicMiddle(Bot bot, PlayerService player, GameLogic logic) {
        this.bot = bot;
        this.playerService = player;
        this.logic = logic;
    }
    
    /** starts the flow of the Turn */
    public void startTurn() {
        bot.getStartValue();
        System.out.println("The bot has " + bot.getHand().size() + " Karten in seinem Hand");
        sendHand();
        for (int i = 0; i < bot.getAction(); i++) {
            layActionCards();
            try {
                // thread to sleep for 4000 milliseconds
                Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        for (int i = 0; i < bot.getBuys(); i++) {
            buyCard();
            try {
                // thread to sleep for 4000 milliseconds
                Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        endTurn();
    }

    public void buyCard() {
        Cards card = buyingMechanism();
        if (card.getCardType() == "Punkte") {
            bot.setPoints(card.getPoints());
        }
        bot.getDiscardDeck().add(card);
        bot.setMoney(- card.getCost());
        sendBuyedCard(card);
        playMoneyOnBoard(card.getCost());
        sendToLog(card.getName(), "buyCard");
    }

    public Cards buyingMechanism() {
        Cards card;
        if  (bot.getMoney() <= 1) {
            card = new Kupfer();
        } else if (bot.getMoney() == 2) {
            // int randomNumber = Math.abs(random.nextInt()) % copyWerte(2).size();
            card = bestChoice(2);
        } else if (bot.getMoney() == 3) {
            // int randomNumber = Math.abs(random.nextInt()) % copyWerte(3).size();
            card = bestChoice(3);
        } else if (bot.getMoney() == 4){
            // int randomNumber = Math.abs(random.nextInt()) % copyWerte(4).size();
            card = bestChoice(4);
        } else if (bot.getMoney() == 5) {
            // int randomNumber = Math.abs(random.nextInt()) % copyWerte(5).size();
            card = bestChoice(5);
        } else if (bot.getMoney() == 6) {
            // int randomNumber = Math.abs(random.nextInt()) % copyWerte(5).size();
            card = bestChoice(6);
        } else if (bot.getMoney() == 7) {
            // int randomNumber = Math.abs(random.nextInt()) % copyWerte(6).size();
            card = bestChoice(7);
        } else if (bot.getMoney() >= 8) {
            card = new Provinz();
        } else {
            // int randomNumber = Math.abs(random.nextInt()) % copyWerte(3).size();
            card = new Kupfer();
        }
        bot.setBuys(-1);
        sendDiscard();
        sendHand();
        return card;
    }

    public LinkedList<Cards> copyWerte(int cost) {
        LinkedList<Cards> copyList = new LinkedList<>();
        for (int i = 0; i < GameLogic.getCardspool().size(); i++) {
            if (GameLogic.getCardspool().get(i).getCost() == cost) {
                copyList.add(GameLogic.getCardspool().get(i));
            }
        }
        return copyList;
    }


    public Cards bestChoice(int cost) {
        Cards bestCard;
        LinkedList<Cards> copyList = new LinkedList<>();
        // Holt Liste möglichst teuersten Karten
        while (copyList.size() < 1){
            copyList = copyWerte(cost);
            cost = cost-1;
        }
        bestCard = copyList.get(Math.abs(random.nextInt(cost)));
        // wählt möglichst beste Karte aus.... Prioritätenliste im SWTICH
        for (int i = 0; i < copyList.size(); i++) {
            switch (copyList.get(i).getName()) {
                    case "Jahrmarkt":
                    bestCard = copyList.get(i);
                    return bestCard;
                    case "Markt":
                    bestCard = copyList.get(i);
                    return bestCard;
                    case "Dorf":
                    bestCard = copyList.get(i);
                    return bestCard;
                    case "Schmiede":
                    bestCard = copyList.get(i);
                    default:
                    bestCard = copyList.get(Math.abs(random.nextInt(cost)));
            }
        }
        return bestCard;
    }
    /** Kann alles gelöscht werden aber schau dir an warum die eine Methode darüber das gleiche macht wie alle darunter.
     */
/*    public LinkedList<Cards> copyWerte2to4() {
        LinkedList<Cards> copyList = new LinkedList<>();
        for(int i = 0 ; i < GameLogic.getCardspool().size(); i++) {
            if (GameLogic.getCardspool().get(i).getCost() >= 2 && GameLogic.getCardspool().get(i).getCost() <= 4) {
                copyList.add(GameLogic.getCardspool().get(i));
            }
        }
        return copyList;
    }

    public LinkedList<Cards> copyWerte5to6() {
        LinkedList<Cards> copyList = new LinkedList<>();
        for (int i = 0; i < GameLogic.getCardspool().size(); i++) {
            if (GameLogic.getCardspool().get(i).getCost() >= 5 && GameLogic.getCardspool().get(i).getCost() <= 6) {
                copyList.add(GameLogic.getCardspool().get(i));
            }
        }
        return copyList;
    }

    public LinkedList<Cards> copyWerte2() {
        LinkedList<Cards> copyList = new LinkedList<>();
        for(int i = 0; i < copyWerte2to4().size(); i++) {
            if(copyWerte2to4().get(i).getCost() ==2) {
                copyList.add(copyWerte2to4().get(i));
            }
        }
        return copyList;
    }
    public LinkedList<Cards> copyWerte3() {
        LinkedList<Cards> copyList = new LinkedList<>();
        for(int i = 0; i < copyWerte2to4().size(); i++) {
            if(copyWerte2to4().get(i).getCost() ==3) {
                copyList.add(copyWerte2to4().get(i));
            }
        }
        return copyList;
    }
    public LinkedList<Cards> copyWerte4() {
        LinkedList<Cards> copyList = new LinkedList<>();
        for(int i = 0; i < copyWerte2to4().size(); i++) {
            if(copyWerte2to4().get(i).getCost() ==4) {
                copyList.add(copyWerte2to4().get(i));
            }
        }
        return copyList;
    }

    public LinkedList<Cards> copyWerte5() {
        LinkedList<Cards> copyList = new LinkedList<>();
        for(int i = 0; i < copyWerte5to6().size(); i++) {
            if(copyWerte5to6().get(i).getCost() ==5) {
                copyList.add(copyWerte2to4().get(i));
            }
        }
        return copyList;
    }

    public LinkedList<Cards> copyWerte6() {
        LinkedList<Cards> copyList = new LinkedList<>();
        for(int i = 0; i < copyWerte5to6().size(); i++) {
            if(copyWerte5to6().get(i).getCost() ==6) {
                copyList.add(copyWerte2to4().get(i));
            }
        }
        return copyList;
    }*/

    public void sendCardToDiscard(int index) {

    }

    /** i says when the deck is empty (for Dieb z.B. when 1 card in Deck) */
    public void emptyDeck(int i) {
        if (bot.getDeck().size() <= i) {
            while (bot.getDiscardDeck().size() > 0) {
                bot.getDeck().add(bot.getDiscardDeck().get(0));
                bot.getDiscardDeck().remove(0);
            }
            Collections.shuffle(bot.getDeck());
        }

        sendDiscard();
        sendHand();
    }

    public void draw5Cards() {
        for (int i = 0; i < 5; i++) {
            drawACard();
        }
    }

    public void drawACard() {
        emptyDeck(0);
        bot.getHand().add(bot.getDeck().get(0));
        if (bot.getDeck().get(0).getCardType().equals("Geld")) {
            bot.setMoney(bot.getDeck().get(0).getPoints());
        }
        bot.getDeck().remove(0);
        sendHand();
    }

    public void layActionCards() {
        LinkedList<Cards> cardsList = copyActionCardsFromHand();

        if(bot.getAction() > 0 && cardsList.size() != 0) {
            int randomNumber = Math.abs(random.nextInt()) % cardsList.size();
            int bestActionNumber = getBestActionNumber(cardsList);
            if (bestActionNumber >= 0){
                randomNumber = bestActionNumber;
            }
            bot.getPlayedCardsList().add(cardsList.get(randomNumber));
            launchCardsEffects(cardsList.get(randomNumber));
            bot.getHand().remove(randomNumber);
            System.out.println("The bot played a " + bot.getPlayedCardsList().getLast().getName());
            Cards card = bot.getPlayedCardsList().getLast();
            sendToLog(card.getName(), "playCard");

            Packet10RenderSpecator zug = new Packets.Packet10RenderSpecator();
            zug.cardName = bot.getPlayedCardsList().getLast().getName();
            bot.getClient().getClient().sendTCP(zug);
        }

        sendHand();

    }

    private int getBestActionNumber(LinkedList<Cards> cardsList){
        for (int i = 0; i < cardsList.size(); i++) {
            switch (cardsList.get(i).getName()) {
                case "Thronsaal":
                    return i;    
                case "Jahrmarkt":
                    return i;
                case "Markt":
                    return i;
                case "Dorf":
                    return i;
                case "Schmiede":
                    return i;
                default:
                    return -1;
            }
        }
        return -1;
    }

    /**
     * well take a string and publish it to all player in the log list used a
     * trigger to decied the kind of the log message
     */
    public void sendToLog(String CardName, String trigger) {
        Packet15log log = new Packets.Packet15log();
        String playerName = "[" + bot.getPlayerName() + "]:";

        if (trigger.equals("playCard")) {
            log.logMessage = playerName + " spielte " + CardName + ".";
        } else if (trigger.equals("endTurn")) {
            log.logMessage = playerName + " beendete seinen Zug.";
        } else if (trigger.equals("buyCard")) {
            log.logMessage = playerName + " kaufte " + CardName + ".";
        }
        bot.getClient().getClient().sendTCP(log);
    }

    public void launchCardsEffects(Cards card) {
        for(int i = 0; i<card.getEffect().size(); i++) {
            if(card.getEffect().get(i) == "addCard") {
                drawACard();
            } else if (card.getEffect().get(i) == "addAction") {
                bot.setAction(1);
                System.out.println("The bot has " + bot.getAction() + " Actions");
            } else if (card.getEffect().get(i) == "addBuy") {
                bot.setBuys(1);
                System.out.println("The bot has " + bot.getBuys() + " buys");
            } else if (card.getEffect().get(i) == "addMoney") {
                bot.setMoney(1);
                System.out.println("The bot has " + bot.getMoney() + " money");
            }  else if (card.getEffect().get(i) == "addKapelleEffect") {
                int randomNumber = Math.abs(random.nextInt()) % bot.getHand().size();
                kapellePlayed(randomNumber);
            } else if (card.getEffect().get(i) == "addWerkstattEffect") {
                werkstattPlayed();
            } else if (card.getEffect().get(i) == "addFestmahlEffect") {
                getFestMahlPlayed();
                festmahlActive();
            } else if (card.getEffect().get(i) == "addGeldverleiherEffect") {
                geldVerleiherPlayed();
            } else if (card.getEffect().get(i) == "addKanzlerEffekt") {
                kanzlerPlayed();
            } else if (card.getEffect().get(i) == "addRatsEffekt") {
                Packet17RatsEffect effect = new Packet17RatsEffect();
                effect.name = bot.getPlayerName();
                bot.getClient().getClient().sendTCP(effect);
            } else if (card.getEffect().get(i) == "addThronsaalEffect") {
                thronSaalPlayed();
            } else if (card.getEffect().get(i) == "addHexeEffect") {
                hexePlayed();
            } else if (card.getEffect().get(i) == "addUmbauEffect") {
                int randomNumber = Math.abs(random.nextInt()) % bot.getHand().size();
                card = bot.getHand().get(randomNumber);
                kapellePlayed(randomNumber);
                System.out.println("The bot has trashed a "  + card.getName());
                LinkedList<Cards> list = getCardsFromPool(card.getCost());
                int newRandomNumber = Math.abs(random.nextInt()) %list.size();

                bot.getDiscardDeck().add(list.get(newRandomNumber));

                System.out.println("The bot has picked a " + list.get(newRandomNumber).getName());


            } else if (card.getEffect().get(i) == "addSpionEffect") {
                Packet24SpionEffect spionEffect = new Packet24SpionEffect();
                spionEffect.playername = bot.getPlayerName();
                for (int j = 0; j < playerService.getPlayers().size(); j++) {
                    if(playerService.getPlayers().size() < 0) {
                        playerService.getPlayers().get(0).getFirstDeckCard();
                    }

                }
                bot.getClient().getClient().sendTCP(spionEffect);

            } else if (card.getEffect().get(i) == "addMilizEffekt") {
                Packet11MilizEffekt effect = new Packet11MilizEffekt();
                effect.playPlayer = bot.getPlayerName();
                bot.getClient().getClient().sendTCP(effect);
            } else if (card.getEffect().get(i) == "addBuerokratEffekt") {
                Silber silber = new Silber();
                Packet14Card2 boughtCard = new Packet14Card2();
                boughtCard.name = silber.getName();
                bot.getClient().getClient().sendTCP(boughtCard);
                bot.getDeck().addFirst(silber);


                Packet29Buerokrat BuerokratPacket = new Packet29Buerokrat();
                BuerokratPacket.ownerName = bot.getPlayerName();
                bot.getClient().getClient().sendTCP(BuerokratPacket);
            } else if (card.getEffect().get(i) == "addAdventure") {
                int numberOfMoneyCards = 0;
                while (numberOfMoneyCards < 2) {
                    if (bot.getDeck().size() == 0) {
                        bot.setDeck(bot.getDiscardDeck());
                        bot.setDiscardDeck(new LinkedList<Cards>());
                        if (bot.getDeck().size() == 0) {
                            return;
                        }
                    } else {
                        if (bot.getDeck().get(0).getCardType() == "Geld") {
                            numberOfMoneyCards++;
                            drawACard();
                            System.out.println("The bot has drawn a " + bot.getDeck().get(0).getName() + " after adventure");
                        } else {
                            bot.getDiscardDeck().add(bot.getDeck().get(0));
                            bot.getDeck().remove(0);
                        }
                    }
                }
            } else if (card.getEffect().get(i) == "addCardLibrary") {
                int randomNumber = random.nextInt()%2;
                while(bot.getHand().size() <7) {
                    if(bot.getDeck().size() > 0) {
                        if(bot.getDeck().get(0).getCardType() == "Aktion") {
                            if(randomNumber == 0) {
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
            } else if (card.getEffect().get(i) == "addDiebEffekt") {
                Packet23DiebEffekt effekt = new Packet23DiebEffekt();
                effekt.playername = bot.getPlayerName();
                bot.getClient().getClient().sendTCP(effekt);
            }
        }
        bot.setAction(-1);
        sendHand();
    }

    /**
     * plays money cards from hand and play them TODO: change so we can get the
     * money for all cards and to accept silver and gold as well as money
     */
    public void playMoneyOnBoard(int price) {
        int anzahlKupfer = 0;
        if (bot.getPlayedCardsList().size() > 0) {
            for (int a = bot.getPlayedCardsList().size() - 1; a >= 0; a--) {
                if (bot.getCardNamePlayed(a) == "Kupfer") {
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
        // otherwise the player send the next player to the host
        update();
        sendToLog("null","endTurn");


        Packet09HandCardNumber card2 = new Packet09HandCardNumber();
        card2.NumberOfCardInHand = bot.getHand().size();
        card2.playerName = bot.getPlayerName();
        bot.getClient().getClient().sendTCP(card2);


        sendHand();

        draw5Cards();

        Packet06EndTurn player = new Packet06EndTurn();
        player.name = bot.getPlayerName();
        bot.getClient().getClient().sendTCP(player);

    }



    public void update() {
        cleanPlayedCards();
        cleanHandCards();
        sendDiscard();
    }

    public void sendHand() {
        Packet09HandCardNumber card2 = new Packet09HandCardNumber();
        card2.NumberOfCardInHand = bot.getHand().size();
        card2.playerName = bot.getPlayerName();
        bot.getClient().getClient().sendTCP(card2);
    }

    public void sendDiscard() {
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


    public void cleanPlayedCards() {
        for(int i = 0 ; i < bot.getPlayedCardsList().size(); i++) {
            bot.getDiscardDeck().add(bot.getPlayedCardsList().getLast());
            bot.getPlayedCardsList().remove(bot.getPlayedCardsList().getLast());
        }
    }

    public void cleanHandCards() {
        bot.getDiscardDeck().addAll(bot.getHand());
        bot.setHand(new LinkedList<Cards>());

    }

    public LinkedList<Cards> copyActionCardsFromHand() {
        LinkedList<Cards> cardsList = new LinkedList<>();
        for(int i = 0; i < bot.getHand().size(); i++) {
            if(bot.getHand().get(i).getCardType() == "Aktion") {
                cardsList.add(bot.getHand().get(i));
            }
        }
        return cardsList;
    }


    public void kapellePlayed(int index) {
       sendCardToTrash(index);
    }

    public void werkstattPlayed() {
        LinkedList<Cards> copyList = copyCardsFromPool();
        int randomNumber = Math.abs(random.nextInt()) % copyList.size();
        bot.getDiscardDeck().add(copyList.get(randomNumber));
        GameLogic.getCardspool().remove(randomNumber);
        System.out.println("The bot has picked a " + copyList.get(randomNumber).getName());
    }

    public LinkedList<Cards> copyCardsFromPool() {
        LinkedList<Cards> copyList = new LinkedList<>();
        for(int i = 0 ; i < GameLogic.getCardspool().size(); i++) {
            if(GameLogic.getCardspool().get(i).getCost() <=4) {
                copyList.add(GameLogic.getCardspool().get(i));
            }
        }
        return copyList;
    }

    public void getFestMahlPlayed() {
        for(int i = 0; i< bot.getPlayedCardsList().size(); i++) {
            if(bot.getPlayedCardsList().get(i).getName().equals("Festmahl")) {
                sendCardToTrash(i);
            }
        }
    }

    public void festmahlActive() {
        int randomNumber = Math.abs(random.nextInt()) % copy5Cost().size();
        bot.getDiscardDeck().add(copy5Cost().get(randomNumber));
        GameLogic.getCardspool().remove(randomNumber);
        System.out.println("The bot picked " + copy5Cost().get(randomNumber).getName());
    }

    public LinkedList<Cards> copy5Cost() {
        LinkedList<Cards> copyList = new LinkedList<>();
        for(int i = 0; i< GameLogic.getCardspool().size(); i++) {
            if(GameLogic.getCardspool().get(i).getCost() <= 5) {
                copyList.add(GameLogic.getCardspool().get(i));
            }
        }
        return copyList;
    }



    public void geldVerleiherPlayed() {
        Cards card = new Kupfer();
        for(int i = 0 ; i < bot.getHand().size(); i++) {
            if(bot.getHand().get(i).getName().equals("Kupfer")) {
                if((bot.getHand().contains(card))) {
                    logic.sendCardToTrash(i);
                    bot.setMoney(3);
                    System.out.println("The fucking bot has now " + bot.getMoney() + " Gold");
                }

            }
        }
    }

    public void kanzlerPlayed() {
        if(bot.getDeck().size() > 0) {
            bot.getDiscardDeck().addAll(bot.getDeck());
            bot.setDeck(new LinkedList<Cards>());
        }
    }

    public void thronSaalPlayed() {
        LinkedList<Cards> copyList = copyActionCardsFromHand();
        int randomNumber = Math.abs(random.nextInt()) % copyList.size();
        Cards card = copyList.get(randomNumber);
        bot.getPlayedCardsList().add(card);
        System.out.println("The card " + card.getName() + " was played for the first time");
        bot.getPlayedCardsList().add(card);
        System.out.println("The card " + card.getName() + " was played for the second time");
        for(int i = 0 ; i < bot.getHand().size(); i++) {
            if(bot.getHand().get(i).getName().equals(card.getName()) && bot.getHand().get(i).getName() != "Thronsaal") {
                bot.getPlayedCardsList().add(bot.getHand().get(i));
                bot.getHand().remove(i);
            }
        }
    }

    public void hexePlayed(){
        Packet10HexeEffect hexeCard = new Packet10HexeEffect();
        hexeCard.ownerName = bot.getPlayerName();
        bot.getClient().getClient().sendTCP(hexeCard);
    }

    public LinkedList<Cards> getCardsFromPool(int index) {
        LinkedList<Cards> copyList = new LinkedList<>();
        for(int i = 0; i < GameLogic.getCardspool().size(); i++) {
            if(GameLogic.getCardspool().get(i).getCost() <= index +2) {
                copyList.add(GameLogic.getCardspool().get(i));
            }
        }
        return copyList;
    }
    public void sendCardFromDeckToDiscard() {
        bot.getDiscardDeck().add(bot.getDeck().get(0));
        bot.getDeck().remove(0);
    }


    public LinkedList<Cards> getTrashCardsList() {
        return trashCards;
    }

    public void sendCardToTrash(int index) {
        /** send to the glbal Trash */
        Packet20GlobalTrash globalTrashCard = new Packet20GlobalTrash();
        globalTrashCard.cardName = bot.getHand().get(index).getName();
        bot.getClient().getClient().sendTCP(globalTrashCard);
        /** local Trash */
        if (bot.getHand().get(index).getCardType() == "Geld") {
            bot.setMoney(-bot.getHand().get(index).getPoints());
        }

        bot.getHand().remove(index);
    }

    public void milizPlayed() {
        int randomNumber = Math.abs(random.nextInt()% bot.getHand().size());
        bot.getDiscardDeck().add(bot.getHand().get(randomNumber));
        bot.getHand().remove(randomNumber);
    }

//    public int getNumberOfGardens() {
//        int sum = 0;
//        for (int i = 0; i < playerService.getPlayer().getDeck().size(); i++) {
//            if (playerService.getPlayer().getDeck().get(i).getName() == "Gaerten") {
//                sum += 1;
//            }
//        }
//        for (int i = 0; i < playerService.getPlayer().getHand().size(); i++) {
//            if (playerService.getPlayer().getHand().get(i).getName() == "Gaerten") {
//                sum += 1;
//            }
//        }
//        return sum;
//    }
//
//    public boolean checkIfGardenInDeck() {
//        for (int i = 0; i < playerService.getPlayer().getDeck().size(); i++) {
//            if (playerService.getPlayer().getDeck().get(i).getName() == "Gaerten") {
//                return true;
//            }
//        }
//        for (int i = 0; i < playerService.getPlayer().getHand().size(); i++) {
//            if (playerService.getPlayer().getHand().get(i).getName() == "Gaerten") {
//                return true;
//            }
//        }
//        for (int i = 0; i < playerService.getPlayer().getDiscardDeck().size(); i++) {
//            if (playerService.getPlayer().getDiscardDeck().get(i).getName() == "Gaerten") {
//                return true;
//            }
//        }
//        return false;
//    }
    public LinkedList<Integer> getCardnumber() {
    return cardnumber;
}

    public static ArrayList<Cards> getCardspool() {
        return cardpool;
    }

    public void changeNumb(String cardname) {
        for (int i = 0; i < cardpool.size(); i++) {
            if (cardpool.get(i).getName().equals(cardname)) {
                if (cardnumber.get(i) > 0) {
                    cardnumber.set(i, cardnumber.get(i) - 1);
                }
            }
        }
    }


    public void sendpoints() {
        int counter1 = playerService.getPlayer().getDeck().size();
        int counter2 = playerService.getPlayer().getHand().size();
        int counter3 = playerService.getPlayer().getDiscardDeck().size();
        int counter = (counter1 + counter2 + counter3) / 10;
        Packet13Points points = new Packet13Points();

        if (logic.checkIfGardenInDeck()) {
            points.points = playerService.getPlayer().getPoints() + (logic.getNumberOfGardens() * counter);
            points.name = playerService.getPlayer().getPlayerName();
        } else {
            points.points = playerService.getPlayer().getPoints();
            points.name = playerService.getPlayer().getPlayerName();
        }
        playerService.getClient().getClient().sendTCP(points);
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

    public void sendBuyedCard(Cards card) {
        Packet14Card2 boughtCard = new Packet14Card2();
        boughtCard.name = card.getName();
        bot.getClient().getClient().sendTCP(boughtCard);
    }


}
