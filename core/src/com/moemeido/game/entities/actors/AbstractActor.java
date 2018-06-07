package com.moemeido.game.entities.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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

    Rectangle bounds;

    AbstractActor(Application app, Stage stage, Vector2 target) {
        this.app = app;
        this.stage = stage;
        this.target = target;
    }

    public abstract void update(float delta);

    /**
     * Used to render the bounds of the actor. Used for debugging.
     */
    public void renderBounds() {
        app.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        app.shapeRenderer.setColor(Color.RED);
        app.shapeRenderer.rect(bounds.x, bounds.y, img.getWidth(), img.getHeight());
        app.shapeRenderer.end();
    }

    void moveToTarget(Image img, Vector2 target, float speed, float delta) {
        Vector2 dir = new Vector2(target.x - img.getX(), target.y - img.getY());
        float hyp = (float) Math.sqrt((dir.x * dir.x) + (dir.y * dir.y));
        float dirX = dir.x /= hyp;
        float dirY = dir.y /= hyp;

        img.setX(img.getX() + (dirX * PPM * speed) * delta);
        img.setY(img.getY() + (dirY * PPM * speed) * delta);
    }

    public abstract void reset();

    public Image getImg() {
        return img;
    }

    public boolean isReadyToDestroy() {
        return isReadyToDestroy;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
