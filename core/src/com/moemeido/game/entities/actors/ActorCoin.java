package com.moemeido.game.entities.actors;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.moemeido.game.Application;
import com.moemeido.game.screens.huds.HUD;

import static com.moemeido.game.utils.B2DVars.PPM;

public class ActorCoin extends AbstractActor {

    private HUD hud;
    private float speed;

    public ActorCoin(Application app, Stage stage, Vector2 target, Vector2 origin, HUD hud) {
        super(app, stage, target, origin);
        this.hud = hud;

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);
        img = new Image(atlas.findRegion("coin2"));
        img.setSize(hud.getUiCoin().getWidth(), hud.getUiCoin().getHeight());
        img.setPosition(origin.x + MathUtils.random(-35, 30), origin.y + MathUtils.random(-35, 30));
        img.setTouchable(Touchable.disabled);

        speed = 45f; // pixels per second

        bounds = new Rectangle();
        stage.addActor(img);
    }

    public void update(float delta) {
        target = hud.getCoinPosition();
        bounds.setPosition(img.getX() + img.getWidth() / 4, img.getY() + img.getHeight() / 4);

        if (hud.getCoinBounds().contains(bounds.x, bounds.y))
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

    public void setReadyToMove(boolean readyToMove) {
        this.readyToMove = readyToMove;
    }

    public Image getImage() {
        return img;
    }

    public boolean isReadyToDestroy() {
        return isReadyToDestroy;
    }
}
