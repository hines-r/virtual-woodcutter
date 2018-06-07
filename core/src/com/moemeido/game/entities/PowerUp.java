package com.moemeido.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.moemeido.game.Application;

public class PowerUp {

    private Application app;
    private Stage stage;

    private Rectangle bounds;

    private boolean isActive;
    private float activeDuration;

    private boolean readyToDestroy;

    private POWER powerType;

    private boolean flyingAway;
    private boolean flyRight;

    private Animation birdAnimation;
    private Animation birdFlyAwayAnimation;
    private boolean offScreen;

    private float birdSpeed;
    private float elapsedTime;
    private float animationWidth, animationHeight;
    private Image powerImg;

    public enum POWER {
        NO_POWER,
        FULL_AUTO,
        MIGHT,
        DOUBLE_LOG
    }

    public PowerUp(Application app, Stage stage, float x, float y, POWER powerType) {
        this.app = app;
        this.stage = stage;
        this.powerType = powerType;

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);

        float frameDuration = 0.4f; // The speed of the animation

        switch (powerType) {
            case FULL_AUTO:
                powerImg = new Image(atlas.findRegion("axe_speed"));
                birdAnimation = new Animation<TextureRegion>(frameDuration, atlas.findRegions("bird_auto"), Animation.PlayMode.LOOP);
                break;
            case MIGHT:
                powerImg = new Image(atlas.findRegion("might"));
                birdAnimation = new Animation<TextureRegion>(frameDuration, atlas.findRegions("bird_might"), Animation.PlayMode.LOOP);
                break;
            case DOUBLE_LOG:
                powerImg = new Image(atlas.findRegion("double_log"));
                birdAnimation = new Animation<TextureRegion>(frameDuration, atlas.findRegions("bird_double_log"), Animation.PlayMode.LOOP);
                break;
        }

        birdFlyAwayAnimation = new Animation<TextureRegion>(frameDuration, atlas.findRegions("bird"), Animation.PlayMode.LOOP);

        elapsedTime = 0f;
        birdSpeed = 110f;
        flyRight = true;

        float birdScale = 2f;
        animationWidth = atlas.findRegion("bird_auto").getRegionWidth() * birdScale;
        animationHeight = atlas.findRegion("bird_auto").getRegionHeight() * birdScale;

        activeDuration = 15f; // If picked up, can remain active for this long

        bounds = new Rectangle(x, y, animationWidth, animationHeight);
    }

    public void update(float delta) {
        // Move the bird in the proper direction
        if(!offScreen) {
            if (flyRight)
                bounds.x += birdSpeed * delta;
            else
                bounds.x -= birdSpeed * delta;

            if (flyingAway) {
                bounds.y += birdSpeed * delta;

                // Check to see if the bounds are off screen
                if (bounds.y > Application.V_HEIGHT) {
                    offScreen = true;
                    birdFlyAwayAnimation = null;
                    birdAnimation = null;
                }
            }
        }

        // Removes the buff once active duration has expired
        if (isActive) {
            activeDuration -= delta;

            if(activeDuration <= 0) {
                readyToDestroy = true;
                isActive = false;
                powerImg.remove(); // Removes from stage
            }
        }

        // Destroys the power once it exceeds world width and updates
        // the x coordinates based on player movement when the player is moving
        else {
            if(bounds.x > Application.V_WIDTH)
                readyToDestroy = true;

            if(app.gsm.getPlayer().isMoving()){
                bounds.x -= app.gsm.getPlayer().getMovementSpeed() * delta;
            }
        }
    }

    public void render(SpriteBatch batch) {
        elapsedTime += Gdx.graphics.getDeltaTime();

        // Don't compute rendering if sprite is off screen
        if(!offScreen) {
            if (!flyingAway)
                batch.draw(((TextureRegion) birdAnimation.getKeyFrame(elapsedTime, true)), bounds.x, bounds.y, animationWidth, animationHeight);
            else
                batch.draw(((TextureRegion) birdFlyAwayAnimation.getKeyFrame(elapsedTime, true)), bounds.x, bounds.y, animationWidth, animationHeight);
        }
    }

    /**
     * Renders the bounds of power-ups
     * Used for debugging
     */
    public void renderBounds() {
        app.shapeRenderer.setAutoShapeType(true);
        app.shapeRenderer.begin();
        app.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        app.shapeRenderer.setColor(Color.RED);
        app.shapeRenderer.rect(bounds.x, bounds.y, animationWidth, animationHeight);
        app.shapeRenderer.end();
    }

    private void activatePower(Player player) {
        stage.addActor(powerImg);
        powerImg.setPosition(bounds.x, bounds.y);
        powerImg.setSize(100, 100);

        powerImg.addAction(Actions.parallel(
                Actions.sizeTo(450, 450, .5f),
                Actions.moveTo((Application.V_WIDTH / 2) - 450 / 2f, (Application.V_HEIGHT / 2) - 450 / 2f, .5f, Interpolation.linear),
                Actions.fadeOut(1f)));

        if(player.getPowers().containsKey(powerType))
            player.getPowers().get(powerType).setActiveDuration(player.getPowers().get(powerType).getActiveDuration() + activeDuration);
        else
            player.getPowers().put(powerType, this);

        isActive = true;
    }

    public boolean checkTouch(Vector3 touch, Player player) {
        if (bounds.contains(touch.x, touch.y) && !flyingAway) {
            // Roll for a random direction the bird can fly off to
            int roll = MathUtils.random(0, 1);
            if(roll == 0) {
                flyRight = false;
                // Flips all frames within the flying away animation when bird needs to fly to the left
                for(TextureRegion t : (TextureRegion[])birdFlyAwayAnimation.getKeyFrames())
                    t.flip(true, false);
            }

            activatePower(player);
            flyingAway = true;
            return true;
        }

        return false;
    }

    public boolean isReadyToDestroy() {
        return readyToDestroy;
    }

    public float getActiveDuration() {
        return activeDuration;
    }

    private void setActiveDuration(float activeDuration) {
        this.activeDuration = activeDuration;
    }
}
