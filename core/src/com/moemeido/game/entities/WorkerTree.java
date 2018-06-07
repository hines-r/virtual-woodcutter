package com.moemeido.game.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.moemeido.game.Application;

public class WorkerTree {

    private float x, y;
    private Sprite treeSprite;

    private Rectangle treeBounds;

    public WorkerTree(Application app, float x, float y){
        this.x = x;
        this.y = y;

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);
        treeSprite = new Sprite(atlas.findRegion("smol_tree"));

        float width = treeSprite.getWidth();
        float height = treeSprite.getHeight();

        treeBounds = new Rectangle(x, y, width, height);
    }

    public void render(SpriteBatch batch) {
        batch.draw(treeSprite, x, y);
    }

    public Vector2 getBounds(){
        return new Vector2(treeBounds.x, treeBounds.y);
    }

}
