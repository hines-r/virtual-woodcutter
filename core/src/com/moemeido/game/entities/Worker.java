package com.moemeido.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.moemeido.game.Application;
import com.moemeido.game.utils.LevelScaling;

import static com.moemeido.game.utils.B2DVars.PPM;

public class Worker implements IUpgradable {

    private Application app;
    private Stage stage;

    private Sprite workerWalk;
    private Sprite workerLog;
    private Animation workerChopAnimation;
    private float stateTime;

    private int level;
    private int logAmount;
    private float movementSpeed;
    private int upgradeCost;
    private int purchaseCost;

    private float movementCap;
    private float gatherTimeCap;

    private boolean moveToTree;
    private boolean returningLogs;
    private boolean gathering;

    private float totalGatherTime;
    private float currentGatherTime;

    private Rectangle workerBounds;
    private Rectangle returnBounds;
    private Rectangle targetBounds;

    private Vector2 target;
    private float targetWidth;
    private float targetHeight;

    private boolean returnComplete;

    public Worker(Application app, Stage stage, Rectangle returnBounds) {
        this.app = app;
        this.stage = stage;
        this.returnBounds = returnBounds;

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);

        workerWalk = new Sprite(atlas.findRegion("worker_axe"));
        workerLog = new Sprite(atlas.findRegion("worker_log"));
        workerChopAnimation = new Animation<TextureRegion>(.1f, atlas.findRegions("worker_axe"));

        workerWalk.setPosition(MathUtils.random(10, 100), MathUtils.random(100, 900));

        level = 1;
        logAmount = 10;
        movementSpeed = 5f;
        totalGatherTime = 3f;
        upgradeCost = 100;
        purchaseCost = 100;

        movementCap = 10f;
        gatherTimeCap = .5f;

        workerBounds = new Rectangle(workerWalk.getX(), workerWalk.getY(), workerWalk.getWidth(), workerWalk.getHeight());

        moveToTree = true;

        target = new Vector2();
        targetWidth = 5;
        targetHeight = 5;
    }

    public void update(float delta) {
        workerBounds.x = workerWalk.getX();
        workerBounds.y = workerWalk.getY();

        if(moveToTree)
            moveToTarget(target);

        else if(gathering)
            chopTree(delta);

        else
            moveToTarget(new Vector2(returnBounds.x + returnBounds.width / 2, returnBounds.y + returnBounds.height / 2));
    }

    public void render(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();

        if(moveToTree)
            batch.draw(workerWalk, workerBounds.x, workerBounds.y);

        else if (gathering)
            batch.draw((TextureRegion)workerChopAnimation.getKeyFrame(stateTime, true), workerBounds.x, workerBounds.y);

        else
            batch.draw(workerLog, workerBounds.x, workerBounds.y);
    }

    private void moveToTarget(Vector2 target) {
        if(!returningLogs)
            targetBounds = new Rectangle(target.x + 30, target.y, targetWidth, targetHeight);
        else
            targetBounds = new Rectangle(target.x - targetWidth / 2, target.y - targetHeight / 2, targetWidth, targetHeight);

        Vector2 dir = new Vector2(target.x - workerBounds.x, target.y - workerBounds.y);
        float hyp = (float) Math.sqrt((dir.x * dir.x) + (dir.y * dir.y));
        float dirX = dir.x /= hyp;
        float dirY = dir.y /= hyp;

        workerWalk.setX(workerWalk.getX()+ (dirX * PPM * movementSpeed) * Gdx.graphics.getDeltaTime());
        workerWalk.setY(workerWalk.getY() + (dirY * PPM * movementSpeed) * Gdx.graphics.getDeltaTime());

        // Begin gathering once worker has reached the target
        if (targetBounds.overlaps(workerBounds) && moveToTree) {
            moveToTree = false;
            gathering = true;
        }

        // Return logs
        else if (returnBounds.overlaps(workerBounds) && !moveToTree && !gathering) {
            moveToTree = true;
            Label.LabelStyle labelStyle1 = new Label.LabelStyle(app.fonts.font30, Color.WHITE);
            Label yieldLabel = new Label("+" + String.valueOf(logAmount), labelStyle1);
            yieldLabel.setPosition(returnBounds.x + MathUtils.random(-25, 25), returnBounds.y + returnBounds.height + 10 + MathUtils.random(0, 55));
            yieldLabel.addAction(
                    Actions.sequence(
                            Actions.parallel(
                                    Actions.moveBy(0, 50, 1f),
                                    Actions.fadeOut(1f)),
                            Actions.removeActor()));
            stage.addActor(yieldLabel);

            app.gsm.getPlayer().setLogCount(app.prefs.getInteger("playerLogs") + logAmount);

            returnComplete = true;
            returningLogs = false;
        }
    }

    private void chopTree(float delta) {
        currentGatherTime += delta;

        if (currentGatherTime >= totalGatherTime) {
            gathering = false;
            returningLogs = true;
            currentGatherTime = 0f;
            stateTime = 0f;
        }
    }

    public void drawBounds() {
        app.shapeRenderer.setColor(Color.BLACK);
        app.shapeRenderer.rect(workerBounds.x, workerBounds.y, workerBounds.width, workerBounds.height);

        app.shapeRenderer.setColor(Color.RED);
        app.shapeRenderer.rect(targetBounds.x, targetBounds.y, targetBounds.width, targetBounds.height);

        app.shapeRenderer.setColor(Color.GREEN);
        app.shapeRenderer.rect(returnBounds.x, returnBounds.y, returnBounds.width, returnBounds.height);
    }

    public void drawGatherProgress() {
        if(gathering) {
            float barLength = workerWalk.getWidth();
            float width = (currentGatherTime / totalGatherTime) * barLength;
            float visualWorkerBounds = 15f; // Actual sprite size is larger to compensate for axe animation

            app.shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
            app.shapeRenderer.setColor(Color.LIGHT_GRAY);
            app.shapeRenderer.rect(workerBounds.x - visualWorkerBounds, workerBounds.y + workerBounds.height + 5, barLength, 10);

            app.shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
            app.shapeRenderer.setColor(Color.WHITE);
            app.shapeRenderer.rect(workerBounds.x - visualWorkerBounds, workerBounds.y + workerBounds.height + 5, width, 10);

            app.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            app.shapeRenderer.setColor(Color.BLACK);
            app.shapeRenderer.rect(workerBounds.x - visualWorkerBounds, workerBounds.y + workerBounds.height + 5, barLength, 10);
        }
    }

    public void setNewTarget(Vector2 target) {
        returnComplete = false;
        this.target = target;
    }

    public void upgrade() {
        level++;
        logAmount += LevelScaling.computeUpgradeYields(level, 10, 0.5f);

        if(movementSpeed + .1f < movementCap)
            movementSpeed += .1f;

        if(totalGatherTime - .1f < gatherTimeCap)
            totalGatherTime -= .1f;

        upgradeCost += LevelScaling.computeUpgradeYields(level, 10, 0.25f);
    }

    public void incrementPurchaseCost() {
        purchaseCost *= 1.5;
    }

    public boolean isReturnComplete() {
        return returnComplete;
    }

    public int getLevel() {
        return level;
    }

    public int getLogAmount() {
        return logAmount;
    }

    public float getTotalGatherTime() {
        return totalGatherTime;
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }

    public int getUpgradeCost() {
        return upgradeCost;
    }

    public int getPurchaseCost() {
        return purchaseCost;
    }
}
