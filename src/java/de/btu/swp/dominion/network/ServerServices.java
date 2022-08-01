package de.btu.swp.dominion.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import de.btu.swp.dominion.cards.*;
import de.btu.swp.dominion.gameLogic.Enemies;
import de.btu.swp.dominion.gameLogic.OtherPlayers;
import de.btu.swp.dominion.gameLogic.PlayerService;
import de.btu.swp.dominion.network.Packets.*;
import java.util.LinkedList;
import java.util.ArrayList;

public class ServerServices {
	private ServerProgram server;
	private PlayerService playerService;

	// constructor
	public ServerServices(int limit, PlayerService playerS) {
		this.server = new ServerProgram(limit, playerS);
		this.playerService = playerS;
	}

	// getter and setter
	public Server getServer() {
		return this.server.getServer();
	}

	public ServerProgram getServerProgram() {
		return this.server;
	}

	public LinkedList<Connection> getAllPlayers() {
		return this.server.getPlayers();
	}

	public ArrayList<Cards> getCardspool() {
		return this.server.getCardpool();
	}

	/** start the Server */
	public void startServer() {
		server.getServer().start();
	}

	/** Shutdown the Server */
	public void stopServer() {
		server.getServer().stop();
	}

	/** get the number of PLayer Connected on the Server */
	public int numberOfPLayerConected() {
		return server.getPlayers().size();
	}

	public boolean equalsNames() {
		if (playerService.getPlayers().getLast().getPlayerName().equals(playerService.getPlayer().getPlayerName())) {
			return true;
		}

		for (int i = 0; i < playerService.getPlayers().size()-1; i++) {
			if (playerService.getPlayers().getLast().getPlayerName().equals(playerService.getPlayers().get(i).getPlayerName())) {
				return true;
			}
		}
		return false;
	}

	// Methoden zum schicken der Packete zu den Clients

	/** send from Server to all the Client */
	public void sendMessage(String m, int i) {
		Packet01Message MessagePacket = new Packet01Message();
		MessagePacket.message = m;
		MessagePacket.stage = i;
		server.getServer().sendToAllTCP(MessagePacket);
	}

	/** send from Server to all the Client -- Boolean in Lobby to launch the Game */
	public void sendReady(Packet03GameLaunch startPacket) {
		server.getServer().sendToAllTCP(startPacket);
	}

	public void sendEnd(Packet12GameEnd gameend) {
		server.getServer().sendToAllTCP(gameend);
	}

	public void sendTurn(Packet04NextPlayer turnPacket) {
		server.getServer().sendToAllTCP(turnPacket);
	}

	public void setName(Packet05Players name) {
		System.out.println(getAllPlayers().size() - 1 + " : " + playerService.getPlayers().size());
		if (getAllPlayers().size() - 1 > playerService.getPlayers().size()) {
			Enemies other = new Enemies(name.playerName);
			playerService.getPlayers().add(other);
			playerService.getServerServices().getAllPlayers().getLast().setName(name.playerName);
		}
		
		while (equalsNames()) {
			playerService.getPlayers().getLast().setPlayerName(playerService.getPlayers().getLast().getPlayerName() + "1");
			Packet32Rename newName = new Packet32Rename();
			newName.playerName = playerService.getPlayers().getLast().getPlayerName();
			playerService.getServerServices().getAllPlayers().getLast().setName(newName.playerName);
			server.getServer().sendToTCP(getAllPlayers().getLast().getID(), newName);
		}

		Packet05Players newNameHost = new Packet05Players();
		newNameHost.playerName = playerService.getPlayer().getPlayerName();
		server.getServer().sendToAllTCP(newNameHost);
		for (int i = 0; i < playerService.getPlayers().size(); i++) {
			Packet05Players newName = new Packet05Players();
			newName.playerName = playerService.getPlayers().get(i).getPlayerName();
			server.getServer().sendToAllTCP(newName);
		}
	}

	public void sendEnd(Packet06EndTurn player) {
		server.getServer().sendToAllTCP(player);
	}

	public void sendCardpool(ArrayList<Cards> pool) {
		for (Cards name : pool) {
			Packet07Card cardpool = new Packet07Card();
			cardpool.card = name.getName();
			server.getServer().sendToAllTCP(cardpool);
		}
	}

	public void sendPoints(Packet13Points points) {
		server.getServer().sendToAllTCP(points);
	}

	public void sendFirstCard(Packet08DiscardDeck playerCard) {
		server.getServer().sendToAllTCP(playerCard);
	}

	public void sendNumberOfCards(Packet09HandCardNumber numberOfCards) {
		server.getServer().sendToAllTCP(numberOfCards);
	}

	public void sendCard(Packet10RenderSpecator zug) {
		server.getServer().sendToAllTCP(zug);
	}

	public void sendEffect(Packet11MilizEffekt effect) {
		server.getServer().sendToAllTCP(effect);
	}

	public void sendhexeCard(Packet10HexeEffect hexeCard) {
		server.getServer().sendToAllTCP(hexeCard);
	}

	public void sendBoughtCard(Packet14Card2 boughtCard) {
		server.getServer().sendToAllTCP(boughtCard);
	}

	public void sendReadyCheck(Packet16ReadyCheck check) {
		server.getServer().sendToAllTCP(check);
	}

	public void sendRatsCard(Packet17RatsEffect effect) {
		server.getServer().sendToAllTCP(effect);
	}

	public void sendDiebeffekt(Packet23DiebEffekt effekt) {
		server.getServer().sendToAllTCP(effekt);
	}

	public void sendlog(Packet15log logMessage) {
		server.getServer().sendToAllTCP(logMessage);
	}

	public void sendTurnZeiger(Packet19TurnZeiger TurnZeiger) {
		server.getServer().sendToAllTCP(TurnZeiger);
	}

	public void sendSecondDeckCard(Packet22SecondDeckCard playerCard) {
		server.getServer().sendToAllTCP(playerCard);
	}

	public void sendDeleteCardFirst(Packet26DeleteFirstDeck card) {
		server.getServer().sendToAllTCP(card);
	}

	public void sendDeleteCardSecond(Packet27DeleteSecondDeck card) {
		server.getServer().sendToAllTCP(card);
	}

	public void sendglobalTrash(Packet20GlobalTrash trashPacket) {
		server.getServer().sendToAllTCP(trashPacket);
	}

	public void sendFirstDeckCard(Packet21FirstDeckCard playerCard) {
		server.getServer().sendToAllTCP(playerCard);
	}

	public void sendSpionEffect(Packet24SpionEffect effect) {
		server.getServer().sendToAllTCP(effect);
	}

	public void sendDeleteCardFirst(Packet28RemoveFirstDeck card) {
		server.getServer().sendToAllTCP(card);
	}

	// //public void sendCardtoDiscard(Packet28SendCardsToDiscardDeck card) {
	// server.getServer().sendToAllTCP(card);
	// }

	public void sendBuerokratEffect(Packet29Buerokrat packet) {
		server.getServer().sendToAllTCP(packet);
	}

	public void sendBuerokratShowCardsEffect(Packet30BuerokratShowTheCards packet) {
		server.getServer().sendToAllTCP(packet);
	}

	public void sendKickedPlayer(Packet31KickPlayer packet) {
		server.getServer().sendToAllTCP(packet);
	}
}