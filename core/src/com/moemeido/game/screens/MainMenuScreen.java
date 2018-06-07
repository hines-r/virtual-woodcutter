package com.moemeido.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.moemeido.game.Application;
import com.moemeido.game.managers.GameScreenManager;
import com.moemeido.game.managers.MyInputProcessor;


public class MainMenuScreen extends AbstractScreen {

    private Skin skin;
    private GlyphLayout glyphLayout;

    private TextureRegion bg;

    public MainMenuScreen(Application app) {
        super(app);
    }

    @Override
    public void show() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new MyInputProcessor(app));
        Gdx.input.setInputProcessor(multiplexer);

        glyphLayout = new GlyphLayout();

        skin = new Skin();
        skin.addRegions(app.assets.get("ui/uiskin.atlas", TextureAtlas.class));
        skin.add("default-font", app.fonts.font40);
        skin.load(Gdx.files.internal("ui/uiskin.json"));

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);
        bg = atlas.findRegion("bg3_no_cloud");

        initButtons();

        app.batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void update(float delta) {

        stage.act(delta);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        app.batch.begin();
        app.batch.draw(bg, 0, 0, Application.V_WIDTH, Application.V_HEIGHT);
        glyphLayout.setText(app.fonts.font100, "WOOD");
        app.fonts.font100.draw(app.batch, glyphLayout, viewport.getWorldWidth() / 2 - glyphLayout.width / 2, viewport.getWorldHeight() - 200 );

        app.batch.end();
        stage.draw();
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

    private void initButtons() {
        Table table = new Table();
        table.setFillParent(true);

        TextButton playButton = new TextButton("Play", skin);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                app.gsm.setScreen(GameScreenManager.STATE.PLAY);
            }
        });

        TextButton quitButton = new TextButton("Quit", skin);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.bottom().padBottom(275);
        table.add(playButton).expandX().width(225).height(50).pad(15);
        table.row();
        table.add(quitButton).expandX().width(225).height(50).pad(15);

        playButton.addAction(Actions.parallel(
                Actions.alpha(0),
                Actions.fadeIn(1f, Interpolation.pow5))
        );

        quitButton.addAction(Actions.parallel(
                Actions.alpha(0),
                Actions.fadeIn(1f, Interpolation.pow5))
        );

        stage.addActor(table);
    }
}
