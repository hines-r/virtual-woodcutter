package com.moemeido.game.background;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.moemeido.game.Application;
import com.moemeido.game.entities.Log;
import com.moemeido.game.entities.Player;
import com.moemeido.game.entities.Tree;
import com.moemeido.game.screens.AbstractScreen;

import static com.moemeido.game.utils.B2DVars.PPM;

public class PlayBackground {

    private AbstractScreen screen;

    private TextureRegion bg1;
    private TextureRegion bg2;
    private TextureRegion cloud1;
    private TextureRegion cloud2;

    private float scale;
    private float speed;

    private float x1;
    private float x2;
    private float x3;
    private float x4;
    private float y1;
    private float y2;

    public PlayBackground(Application app, AbstractScreen screen) {
        this.screen = screen;

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);

        bg1 = atlas.findRegion("bg3_no_cloud");
        bg2 = atlas.findRegion("bg3_no_cloud");

        cloud1 = atlas.findRegion("cloud1");
        cloud2 = atlas.findRegion("cloud2");

        speed = 25;
        scale = 6f;

        // clouds starting positions
        x1 = screen.viewport.getWorldWidth();
        x2 = screen.viewport.getWorldWidth() * 2;
        y1 = setRandomY();
        y2 = setRandomY();

        // background starting positions
        x3 = 0;
        x4 = screen.viewport.getWorldWidth();
    }

    public void update(float delta, Player player, Tree tree, Array<Log> logs) {
        x1 -= speed * delta;
        x2 = x1 + cloud1.getRegionWidth() * scale + screen.viewport.getWorldWidth();

        if (x1 <= -cloud1.getRegionWidth() * scale - screen.viewport.getWorldWidth() * 2) {
            x1 = screen.viewport.getWorldWidth();
            x2 = x1 + cloud1.getRegionWidth() * scale;
            y1 = setRandomY();
            y2 = setRandomY();
        }

        if (player.isMoving()) {
            x3 -= player.getMovementSpeed() * delta;
            x4 = x3 - screen.viewport.getWorldWidth();
            tree.getBounds().x -= (player.getMovementSpeed() * delta) / PPM;

            if(x3 <= 0) {
                x3 = screen.viewport.getWorldWidth();
                x4 = 0;
            }

            // Allows the logs to also move with the scrolling background by setting the linear velocity equal to the players x velocity
            // while maintaining the parabolic curve by preserving the original y velocity.
            for (Log log : logs) {
                log.getBody().setLinearVelocity(-player.getMovementSpeed() / PPM, log.getBody().getLinearVelocity().y);
            }
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(bg1, x3, 0, Application.V_WIDTH, Application.V_HEIGHT);
        batch.draw(bg2, x4, 0, Application.V_WIDTH, Application.V_HEIGHT);
        batch.draw(cloud1, x1, y1, cloud1.getRegionWidth() * scale, cloud1.getRegionHeight() * scale);
        batch.draw(cloud2, x2, y2, cloud2.getRegionWidth() * scale, cloud2.getRegionHeight() * scale);
    }

    private float setRandomY() {
        return MathUtils.random(750f, 900f);
    }

    public float getX() {
        return x3;
    }


}
