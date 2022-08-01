package de.btu.swp.dominion.gameLogic;

import de.btu.swp.dominion.cards.*;

public interface OtherPlayers {

	public String getPlayerName();

	public Boolean getHost();

	public Boolean getReady();

	public int getNumberCardsInHand();

	public int getPoints();

	public String getFirstDiscardCard();

	public Cards getFirstDeckCard();

	public Cards getSecondDeckCard();

	public Boolean getDefends();

	public void setPlayerName(String name);

	public void setHost(Boolean host);

	public void setReady(Boolean ready);

	public void setNumberCardsInHand(int number);

	public void setPoints(int points);

	public void setFirstDiscardCard(String imagePath);

	public void setFirstDeckCard(Cards imagePath);

	public void setSecondDeckCard(Cards imagePath);

	public void setDefends(Boolean status);
}