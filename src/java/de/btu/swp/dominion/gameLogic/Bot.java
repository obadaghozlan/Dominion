package de.btu.swp.dominion.gameLogic;

import de.btu.swp.dominion.cards.*;
import de.btu.swp.dominion.network.MPClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class Bot implements OtherPlayers {

    private MPClient client;

	private String playerName;
	private Boolean host;
	private Boolean ready; // ready to join the game after pressing ready in Lobby
	private Boolean end = false;
	private Boolean yourTurn = false;

	private int numberOfCardsInHand;
	private int buys;
	private int action;
	private int money = 0;
	private int points;
	private boolean shutDownServer = false;
	private LinkedList<Cards> deck = new LinkedList<>();
	private LinkedList<Cards> hand = new LinkedList<>();
	private LinkedList<Cards> discardDeck = new LinkedList<>();
	private LinkedList<Cards> playedCards = new LinkedList<>();
	private ArrayList<Cards> specCards = new ArrayList<>();
	private ArrayList<Cards> kapelleCards = new ArrayList<>();
	private ArrayList<Cards> spionCards = new ArrayList<>();
	/** used in Bürokrat */
	private ArrayList<Cards> loserHandCards = new ArrayList<>();
    private String loserName;
    
	private String firstDiscardCard;
	private Cards firstDeckCard;
	private Cards secondDeckCard;
	private Boolean defends = false;
	
	private LinkedList<OtherPlayers> players = new LinkedList<>();

    public Bot(String name, PlayerService player) {
		this.playerName = name;
		initClient(player);
		this.firstDiscardCard = "/src/main/resources/basis/Blanko.png";
		Cards card = new Kupfer();
		Cards card1 = new Anwesen();
		this.getDeck().add(card);
		this.getDeck().add(card);
		this.getDeck().add(card);
		this.getDeck().add(card);
		this.getDeck().add(card);
		this.getDeck().add(card);
		this.getDeck().add(card);
		this.getDeck().add(card1);
		this.getDeck().add(card1);
		this.getDeck().add(card1);
		setPoints(3);
		Collections.shuffle(this.getDeck());

		for(int i = 0 ; i < 5; i++) {
			this.getHand().add(this.getDeck().get(0));
			if(this.getDeck().get(0).getCardType() == "Geld"){
				this.setMoney(this.getDeck().get(0).getPoints());
			}
			this.getDeck().remove(0);
		}
		this.action = 1;
		this.buys = 1;
    }
    
    public void initClient(PlayerService player) {
        this.client = new MPClient(this, player);
	}
	
	public MPClient getClient() {
		return this.client;
	}

	/** get all other Players connected */
	public LinkedList<OtherPlayers> getPlayers() {
		return this.players;
	}

	public String getFirstDiscardCard() {
		return this.firstDiscardCard;
	}

	public Cards getFirstDeckCard() {
		return this.firstDeckCard;
	}

	public Cards getSecondDeckCard() {
		return this.secondDeckCard;
	}

	public Boolean getDefends() {
		return this.defends;
	}

	public void setFirstDiscardCard(String imagePath) {
		this.firstDiscardCard = imagePath;
	}

	public void setFirstDeckCard(Cards imagePath) {
		this.firstDeckCard = imagePath;
	}

	public void setSecondDeckCard(Cards imagePath) {
		this.secondDeckCard = imagePath;
	}

	public void setDefends(Boolean status) {
		this.defends = status;
	}

	public ArrayList<Cards> getSpecCards() {
		return this.specCards;
	}

	public ArrayList<Cards> getKapelleCards() {
		return this.kapelleCards;
	}

	public ArrayList<Cards> getloserHandCards() {
		return this.loserHandCards;
	}

	public void setKapelleCards(ArrayList<Cards> kapelleCards) {
		this.kapelleCards = kapelleCards;
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public void setPlayerName(String playername) {
		this.playerName = playername;
	}

	public Boolean getHost() {
		return this.host;
	}

	public void setHost(Boolean host) {
		this.host = host;
	}

	public Boolean getReady() {
		return this.ready;
	}

	public void setloserName(String loserName) {
		this.loserName = loserName;
	}

	public String getloserName() {
		return this.loserName;
	}

	public void setReady(Boolean ready) {
		this.ready = ready;
	}

	public Boolean getEnd() {
		return this.end;
	}

	public void setEnd(Boolean gameend) {
		this.end = gameend;
	}

	public Boolean getYourTurn() {
		return this.yourTurn;
	}

	public void setYourTurn(Boolean yourTurn) {
		this.yourTurn = yourTurn;
	}

	public int getNumberCardsInHand() {
		return this.numberOfCardsInHand;
	}

	public void setNumberCardsInHand(int NumberOfCardsInHand) {
		this.numberOfCardsInHand = NumberOfCardsInHand;
	}

	public int getBuys() {
		return this.buys;
	}

	public void setBuys(int buys) {
		this.buys = this.buys + buys;
	}

	public int getAction() {
		return this.action;
	}

	public void setAction(int action) {
		this.action = this.action + action;
	}

	public int getPoints() {
		return this.points;
	}

	public void setPoints(int points) {
		this.points = this.points + points;
	}

	public int getMoney() {
		return this.money;
	}

	/** add the Money */
	public void setMoney(int money) {
		this.money = this.money + money;
	}

	public LinkedList<Cards> getDeck() {
		return this.deck;
	}

	public void setDeck(LinkedList<Cards> rightList) {
		this.deck = rightList;
		Collections.shuffle(this.deck);
	}

	public LinkedList<Cards> getHand() {
		return this.hand;
	}

	public void setHand(LinkedList<Cards> hand1) {
		this.hand = hand1;
	}

	public LinkedList<Cards> getDiscardDeck() {
		return this.discardDeck;
	}

	public void setDiscardDeck(LinkedList<Cards> discarddeck) {
		this.discardDeck = discarddeck;
	}

	public void addDiscardDeck(LinkedList<Cards> discarddeck) {
		this.discardDeck.addAll(discarddeck);
	}

	public LinkedList<Cards> getPlayedCardsList() {
		return this.playedCards;
	}

	public void setPlayedCardsList(LinkedList<Cards> playedCards) {
		this.playedCards = playedCards;
	}

	public ArrayList<Cards> getSpionCards() {
		return this.spionCards;
	}

	public void setSpionCards(Cards card) {
		this.spionCards.set(0, card);
	}

	public String getCardName(int i) {
		return this.hand.get(i).getName();
	}

	public String getCardNamePlayed(int i) {
		return this.playedCards.get(i).getName();
	}

	public String getCardImage(int i) {
		return this.hand.get(i).getImagePath();
	}

	public String getPlayedCardImage(int i) {
		return this.playedCards.get(i).getImagePath();
	}

	public String getDiscardCardImage(int i) {
		return this.discardDeck.get(i).getImagePath();
	}

	public void getStartValue() {
		this.money = 0;
		this.action = 1;
		this.buys = 1;
	}

	/** used to shut down the server and kick all players */
	public Boolean getShutDownServer() {
		return this.shutDownServer;
	}

	public void setShutDownServer(Boolean control) {
		this.shutDownServer = control;
	}

	public Boolean isDefendsOn() {
		for (Cards card : hand) {
			if (card.getName().equals("Burggraben")) {
				return true;
			}
		}
		return false;
	}
}
