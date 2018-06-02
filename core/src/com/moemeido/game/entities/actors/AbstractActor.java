package com.moemeido.game.entities.actors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Pool;
import com.moemeido.game.Application;
import com.moemeido.game.screens.huds.HUD;

import static com.moemeido.game.utils.B2DVars.PPM;

public abstract class AbstractActor implements Pool.Poolable {

    protected final Application app;
    protected final Stage stage;

    Image img;
    float imgScale;

    Vector2 origin, target;
    boolean isReadyToDestroy;
    boolean readyToMove;

    float speed;

    Rectangle bounds;
    Rectangle targetBounds;

    AbstractActor(Application app, Stage stage, Vector2 origin, Vector2 target) {
        this.app = app;
        this.stage = stage;
        this.origin = origin;
        this.target = target;
    }

    AbstractActor(Application app, Stage stage, Vector2 origin, HUD hud) {
        this.app = app;
        this.stage = stage;
        this.origin = origin;
    }

    public abstract void update(float delta);

    private void moveToTarget() {

    }

    private void moveToHud(Image img, Vector2 target, float speed, float delta) {
        Vector2 dir = new Vector2(target.x - img.getX(), target.y - img.getY());
        float hyp = (float) Math.sqrt((dir.x * dir.x) + (dir.y * dir.y));
        float dirx = dir.x /= hyp;
        float diry = dir.y /= hyp;

        img.setX(img.getX() + (dirx * PPM * speed) * delta);
        img.setY(img.getY() + (diry * PPM * speed) * delta);
    }

    public abstract void reset();

    public Image getImg() {
        return img;
    }

    public float getImgScale() {
        return imgScale;
    }

    public Vector2 getOrigin() {
        return origin;
    }

    public Vector2 getTarget() {
        return target;
    }

    public boolean isReadyToDestroy() {
        return isReadyToDestroy;
    }

    public boolean isReadyToMove() {
        return readyToMove;
    }

    public float getSpeed() {
        return speed;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Rectangle getTargetBounds() {
        return targetBounds;
    }
}
