package com.moemeido.game.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.moemeido.game.Application;
import com.moemeido.game.managers.GameScreenManager;

public class LoadingScreen extends AbstractScreen {

    private float progress;

    private int loadIndex;
    private float stringTimer;
    private Array<String> loading;

    public LoadingScreen(Application app) {
        super(app);
    }

    @Override
    public void show() {
        loading = new Array<String>();
        loading.add("Loading");
        loading.add("Loading.");
        loading.add("Loading..");
        loading.add("Loading...");

        app.shapeRenderer.setProjectionMatrix(camera.combined);
        app.batch.setProjectionMatrix(camera.combined);
        progress = 0f;
    }

    @Override
    public void update(float delta) {
        progress = MathUtils.lerp(progress, app.assets.getProgress(), .1f);

        // Adds a small animation to the loading text with the ellipsis
        stringTimer += delta;
        if (stringTimer >= .15f) {
            loadIndex++;
            stringTimer = 0;
            if(loadIndex == loading.size) loadIndex = 0;
        }

        // keeps returning false, until all the assets are done loading!
        // once done loading, do anything!
        if (app.assets.update() && progress >= app.assets.getProgress() - .001f) {
            app.gsm.setScreen(GameScreenManager.STATE.MAIN_MENU);
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        app.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // back part of the loading bar!
        app.shapeRenderer.setColor(Color.BLACK);
        app.shapeRenderer.rect(
                32,
                camera.viewportHeight / 2 - 8,
                camera.viewportWidth - 64,
                16);

        // front of the loading bar!
        app.shapeRenderer.setColor(Color.PINK);
        app.shapeRenderer.rect(
                32,
                camera.viewportHeight / 2 - 8,
                progress * (camera.viewportWidth - 64),
                16);

        app.shapeRenderer.end();

        app.batch.begin();
        app.fonts.font20.draw(app.batch, loading.get(loadIndex), 32, camera.viewportHeight / 2 + 32);
        app.batch.end();
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        super.dispose();
    }

}
