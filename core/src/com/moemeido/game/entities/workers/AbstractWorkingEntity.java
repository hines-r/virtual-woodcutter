package com.moemeido.game.entities.workers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.moemeido.game.Application;
import com.moemeido.game.entities.IUpgradable;
import com.moemeido.game.screens.AbstractScreen;
import com.moemeido.game.utils.LevelScaling;
import com.moemeido.game.utils.UITools;

public abstract class AbstractWorkingEntity extends Actor implements IUpgradable {

    protected final Application app;

    Stage stage, dynamicStage;

    float x, y;
    float width, height;

    int level;
    int boostLevel;
    int baseBoostLevel;
    private int timesBoosted;

    int upgradeCost;
    int baseCost;

    boolean working;
    boolean readyToCollect;

    int intake;
    int currentIntake;
    int yield;
    int currentYield;

    float currentWorkTime;
    float totalWorkTime;
    float minWorkTime;

    TextureRegion tex;
    float texScale;
    float collectScale;

    Rectangle bounds;
    Vector2 center;

    VisProgressBar levelBar;

    UITools dialog;

    AbstractWorkingEntity(Application app, Stage stage, Stage dynamicStage, float x, float y) {
        this.app = app;
        this.stage = stage;
        this.dynamicStage = dynamicStage;
        this.x = x;
        this.y = y;

        dialog = new UITools(app, stage);
    }

    public void update(float delta) {
        if(working)
            currentWorkTime += delta;

        if (currentWorkTime >= totalWorkTime) {
            working = false;
            currentWorkTime = 0f;
            readyToCollect = true;
        }
    }

    public abstract void render(SpriteBatch batch);

    /**
     * Draws a progress bar below the entity equal to its width
     */
    public void drawProgressBar() {
        float barLength = tex.getRegionWidth() * texScale;
        float width = (currentWorkTime / totalWorkTime) * barLength;

        app.shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        app.shapeRenderer.setColor(Color.LIGHT_GRAY);
        app.shapeRenderer.rect(x, y - 35, barLength, 25);

        app.shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        app.shapeRenderer.setColor(Color.WHITE);
        app.shapeRenderer.rect(x, y - 35, width, 25);

        app.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        app.shapeRenderer.setColor(Color.BLACK);
        app.shapeRenderer.rect(x, y - 35, barLength, 25);
    }

    public void upgrade() {
        level++;

        if (level >= boostLevel){
            timesBoosted++;
            boostLevel += baseBoostLevel + (5 * timesBoosted);

            levelBar.setRange(level, boostLevel);

            intake = LevelScaling.calculateGrowth(intake, 1.75f, timesBoosted);
            yield = LevelScaling.calculateGrowth(yield, 1.75f, timesBoosted);
        }

        currentIntake += intake;
        currentYield += yield;

        if (totalWorkTime > minWorkTime){
            totalWorkTime -= .05f;
        }

        upgradeCost = LevelScaling.calculateGrowth(baseCost, 1.07f, level);

        levelBar.setValue(level);
    }

    public Vector2 getPosition() {
        return new Vector2(bounds.x, bounds.y);
    }

    public Vector2 getCenter() {
        return center;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean isWorking() {
        return working;
    }

    public boolean isReadyToCollect() {
        return readyToCollect;
    }

    public int getLevel() {
        return level;
    }
}
