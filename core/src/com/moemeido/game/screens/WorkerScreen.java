package com.moemeido.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.moemeido.game.Application;
import com.moemeido.game.entities.CollectionBox;
import com.moemeido.game.entities.Worker;
import com.moemeido.game.entities.WorkerTree;
import com.moemeido.game.managers.MyInputProcessor;
import com.moemeido.game.utils.UITools;

import java.util.Locale;

public class WorkerScreen extends AbstractScreen {

    private TextureAtlas atlas;
    private HUD hud;
    private Stage dynamicStage;

    private TextureRegion bg;
    private Array<Worker> workers;
    private Array<WorkerTree> trees;

    private CollectionBox collectionBox;

    private VisWindow upgradeWindow, dialogWindow;
    private VisLabel levelLabel, logAmountLabel, timeAmountLabel, speedAmountLabel;
    private VisProgressBar levelBar;
    private VisTextButton windowUpgradeButton;

    private VisTable gridTable;
    private Array<Image> unlockGrid;

    private UITools uiTools;

    public WorkerScreen(Application app) {
        super(app);
        hud = new HUD(app, stage);
        dynamicStage = new Stage(new FitViewport(Application.V_WIDTH, Application.V_HEIGHT));

        atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);
        bg = atlas.findRegion("grass_bg2");

        collectionBox = new CollectionBox(app, 25, Application.V_HEIGHT / 2);

        workers = new Array<Worker>();

        trees = new Array<WorkerTree>();
        trees.add(new WorkerTree(app, 300, 800));
        trees.add(new WorkerTree(app, 415, 675));
        trees.add(new WorkerTree(app, 390, 500));
        trees.add(new WorkerTree(app, 425, 320));
        trees.add(new WorkerTree(app, 325, 190));

        unlockGrid = new Array<Image>();
        setupUpgradeTable();

        // Checks to see if there was previously workers from preferences first
        // if not, add a single worker to the array
        if(app.prefs.getInteger("workersSize") > 1)
            initiateWorkers();
        else
            addWorker();

        uiTools = new UITools(app, stage);
    }

    @Override
    public void show() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new MyInputProcessor(app));
        Gdx.input.setInputProcessor(multiplexer);

        hud.initXPBar();
    }

    @Override
    public void update(float delta) {
        stage.act(delta);
        hud.update();
        app.gsm.globalUpdate(delta);

        app.batch.setProjectionMatrix(camera.combined);
        app.shapeRenderer.setProjectionMatrix(camera.combined);

        for (int i = 0; i < workers.size; i++) {
            Worker worker = workers.get(i);
            if (worker.isReturnComplete()) {
                int roll = MathUtils.random(0, trees.size - 1);
                Vector2 target = trees.get(roll).getBounds();
                worker.setNewTarget(target);
            }
        }

        if(workers.size > 0)
            updateUpgradeTable();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        app.batch.begin();
        app.batch.draw(bg, 0, 0, Application.V_WIDTH, Application.V_HEIGHT);

        for (int i = 0; i < trees.size; i++) {
            WorkerTree tree = trees.get(i);
            tree.render(app.batch);
        }

        collectionBox.render(app.batch);

        for (int i = 0; i < workers.size; i++) {
            Worker worker = workers.get(i);
            worker.render(app.batch);
        }

        app.batch.end();

        app.shapeRenderer.setAutoShapeType(true);
        app.shapeRenderer.begin();
        for (int i = 0; i < workers.size; i++) {
            Worker worker = workers.get(i);
            //worker.drawBounds();
            worker.drawGatherProgress();
        }
        //collectionBox.drawBounds();
        app.shapeRenderer.end();

        dynamicStage.draw();
        stage.draw();
    }

    private void setupUpgradeTable() {
        VisTable stageButtonTable = new VisTable();
        stageButtonTable.setFillParent(true);
        stageButtonTable.setDebug(false);

        VisTextButton.VisTextButtonStyle buttonStyle1 = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().getDrawable("button"),
                VisUI.getSkin().getDrawable("button-down"),
                VisUI.getSkin().getDrawable("button"),
                app.fonts.font30);

        final VisTextButton upgradeButton = new VisTextButton("Upgrades", buttonStyle1);

        stageButtonTable.bottom();
        stageButtonTable.add(upgradeButton).padBottom(100).width(125).height(50);

        //////////////////////////

        VisTable stageWindowTable = new VisTable();
        stageWindowTable.setFillParent(true);
        stageButtonTable.setDebug(false);

        Window.WindowStyle windowStyle1 = new Window.WindowStyle(app.fonts.font30, Color.WHITE, VisUI.getSkin().getDrawable("window"));
        upgradeWindow = new VisWindow("Worker Upgrades", windowStyle1);
        upgradeWindow.setMovable(false);
        upgradeWindow.setVisible(false);

        upgradeWindow.getTitleTable().padTop(20).padLeft(5).padRight(5);

        VisTextButton quitButton = new VisTextButton("Quit", buttonStyle1);
        upgradeWindow.getTitleTable().add(quitButton).right();

        VisTable windowTable = new VisTable();
        windowTable.setDebug(false);
        upgradeWindow.add(windowTable).width(450);

        stageWindowTable.center();
        stageWindowTable.add(upgradeWindow).padBottom(125);

        Label.LabelStyle labelStyle1 = new Label.LabelStyle(app.fonts.font20, Color.WHITE);
        levelLabel = new VisLabel("Level " + obtainWorkerLevel(), labelStyle1);

        windowTable.top();
        windowTable.add(levelLabel).left().pad(10).padTop(25);

        windowTable.row();

        levelBar = new VisProgressBar(1, 25, 1, false);
        windowTable.add(levelBar).expandX().fillX().pad(10).colspan(2);

        windowTable.row();

        VisLabel logLabel = new VisLabel("Log Amount:", labelStyle1);
        VisLabel timeLabel = new VisLabel("Gather Speed:", labelStyle1);
        VisLabel speedLabel = new VisLabel("Movement Speed:", labelStyle1);

        logAmountLabel = new VisLabel("0", labelStyle1);
        timeAmountLabel = new VisLabel("0s", labelStyle1);
        speedAmountLabel = new VisLabel("0", labelStyle1);

        windowTable.add(logLabel).expandX().pad(15).left();
        windowTable.add(logAmountLabel).expandX().pad(15).right();
        windowTable.row();
        windowTable.addSeparator().colspan(2).width(410);
        windowTable.row();

        windowTable.add(timeLabel).expandX().pad(15).left();
        windowTable.add(timeAmountLabel).expandX().pad(15).right();
        windowTable.row();
        windowTable.addSeparator().colspan(2).width(410);
        windowTable.row();

        windowTable.add(speedLabel).expandX().pad(15).left();
        windowTable.add(speedAmountLabel).expandX().pad(15).right();
        windowTable.row();
        windowTable.addSeparator().colspan(2).width(410).padBottom(10);
        windowTable.row();

        createWorkerGrid(windowTable, 2, 5);

        windowTable.row();

        windowUpgradeButton = new VisTextButton("Upgrade\n0g", buttonStyle1);
        windowTable.add(windowUpgradeButton).fillX().expandY().colspan(2).pad(5).bottom();

        //////////////////////////

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                upgradeWindow.addAction(Actions.sequence(
                        Actions.fadeOut(.25f, Interpolation.pow5),
                        Actions.hide()
                ));
            }
        });

        upgradeButton.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                if (!upgradeWindow.isVisible()) {
                    upgradeWindow.addAction(Actions.parallel(
                            Actions.show(),
                            Actions.alpha(0),
                            Actions.alpha(1, .25f, Interpolation.pow5),
                            Actions.sequence(
                                    Actions.moveBy(0, 75),
                                    Actions.moveBy(0, -75, .25f, Interpolation.pow5))
                    ));
                }
                else {
                    upgradeWindow.addAction(Actions.sequence(
                            Actions.fadeOut(.25f, Interpolation.pow5),
                            Actions.hide()
                    ));
                }
            }
        });

        windowUpgradeButton.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                if (workers.size == unlockGrid.size) {
                    if (app.prefs.getInteger("playerGold") >= workers.first().getUpgradeCost()) {
                        app.gsm.getPlayer().setGoldCount(app.prefs.getInteger("playerGold") - workers.first().getUpgradeCost());
                        upgradeWorkers();
                    } else {
                        uiTools.displayDialogWindow(stage);
                    }
                } else {
                    if (app.prefs.getInteger("playerGold") >= workers.first().getPurchaseCost()) {
                        app.gsm.getPlayer().setGoldCount(app.prefs.getInteger("playerGold") - workers.first().getPurchaseCost());
                        addWorker();
                    } else {
                        uiTools.displayDialogWindow(stage);
                    }
                }
            }
        });

        stage.addActor(stageButtonTable);
        stage.addActor(stageWindowTable);
    }

    private void createWorkerGrid(VisTable table, int rows, int columns) {
        gridTable = new VisTable();
        gridTable.setDebug(false);

        for(int r = 0; r < rows; r++){
            for (int c = 0; c < columns; c++) {
                Image lockedImg = new Image(atlas.findRegion("padlock"));
                unlockGrid.add(lockedImg);
                lockedImg.setScaling(Scaling.fit);
                gridTable.add(lockedImg).pad(10).size(50);
            }
            gridTable.row();
        }
        table.add(gridTable).colspan(2);
        table.row();
    }

    private void addWorker() {
        if (workers.size != unlockGrid.size) {
            Image workerImg = new Image(atlas.findRegion("smol_worker"));
            workerImg.setScaling(Scaling.fit);

            unlockGrid.get(workers.size).setDrawable(workerImg.getDrawable());

            Worker newWorker = new Worker(app, dynamicStage, collectionBox.getBounds());
            workers.add(newWorker);
            obtainRandomTarget();

            for (int i = 0; i < workers.size; i++) {
                workers.get(i).incrementPurchaseCost();
            }
        } else {
            System.out.println("You have the maximum amount of workers!");
        }
    }

    private void obtainRandomTarget() {
        for (int i = 0; i < workers.size; i++) {
            Worker worker = workers.get(i);
            int roll = MathUtils.random(0, trees.size - 1);
            Vector2 target = trees.get(roll).getBounds();
            worker.setNewTarget(target);
        }
    }

    private void updateUpgradeTable(){
        levelLabel.setText("Level " + obtainWorkerLevel());
        logAmountLabel.setText(String.valueOf(workers.first().getLogAmount()));
        timeAmountLabel.setText(String.format(Locale.getDefault(), "%.1f", workers.first().getTotalGatherTime()) + "s");
        speedAmountLabel.setText(String.format(Locale.getDefault(), "%.1f", workers.first().getMovementSpeed()));

        // Changes buy button to an upgrade button once all workers have been unlocked
        if (workers.size == unlockGrid.size) {
            windowUpgradeButton.setText("Upgrade\n" + String.valueOf(workers.first().getUpgradeCost()) + "g");
            levelBar.setValue(workers.first().getLevel());

        } else {
            windowUpgradeButton.setText("Buy Worker\n" + String.valueOf(workers.first().getPurchaseCost()) + "g");
        }
    }

    private int obtainWorkerLevel(){
        if (workers.size > 0)
            return workers.get(0).getLevel();

        return 0;
    }

    private void upgradeWorkers(){
        if (workers.size > 0) {
            for (int i = 0; i < workers.size; i++) {
                Worker worker = workers.get(i);
                worker.updateLevel(levelBar);
                worker.upgrade();
            }
        }
    }

    public void updateWorkers(float delta) {
        if (workers.size > 0) {
            for (int i = 0; i < workers.size; i++) {
                Worker worker = workers.get(i);
                worker.update(delta);
            }
        }

        dynamicStage.act(delta);
    }

    @Override
    public void pause() {
        app.prefs.putInteger("workersSize", workers.size).flush();
        app.prefs.putInteger("workersLevel", workers.first().getLevel()).flush();
    }

    private void initiateWorkers(){
        int arraySize = app.prefs.getInteger("workersSize");
        int arrayLevel = app.prefs.getInteger("workersLevel");

        for (int i = 0; i < arraySize; i++) {
            addWorker();
            for (int j = 1; j < arrayLevel; j++) {
                workers.get(i).updateLevel(levelBar);
                workers.get(i).upgrade();
            }
        }
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        upgradeWindow.setVisible(false);
    }

    @Override
    public void dispose() {
        super.dispose();
        dynamicStage.dispose();
    }
}
