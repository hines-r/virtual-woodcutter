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
import com.moemeido.game.entities.actors.ActorCoin;
import com.moemeido.game.entities.workers.Plot;
import com.moemeido.game.entities.workers.Workshop;
import com.moemeido.game.managers.MyGestureListener;
import com.moemeido.game.managers.MyInputProcessor;

public class WorkshopScreen extends AbstractScreen {

    private Vector3 touch;

    private HUD hud;
    private TextureRegion bg1;

    private Array<Workshop> workshops;
    private Array<ActorCoin> coins;

    private Stage dynamicStage;
    private Plot plot;

    private MyGestureListener myGestureListener;

    public WorkshopScreen(Application app) {
        super(app);

        dynamicStage = new Stage(new FitViewport(Application.APP_WIDTH, Application.APP_HEIGHT));

        touch = new Vector3();

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);
        bg1 = atlas.findRegion("grass_bg2");

        hud = new HUD(app, stage, dynamicStage);

        workshops = new Array<Workshop>();

        plot = new Plot(app,this, stage, dynamicStage, viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 4f, Plot.PlotType.WORKSHOP);

        // Checks to see if the workshops array previously had any purchased workshops
        if(app.prefs.getInteger("workshopCount") > 0)
            initiateWorkshops();
        else
            addWorkshop();
    }

    @Override
    public void show() {
        // places camera on default position upon entering workshop screen
        camera.position.set(Application.V_WIDTH / 2, Application.V_HEIGHT / 2, 0);
        camera.update();

        dynamicStage.getCamera().position.set(Application.V_WIDTH / 2, Application.V_HEIGHT / 2, 0);
        dynamicStage.getCamera().update();

        myGestureListener = new MyGestureListener(app, camera, dynamicStage, 2){
            @Override
            public boolean tap(float x, float y, int count, int button) {
                touch.x = Gdx.input.getX();
                touch.y = Gdx.input.getY();
                camera.unproject(touch);

                if (plot.checkTouch(touch))
                    addWorkshop();

                for (Workshop workshop : workshops) {
                    if(workshop.checkTouch(touch))
                        coinExplosion(workshop);
                }

                return true;
            }
        };
        GestureDetector gestureDetector = new GestureDetector(myGestureListener);
        gestureDetector.setLongPressSeconds(.2f);
        gestureDetector.setTapSquareSize(30f);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(dynamicStage);
        multiplexer.addProcessor(gestureDetector);
        multiplexer.addProcessor(new MyInputProcessor(app));
        Gdx.input.setInputProcessor(multiplexer);

        hud.initXPBar();
        coins = new Array<ActorCoin>();
    }

    public void addWorkshop() {
        workshops.add(new Workshop(app, stage, dynamicStage, plot.getCenter().x, plot.getPosition().y));
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

    private void coinExplosion(Workshop workshop) {
        int rollAmount = MathUtils.random(10, 30);

        for(int i = 0 ; i < rollAmount; i++) {
            final ActorCoin coin = new ActorCoin(app, dynamicStage, hud.getCoinPosition(), workshop.getCenter(), hud);
            coins.add(coin);

            float moveToX = coin.getPosition().x - workshop.getCenter().x;
            float moveToY = coin.getPosition().y - workshop.getCenter().y;

            coin.getImage().addAction(Actions.sequence(
                    Actions.moveBy(moveToX * MathUtils.random(2f, 6.5f), moveToY * MathUtils.random(2f, 6.5f), .45f, Interpolation.exp5Out),
                    Actions.delay(MathUtils.random(.1f, .5f)),
                    Actions.addAction(new Action() {
                        @Override
                        public boolean act(float delta) {
                            coin.setReadyToMove(true);
                            return true;
                        }
                    })
            ));
        }
    }

    @Override
    public void update(float delta) {
        stage.act(delta);
        handleGestures(delta);
        camera.update();

        app.batch.setProjectionMatrix(camera.combined);
        app.shapeRenderer.setProjectionMatrix(camera.combined);

        hud.update();
        app.gsm.globalUpdate(delta);

        // Only updates the moving log animation spawner if the workshop isn't ready to collect
        for (ActorCoin coin : coins) {
            coin.update(delta);

            if(coin.isReadyToDestroy()) {
                coins.removeValue(coin, true);
                coin.getImage().remove();
            }
        }

        plot.update();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        app.batch.begin();
        app.batch.draw(bg1, 0, 0, Application.V_WIDTH, Application.V_HEIGHT);
        app.batch.draw(bg1, 0, Application.V_HEIGHT, Application.V_WIDTH, Application.V_HEIGHT);
        app.batch.draw(bg1, 0, Application.V_HEIGHT * 2, Application.V_WIDTH, Application.V_HEIGHT);

        for (Workshop w : workshops)
            w.render(app.batch);

        app.batch.end();

        app.shapeRenderer.setAutoShapeType(true);
        app.shapeRenderer.begin();

        for (Workshop w : workshops)
            w.drawProgressBar();

        app.shapeRenderer.end();

        plot.drawBounds();

        dynamicStage.draw();
        stage.draw();
    }

    public void updateWorkshops(float delta) {
        dynamicStage.act(delta);

        for(int i = 0; i < workshops.size; i++) {
            Workshop workshop = workshops.get(i);
            workshop.update(delta);
        }
    }

    @Override
    public void pause() {
        app.prefs.putInteger("workshopCount", workshops.size).flush();

        for (int i = 0; i < workshops.size; i++) {
            Workshop w = workshops.get(i);
            app.prefs.putInteger("workshopLevel" + i, w.getLevel()).flush();
        }
    }

    private void initiateWorkshops() {
        int workshopCount = app.prefs.getInteger("workshopCount");

        for (int i = 0; i < workshopCount; i++) {
            addWorkshop();
            for (int j = 1; j < app.prefs.getInteger("workshopLevel" + i); j++) {
                workshops.get(i).upgrade();
            }
        }
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        for (int i = 0; i < coins.size; i++) {
            ActorCoin coin = coins.get(i);
            coin.getImage().remove();
        }

        for(int i = 0; i < workshops.size; i++) {
            Workshop workshop = workshops.get(i);
            workshop.getUpgradeWindow().setVisible(false);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        dynamicStage.dispose();
    }

    public Array<Workshop> getWorkshops() {
        return workshops;
    }

    public boolean containsVisibleWindow() {
        for(int i = 0; i < workshops.size; i++) {
            Workshop workshop = workshops.get(i);
            if(workshop.getUpgradeWindow().isVisible())
                return true;
        }

        return false;
    }

    public HUD getHUD(){
        return hud;
    }
}
