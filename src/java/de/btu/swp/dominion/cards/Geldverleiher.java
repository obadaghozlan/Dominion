package de.btu.swp.dominion.cards;

import java.util.ArrayList;

public class Geldverleiher implements Cards {
    private final String name = "Geldverleiher";
    private final int cost = 4;
    private final int points = 0;
    private final String cardType = "Aktion";
    private ArrayList<String> effectList = new ArrayList<>();
    private final String imagePath = "/src/main/resources/basis/Geldverleiher.png";

    public Geldverleiher() {
        effectList.add("addGeldverleiherEffect");
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getCost() {
        return this.cost;
    }

    @Override
    public int getPoints() {
        return this.points;
    }

    @Override
    public String getCardType() {
        return this.cardType;
    }

    @Override
    public ArrayList<String> getEffect() {
        return this.effectList;
    }

    @Override
    public String getImagePath() {
        return this.imagePath;
    }
}
