package de.btu.swp.dominion.gameLogic;

import de.btu.swp.dominion.cards.*;
import de.btu.swp.dominion.gameLogic.Trigger;
import de.btu.swp.dominion.network.*;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

public class PlayerService {
	private Player player = null;
	private LinkedList<OtherPlayers> players = new LinkedList<>();
	private MPClient client;
	private ServerServices services;
	private Trigger trigger;

	/**
	 * constructor + give the playername and Host status
	 */
	public PlayerService(String PlayerName, Boolean Host) {
		this.player = new Player(PlayerName, Host);
		this.trigger = new Trigger();
		this.players = new LinkedList<OtherPlayers>();
	}

	public PlayerService() {
		this.player = new Player();
		this.trigger = new Trigger();
		this.players = new LinkedList<OtherPlayers>();
	}

	/**
	 * use to get Or set things into Class Player
	 */
	public Player getPlayer() {
		return this.player;
	}

	/** get all other Players connected */
	public LinkedList<OtherPlayers> getPlayers() {
		return this.players;
	}

	/**
	 * the player will connect to the Server as a Client
	 */
	public void initConnection() {
		client = new MPClient(this);
	}

	/**
	 * define the Server with the Players limit :: Used in the Haupt Menu Mussed be
	 * used once
	 *
	 * @throws IOException
	 */
	public void initServer(int PlayerLimit) throws IOException {
		services = new ServerServices(PlayerLimit, this);
		services.startServer();
	}

	/**
	 * get the Object from the ServerServices
	 *
	 * @return
	 */
	public ServerServices getServerServices() {
		return services;
	}

	/**
	 * get an Object from Class MPClient used to Send Objects To the Server
	 */
	public MPClient getClient() {
		return client;
	}

	/**
	 * Ziel: show a zeiger if a set of Cards are Chosen for single & multiplayer
	 *
	 * @return
	 */
	public Trigger getTrigger() {
		return this.trigger;
	}

	public void setPlayer(Player newplayer) {
		this.player = newplayer;
	}

	public void drawCard() {
		if (player.getDeck().size() == 0) {
			while (player.getDiscardDeck().size() > 0) {
				player.getDeck().add(player.getDiscardDeck().get(0));
				player.getDiscardDeck().remove(0);
			}
			Collections.shuffle(player.getDeck());
		}

		if (player.getDeck().size() > 0) {
			// card instanceof Money
			if (player.getDeck().get(0).getCardType().equals("Geld")) {
				player.setMoney(player.getDeck().get(0).getPoints());
			}
			player.getHand().add(player.getDeck().get(0));
			player.getDeck().remove(0);
		}
	}

	public Boolean isDefendsOn() {
		for (Cards card : getPlayer().getHand()) {
			if (card.getName().equals("Burggraben")) {
				return true;
			}
		}
		return false;
	}

	public Boolean allReady() {
		if (services.getServerProgram().getServerLimit() - 1 == getPlayers().size()) {
			for (int i = 0; i < getPlayers().size(); i++) {
				if (!getPlayers().get(i).getReady()) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
}
