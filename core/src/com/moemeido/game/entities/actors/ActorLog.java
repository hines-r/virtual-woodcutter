package com.moemeido.game.entities.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.moemeido.game.Application;
import com.moemeido.game.entities.actors.AbstractActor;
import com.moemeido.game.screens.huds.HUD;

import static com.moemeido.game.utils.B2DVars.PPM;

public class ActorLog extends AbstractActor {

    private Application app;
    private HUD hud;

    private Image logImg, gold;
    private float logScale;

    private Vector2 origin, target;
    private boolean isReadyToDestroy;

    private float speed;
    private float timeToTarget;

    private float positionX, positionY;
    private float velocityX, velocityY;
    private float gravity;

    private Rectangle bounds;

    private boolean readyToMove;

    private boolean dynamicLog;

    public ActorLog(Application app, Stage stage, Vector2 origin, Vector2 target) {
        super(app, stage, origin, target);
        this.app = app;
        this.origin = origin;
        this.target = target;

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);
        logImg = new Image(atlas.findRegion("log1"));
        gold = new Image(atlas.findRegion("coin2"));
        logImg.setPosition(origin.x, origin.y);
        logScale = 1.5f;
        logImg.setSize(logImg.getWidth() * logScale, logImg.getHeight() * logScale);
        logImg.setTouchable(Touchable.disabled);
        stage.addActor(logImg);

        timeToTarget = 2f;

        gravity = 9.81f;

        Vector2 initialVelocity =  calculateInitialVelocity();

        velocityX = initialVelocity.x;
        velocityY = initialVelocity.y;

        bounds = new Rectangle(logImg.getX(), logImg.getY(), logImg.getWidth(), logImg.getHeight());
    }

    public ActorLog(Application app, Stage stage, Vector2 origin, HUD hud) {
        super(app, stage, origin, hud);
        this.app = app;
        this.origin = origin;
        this.hud = hud;

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);
        logImg = new Image(atlas.findRegion("log1"));
        logImg.setPosition(origin.x + MathUtils.random(-35, 30), origin.y + MathUtils.random(-35, 30));
        logScale = 1.5f;
        logImg.setSize(logImg.getWidth() * logScale, logImg.getHeight() * logScale);
        logImg.setTouchable(Touchable.disabled);
        stage.addActor(logImg);

        target = hud.getLogPosition();

        speed = 45f;

        dynamicLog = true;

        bounds = new Rectangle(logImg.getX(), logImg.getY(), logImg.getWidth(), logImg.getHeight());
    }

    public void update(float delta) {
        if (dynamicLog) {
            target.x = hud.getLogPosition().x;
            target.y = hud.getLogPosition().y;
            bounds.setPosition(logImg.getX() + logImg.getWidth() / 4, logImg.getY() + logImg.getHeight() / 4);

            if (hud.getLogBounds().contains(bounds.x, bounds.y))
                isReadyToDestroy = true;

            else if (readyToMove)
                moveToHud(delta);
        }

        else {
            if (logImg.getX() == target.x && logImg.getY() == target.y) {
                isReadyToDestroy = true;
                logImg.setDrawable(gold.getDrawable());
                logImg.addAction(
                        Actions.sequence(
                                Actions.parallel(Actions.fadeOut(1f), Actions.moveBy(0, 50f, 1f)),
                                Actions.removeActor())
                );
            }
        }
    }

    public void renderBounds() {
        app.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        app.shapeRenderer.setColor(Color.RED);
        app.shapeRenderer.rect(bounds.x, bounds.y, logImg.getWidth(), logImg.getHeight());
        app.shapeRenderer.end();
    }

    private Vector2 calculateInitialVelocity() {
        float displacementX = target.x / PPM - logImg.getX() / PPM;
        float displacementY = target.y / PPM - logImg.getY() / PPM;

        float jumptHeight = 12f;

        Vector2 initialVelocity = new Vector2(
                displacementX / (float)((Math.sqrt((-2 * jumptHeight) / -gravity) + Math.sqrt((2 * (displacementY - jumptHeight)) / -gravity))),
                (float)(Math.sqrt(-2f * -gravity * jumptHeight)));

        return initialVelocity;
    }

    public void render(SpriteBatch batch) {

    }

    public void moveToTarget() {

        logImg.addAction(Actions.moveTo(target.x, target.y, timeToTarget, Interpolation.exp5));
    }

    private void moveToHud(float delta) {
        Vector2 dir = new Vector2(target.x - logImg.getX(), target.y - logImg.getY());
        float hyp = (float) Math.sqrt((dir.x * dir.x) + (dir.y * dir.y));
        float dirx = dir.x /= hyp;
        float diry = dir.y /= hyp;

        logImg.setX(logImg.getX() + (dirx * PPM * speed) * delta);
        logImg.setY(logImg.getY() + (diry * PPM * speed) * delta);
    }

    @Override
    public void reset() {

    }

    public Vector2 getPosition() {
        return new Vector2(logImg.getX(), logImg.getY());
    }

    public Image getImage() {
        return logImg;
    }

    public boolean isReadyToMove() {
        return readyToMove;
    }

    public void setReadyToMove(boolean readyToMove) {
        this.readyToMove = readyToMove;
    }

    public boolean isReadyToDestroy() {
        return isReadyToDestroy;
    }
}
