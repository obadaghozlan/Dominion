package de.btu.swp.dominion.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import de.btu.swp.dominion.cards.*;
import de.btu.swp.dominion.game.LobbyCon;
import de.btu.swp.dominion.gameLogic.*;
import de.btu.swp.dominion.network.Packets.*;

public class BotNetworkListener extends Listener {
	private Client client;
	private Bot bot;
	private BotLogic botlogic;
	private GameLogic logic;

	public void initBot(Client client, Bot bot, PlayerService player) {
		this.client = client;
		this.bot = bot;
		GameLogic logic = new GameLogic(player);
		this.botlogic = new BotLogic(bot, logic);
	}

	@Override
	public void connected(Connection c) {
		Packet00Request ClientMessage = new Packet00Request();
		client.sendTCP(ClientMessage);
		Packet01Message test = new Packet01Message();
		test.clientname = bot.getPlayerName();
		test.message = (" ist beigetreten!");
		client.sendTCP(test);
	}

	@Override
	public void disconnected(Connection c) {
		System.out.print("[" + bot.getPlayerName() + "]: " + "I logged out ");
		// exit if you lose connection
		// Launcher restart = new Launcher();
		// Platform.runLater(() -> restart.start(new Stage()));
	}

	public Boolean isPlayerinList(String name) {
		for (int i = 0; i < bot.getPlayers().size(); i++) {
			OtherPlayers others = bot.getPlayers().get(i);
			if (others.getPlayerName().equals(name)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void received(Connection c, Object o) {

		// reciving the message from server
		if (o instanceof Packet01Message) {
		} else if (o instanceof Packet03GameLaunch) {
			Packet09HandCardNumber number = new Packet09HandCardNumber();
			number.NumberOfCardInHand = 5;
			number.playerName = bot.getPlayerName();
			client.sendTCP(number);
		} else if (o instanceof Packet04NextPlayer) { //wichtigstes Packet
			Packet04NextPlayer TurnPacket = (Packet04NextPlayer) o;
			if (TurnPacket.playerN.equals(bot.getPlayerName())) {
				System.out.println("Hallo bin da");
				// show who have the turn
				Packet19TurnZeiger turnZeiger = new Packet19TurnZeiger();
				turnZeiger.playerName = bot.getPlayerName();
				client.sendTCP(turnZeiger);

				//starts turn of the bot
				botlogic.startTurn();
			}

		} else if (o instanceof Packet05Players) {
			Packet05Players name = (Packet05Players) o;

			if (!isPlayerinList(name.playerName) && !bot.getPlayerName().equals(name.playerName)) {
				Enemies other = new Enemies(name.playerName);
				bot.getPlayers().add(other);
			}

		} else if (o instanceof Packet06EndTurn) {
		} else if (o instanceof Packet07Card) {
		} else if (o instanceof Packet08DiscardDeck) {
		} else if (o instanceof Packet09HandCardNumber) {
		} else if (o instanceof Packet10RenderSpecator) {
		} else if (o instanceof Packet11MilizEffekt) {
			Packet11MilizEffekt effect = (Packet11MilizEffekt) o;
			if (!effect.playPlayer.equals(bot.getPlayerName())) {
				if (bot.getHand().size() > 3 && !bot.isDefendsOn()) {
					botlogic.reactToMiliz();
				}
			}
		} else if (o instanceof Packet10HexeEffect) {
			Packet10HexeEffect hexePacket = (Packet10HexeEffect) o;
			if (!hexePacket.ownerName.equals(bot.getPlayerName()) && !bot.isDefendsOn()) {
				if (GameLogic.getCardnumber().get(16) != 0) {
					Cards fluch = new Fluch();
					bot.getDiscardDeck().add(fluch);
					Packet14Card2 card = new Packet14Card2();
					card.name = "Fluch";
					client.sendTCP(card);
				}
				botlogic.sendDiscard();
			}
		} else if (o instanceof Packet12GameEnd) {
			Packet12GameEnd gameend = (Packet12GameEnd) o;
			bot.setEnd(gameend.end);
			if (gameend.end == true) {
				botlogic.sendpoints();
			}
		} else if (o instanceof Packet13Points) {
		} else if (o instanceof Packet14Card2) {
		} else if (o instanceof Packet16ReadyCheck) {
		} else if (o instanceof Packet15log) {
		} else if (o instanceof Packet17RatsEffect) {
			Packet17RatsEffect effect = (Packet17RatsEffect) o;
			if (!effect.name.equals(bot.getPlayerName())) {
				botlogic.drawACard();
				botlogic.sendHand();
			}
		} else if (o instanceof Packet19TurnZeiger) {
		} else if (o instanceof Packet21FirstDeckCard) {
			Packet21FirstDeckCard firstCard = (Packet21FirstDeckCard) o;
			for (int i = 0; i < bot.getPlayers().size(); i++) {
				if (bot.getPlayers().get(i).getPlayerName().equals(firstCard.playername)) {
					bot.getPlayers().get(i).setFirstDeckCard(GameLogic.stringtoCards(firstCard.cardname));
				}
			}

		} else if (o instanceof Packet22SecondDeckCard) {
			Packet22SecondDeckCard secondCard = (Packet22SecondDeckCard) o;
			for (int i = 0; i < bot.getPlayers().size(); i++) {
				if (bot.getPlayers().get(i).getPlayerName().equals(secondCard.playername)) {
					bot.getPlayers().get(i).setSecondDeckCard(GameLogic.stringtoCards(secondCard.cardname));
				}
			}

		} else if (o instanceof Packet23DiebEffekt) {
			Packet23DiebEffekt effekt = (Packet23DiebEffekt) o;
			if (!bot.getPlayerName().equals(effekt.playername) && !bot.isDefendsOn()) {
				botlogic.emptyDeck(1);

				Packet21FirstDeckCard carddeck = new Packet21FirstDeckCard();
				carddeck.cardname = bot.getDeck().get(0).getName();
				carddeck.playername = bot.getPlayerName();
				client.sendTCP(carddeck);
				Packet22SecondDeckCard carddeck2 = new Packet22SecondDeckCard();
				carddeck2.cardname = bot.getDeck().get(1).getName();
				carddeck2.playername = bot.getPlayerName();
				client.sendTCP(carddeck2);
			} else {
				Packet21FirstDeckCard carddeck = new Packet21FirstDeckCard();
				carddeck.cardname = "Blanko";
				carddeck.playername = bot.getPlayerName();
				client.sendTCP(carddeck);
				Packet22SecondDeckCard carddeck2 = new Packet22SecondDeckCard();
				carddeck2.cardname = "Blanko";
				carddeck2.playername = bot.getPlayerName();
				client.sendTCP(carddeck2);
			}
		} else if (o instanceof Packet26DeleteFirstDeck) {
			Packet26DeleteFirstDeck card = (Packet26DeleteFirstDeck) o;
			if (bot.getPlayerName().equals(card.playername)) {
				botlogic.emptyDeck(0);
				bot.getDeck().remove(0);
			}
		} else if (o instanceof Packet27DeleteSecondDeck) {
			Packet27DeleteSecondDeck card = (Packet27DeleteSecondDeck) o;
			if (bot.getPlayerName().equals(card.playername)) {
				botlogic.emptyDeck(1);
				bot.getDeck().remove(1);
			}
		} else if (o instanceof Packet20GlobalTrash) {
		} else if (o instanceof Packet24SpionEffect) {
			Packet24SpionEffect effect = (Packet24SpionEffect) o;
			if (!effect.playername.equals(bot.getPlayerName()) && !bot.isDefendsOn()) {
				Packet21FirstDeckCard carddeck = new Packet21FirstDeckCard();
				botlogic.emptyDeck(0);
				carddeck.cardname = bot.getDeck().get(0).getName();
				carddeck.playername = bot.getPlayerName();
				client.sendTCP(carddeck);
			}
		}

		else if (o instanceof Packet28RemoveFirstDeck) {
			Packet28RemoveFirstDeck card = (Packet28RemoveFirstDeck) o;
			if (bot.getPlayerName().equals(card.playername)) {
				botlogic.sendCardFromDeckToDiscard();
				botlogic.sendDiscard();
			}
		}

		else if (o instanceof Packet29Buerokrat) {
			Packet29Buerokrat packet = (Packet29Buerokrat) o;
			// do the effect for all the other players but not the owner
			if (!bot.getPlayerName().equals(packet.ownerName) && !bot.isDefendsOn()) {
				// scan for points in the hand
				int hasPoints = -1;
				for (int i = 0; i < bot.getHand().size(); i++) {
					if (bot.getHand().get(i).getCardType().equals("Punkte")) {
						hasPoints = i;
					}
				}
				// if the player have points cards
				if (hasPoints != -1) {
					botlogic.reactToBuerokrat(hasPoints);
				} else {
					// the player have no Points (show his cards)
					Packet30BuerokratShowTheCards showMyCards = new Packet30BuerokratShowTheCards();
					for (Cards card : bot.getHand()) {
						showMyCards.loserHand = card.getName();
						showMyCards.loserName = bot.getPlayerName();
						client.sendTCP(showMyCards);
					}
				}
			}
		}
		// show the players Card to the others
		else if (o instanceof Packet30BuerokratShowTheCards) {
		} else if (o instanceof Packet31KickPlayer) {
			Packet31KickPlayer packet = (Packet31KickPlayer) o;
			if (packet.playerName.equals(bot.getPlayerName())) {
				LobbyCon.setBotIndex(1);
				bot.getClient().getClient().close();
				bot = null;
			} else {
				for (int i = 0; i < bot.getPlayers().size(); i++) {
					if (bot.getPlayers().get(i).getPlayerName().equals(packet.playerName)) {
						bot.getPlayers().remove(i);
					}
				}
			}
		}
		else if (o instanceof Packet32Rename) {
			Packet32Rename name = (Packet32Rename) o;
			bot.setPlayerName(name.playerName);
		}
	}
}