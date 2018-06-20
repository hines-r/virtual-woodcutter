package com.moemeido.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.moemeido.game.Application;
import com.moemeido.game.managers.GameScreenManager;

import java.text.NumberFormat;
import java.util.Locale;

public class HUD {

    private Application app;
    private Stage dynamicStage;

    private Skin skin;
    private TextButton farmButton;
    private TextButton shopButton;
    private TextButton workerButton;
    private TextButton workshopButton;
    private Color buttonSelectColor;

    private Table buttonTable;
    private Table topTable;

    private Image uiLog;
    private Image uiCoin;

    private VisProgressBar xpBar;
    private Label logLabel;
    private Label goldLabel;
    private Label playerLevel, playerXP;

    private boolean dynamicHUD;

    private Rectangle logBounds;
    private Rectangle coinBounds;

    public HUD(Application app, Stage stage) {
        this.app = app;

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);
        uiLog = new Image(atlas.findRegion("log1"));
        uiCoin = new Image(atlas.findRegion("coin2"));

        uiCoin.setSize(uiLog.getWidth(), uiLog.getHeight());

        skin = new Skin();
        skin.addRegions(app.assets.get("ui/uiskin.atlas", TextureAtlas.class));
        skin.add("default-font", app.fonts.font30);
        skin.load(Gdx.files.internal("ui/uiskin.json"));

        buttonTable = new Table();
        buttonTable.setFillParent(true);
        buttonTable.setDebug(false);
        stage.addActor(buttonTable);
        initButtons();

        topTable = new Table();
        topTable.setFillParent(true);
        topTable.setDebug(false);
        stage.addActor(topTable);

        initTopUI();
    }

    public HUD(Application app, Stage stage, Stage dynamicStage) {
        this.app = app;
        this.dynamicStage = dynamicStage;

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);
        uiLog = new Image(atlas.findRegion("log1"));
        uiCoin = new Image(atlas.findRegion("coin2"));

        uiCoin.setSize(uiLog.getWidth(), uiLog.getHeight());

        skin = new Skin();
        skin.addRegions(app.assets.get("ui/uiskin.atlas", TextureAtlas.class));
        skin.add("default-font", app.fonts.font30);
        skin.load(Gdx.files.internal("ui/uiskin.json"));

        buttonTable = new Table();
        buttonTable.setFillParent(true);
        buttonTable.setDebug(false);
        stage.addActor(buttonTable);
        initButtons();

        topTable = new Table();
        topTable.setFillParent(true);
        topTable.setDebug(false);
        stage.addActor(topTable);

        initTopUI();

        dynamicHUD = true;
        logBounds = new Rectangle(getStageCoordinates(uiLog).x, getStageCoordinates(uiLog).y, uiLog.getWidth(), uiLog.getHeight());
        coinBounds = new Rectangle(getStageCoordinates(uiCoin).x, getStageCoordinates(uiCoin).y, uiCoin.getWidth(), uiCoin.getHeight());
    }

    public void update() {
        // If the hud is dynamic, moves the bounds of the ui log and coin according the the stage camera position
        if (dynamicHUD) {
            logBounds.setPosition(
                    dynamicStage.getCamera().position.x - dynamicStage.getWidth() / 2 + uiLog.getX(),
                    dynamicStage.getCamera().position.y - dynamicStage.getHeight() / 2 + uiLog.getY());
            coinBounds.setPosition(
                    dynamicStage.getCamera().position.x - dynamicStage.getWidth() / 2 + uiCoin.getX(),
                    dynamicStage.getCamera().position.y - dynamicStage.getHeight() / 2 + uiCoin.getY());
        }

        // Change button color depending which button is selected
        if (app.gsm.getCurrentState() == GameScreenManager.STATE.WORKSHOP)
            workshopButton.setColor(buttonSelectColor);

        else if (app.gsm.getCurrentState() == GameScreenManager.STATE.LOGGING)
            farmButton.setColor(buttonSelectColor);

        else if (app.gsm.getCurrentState() == GameScreenManager.STATE.WORKERS)
            workerButton.setColor(buttonSelectColor);

        else if (app.gsm.getCurrentState() == GameScreenManager.STATE.SHOP)
            shopButton.setColor(buttonSelectColor);

        logLabel.setText(String.valueOf(NumberFormat.getInstance(Locale.getDefault()).format(app.prefs.getInteger("playerLogs"))));
        goldLabel.setText(String.valueOf(NumberFormat.getInstance(Locale.getDefault()).format(app.prefs.getInteger("playerGold"))));
        playerLevel.setText("Level: " + String.valueOf(NumberFormat.getInstance(Locale.getDefault()).format(app.prefs.getInteger("playerLevel"))));
        playerXP.setText("XP: " + String.valueOf(NumberFormat.getInstance(Locale.getDefault()).format(app.prefs.getInteger("playerXp"))));

        // Resets the range of values on the xp bar if the player has gained or exceeded the amount of xp needed to obtain the next level
        if (xpBar.getValue() >= xpBar.getMaxValue() || xpBar.getValue() == 0){
            xpBar.setRange(app.prefs.getInteger("previousXp"), app.prefs.getInteger("playerXpNeeded"));
            xpBar.setValue(xpBar.getMinValue());
        }

        xpBar.setValue(app.prefs.getInteger("playerXp"));
    }

    private void initButtons() {
        buttonSelectColor = Color.FOREST;

        workshopButton = new TextButton("Workshop", skin);
        workshopButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (app.gsm.getCurrentState() != GameScreenManager.STATE.WORKSHOP) {
                    app.gsm.setScreen(GameScreenManager.STATE.WORKSHOP);
                } else if (app.gsm.getCurrentState() == GameScreenManager.STATE.WORKSHOP) {
                    app.gsm.setScreen(GameScreenManager.STATE.PLAY);
                }
            }
        });

        farmButton = new TextButton("Logging", skin);
        farmButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {

                if (app.gsm.getCurrentState() != GameScreenManager.STATE.LOGGING) {
                    app.gsm.setScreen(GameScreenManager.STATE.LOGGING);
                } else if (app.gsm.getCurrentState() == GameScreenManager.STATE.LOGGING) {
                    app.gsm.setScreen(GameScreenManager.STATE.PLAY);
                }
            }
        });

        workerButton = new TextButton("Workers", skin);
        workerButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {

                if (app.gsm.getCurrentState() != GameScreenManager.STATE.WORKERS) {
                    app.gsm.setScreen(GameScreenManager.STATE.WORKERS);
                } else if (app.gsm.getCurrentState() == GameScreenManager.STATE.WORKERS) {
                    app.gsm.setScreen(GameScreenManager.STATE.PLAY);
                }
            }
        });

        shopButton = new TextButton("Shop", skin);
        shopButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (app.gsm.getCurrentState() != GameScreenManager.STATE.SHOP) {
                    app.gsm.setScreen(GameScreenManager.STATE.SHOP);
                } else if (app.gsm.getCurrentState() == GameScreenManager.STATE.SHOP) {
                    app.gsm.setScreen(GameScreenManager.STATE.PLAY);
                }
            }
        });

        buttonTable.right().bottom();
        buttonTable.add(workshopButton).expandX().uniform().width(115).height(55).padBottom(8);
        buttonTable.add(farmButton).expandX().uniform().width(115).height(55).padBottom(8);
        buttonTable.add(workerButton).expandX().uniform().width(115).height(55).padBottom(8);
        buttonTable.add(shopButton).expandX().uniform().width(115).height(55).padBottom(8);
    }

    private void initTopUI() {
        topTable.top().left();

        topTable.add(uiLog).pad(5);
        logLabel = new Label("", skin);
        topTable.add(logLabel).expandX().pad(5).align(Align.left);

        playerLevel = new Label("", skin);
        topTable.add(playerLevel).padTop(5).padLeft(5).padRight(10).padBottom(5).right();

        Table xpTable = new Table();
        playerXP = new Label("", skin);
        xpTable.add(playerXP).padRight(5);
        xpBar = new VisProgressBar(0, 0, 1, false);
        xpBar.getStyle().background.setMinHeight(20);
        xpBar.getStyle().knob.setMinHeight(20);
        xpTable.add(xpBar);
        topTable.add(xpTable).pad(5).right();

        topTable.row();
        topTable.add(uiCoin).size(uiLog.getWidth()).pad(5);
        goldLabel = new Label("", skin);
        topTable.add(goldLabel).pad(5).align(Align.left);
    }

    /**
     * Converts from local to stage coordinates from within a table.
     * @param actor - the actor to return stage coordinates from.
     * @return the parent coordinates of items within a table or other component
     */
    private Vector2 getStageCoordinates(Actor actor) {
        return actor.localToParentCoordinates(new Vector2(topTable.getX(), topTable.getY()));
    }

    /**
     * Draws the bounds of the log and coin ui components.
     * Used for debugging.
     */
    public void drawBounds() {
        app.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        app.shapeRenderer.setColor(Color.RED);
        app.shapeRenderer.rect(logBounds.x, logBounds.y, logBounds.width, logBounds.height);
        app.shapeRenderer.rect(coinBounds.x, coinBounds.y, coinBounds.width, coinBounds.height);
        app.shapeRenderer.end();
    }

    /**
     * Needed to initialize the xp bar each time the user enters a new screen. We can't call getPlayer() within
     * the initTopUI() method so call this in the show method for each screen with a hud.
     */
    public void initXPBar() {
        xpBar.setRange(app.gsm.getPlayer().getPreviousExperience(), app.gsm.getPlayer().getExperienceNeeded());
    }

    public Vector2 getLogPosition() {
        return new Vector2(logBounds.x, logBounds.y);
    }

    public Vector2 getCoinPosition() {
        return new Vector2(coinBounds.x, coinBounds.y);
    }

    public Rectangle getLogBounds() {
        return logBounds;
    }

    public Rectangle getCoinBounds() {
        return coinBounds;
    }

    public Image getUiLog() {
        return uiLog;
    }

    public Image getUiCoin() {
        return uiCoin;
    }
}
