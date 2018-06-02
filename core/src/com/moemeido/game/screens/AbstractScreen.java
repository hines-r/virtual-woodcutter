package com.moemeido.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.moemeido.game.Application;


public abstract class AbstractScreen implements Screen {

    protected final Application app;

    public OrthographicCamera camera;
    public Viewport viewport;
    public Stage stage;

    public AbstractScreen(final Application app) {
        this.app = app;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Application.V_WIDTH, Application.V_HEIGHT);
        this.viewport = new FitViewport(Application.V_WIDTH, Application.V_HEIGHT);
        this.stage = new Stage(viewport);
        Gdx.input.setCatchBackKey(true);
    }

    public abstract void update(float delta);

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);
    }

    @Override
    public void resize(int width, int height) {

        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {

        this.stage.dispose();
    }

}
