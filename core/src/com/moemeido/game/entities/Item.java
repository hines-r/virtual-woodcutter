package com.moemeido.game.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.moemeido.game.Application;
import com.moemeido.game.managers.GameScreenManager;
import com.moemeido.game.utils.LevelScaling;

public class Item {

    private Application app;

    private TextureRegion itemTex;

    private String itemName;
    private int itemCost;
    private int baseCost;
    private int itemLevel;

    private int maxLevel;
    private float growthModifier;

    private String description;
    private String currentStat;

    private ItemID id;

    public enum ItemID {
        HATCHET,
        BOOTS,
        LOG_MODIFIER,
        GOLD_MODIFIER
    }

    public Item(Application app, ItemID itemId) {
        this.app = app;

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);

        id = itemId;

        switch (id) {
            case HATCHET:
                itemTex = atlas.findRegion("hatchet2");
                itemName = "Hatchet Upgrade";
                description = "Increase the cutting power of your axe!";
                itemCost = 250;
                break;
            case BOOTS:
                itemTex = atlas.findRegion("boots");
                itemName = "Boots Upgrade";
                description = "Move more quickly between trees!";
                itemCost = 250;
                break;
            case LOG_MODIFIER:
                itemTex = atlas.findRegion("log1");
                itemName = "More Logs!";
                description = "Increases logs earned by 10%";
                itemCost = 1000;
                break;
            case GOLD_MODIFIER:
                itemTex = atlas.findRegion("coin2");
                itemName = "More Gold!";
                description = "Increases gold earned by 10%";
                itemCost = 10000;
                break;
        }

        baseCost = itemCost;

        growthModifier = 2.36f;

        itemLevel = 1;
        maxLevel = 10;
    }

    public void updatePlayerStat(){
        switch (id) {
            case HATCHET:
                currentStat = "Current strength: " + app.gsm.getPlayer().getStrength();
                break;
            case BOOTS:
                currentStat = "Current speed: " + app.gsm.getPlayer().getMovementSpeed();
                break;
        }
    }

    /**
     * Upgrades the appropriate attributes according to the unique item identifier.
     * This will also scale the items next upgrade cost by the growth modifier.
     */
    public void upgrade() {
        itemCost = LevelScaling.calculateGrowth(baseCost, growthModifier, itemLevel);
        itemLevel++;

        switch (id) {
            case HATCHET:
                app.gsm.getPlayer().setStrength(app.gsm.getPlayer().getStrength() + 1);
                break;
            case BOOTS:
                app.gsm.getPlayer().setMovementSpeed(app.gsm.getPlayer().getMovementSpeed() + 1);
                break;
        }

        updatePlayerStat();
    }

    public TextureRegion getItemTex() {
        return itemTex;
    }

    public float getWidth() {
        return itemTex.getRegionWidth();
    }

    public float getHeight() {
        return itemTex.getRegionHeight();
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemCost() {
        return itemCost;
    }

    public int getItemLevel() {
        return itemLevel;
    }

    public int getMaxLevel() { return maxLevel; }

    public String getDescription() { return description; }

    public String getCurrentStat() {
        return currentStat;
    }
}
