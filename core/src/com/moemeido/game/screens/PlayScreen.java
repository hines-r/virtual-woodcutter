package com.moemeido.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.moemeido.game.Application;
import com.moemeido.game.background.PlayBackground;
import com.moemeido.game.entities.Log;
import com.moemeido.game.entities.Player;
import com.moemeido.game.entities.PowerUp;
import com.moemeido.game.entities.Tree;
import com.moemeido.game.managers.MyInputProcessor;
import com.moemeido.game.screens.huds.HUD;
import com.moemeido.game.utils.PowerUpSpawner;

import java.util.Locale;

import static com.moemeido.game.utils.B2DVars.*;

public class PlayScreen extends AbstractScreen {

    private World world;
    private Box2DDebugRenderer b2dr;
    private Vector3 touch;
    private MyInputProcessor inputProcessor;

    private HUD hud;

    private Tree tree;
    private Array<Log> logs; // Holds all active logs on the screen
    private Pool<Log> logPool; // Keeps a pool of logs to be used again

    private Array<PowerUp> powerUps; // Holds active power-ups for updating/rendering
    private PowerUpSpawner spawner;

    private Array<Body> bodies; // Holds all bodies added to the world

    private Player player;

    private PlayBackground bg;

    private Body platform;

    private Table powerTable;
    private Label autoTimeLabel, mightTimeLabel, doubleTimeLabel;

    private Table debugTable;
    private Label nextPowerLabel, xpRequiredLabel;
    private Label treeHitsLabel, treeLifeLabel, logsDropLabel;

    public PlayScreen(final Application app) {
        super(app);
        world = new World(new Vector2(0, -9.81f), true);
        b2dr = new Box2DDebugRenderer();
        touch = new Vector3();
        inputProcessor = new MyInputProcessor(app);
        tree = new Tree(app, Tree.Type.OAK);
        player = new Player(app, 200, 535);

        bg = new PlayBackground(app, this);
        hud = new HUD(app, stage);
        powerUps = new Array<PowerUp>();
        spawner = new PowerUpSpawner(app, stage);

        powerTable = new Table();
        powerTable.setFillParent(true);
        powerTable.setDebug(false);

        Label.LabelStyle labelStyle = new Label.LabelStyle(app.fonts.font30, Color.WHITE);
        autoTimeLabel = new Label("", labelStyle);
        mightTimeLabel = new Label("", labelStyle);
        doubleTimeLabel = new Label("", labelStyle);

        autoTimeLabel.setAlignment(Align.center);
        mightTimeLabel.setAlignment(Align.center);
        doubleTimeLabel.setAlignment(Align.center);

        powerTable.bottom().padBottom(250);
        powerTable.add(autoTimeLabel).prefHeight(0);
        powerTable.row();
        powerTable.add(mightTimeLabel).prefHeight(0);
        powerTable.row();
        powerTable.add(doubleTimeLabel).prefHeight(0);

        stage.addActor(powerTable);

        initDebugTable();
    }

    @Override
    public void show() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(inputProcessor);
        Gdx.input.setInputProcessor(multiplexer);

        bodies = new Array<Body>();

        logs = new Array<Log>();

        logPool = new Pool<Log>(0, 150) {
            @Override
            protected Log newObject() {
                return new Log(app, world, tree);
            }
        };

        createPlatform();

        hud.initXPBar();

        app.batch.setProjectionMatrix(camera.combined);
        app.shapeRenderer.setProjectionMatrix(camera.combined);

        debugTable.addAction(Actions.alpha(0));
        debugTable.addAction(Actions.fadeIn(.5f));
    }

    private void handleInput() {
        touch.x = Gdx.input.getX();
        touch.y = Gdx.input.getY();
        camera.unproject(touch);

        // Checks if a power-up is touched
        // If touched, remove from active array and add to consumed array
        if (Gdx.input.justTouched()) {
            for (PowerUp p : powerUps) {
                if(p.checkTouch(touch, player))
                    return;
            }
        }

        // Checks if the player has the chainsaw first
        if (player.isAutoChopping() && inputProcessor.isDown() && !player.isMoving()) {

            player.setChopping(true);

            if(player.isReadyToSwing()) {
                player.setReadyToSwing(false);
                player.setSwingTimer(0);
                tree.setHitsToBreak(tree.getHitsToBreak() - player.getStrength());
            }

            checkTree();
        }

        // Wont return true on isDown if a scene2d widget is pressed (fixes next screen touch bug)
        else if (Gdx.input.justTouched() && inputProcessor.isDown() && !player.isMoving()) {
            player.setChopping(true);
            tree.setHitsToBreak(tree.getHitsToBreak() - player.getCurrentStrength());

            checkTree();
        }
    }

    @Override
    public void update(float delta) {
        world.step(1f / Application.APP_FPS, 6, 2);
        stage.act();
        updateDebugTable();
        spawner.update(delta, powerUps);
        tree.update();
        player.update(delta, tree);
        hud.update();
        bg.update(delta, player, tree, logs);

        // Updates active logs and destroys them if they can't fit in the pool
        for (Log log : logs) {
            log.update(delta);

            if (log.isReadyToDestroy()) {
                if (logPool.getFree() < logPool.max) {
                    logs.removeValue(log, true);
                    logPool.free(log);
                } else {
                    logs.removeValue(log, true);
                    world.destroyBody(log.getBody());
                    bodies.removeValue(log.getBody(), true);
                }
            }
        }

        // Updates and removes power-ups past the screen
        for (PowerUp p : powerUps) {
            p.update(delta);

            if (p.isReadyToDestroy())
                powerUps.removeValue(p, true);
        }

        handleInput();

        app.gsm.globalUpdate(delta);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        app.batch.begin();

        bg.render(app.batch);

        tree.render(app.batch);

        for (Log log : logs)
            log.render(app.batch);

        player.render(app.batch);

        if (player.getPowers().containsKey(PowerUp.POWER.FULL_AUTO)) {
            autoTimeLabel.setText("FULL AUTO " + String.format(Locale.getDefault(),"%.0f", Math.abs(player.getPowers().get(PowerUp.POWER.FULL_AUTO).getActiveDuration())));
        } else {
            autoTimeLabel.setText("");
        }

        if (player.getPowers().containsKey(PowerUp.POWER.MIGHT)) {
            mightTimeLabel.setText("MIGHT " + String.format(Locale.getDefault(),"%.0f", Math.abs(player.getPowers().get(PowerUp.POWER.MIGHT).getActiveDuration())));
        } else {
            mightTimeLabel.setText("");
        }

        if (player.getPowers().containsKey(PowerUp.POWER.DOUBLE_LOG)) {
            doubleTimeLabel.setText("DOUBLE LOGS " + String.format(Locale.getDefault(),"%.0f", Math.abs(player.getPowers().get(PowerUp.POWER.DOUBLE_LOG).getActiveDuration())));
        } else {
            doubleTimeLabel.setText("");
        }

        for (PowerUp p : powerUps)
            p.render(app.batch);

        app.batch.end();

//        for (PowerUp p : powerUps)
//            p.renderBounds();

        //b2dr.render(world, camera.combined.cpy().scl(PPM));
        stage.draw();
    }

    private void initDebugTable() {
        debugTable = new Table();
        debugTable.setFillParent(true);
        debugTable.setDebug(false);

        Label.LabelStyle labelStyle1 = new Label.LabelStyle(app.fonts.font20, Color.WHITE);
        nextPowerLabel = new Label("Next power-up: " + spawner.getTimeToSpawn(), labelStyle1);
        xpRequiredLabel = new Label("Next level: " + app.prefs.getInteger("playerXpNeeded") + "xp", labelStyle1);
        logsDropLabel = new Label("Logs to drop: " + tree.getLogsToDrop(), labelStyle1);
        treeLifeLabel = new Label("Tree life: " + tree.getCurrentLife(), labelStyle1);
        treeHitsLabel = new Label("Hits to break: " + tree.getHitsToBreak(), labelStyle1);

        Label timePassedSeconds = new Label(String.valueOf("Time passed in seconds: " + app.prefs.getLong("timePassed")), labelStyle1);
        Label timePassedMinutes = new Label(String.valueOf("Time passed in minutes: " + (app.prefs.getLong("timePassed")) / 60), labelStyle1);

        debugTable.bottom();
        debugTable.add(timePassedSeconds).padLeft(18f).padBottom(5f).align(Align.left).expandX();
        debugTable.row();
        debugTable.add(timePassedMinutes).padLeft(18f).padBottom(5f).align(Align.left).expandX();
        debugTable.row();
        debugTable.add(nextPowerLabel).padLeft(18f).padBottom(5f).align(Align.left).expandX();
        debugTable.row();
        debugTable.add(xpRequiredLabel).padLeft(18f).padBottom(5f).align(Align.left).expandX();
        debugTable.row();
        debugTable.add(logsDropLabel).padLeft(18f).padBottom(5f).align(Align.left).expandX();
        debugTable.row();
        debugTable.add(treeLifeLabel).padLeft(18f).padBottom(5f).align(Align.left).expandX();
        debugTable.row();
        debugTable.add(treeHitsLabel).padLeft(18f).padBottom(5f).align(Align.left).expandX();

        debugTable.padBottom(70);

        stage.addActor(debugTable);
    }

    private void updateDebugTable() {
        nextPowerLabel.setText("Next power-up: " + spawner.getTimeToSpawn());
        xpRequiredLabel.setText("Next level: " + player.getExperienceNeeded() + "xp");
        logsDropLabel.setText("Logs to drop: " + tree.getLogsToDrop());
        treeLifeLabel.setText("Tree life: " + tree.getCurrentLife());
        treeHitsLabel.setText("Hits to break: " + tree.getHitsToBreak());
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        // Destroy any bodies within the world
        world.getBodies(bodies);
        for (Body body : bodies) {
            if (!world.isLocked())
                world.destroyBody(body);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Checks if the tree is ready to drop loot.
     */
    private void checkTree() {
        if (tree.getHitsToBreak() <= 0) {
            tree.dropLogs(logPool, logs, tree.getLogsToDrop(), platform.getPosition());

            for (Log log : logs) {
                player.receiveLogs(log.getLogYield());
            }

            player.setLogCount(app.prefs.getInteger("playerLogs") + tree.getLogsToDrop());
            tree.setCurrentLife(tree.getCurrentLife() - 1);
            tree.resetDrops();
        }
    }

    /**
     * Creates a platform that the logs fall on
     */
    private void createPlatform() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(Application.V_WIDTH / 2 / PPM, 525 / PPM);
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.fixedRotation = true;
        platform = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Application.V_WIDTH / 2 / PPM, 50 / 2  / PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.filter.categoryBits = GROUND_BIT;
        fdef.filter.maskBits = LOG_BIT | POWER_BIT;
        platform.createFixture(fdef);
        shape.dispose();
    }

    public Player getPlayer() {
        return player;
    }

}
