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

    private Application app;
    private HUD hud;

    private Image coinImg;
    private float coinScale;

    private Vector2 origin, target;
    private boolean isReadyToDestroy;
    private boolean readyToMove;

    private float speed;

    private Rectangle targetBounds;
    private Rectangle bounds;

    public ActorCoin(Application app, Stage stage, Vector2 origin, HUD hud) {
        super(app, stage, origin, hud);
        this.app = app;
        this.origin = origin;
        this.hud = hud;

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);
        coinImg = new Image(atlas.findRegion("coin2"));

        coinScale = 1f;
        coinImg.setSize(hud.getUiCoin().getWidth(), hud.getUiCoin().getHeight());

        coinImg.setPosition(origin.x + MathUtils.random(-35, 30), origin.y + MathUtils.random(-35, 30));

        coinImg.setTouchable(Touchable.disabled);

        speed = 45f; // pixels per second

        target = hud.getCoinPosition();
        bounds = new Rectangle();

        stage.addActor(coinImg);
    }

    public void update(float delta) {
        target.x = hud.getCoinPosition().x;
        target.y = hud.getCoinPosition().y;
        bounds.setPosition(coinImg.getX() + coinImg.getWidth() / 4, coinImg.getY() + coinImg.getHeight() / 4);

        if (hud.getCoinBounds().contains(bounds.x, bounds.y))
            isReadyToDestroy = true;

        else if (readyToMove)
            moveToTarget(delta);
    }

    public void drawTarget() {
        app.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        app.shapeRenderer.rect(hud.getCoinBounds().x - 1, hud.getCoinBounds().y - 1, hud.getUiCoin().getWidth(), hud.getUiCoin().getHeight());
        app.shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        app.shapeRenderer.end();
    }

    private void moveToTarget(float delta) {
        Vector2 dir = new Vector2(target.x - coinImg.getX(), target.y - coinImg.getY());
        float hyp = (float) Math.sqrt((dir.x * dir.x) + (dir.y * dir.y));
        float dirx = dir.x /= hyp;
        float diry = dir.y /= hyp;

        coinImg.setX(coinImg.getX() + (dirx * PPM * speed) * delta);
        coinImg.setY(coinImg.getY() + (diry * PPM * speed) * delta);
    }

    @Override
    public void reset() {

    }

    public Vector2 getPosition() {
        return new Vector2(coinImg.getX(), coinImg.getY());
    }

    public void setReadyToMove(boolean readyToMove) {
        this.readyToMove = readyToMove;
    }

    public Image getImage() {
        return coinImg;
    }

    public boolean isReadyToDestroy() {
        return isReadyToDestroy;
    }
}
