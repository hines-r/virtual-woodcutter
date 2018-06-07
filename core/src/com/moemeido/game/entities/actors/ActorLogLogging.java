package com.moemeido.game.entities.actors;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.moemeido.game.Application;
import com.moemeido.game.screens.huds.HUD;

public class ActorLogLogging extends AbstractActor {

    private HUD hud;
    private float speed;

    public ActorLogLogging(Application app, Stage stage, Vector2 target, HUD hud) {
        super(app, stage, target);
        this.hud = hud;
        this.target = hud.getLogPosition();

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);
        img = new Image(atlas.findRegion("log1"));

        float randPosX = -35;
        float randPosY = 30;
        img.setPosition(origin.x + MathUtils.random(randPosX, randPosY), origin.y + MathUtils.random(randPosX, randPosY));

        imgScale = 1.5f;
        img.setSize(img.getWidth() * imgScale, img.getHeight() * imgScale);
        img.setTouchable(Touchable.disabled);
        stage.addActor(img);

        bounds = new Rectangle(img.getX(), img.getY(), img.getWidth(), img.getHeight());

        speed = 45f;
    }

    public void update(float delta) {
        target.x = hud.getLogPosition().x;
        target.y = hud.getLogPosition().y;
        bounds.setPosition(img.getX() + img.getWidth() / 4, img.getY() + img.getHeight() / 4);

        if (hud.getLogBounds().contains(bounds.x, bounds.y))
            isReadyToDestroy = true;

        else if (readyToMove)
            moveToTarget(img, target, speed, delta);
    }

    @Override
    public void reset() {

    }

    public Vector2 getPosition() {
        return new Vector2(img.getX(), img.getY());
    }

    public Image getImage() {
        return img;
    }

    public void setReadyToMove(boolean readyToMove) {
        this.readyToMove = readyToMove;
    }

    public boolean isReadyToDestroy() {
        return isReadyToDestroy;
    }
}
