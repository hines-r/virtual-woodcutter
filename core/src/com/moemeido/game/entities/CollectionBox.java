package com.moemeido.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.moemeido.game.Application;

public class CollectionBox {

    private Application app;

    private float x, y;

    private Sprite boxSprite;
    private float scale;

    private Rectangle bounds;

    public CollectionBox(Application app, float x, float y) {
        this.app = app;
        this.x = x;
        this.y = y;

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);
        boxSprite = new Sprite(atlas.findRegion("collect_box"));

        scale = 1.25f; // Scales the box size up a bit

        bounds = new Rectangle(x, y, boxSprite.getWidth() * scale, boxSprite.getHeight() * scale);
    }

    public void render(SpriteBatch batch) {
        batch.draw(boxSprite, x, y, boxSprite.getWidth() * scale, boxSprite.getHeight() * scale);
    }

    /**
     * Draws the bounds of the collection box.
     * Used for debugging.
     */
    public void drawBounds() {
        app.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        app.shapeRenderer.setColor(Color.GREEN);
        app.shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public Rectangle getBounds() {
        return bounds;
    }


}
