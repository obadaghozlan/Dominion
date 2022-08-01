package de.btu.swp.dominion.cards;

import java.util.ArrayList;

public class Spion implements Cards{

    private final String name = "Spion";
    private final int cost = 4;
    private final int points = 0;
    private final String cardsType = "Aktion";
    private ArrayList<String> effectList = new ArrayList<>();
    private final String imagePath = "/src/main/resources/basis/Spion.png";

    public Spion(){
        effectList.add("addCard");
        effectList.add("addAction");
        effectList.add("addSpionEffect");
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
        return this.cardsType;
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

