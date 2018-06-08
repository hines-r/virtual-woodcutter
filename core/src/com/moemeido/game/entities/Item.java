package com.moemeido.game.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.moemeido.game.Application;
import com.moemeido.game.utils.LevelScaling;

public class Item implements IUpgradable {

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
                maxLevel = 9;
                break;
            case BOOTS:
                itemTex = atlas.findRegion("boots");
                itemName = "Boots Upgrade";
                description = "Move more quickly between trees!";
                itemCost = 250;
                maxLevel = 9;
                break;
            case LOG_MODIFIER:
                itemTex = atlas.findRegion("log1");
                itemName = "More Logs!";
                description = "Increases logs earned by 10%";
                itemCost = 1000;
                maxLevel = 10;
                break;
            case GOLD_MODIFIER:
                itemTex = atlas.findRegion("coin2");
                itemName = "More Gold!";
                description = "Increases gold earned by 10%";
                itemCost = 10000;
                maxLevel = 10;
                break;
        }

        baseCost = itemCost;
        growthModifier = 1.15f;

        // only adds item to prefs if they have been upgraded
        if (isInPrefs()){
            updateExistingItem();
        }
        else{
            itemLevel = 0;
        }
    }

    private void updateExistingItem(){
        itemLevel = app.prefs.getInteger(getPrefsName() + "ItemLevel");
        itemCost = app.prefs.getInteger(getPrefsName() + "ItemCost");
    }

    private boolean isInPrefs(){
        return app.prefs.getString(getPrefsName()).equals(this.itemName);
    }

    public void updatePlayerStat(){
        switch (id) {
            case HATCHET:
                currentStat = "Current strength: " + app.gsm.getPlayer().getStrength();
                break;
            case BOOTS:
                currentStat = "Current speed: " + app.gsm.getPlayer().getMovementSpeed();
                break;
            case GOLD_MODIFIER:
                currentStat = "Current bonus: " + (int) (app.gsm.getPlayer().getGoldModifier() * 100f) + "%";
                break;
            case LOG_MODIFIER:
                currentStat = "Current bonus: " + (int) (app.gsm.getPlayer().getLogModifier() * 100f) + "%";
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

        app.prefs.putString(getPrefsName(), itemName).flush();
        app.prefs.putInteger(itemName.replaceAll("\\s+", "") + "ItemCost", itemCost).flush();
        app.prefs.putInteger(itemName.replaceAll("\\s+", "") + "ItemLevel", itemLevel).flush();

        switch (id) {
            case HATCHET:
                app.gsm.getPlayer().setStrength(app.gsm.getPlayer().getStrength() + 1f);
                break;
            case BOOTS:
                app.gsm.getPlayer().setMovementSpeed(app.gsm.getPlayer().getMovementSpeed() + 10f);
                break;
            case GOLD_MODIFIER:
                app.gsm.getPlayer().setGoldModifier(app.gsm.getPlayer().getGoldModifier() + .1f);
                break;
            case LOG_MODIFIER:
                app.gsm.getPlayer().setLogModifier(app.gsm.getPlayer().getLogModifier() + .1f);
                break;
        }

        updatePlayerStat();
    }

    /**
     * Returns the name of the item without spaces. This name is used within
     * the preferences file to enabled persistence.
     * @return the formatted name
     */
    private String getPrefsName(){
        return itemName.replaceAll("\\s+", "");
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
