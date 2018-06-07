package com.moemeido.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.moemeido.game.Application;
import com.moemeido.game.utils.LevelScaling;

import java.util.HashMap;
import java.util.Iterator;

import static com.moemeido.game.utils.B2DVars.PPM;

public class Player {

    private Application app;

    private TextureAtlas atlas;
    private Animation animation;
    private float elapsedTime = 0f;

    private boolean isChopping;

    private float playerScale;

    private float x;
    private float y;
    private float distanceFromTree;

    private float strength; // determines how many hits player needs to tap a tree for it to drop logs
    private float currentStrength;

    private float swingTimer;
    private float swingPerSecond;
    private boolean readyToSwing;
    private boolean autoChop;

    private int logCount;
    private int goldCount;

    private Rectangle bounds;
    private float movementSpeed;
    private boolean moving;

    private HashMap<PowerUp.POWER, PowerUp> powers;

    private int level;
    private int experience;
    private int experienceNeeded;
    private int previousExperience;
    private float growthModifier;

    public Player(Application app, float x, float y) {
        this.app = app;
        this.x = x;
        this.y = y;

        distanceFromTree = 150; // pixels away from tree x coordinate

        powers = new HashMap<PowerUp.POWER, PowerUp>();

        strength = app.prefs.getFloat("playerStrength");

        if (strength <= 0) {
            strength = 1f; // base amount of strength
            app.prefs.putFloat("playerStrength", strength).flush(); // sets player prefs strength if it's the first initialization
            System.out.println("Setting strength to: " + strength);
        }

        currentStrength = strength;

        goldCount = app.prefs.getInteger("playerGold");
        logCount = app.prefs.getInteger("playerLogs");

        level = app.prefs.getInteger("playerLevel");

        experience = app.prefs.getInteger("playerXp");
        growthModifier = 1.25f;
        experienceNeeded = LevelScaling.computeExperiencePoints(level, growthModifier);
        setExperienceNeeded(experienceNeeded);

        atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);
        animation = new Animation<TextureRegion>(.05f, atlas.findRegions("player"));

        playerScale = 5f;

        swingTimer = 0f;
        swingPerSecond = .05f;

        // Creates a rectangle around the player to get position and bounds
        bounds = new Rectangle(x, y,
                atlas.findRegion("player").getRegionWidth() * playerScale,
                atlas.findRegion("player").getRegionHeight() * playerScale);

        movementSpeed = 300f;
    }

    public void update(float delta, Tree tree) {
        if (powers.containsKey(PowerUp.POWER.MIGHT)) {
            currentStrength = 3;
        }
        else {
            currentStrength = strength;
        }

        if (powers.containsKey(PowerUp.POWER.FULL_AUTO)) {
            autoChop = true;
            swingTimer += delta;
            if (swingTimer >= swingPerSecond) {
                readyToSwing = true;
            }
        }
        else {
            autoChop = false;
        }

        // Had problem with concurrent modification exception
        // Cannot remove from list with for each loop but you can use an iterator!
        for(Iterator<PowerUp> it = powers.values().iterator(); it.hasNext();) {
            PowerUp p = it.next();

            if(p.isReadyToDestroy()) {
                it.remove();
            }
        }

        moveForward(tree);

        checkLevel();
    }

    public void render(SpriteBatch batch) {
        elapsedTime += Gdx.graphics.getDeltaTime();

        if(!animation.isAnimationFinished(elapsedTime) && isChopping)
            batch.draw((TextureRegion) animation.getKeyFrame(elapsedTime, true), bounds.x, bounds.y,
                    ((TextureRegion) animation.getKeyFrame(elapsedTime)).getRegionWidth() * playerScale,
                    ((TextureRegion) animation.getKeyFrame(elapsedTime)).getRegionHeight() * playerScale);

        else {
            batch.draw(atlas.findRegion("player"), bounds.x, bounds.y,
                    atlas.findRegion("player").getRegionWidth() * playerScale,
                    atlas.findRegion("player").getRegionHeight() * playerScale);
            elapsedTime = 0;
            isChopping = false;
        }
    }

    private void moveForward(Tree tree) {
        moving = !(tree.getBounds().x * PPM - bounds.x <= distanceFromTree);
    }

    public void receiveLogs(int amount) {
        logCount += amount;
    }

    private void checkLevel() {
        if (app.prefs.getInteger("playerXp") >= experienceNeeded) {
            level++;
            setLevel(app.prefs.getInteger("playerLevel") + 1);
            previousExperience = experienceNeeded;
            app.prefs.putInteger("previousXp", previousExperience).flush();
            experienceNeeded = LevelScaling.computeExperiencePoints(app.prefs.getInteger("playerLevel"), growthModifier);
            setExperienceNeeded(experienceNeeded);
        }
    }

    public void dispose() {

    }

    public float getX() {
        return x;
    }

    public boolean isMoving() {
        return moving;
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }

    public boolean isAutoChopping() {
        return autoChop;
    }

    public boolean isReadyToSwing() { return readyToSwing; }

    public void setSwingTimer(float swingTimer) {
        this.swingTimer = swingTimer;
    }

    public void setReadyToSwing(boolean readyToSwing) { this.readyToSwing = readyToSwing; }

    public void setLogCount(int logCount) {
        //this.logCount = logCount;
        app.prefs.putInteger("playerLogs", logCount);
        app.prefs.flush();
    }

    public float getCurrentStrength() {
        return currentStrength;
    }

    public void setChopping(boolean chopping) { isChopping = chopping; }

    public int getGoldCount() {
        return app.prefs.getInteger("playerGold");
    }

    public void buyItem(int itemCost) {
        goldCount -= itemCost;
    }

    public void setGoldCount(int goldCount) {
        //this.goldCount = goldCount;
        app.prefs.putInteger("playerGold", goldCount);
        app.prefs.flush();
    }

    public HashMap<PowerUp.POWER, PowerUp> getPowers() {
        return powers;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        //this.experience = experience;
        app.prefs.putInteger("playerXp", experience);
        app.prefs.flush();
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        //this.level = level;
        app.prefs.putInteger("playerLevel", level);
        app.prefs.flush();
    }

    public void setExperienceNeeded(int experienceNeeded){
        app.prefs.putInteger("playerXpNeeded", experienceNeeded);
        app.prefs.flush();
    }

    public int getExperienceNeeded() {
        return experienceNeeded;
    }

    public int getPreviousExperience() {
        return previousExperience;
    }

    public void setStrength(float strength) {
        app.prefs.putFloat("playerStrength", strength).flush();
        this.strength = strength;
    }

    public void setMovementSpeed(float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public float getStrength() {
        return strength;
    }
}
