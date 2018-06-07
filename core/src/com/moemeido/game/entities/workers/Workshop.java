package com.moemeido.game.entities.workers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.moemeido.game.Application;
import com.moemeido.game.utils.LogSpawner;

import java.util.Locale;

public class Workshop extends AbstractWorkingEntity  {

    // Texture and animation
    private Animation workshopAnimation;
    private Animation collectAnimation;
    private float stateTime1, stateTime2;

    // VisUI stuff
    private VisWindow upgradeWindow;
    private VisTextButton upgradeDynamicButton, upgradeWindowButton;
    private VisLabel intakeAmountLabel, goldYieldAmountLabel, timeAmountLabel;

    private LogSpawner spawner;

    public Workshop(Application app, Stage stage, Stage dynamicStage, float x, float y) {
        super(app, stage, dynamicStage, x, y);

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);
        tex = atlas.findRegion("new_workshop");
        texScale = 2f;

        workshopAnimation = new Animation<TextureRegion>(.05f, atlas.findRegions("workshop"), Animation.PlayMode.LOOP);
        collectAnimation = new Animation<TextureRegion>(.5f, atlas.findRegions("coin_collect"), Animation.PlayMode.LOOP);
        collectScale = 3f;

        level = 1;
        boostLevel = 5;
        baseBoostLevel = boostLevel;

        upgradeCost = 100;
        baseCost = upgradeCost;

        baseYield = 50;
        currentYield = baseYield;
        baseIntake = 25;
        currentIntake = baseIntake;

        totalWorkTime = 5f;
        currentWorkTime = 0f;
        minWorkTime = 3f;

        width = tex.getRegionWidth() * texScale;
        height = tex.getRegionHeight() * texScale;

        // Sets origin within the center of the image
        this.x -= width / 2;
        this.y += height / 2;
        bounds = new Rectangle(this.x, this.y, width, height);

        center = new Vector2(
                bounds.x + tex.getRegionWidth() * texScale / 2,
                bounds.y + tex.getRegionHeight() * texScale / 2);

        setupUpgradeButton();
        setupUpgradeWindow();

        spawner = new LogSpawner(app, dynamicStage, this);
    }

    public void update(float delta) {
        super.update(delta);

        // Collects logs from the player automatically if they have enough available
        if(!readyToCollect && !working && app.prefs.getInteger("playerLogs") >= currentIntake) {
            app.gsm.getPlayer().setLogCount(app.prefs.getInteger("playerLogs") - currentIntake);
            working = true;
        }

        if(app.gsm.hasVisibleWindow())
            upgradeDynamicButton.setTouchable(Touchable.disabled);
        else
            upgradeDynamicButton.setTouchable(Touchable.enabled);

        // Updates the text of labels and buttons for when they're upgraded
        upgradeWindow.getTitleLabel().setText("Workshop Level " + level);
        intakeAmountLabel.setText(String.valueOf(currentIntake));
        goldYieldAmountLabel.setText(String.valueOf(currentYield));
        timeAmountLabel.setText(String.format(Locale.getDefault(), "%.1f", totalWorkTime) + "s");
        upgradeDynamicButton.setText("Level " + level);
        upgradeWindowButton.setText("Upgrade!\n" + upgradeCost + "g");

        spawner.update(delta);
    }

    public void render(SpriteBatch batch) {
        if (!readyToCollect && working) {
            stateTime1 += Gdx.graphics.getDeltaTime();
            batch.draw((TextureRegion) workshopAnimation.getKeyFrame(stateTime1, true),
                    x, y, width, height);
        } else {
            batch.draw((TextureRegion) workshopAnimation.getKeyFrame(stateTime1, false),
                    x, y, width, height);
        }

        app.fonts.font20.draw(batch, "Intake: " + currentIntake, x, y - 50);
        app.fonts.font20.draw(batch, "Yield: " + currentYield + "g", x, y - 75);

        if (readyToCollect) {
            stateTime2 += Gdx.graphics.getDeltaTime();
            batch.draw((TextureRegion)collectAnimation.getKeyFrame(stateTime2, true),
                    x + width / 2 - ((TextureRegion)collectAnimation.getKeyFrame(stateTime2)).getRegionWidth() * collectScale / 2,
                    y + height + 10f,
                    ((TextureRegion)collectAnimation.getKeyFrame(stateTime2)).getRegionWidth() * collectScale,
                    ((TextureRegion)collectAnimation.getKeyFrame(stateTime2)).getRegionHeight() * collectScale);
        }
    }

    public void drawProgressBar() {
        super.drawProgressBar();
    }

    private void setupUpgradeButton() {
        float buttonWidth = 100f;

        VisTable buttonTable = new VisTable();
        buttonTable.setPosition(x + tex.getRegionWidth() * texScale - buttonWidth / 2, y - 65);
        buttonTable.setDebug(false);

        VisTextButton.VisTextButtonStyle buttonStyle1 = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().getDrawable("button"),
                VisUI.getSkin().getDrawable("button-down"),
                VisUI.getSkin().getDrawable("button"),
                app.fonts.font20);

        upgradeDynamicButton = new VisTextButton("Level " + level, buttonStyle1);
        buttonTable.add(upgradeDynamicButton).width(buttonWidth).height(35);

        dynamicStage.addActor(buttonTable);

        upgradeDynamicButton.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                upgradeDynamicButton.setTouchable(Touchable.enabled);
                upgradeWindow.addAction(Actions.parallel(
                        Actions.show(),
                        Actions.alpha(0),
                        Actions.alpha(1, .25f, Interpolation.pow5),
                        Actions.sequence(
                                Actions.moveBy(0, 75),
                                Actions.moveBy(0, -75, .25f, Interpolation.pow5))
                ));
            }
        });
    }

    private void setupUpgradeWindow() {
        VisTable stageTable = new VisTable();
        stageTable.setFillParent(true);
        stageTable.setDebug(false);

        Window.WindowStyle windowStyle = new Window.WindowStyle(app.fonts.font30, Color.WHITE, VisUI.getSkin().getDrawable("window"));
        upgradeWindow = new VisWindow("Workshop Level " + level, windowStyle);
        upgradeWindow.setFillParent(false);
        upgradeWindow.setDebug(false);
        upgradeWindow.setVisible(false);
        upgradeWindow.setMovable(false);
        upgradeWindow.setTouchable(Touchable.enabled);

        VisTextButton.VisTextButtonStyle buttonStyle1 = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().getDrawable("button"),
                VisUI.getSkin().getDrawable("button-down"),
                VisUI.getSkin().getDrawable("button"),
                app.fonts.font30);

        upgradeWindow.getTitleTable().padTop(20).padLeft(5).padRight(5);

        VisTextButton quitButton = new VisTextButton("Quit", buttonStyle1);
        upgradeWindow.getTitleTable().add(quitButton).right();
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                upgradeWindow.addAction(Actions.sequence(
                        Actions.fadeOut(.25f, Interpolation.pow5),
                        Actions.hide()
                ));
            }
        });

        levelBar = new VisProgressBar(0, boostLevel, 1, false);
        levelBar.setRange(level, boostLevel);
        VisTable windowTable = new VisTable();
        windowTable.setDebug(false);
        windowTable.top();
        windowTable.add(levelBar).expandX().fill().pad(15).colspan(2).padTop(30);

        Label.LabelStyle labelStyle1 = new Label.LabelStyle(app.fonts.font20, Color.WHITE);
        VisLabel intakeLabel = new VisLabel("Intake: ", labelStyle1);
        VisLabel goldYieldLabel = new VisLabel("Gold Yield: ", labelStyle1);
        VisLabel timeLabel = new VisLabel("Work Time: ", labelStyle1);

        intakeAmountLabel = new VisLabel(String.valueOf(baseIntake), labelStyle1);
        goldYieldAmountLabel = new VisLabel(String.valueOf(baseYield), labelStyle1);
        timeAmountLabel = new VisLabel(String.format(Locale.getDefault(), "%.1f", totalWorkTime) + "s", labelStyle1);

        windowTable.row();
        windowTable.add(intakeLabel).pad(15).left();
        windowTable.add(intakeAmountLabel).pad(15).right();
        windowTable.row();
        windowTable.addSeparator().colspan(2).width(410);
        windowTable.row();
        windowTable.add(goldYieldLabel).pad(15).left();
        windowTable.add(goldYieldAmountLabel).pad(15).right();
        windowTable.row();
        windowTable.addSeparator().colspan(2).width(410);
        windowTable.row();
        windowTable.add(timeLabel).pad(15).left();
        windowTable.add(timeAmountLabel).pad(15).right();
        windowTable.row();
        windowTable.addSeparator().colspan(2).width(410);
        windowTable.row();

        upgradeWindowButton = new VisTextButton("Upgrade!\n" + upgradeCost + "g", buttonStyle1);

        upgradeWindowButton.addListener(new ClickListener(){
            @Override
            public void clicked (InputEvent event, float x, float y) {
                if (app.prefs.getInteger("playerGold") >= upgradeCost) {
                    app.gsm.getPlayer().setGoldCount(app.prefs.getInteger("playerGold") - upgradeCost);
                    upgrade();
                } else {
                    System.out.println("Not enough gold!");
                    dialog.displayDialogWindow(stage);
                }
            }
        });

        windowTable.add(upgradeWindowButton).fillX().expandY().colspan(2).spaceTop(100).pad(5).bottom();

        upgradeWindow.add(windowTable).width(450);
        stageTable.add(upgradeWindow).center().padBottom(125);
        stage.addActor(stageTable);
    }

    public void upgrade() {
        super.upgrade();
    }

    public boolean checkTouch(Vector3 touch) {
        if (bounds.contains(touch.x, touch.y) && readyToCollect && !app.gsm.hasVisibleWindow()) {
            collectYields();
            stateTime2 = 0f;
            return true;
        }

        return false;
    }

    private void collectYields() {
        app.gsm.getPlayer().setGoldCount(app.prefs.getInteger("playerGold") + currentYield);
        readyToCollect = false;
    }

    public VisWindow getUpgradeWindow() {
        return upgradeWindow;
    }

}
