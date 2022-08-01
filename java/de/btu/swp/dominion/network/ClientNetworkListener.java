package de.btu.swp.dominion.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import de.btu.swp.dominion.cards.*;
import de.btu.swp.dominion.game.ChatCon;
import de.btu.swp.dominion.game.GameCon;
import de.btu.swp.dominion.game.LobbyCon;
import de.btu.swp.dominion.gameLogic.*;
import de.btu.swp.dominion.network.Packets.*;

public class ClientNetworkListener extends Listener {
	private Client client;
	private PlayerService playerService;
	private GameLogic logic;

	public void init(Client client, PlayerService player) {
		this.client = client;
		this.playerService = player;
		this.logic = new GameLogic(player);
	}

	@Override
	public void connected(Connection c) {
		Packet00Request ClientMessage = new Packet00Request();
		client.sendTCP(ClientMessage);
		Packet01Message test = new Packet01Message();
		test.clientname = playerService.getPlayer().getPlayerName();
		test.message = (" ist beigetreten!");
		client.sendTCP(test);
	}

	@Override
	public void disconnected(Connection c) {
		System.out.print("[" + playerService.getPlayer().getPlayerName() + "]: " + "I logged out ");
		// exit if you lose connection
		// Launcher restart = new Launcher();
		// Platform.runLater(() -> restart.start(new Stage()));
	}

	public Boolean isPlayerinList(String name) {
		for (int i = 0; i < playerService.getPlayers().size(); i++) {
			OtherPlayers others = playerService.getPlayers().get(i);
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
			Packet01Message MessagePacket = (Packet01Message) o;
			if (MessagePacket.stage == 1) {
				LobbyCon lobby = new LobbyCon();
				lobby.addTextToList(MessagePacket.message);
			}
			if (MessagePacket.stage == 2) {
				ChatCon chat = new ChatCon();
				chat.addTextToList(MessagePacket.message);
			}

		} else if (o instanceof Packet03GameLaunch) {
			// reciving the message from server
			Packet03GameLaunch StartPacket = (Packet03GameLaunch) o;
			playerService.getPlayer().setReady(StartPacket.start);

		} else if (o instanceof Packet04NextPlayer) {
			Packet04NextPlayer TurnPacket = (Packet04NextPlayer) o;

			if (TurnPacket.playerN.equals(playerService.getPlayer().getPlayerName())) {
				playerService.getPlayer().setYourTurn(true);

				/** show how have the turn */
				Packet19TurnZeiger turnZeiger = new Packet19TurnZeiger();
				turnZeiger.playerName = playerService.getPlayer().getPlayerName();
				client.sendTCP(turnZeiger);
			}

		} else if (o instanceof Packet05Players) {
			Packet05Players name = (Packet05Players) o;

			if (!isPlayerinList(name.playerName) && !playerService.getPlayer().getPlayerName().equals(name.playerName)) {
				Enemies other = new Enemies(name.playerName);
				playerService.getPlayers().add(other);
			}

		} else if (o instanceof Packet06EndTurn) {
			Packet06EndTurn player = (Packet06EndTurn) o;
			playerService.getPlayer().getSpecCards().clear();

			if (playerService.getPlayer().getHost() == true) {
				logic.changeTurn(player.name);
			}
		} else if (o instanceof Packet07Card) {
			Packet07Card pool = (Packet07Card) o;

			if (GameLogic.getCardspool().size() < 10) {
				GameLogic.getCardspool().add(GameLogic.stringtoCards(pool.card));
			}
		} else if (o instanceof Packet08DiscardDeck) {
			Packet08DiscardDeck firstCard = (Packet08DiscardDeck) o;
			for (int i = 0; i < playerService.getPlayers().size(); i++) {
				if (playerService.getPlayers().get(i).getPlayerName().equals(firstCard.playerName)) {
					playerService.getPlayers().get(i).setFirstDiscardCard(firstCard.cardName);
				}
			}

		} else if (o instanceof Packet09HandCardNumber) {
			Packet09HandCardNumber handCard = (Packet09HandCardNumber) o;
			for (int i = 0; i < playerService.getPlayers().size(); i++) {
				OtherPlayers others = playerService.getPlayers().get(i);
				if (others.getPlayerName().equals(handCard.playerName)) {
					others.setNumberCardsInHand(handCard.NumberOfCardInHand);
				}
			}

		} else if (o instanceof Packet10RenderSpecator) {
			Packet10RenderSpecator zug = (Packet10RenderSpecator) o;
			playerService.getPlayer().getSpecCards().add(GameLogic.stringtoCards(zug.cardName));

		} else if (o instanceof Packet11MilizEffekt) {
			Packet11MilizEffekt effect = (Packet11MilizEffekt) o;
			if (!effect.playPlayer.equals(playerService.getPlayer().getPlayerName())) {
				if (playerService.getPlayer().getHand().size() > 3 && !playerService.isDefendsOn()) {
					GameLogic.setMilizActive(true);
				}
			}
		} else if (o instanceof Packet10HexeEffect) {
			Packet10HexeEffect hexePacket = (Packet10HexeEffect) o;
			if (!hexePacket.ownerName.equals(playerService.getPlayer().getPlayerName()) && !playerService.isDefendsOn()) {
				if (GameLogic.getCardnumber().get(16) != 0) {
					Cards fluch = new Fluch();
					playerService.getPlayer().getDiscardDeck().add(fluch);
					Packet14Card2 card = new Packet14Card2();
					card.name = "Fluch";
					client.sendTCP(card);
				}
			}
		} else if (o instanceof Packet12GameEnd) {
			Packet12GameEnd gameend = (Packet12GameEnd) o;
			playerService.getPlayer().setEnd(gameend.end);
			GuiDesigner gameEndSound = new GuiDesigner();
			if (gameend.end == true) {
				logic.sendpoints();
			}
			MetaData metaData = new MetaData();
			if (metaData.getSoundOnMeta()) {
				gameEndSound.getGameEndAudio().play();
			}

		} else if (o instanceof Packet13Points) {
			Packet13Points points = (Packet13Points) o;
			if (!playerService.getPlayer().getPointslist().containsKey(points.name)){
				playerService.getPlayer().getPointslist().put(points.name, points.points);
				}	

		} else if (o instanceof Packet14Card2) {
			Packet14Card2 card = (Packet14Card2) o;
			logic.changeNumb(card.name);

		} else if (o instanceof Packet16ReadyCheck) {
			Packet16ReadyCheck check = (Packet16ReadyCheck) o;
			for (int i = 0; i < playerService.getPlayers().size(); i++) {
				if (playerService.getPlayers().get(i).getPlayerName().equals(check.playerName)) {
					playerService.getPlayers().get(i).setReady(check.ready);
				}
			}
		} else if (o instanceof Packet15log) {
			Packet15log logMessage = (Packet15log) o;
			GameCon game = new GameCon();
			game.addTextToLogList(logMessage.logMessage);
		} else if (o instanceof Packet17RatsEffect) {
			Packet17RatsEffect effect = (Packet17RatsEffect) o;
			if (!effect.name.equals(playerService.getPlayer().getPlayerName())) {
				playerService.drawCard();
			}
		}

		else if (o instanceof Packet19TurnZeiger) {
			Packet19TurnZeiger turnChack = (Packet19TurnZeiger) o;
			playerService.getTrigger().setCurrentPlayerName(turnChack.playerName);

		} else if (o instanceof Packet21FirstDeckCard) {
			Packet21FirstDeckCard firstCard = (Packet21FirstDeckCard) o;
			for (int i = 0; i < playerService.getPlayers().size(); i++) {
				if (playerService.getPlayers().get(i).getPlayerName().equals(firstCard.playername)) {
					playerService.getPlayers().get(i).setFirstDeckCard(GameLogic.stringtoCards(firstCard.cardname));
				}
			}

		} else if (o instanceof Packet22SecondDeckCard) {
			Packet22SecondDeckCard secondCard = (Packet22SecondDeckCard) o;
			for (int i = 0; i < playerService.getPlayers().size(); i++) {
				if (playerService.getPlayers().get(i).getPlayerName().equals(secondCard.playername)) {
					playerService.getPlayers().get(i)
							.setSecondDeckCard(GameLogic.stringtoCards(secondCard.cardname));
				}
			}

		} else if (o instanceof Packet23DiebEffekt) {
			Packet23DiebEffekt effekt = (Packet23DiebEffekt) o;
			if (!playerService.getPlayer().getPlayerName().equals(effekt.playername) && !playerService.isDefendsOn()) {
				logic.refillDeck(1);

				Packet21FirstDeckCard carddeck = new Packet21FirstDeckCard();
				carddeck.cardname = playerService.getPlayer().getDeck().get(0).getName();
				carddeck.playername = playerService.getPlayer().getPlayerName();
				client.sendTCP(carddeck);
				Packet22SecondDeckCard carddeck2 = new Packet22SecondDeckCard();
				carddeck2.cardname = playerService.getPlayer().getDeck().get(1).getName();
				carddeck2.playername = playerService.getPlayer().getPlayerName();
				client.sendTCP(carddeck2);
			} else {
				Packet21FirstDeckCard carddeck = new Packet21FirstDeckCard();
				carddeck.cardname = "Blanko";
				carddeck.playername = playerService.getPlayer().getPlayerName();
				client.sendTCP(carddeck);
				Packet22SecondDeckCard carddeck2 = new Packet22SecondDeckCard();
				carddeck2.cardname = "Blanko";
				carddeck2.playername = playerService.getPlayer().getPlayerName();
				client.sendTCP(carddeck2);
			}
		} else if (o instanceof Packet26DeleteFirstDeck) {
			Packet26DeleteFirstDeck card = (Packet26DeleteFirstDeck) o;
			if (playerService.getPlayer().getPlayerName().equals(card.playername)) {
				playerService.getPlayer().getDeck().remove(0);
			}
		} else if (o instanceof Packet27DeleteSecondDeck) {
			Packet27DeleteSecondDeck card = (Packet27DeleteSecondDeck) o;
			if (playerService.getPlayer().getPlayerName().equals(card.playername)) {
				logic.refillDeck(1);
				playerService.getPlayer().getDeck().remove(1);
			}
		} else if (o instanceof Packet20GlobalTrash) {
			Packet20GlobalTrash trashPacket = (Packet20GlobalTrash) o;
			logic.getTrashCardsList().add(GameLogic.stringtoCards(trashPacket.cardName));

		} else if (o instanceof Packet24SpionEffect) {
			Packet24SpionEffect effect = (Packet24SpionEffect) o;
			if (!effect.playername.equals(playerService.getPlayer().getPlayerName())) {
				if (!playerService.isDefendsOn()) {
					Packet21FirstDeckCard carddeck = new Packet21FirstDeckCard();
					logic.refillDeck(0);
					carddeck.cardname = playerService.getPlayer().getDeck().get(0).getName();
					carddeck.playername = playerService.getPlayer().getPlayerName();
					client.sendTCP(carddeck);
				}
			}
		}

		else if (o instanceof Packet28RemoveFirstDeck) {
			Packet28RemoveFirstDeck card = (Packet28RemoveFirstDeck) o;
			if (playerService.getPlayer().getPlayerName().equals(card.playername)) {
				logic.sendCardToDiscard(0);
			}
		}

		else if (o instanceof Packet29Buerokrat) {
			Packet29Buerokrat packet = (Packet29Buerokrat) o;
			// do the effect for all the other players but not the owner
			if (!playerService.getPlayer().getPlayerName().equals(packet.ownerName) && !playerService.isDefendsOn()) {
				// scan for points in the hand
				if (!logic.getBuerokratActive()) {
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
							client.sendTCP(showMyCards);
						}
					}
				} else {
					playerService.getTrigger().setBuerokratWaiting(true);
				}
			}
		}
		/** show the players Card to the others */
		else if (o instanceof Packet30BuerokratShowTheCards) {
			Packet30BuerokratShowTheCards packet = (Packet30BuerokratShowTheCards) o;
			if (!playerService.getPlayer().getPlayerName().equals(packet.loserName)) {
				playerService.getPlayer().setloserName(packet.loserName);
				GameLogic.setBuerokratLoserActive(true);
				playerService.getPlayer().getloserHandCards().add(GameLogic.stringtoCards(packet.loserHand));
			}
		}
		else if (o instanceof Packet31KickPlayer) {
			Packet31KickPlayer packet = (Packet31KickPlayer) o;
			if (packet.playerName.equals(playerService.getPlayer().getPlayerName())) {
				playerService.getClient().getClient().close();
				this.playerService = null;
				this.logic = null;
			} else {
				for (int i = 0; i < playerService.getPlayers().size(); i++) {
					if (playerService.getPlayers().get(i).getPlayerName().equals(packet.playerName)) {
						playerService.getPlayers().remove(i);
					}
					if (playerService.getPlayer().getHost()) {
						if (playerService.getServerServices().getAllPlayers().get(i+1).toString().equals(packet.playerName)) {
							playerService.getServerServices().getAllPlayers().remove(i+1);
						}
					}
				}
			}
		}
		else if (o instanceof Packet32Rename) {
			Packet32Rename name = (Packet32Rename) o;
			playerService.getPlayer().setPlayerName(name.playerName);
		}
	}
}