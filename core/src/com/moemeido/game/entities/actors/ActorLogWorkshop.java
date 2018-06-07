package com.moemeido.game.entities.actors;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.moemeido.game.Application;

public class ActorLogWorkshop extends AbstractActor {

    private Image goldCoin;
    private float timeToTarget;

    public ActorLogWorkshop(Application app, Stage stage, Vector2 target, Vector2 origin) {
        super(app, stage, target, origin);

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);
        img = new Image(atlas.findRegion("log1"));
        img.setPosition(origin.x, origin.y);
        imgScale = 1.5f;
        img.setSize(img.getWidth() * imgScale, img.getHeight() * imgScale);
        img.setTouchable(Touchable.disabled);
        stage.addActor(img);

        bounds = new Rectangle(img.getX(), img.getY(), img.getWidth(), img.getHeight());

        goldCoin = new Image(atlas.findRegion("coin2"));
        timeToTarget = 2f;
    }

    public void update(float delta) {
        if (img.getX() == target.x && img.getY() == target.y) {
            isReadyToDestroy = true;
            img.setDrawable(goldCoin.getDrawable());
            img.addAction(
                    Actions.sequence(
                            Actions.parallel(Actions.fadeOut(1f), Actions.moveBy(0, 50f, 1f)),
                            Actions.removeActor())
            );
        }
    }

    public void linearMovement() {
        img.addAction(Actions.moveTo(target.x, target.y, timeToTarget, Interpolation.exp5));
    }

    @Override
    public void reset() {

    }

    public Image getImage() {
        return img;
    }

    public boolean isReadyToDestroy() {
        return isReadyToDestroy;
    }
}
