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

import java.util.Locale;

public class LoggingTree extends AbstractWorkingEntity {

    // Animation
    private Animation collectAnimation;
    private float stateTime;

    // VisUI stuff
    private VisTextButton upgradeStageButton, upgradeWindowButton;
    private VisWindow upgradeWindow;
    private VisLabel yieldAmountLabel, timeAmountLabel;

    public LoggingTree(Application app, Stage stage, Stage dynamicStage, float x, float y) {
        super(app, stage, dynamicStage, x, y);

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);
        tex = atlas.findRegion("tree_bunch");

        collectAnimation = new Animation<TextureRegion>(.5f, atlas.findRegions("log_collect"), Animation.PlayMode.LOOP);
        collectScale = 3f;

        level = 1;
        boostLevel = 5;
        baseBoostLevel = boostLevel;

        upgradeCost = 100;
        baseCost = upgradeCost;

        baseYield = 10;
        currentYield = baseYield;
        texScale = 3.5f;

        totalWorkTime = 5f;
        currentWorkTime = 0f;
        minWorkTime = 3f;

        width = tex.getRegionWidth() * texScale;
        height = tex.getRegionHeight() * texScale;

        // Places origin within the center of the image
        this.x -= width / 2;

        bounds = new Rectangle(this.x, this.y, width, height);

        working = true;

        center = new Vector2(
                bounds.x + tex.getRegionWidth() * texScale / 2,
                bounds.y + tex.getRegionHeight() * texScale / 2);

        setupUpgradeButton();
        setupUpgradeWindow();
    }

    public void update(float delta) {
        super.update(delta);

        if (app.gsm.hasVisibleWindow())
            upgradeStageButton.setTouchable(Touchable.disabled);
        else
            upgradeStageButton.setTouchable(Touchable.enabled);

        upgradeStageButton.setText("Level " + level);
        upgradeWindow.getTitleLabel().setText("Trees Level " + level);
        upgradeWindowButton.setText("Upgrade\n" + upgradeCost + "g");

        yieldAmountLabel.setText(String.valueOf(currentYield));
        timeAmountLabel.setText(String.format(Locale.getDefault(), "%.1f", totalWorkTime) + "s");
    }

    private void setupUpgradeButton() {
        float buttonWidth = 100f;
        float buttonHeight = 35f;

        VisTable buttonTable = new VisTable();
        buttonTable.setPosition(x + tex.getRegionWidth() * texScale - buttonWidth / 2, y - 65);

        VisTextButton.VisTextButtonStyle buttonStyle1 = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().getDrawable("button"),
                VisUI.getSkin().getDrawable("button-down"),
                VisUI.getSkin().getDrawable("button"),
                app.fonts.font20);

        upgradeStageButton = new VisTextButton("Level " + level, buttonStyle1);
        upgradeStageButton.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
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

        buttonTable.add(upgradeStageButton).width(buttonWidth).height(buttonHeight);
        dynamicStage.addActor(buttonTable);
    }

    private void setupUpgradeWindow() {
        Window.WindowStyle windowStyle = new Window.WindowStyle(app.fonts.font30, Color.WHITE, VisUI.getSkin().getDrawable("window"));
        upgradeWindow = new VisWindow("Trees Level " + level, windowStyle);
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

        VisTable upgradeTable = new VisTable();
        upgradeWindow.add(upgradeTable).width(450);

        levelBar = new VisProgressBar(0, boostLevel, 1, false);
        levelBar.setRange(level, boostLevel);
        upgradeTable.add(levelBar).expandX().fillX().pad(15).padTop(30).colspan(2);
        upgradeTable.row();

        Label.LabelStyle labelStyle1 = new Label.LabelStyle(app.fonts.font20, Color.WHITE);

        VisLabel yieldLabel = new VisLabel("Log Amount: ", labelStyle1);
        upgradeTable.add(yieldLabel).pad(15).left();

        yieldAmountLabel = new VisLabel(String.valueOf(currentYield), labelStyle1);
        upgradeTable.add(yieldAmountLabel).pad(15).right();
        upgradeTable.row();

        upgradeTable.addSeparator().colspan(2).width(410);
        upgradeTable.row();

        VisLabel timeLabel = new VisLabel("Work Time: ", labelStyle1);
        upgradeTable.add(timeLabel).pad(15).left();

        timeAmountLabel = new VisLabel(String.format(Locale.getDefault(), "%.1f", totalWorkTime) + "s", labelStyle1);
        upgradeTable.add(timeAmountLabel).pad(15).right();
        upgradeTable.row();

        upgradeTable.addSeparator().colspan(2).width(410);
        upgradeTable.row();

        upgradeWindowButton = new VisTextButton("Upgrade\n" + upgradeCost + "g", buttonStyle1);
        upgradeWindowButton.addListener(new ClickListener(){
            @Override
            public void clicked (InputEvent event, float x, float y) {
                if (app.prefs.getInteger("playerGold") >= upgradeCost) {
                    app.gsm.getPlayer().setGoldCount(app.prefs.getInteger("playerGold") - upgradeCost);
                    upgrade();
                }
                else{
                    System.out.println("Not enough gold!");
                    dialog.displayDialogWindow(stage);
                }
            }
        });

        upgradeTable.add(upgradeWindowButton).fillX().expandY().colspan(2).spaceTop(100).pad(5).bottom();

        VisTable stageTable = new VisTable();
        stageTable.setFillParent(true);
        stageTable.add(upgradeWindow).center().padBottom(125);
        stage.addActor(stageTable);
    }

    @Override
    public void upgrade() {
        super.upgrade();
    }


    public void render(SpriteBatch batch) {
        batch.draw(tex, x, y, width, height);

        stateTime += Gdx.graphics.getDeltaTime();

        app.fonts.font20.draw(batch, "Yield: " + currentYield + " logs", x, y - 50);

        if (readyToCollect) {
            batch.draw((TextureRegion)collectAnimation.getKeyFrame(stateTime, true),
                    x + width / 2 - ((TextureRegion)collectAnimation.getKeyFrame(stateTime)).getRegionWidth() * collectScale / 2,
                    y + height - 35f,
                    ((TextureRegion)collectAnimation.getKeyFrame(stateTime)).getRegionWidth() * collectScale,
                    ((TextureRegion)collectAnimation.getKeyFrame(stateTime)).getRegionHeight() * collectScale);
        }
    }

    public boolean checkTouch(Vector3 touch) {
        if (bounds.contains(touch.x, touch.y) && readyToCollect && !app.gsm.hasVisibleWindow()) {
            app.gsm.getPlayer().setLogCount(app.prefs.getInteger("playerLogs") + currentYield);
            readyToCollect = false;
            working = true;
            stateTime = 0f;
            return true;
        }

        return false;
    }

    public void drawProgressBar() {
        super.drawProgressBar();
    }

    public VisWindow getUpgradeWindow() {
        return upgradeWindow;
    }
}
