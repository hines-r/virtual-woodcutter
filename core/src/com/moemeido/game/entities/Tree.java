package com.moemeido.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.moemeido.game.Application;

import static com.moemeido.game.utils.B2DVars.PPM;

public class Tree {

    private Application app;

    private float treeWidth;
    private float treeHeight;

    private float hitsToBreak;
    private float totalHits;

    private int currentLife;
    private int totalLife;

    private int minDrop;
    private int maxDrop;

    private int logsToDrop;
    private boolean readyToDrop;

    private int experience;

    private TextureRegion treeTex;

    private Rectangle bounds;

    public enum Type {
        OAK
    }
    private Type type;

    public Tree(Application app, Type type) {
        this.app = app;
        this.type = type;

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);

        minDrop = 1;
        maxDrop = 3;

        totalLife = MathUtils.random(25, 50); // Takes 5 to 10 hits to destroy tree
        currentLife = totalLife;

        if (type == Type.OAK) {
            hitsToBreak = 10;
            logsToDrop = MathUtils.random(minDrop, maxDrop);
            experience = 10;
            treeTex = atlas.findRegion("tree3");
        }

        totalHits = hitsToBreak;

        float treeScale = 6f;
        treeWidth = (float)treeTex.getRegionWidth() * treeScale;
        treeHeight = (float)treeTex.getRegionHeight() * treeScale;

        bounds = new Rectangle();
        setPosition();
        bounds.setSize(treeWidth, treeHeight);
    }

    public void update() {
        if (hitsToBreak <= 1)
            readyToDrop = true;

        if(currentLife < 1)
            resetTree();
    }

    private void calculateDropAmount() {
        logsToDrop = MathUtils.random(minDrop, maxDrop);

        if(app.gsm.getPlayer().getPowers().containsKey(PowerUp.POWER.DOUBLE_LOG))
            logsToDrop *= 2;
    }

    public void dropLogs(Pool<Log> logPool, Array<Log> logs, int numToDrop, Vector2 target) {
        float jumpMin = 5.1f;
        float jumpMax = 6.9f;

        for (int i = 0; i < numToDrop; i++) {
            Log log = logPool.obtain();
            logs.add(log);
            Vector2 randomPos = new Vector2(MathUtils.random(35f / PPM, 190f / PPM), target.y);
            log.jumpToTarget(randomPos, MathUtils.random(jumpMin, jumpMax));
        }

        int currentXp = app.prefs.getInteger("playerXp");
        app.gsm.getPlayer().setExperience(currentXp + experience);
    }

    private void setPosition() {
        float min = 755f;
        float max = 825f;

        bounds.x = (((float)Application.V_WIDTH * 2f) - (float)Application.V_WIDTH / 2f) / PPM;
        bounds.y = MathUtils.random(min, max) / PPM;
    }

    /**
     * Draws visible bars below trees to indicate the amount of hits needed for it to drop logs.
     * Used for debugging.
     */
    public void drawProgress() {
        float barLength = treeWidth / 2;

        // back of the bar
        app.shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        app.shapeRenderer.setColor(Color.WHITE);
        app.shapeRenderer.rect(
                bounds.x * PPM - treeWidth / 4,
                bounds.y * PPM - treeHeight / 2,
                barLength,
                16);

        float width = hitsToBreak / totalHits * barLength;

        // inner bar
        app.shapeRenderer.setColor(Color.RED);
        app.shapeRenderer.rect(
                bounds.x * PPM - treeWidth / 4,
                bounds.y * PPM - treeHeight / 2,
                width,
                16);


        // outline of bar
        app.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        app.shapeRenderer.setColor(Color.BLACK);
        app.shapeRenderer.rect(
                bounds.x * PPM - treeWidth / 4,
                bounds.y * PPM - treeHeight / 2,
                barLength,
                16);
    }

    public void render(SpriteBatch batch) {
        batch.draw(treeTex, bounds.x * PPM - treeWidth / 2f, bounds.y * PPM - treeHeight / 2f, treeWidth, treeHeight);
    }

    public void resetDrops() {
        readyToDrop = false;
        hitsToBreak = totalHits;
        calculateDropAmount();
    }

    private void resetTree() {
        currentLife = totalLife;
        setPosition();
    }

    public float getHitsToBreak() { return hitsToBreak; }

    public void setHitsToBreak(float hitsToBreak) { this.hitsToBreak = hitsToBreak; }

    public int getLogsToDrop() { return logsToDrop; }

    public boolean isReadyToDrop() { return readyToDrop; }

    public Rectangle getBounds() { return bounds; }

    public int getCurrentLife() { return currentLife; }

    public void setCurrentLife(int currentLife) { this.currentLife = currentLife; }
}
