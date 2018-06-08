package com.moemeido.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.moemeido.game.Application;
import com.moemeido.game.entities.Item;
import com.moemeido.game.managers.MyInputProcessor;
import com.moemeido.game.utils.UITools;

public class ShopScreen extends AbstractScreen {

    private HUD hud;
    private TextureRegion bg;
    private GlyphLayout glyphLayout;

    private UITools uiTools;

    private Array<Item> items;
    private Array<VisLabel> currentBonusLabels;

    public ShopScreen(Application app)
    {
        super(app);
        hud = new HUD(app, stage);
        glyphLayout = new GlyphLayout();
        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);
        bg = atlas.findRegion("plank_bg2");

        items = new Array<Item>();
        currentBonusLabels = new Array<VisLabel>();

        setupScrollPane();
        uiTools = new UITools(app, stage);
    }

    @Override
    public void show() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new MyInputProcessor(app));
        Gdx.input.setInputProcessor(multiplexer);

        app.batch.setProjectionMatrix(camera.combined);
        app.shapeRenderer.setProjectionMatrix(camera.combined);

        hud.initXPBar();

        updateCurrentStats();
    }

    private void setupScrollPane() {
        VisTable stageTable = new VisTable();
        stageTable.setFillParent(true);
        stageTable.setDebug(false);

        items = new Array<Item>();

        // For each item defined within the Item class, place within the scrollable table
        for (int i = 0; i < Item.ItemID.values().length; i++){
            items.add(new Item(app, Item.ItemID.values()[i]));
        }

        VisTable scrollableTable = new VisTable();
        scrollableTable.setDebug(false);

        Window.WindowStyle windowStyle1 = new Window.WindowStyle(app.fonts.font20, Color.WHITE, VisUI.getSkin().getDrawable("window"));

        Label.LabelStyle labelStyle1 = new Label.LabelStyle(app.fonts.font30, Color.WHITE);
        Label.LabelStyle labelStyle2 = new Label.LabelStyle(app.fonts.font20, Color.WHITE);

        VisTextButton.VisTextButtonStyle buttonStyle1 = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().getDrawable("button"),
                VisUI.getSkin().getDrawable("button-down"),
                VisUI.getSkin().getDrawable("button"),
                app.fonts.font30);

        for (int i = 0; i < items.size; i++) {
            Image img = new Image(items.get(i).getItemTex());
            img.setScaling(Scaling.fit);
            scrollableTable.add(img).size(100).left();
            scrollableTable.addSeparator(true).pad(5);

            VisTable textTable = new VisTable();
            textTable.setDebug(false);

            VisLabel itemNameLabel = new VisLabel(items.get(i).getItemName(), labelStyle1);
            textTable.add(itemNameLabel).expandX().left();
            textTable.row();

            VisLabel descriptionLabel = new VisLabel("\nThis is a test.\nThis is a new line.", labelStyle2);
            descriptionLabel.setText(items.get(i).getDescription());
            textTable.add(descriptionLabel).expandX().left();
            textTable.row();

            final VisLabel currentBonusLabel = new VisLabel(items.get(i).getCurrentStat(), labelStyle2);
            currentBonusLabels.add(currentBonusLabel);
            textTable.add(currentBonusLabel).expand().bottom().left();
            textTable.row();

            scrollableTable.add(textTable).expandX().fill().pad(5);

            final VisTextButton upgradeButton = new VisTextButton("Upgrade!\n" + items.get(i).getItemCost() + "g", buttonStyle1);
            scrollableTable.add(upgradeButton).right().pad(5).width(100).height(75);
            scrollableTable.row();

            // Starts at a minimum of 1 because items start at level 1
            final VisProgressBar levelBar = new VisProgressBar(0, items.get(i).getMaxLevel(), 1, false);

            if (items.get(i).getItemLevel() == items.get(i).getMaxLevel()){
                levelBar.setValue(items.get(i).getItemLevel() + 1);
                upgradeButton.setDisabled(true);
                upgradeButton.setText("Max\nLevel!");
            }
            else{
                levelBar.setValue(items.get(i).getItemLevel());
            }

            scrollableTable.add(levelBar).expandX().fillX().colspan(5).pad(5);
            scrollableTable.row();

            final int finalI = i;
            upgradeButton.addListener(new ClickListener(){
                @Override
                public void clicked (InputEvent event, float x, float y) {
                    if (items.get(finalI).getItemLevel() == items.get(finalI).getMaxLevel())
                        return;

                    if (items.get(finalI).getItemLevel() < items.get(finalI).getMaxLevel()) {
                        if (app.gsm.getPlayer().getGoldCount() >= items.get(finalI).getItemCost()) {
                            app.gsm.getPlayer().setGoldCount(app.gsm.getPlayer().getGoldCount() - items.get(finalI).getItemCost());
                            items.get(finalI).upgrade();
                            currentBonusLabel.setText(items.get(finalI).getCurrentStat());
                            upgradeButton.setText("Upgrade!\n" + items.get(finalI).getItemCost() + "g");
                            levelBar.setValue(items.get(finalI).getItemLevel());

                            if (items.get(finalI).getItemLevel() == items.get(finalI).getMaxLevel()){
                                upgradeButton.setDisabled(true);
                                upgradeButton.setText("Max\nLevel!");
                            }
                        } else {
                            uiTools.displayDialogWindow(stage);
                        }
                    }
                }
            });
        }

        VisScrollPane scrollPane = new VisScrollPane(scrollableTable);
        scrollPane.setupFadeScrollBars(0, 0);

        VisWindow scrollWindow = new VisWindow("", windowStyle1);
        scrollWindow.getTitleLabel().setAlignment(Align.center);
        scrollWindow.setMovable(false);
        scrollWindow.add(scrollPane);

        stageTable.add(scrollWindow).padBottom(150);
        stage.addActor(stageTable);
    }

    @Override
    public void update(float delta) {
        stage.act(delta);
        hud.update();

        app.gsm.globalUpdate(delta);
    }

    /**
     * Gets the players stat that a particular item modifies and updates
     * item labels accordingly.
     */
    private void updateCurrentStats(){
        for (int i = 0; i < items.size; i++) {
            items.get(i).updatePlayerStat();
            currentBonusLabels.get(i).setText(items.get(i).getCurrentStat());
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        app.batch.begin();

        app.batch.draw(bg, 0, 0, Application.V_WIDTH, Application.V_HEIGHT);

        glyphLayout.setText(app.fonts.font40, "Super Awesome Shop");
        app.fonts.font40.draw(app.batch, glyphLayout, viewport.getWorldWidth() / 2 - glyphLayout.width / 2, viewport.getWorldHeight() - glyphLayout.height / 2 - 50);

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


}
