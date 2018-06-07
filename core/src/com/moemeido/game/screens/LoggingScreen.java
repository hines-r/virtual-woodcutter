package com.moemeido.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.moemeido.game.Application;
import com.moemeido.game.entities.actors.ActorLogLogging;
import com.moemeido.game.entities.workers.LoggingTree;
import com.moemeido.game.entities.workers.Plot;
import com.moemeido.game.managers.MyGestureListener;
import com.moemeido.game.managers.MyInputProcessor;
import com.moemeido.game.screens.huds.HUD;

public class LoggingScreen extends AbstractScreen {

    private Vector3 touch;

    private Stage dynamicStage;

    private HUD hud;
    private TextureRegion bg1, bg2;

    private Array<LoggingTree> trees;

    private Array<ActorLogLogging> logs;

    private Plot plot;

    private Array<Boolean> visibleWindows;

    private GestureDetector gestureDetector;
    private MyGestureListener myGestureListener;

    public LoggingScreen(Application app) {
        super(app);

        touch = new Vector3();
        dynamicStage = new Stage(new FitViewport(Application.APP_WIDTH, Application.APP_HEIGHT));
        hud = new HUD(app, stage, dynamicStage);

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);
        bg1 = atlas.findRegion("grass_bg2");
        bg2 = atlas.findRegion("grass_bg2");

        trees = new Array<LoggingTree>();

        plot = new Plot(app, this, stage, dynamicStage, viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 4f, Plot.PlotType.LOGGING);

        // Checks to see if there was any previous trees within the trees array and adds them back
        if(app.prefs.getInteger("loggingTreeCount") > 0)
            initiateTrees();
        else
            addLoggingTree();
    }

    @Override
    public void show() {
        myGestureListener = new MyGestureListener(app, camera, dynamicStage){
            @Override
            public boolean tap(float x, float y, int count, int button) {
                touch.x = Gdx.input.getX();
                touch.y = Gdx.input.getY();
                camera.unproject(touch);

                if(plot.checkTouch(touch))
                    addLoggingTree();

                for (LoggingTree tree : trees) {
                    if(tree.checkTouch(touch))
                        logExplosion(tree);
                }

                return false;
            }
        };
        gestureDetector = new GestureDetector(myGestureListener);
        gestureDetector.setLongPressSeconds(.2f);
        gestureDetector.setTapSquareSize(30f);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(dynamicStage);
        multiplexer.addProcessor(gestureDetector);
        multiplexer.addProcessor(new MyInputProcessor(app));
        Gdx.input.setInputProcessor(multiplexer);

        hud.initXPBar();
        logs = new Array<ActorLogLogging>();
    }

    public void addLoggingTree(){
        trees.add(new LoggingTree(app, stage, dynamicStage, plot.getCenter().x, plot.getPosition().y));
        plot.movePosition();
        plot.incrementCost();
    }

    private void handleGestures(float delta) {
        if(!containsVisibleWindow()) {
            myGestureListener.flingDecelerate(delta);
            camera.position.y += myGestureListener.getFlingVelocityY() * delta;
            dynamicStage.getCamera().position.y += myGestureListener.getFlingVelocityY() * delta;
            myGestureListener.keepCameraInBounds();
        }
    }

    @Override
    public void update(float delta) {
        handleGestures(delta);
        camera.update();

        app.batch.setProjectionMatrix(camera.combined);
        app.shapeRenderer.setProjectionMatrix(camera.combined);

        stage.act(delta);
        dynamicStage.act(delta);
        hud.update();
        app.gsm.globalUpdate(delta);

        for(ActorLogLogging log : logs) {
            log.update(delta);

            if (log.isReadyToDestroy()) {
                logs.removeValue(log, true);
                log.getImage().remove();
            }
        }

        plot.update();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        app.batch.begin();
        app.batch.draw(bg1, 0, 0, Application.V_WIDTH, Application.V_HEIGHT);
        app.batch.draw(bg2, 0, Application.V_HEIGHT, Application.V_WIDTH, Application.V_HEIGHT);
        app.batch.draw(bg2, 0, Application.V_HEIGHT * 2, Application.V_WIDTH, Application.V_HEIGHT);

        for (LoggingTree tree : trees)
            tree.render(app.batch);

        app.batch.end();

        app.shapeRenderer.setAutoShapeType(true);
        app.shapeRenderer.begin();

        if(trees.size != 0) {
            for (int i = 0; i < trees.size; i++) {
                LoggingTree t = trees.get(i);
                t.drawProgressBar();
            }
        }

        app.shapeRenderer.end();
        plot.drawBounds();
        dynamicStage.draw();
        stage.draw();
    }

    private void logExplosion(LoggingTree loggingTree) {
        int rollAmount = MathUtils.random(10, 30);

        for(int i = 0 ; i < rollAmount; i++) {
            final ActorLogLogging log = new ActorLogLogging(app, dynamicStage, loggingTree.getCenter(), hud);
            logs.add(log);

            float moveToX = log.getPosition().x - loggingTree.getCenter().x;
            float moveToY = log.getPosition().y - loggingTree.getCenter().y;

            log.getImage().addAction(Actions.sequence(
                    Actions.moveBy(moveToX * MathUtils.random(2f, 6.5f), moveToY * MathUtils.random(2f, 6.5f), .45f, Interpolation.exp5Out),
                    Actions.delay(MathUtils.random(.1f, .5f)),
                    Actions.addAction(new Action() {
                        @Override
                        public boolean act(float delta) {
                            log.setReadyToMove(true);
                            return true;
                        }
                    }),
                    Actions.sizeTo(hud.getUiLog().getWidth(), hud.getUiLog().getHeight(), .75f, Interpolation.exp5Out)
            ));
        }
    }

    public void updateLoggingTrees(float delta) {
        for (LoggingTree tree : trees) {
            tree.update(delta);
        }
    }

    @Override
    public void pause() {
        app.prefs.putInteger("loggingTreeCount", trees.size).flush();

        for (int i = 0; i < trees.size; i++) {
            LoggingTree t = trees.get(i);
            app.prefs.putInteger("loggingTreeLevel" + i, t.getLevel()).flush();
        }
    }

    private void initiateTrees() {
        int treeCount = app.prefs.getInteger("loggingTreeCount");

        for (int i = 0; i < treeCount; i++) {
            addLoggingTree();
            for (int j = 1; j < app.prefs.getInteger("loggingTreeLevel" + i); j++) {
                trees.get(i).upgrade();
            }
        }
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        for (ActorLogLogging log : logs) {
            log.getImage().remove();
        }

        for(int i = 0; i < trees.size; i++) {
            LoggingTree tree = trees.get(i);
            tree.getUpgradeWindow().setVisible(false);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        dynamicStage.dispose();
    }

    public Array<LoggingTree> getTrees() {
        return trees;
    }

    public Boolean containsVisibleWindow() {
        for(int i = 0; i < trees.size; i++) {
            LoggingTree tree = trees.get(i);
            if(tree.getUpgradeWindow().isVisible())
                return true;
        }

        return false;
    }
}
