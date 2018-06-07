package com.moemeido.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.moemeido.game.Application;
import com.moemeido.game.entities.Item;
import com.moemeido.game.managers.MyInputProcessor;

public class ShopScreen extends AbstractScreen {

    private HUD hud;
    private TextureRegion bg;
    private GlyphLayout glyphLayout;

    private Skin skin;
    private VisTable mainTable;
    private VisScrollPane scrollPane;
    private List list;
    private Array<Label> labels;
    private Array<TextButton> buttons;

    private VisTable textTable;
    private Image uiLog, uiCoin;
    private VisLabel logLabel, goldLabel;

    public ShopScreen(Application app) {
        super(app);
        hud = new HUD(app, stage);
        glyphLayout = new GlyphLayout();
        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);
        bg = atlas.findRegion("plank_bg2");

        skin = new Skin();
        skin.addRegions(app.assets.get("ui/uiskin.atlas", TextureAtlas.class));
        skin.add("default-font", app.fonts.font30);
        skin.load(Gdx.files.internal("ui/uiskin.json"));

        mainTable = new VisTable();
        mainTable.setFillParent(true);
        mainTable.setDebug(false);
        stage.addActor(mainTable);

        VisTextButton.VisTextButtonStyle buttonStyle1 = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().getDrawable("button"),
                VisUI.getSkin().getDrawable("button-down"),
                VisUI.getSkin().getDrawable("button"),
                app.fonts.font30);

        Label.LabelStyle labelStyle1 = new Label.LabelStyle(app.fonts.font20, Color.WHITE);

        labels = new Array<Label>();
        buttons = new Array<TextButton>();
        for(int i = 0; i < 10; i++) {
            labels.add(new VisLabel(String.valueOf(i), labelStyle1));
            buttons.add(new VisTextButton("Testing " + i, buttonStyle1));
        }

        list = new List(skin);
        list.setItems(labels);

        setupScrollPane();

        textTable = new VisTable();
        textTable.setFillParent(true);
        textTable.setDebug(false);
        stage.addActor(textTable);

        textTable.bottom();

        uiCoin = new Image(atlas.findRegion("coin2"));
        uiCoin.setScaling(Scaling.fit);
        textTable.add(uiCoin).padBottom(210).size(30);
        goldLabel = new VisLabel("0", labelStyle1);
        goldLabel.setAlignment(Align.left);
        textTable.add(goldLabel).align(Align.left).padBottom(210).padLeft(10);
    }

    private void setupScrollPane() {
        final VisTextButton.VisTextButtonStyle buttonStyle1 = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().getDrawable("button"),
                VisUI.getSkin().getDrawable("button-down"),
                VisUI.getSkin().getDrawable("button"),
                app.fonts.font30);

        Label.LabelStyle labelStyle1 = new Label.LabelStyle(app.fonts.font20, Color.WHITE);

        skin.get(List.ListStyle.class).selection.setBottomHeight(50);
        skin.get(List.ListStyle.class).selection.setTopHeight(50);

        VisTable scrollableTable = new VisTable();
        scrollableTable.setDebug(false);

        final Array<Item> items = new Array<Item>();
        //items.add(new Item(app, Item.ItemID.HATCHET));
        //items.add(new Item(app, Item.ItemID.BOOTS));

        for(int i = 0; i < items.size; i ++) {
            Image img = new Image(items.get(i).getItemTex());
            img.setScaling(Scaling.fit);
            scrollableTable.add(img).expandX().padTop(10).padBottom(10).size(100);

            final Label itemNameLabel = new Label(items.get(i).getItemName()+ " " + items.get(i).getItemLevel(), labelStyle1);
            itemNameLabel.setAlignment(Align.center);
            scrollableTable.add(itemNameLabel).expandX().center().padTop(10).padBottom(10);

            final VisTextButton upgradeButton = new VisTextButton(items.get(i).getItemCost() + "g\nUpgrade", buttonStyle1);
            final int finalI = i;

            upgradeButton.addListener(new ClickListener(){
                @Override
                public void clicked (InputEvent event, float x, float y) {
                    final VisDialog dialog = new VisDialog("");

                    final Label dialogLabel = new Label("Would you like to upgrade this item\n" +
                            "to level " + (items.get(finalI).getItemLevel() + 1) + " for " + items.get(finalI).getItemCost() + "g?", skin);
                    dialogLabel.setAlignment(Align.center);
                    dialog.getContentTable().add(dialogLabel).pad(50);

                    VisTextButton yes = new VisTextButton("Yes!", buttonStyle1);
                    yes.addListener( new ClickListener(){
                        @Override
                        public void clicked (InputEvent event, float x, float y) {
                            if (app.gsm.getPlayer().getGoldCount() >= items.get(finalI).getItemCost()) {
                                items.get(finalI).upgrade();
                                upgradeButton.setText(items.get(finalI).getItemCost() + "g\nUpgrade");
                                dialog.hide();
                                itemNameLabel.setText(items.get(finalI).getItemName() + " " + items.get(finalI).getItemLevel());
                            } else {
                                dialogLabel.setText("Not enough gold!");
                            }
                        }
                    });
                    dialog.getButtonsTable().add(yes).width(150).height(55).pad(10);

                    VisTextButton no = new VisTextButton("No!", buttonStyle1);
                    no.addListener( new ClickListener(){
                        @Override
                        public void clicked (InputEvent event, float x, float y) {
                            dialog.hide();
                        }
                    });
                    dialog.getButtonsTable().add(no).width(150).height(55).pad(10);

                    dialog.show(stage);
                }
            });
            scrollableTable.add(upgradeButton).expandX().padTop(10).padBottom(10).width(125);

            scrollableTable.row();
        }

        scrollPane = new VisScrollPane(scrollableTable);
        scrollPane.setupFadeScrollBars(0f, 0f);
        mainTable.add(scrollPane).expand().fill().center().padTop(75).padBottom(275).width(500);
    }

    @Override
    public void show() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new MyInputProcessor(app));
        Gdx.input.setInputProcessor(multiplexer);

        stage.addActor(mainTable);

        app.batch.setProjectionMatrix(camera.combined);
        app.shapeRenderer.setProjectionMatrix(camera.combined);

        hud.initXPBar();
    }

    @Override
    public void update(float delta) {
        stage.act(delta);
        hud.update();

        goldLabel.setText(String.valueOf(app.gsm.getPlayer().getGoldCount()));

        app.gsm.globalUpdate(delta);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        app.batch.begin();

        app.batch.draw(bg, 0, 0, Application.V_WIDTH, Application.V_HEIGHT);

        glyphLayout.setText(app.fonts.font40, "Shop");
        app.fonts.font40.draw(app.batch, glyphLayout, viewport.getWorldWidth() / 2 - glyphLayout.width / 2, viewport.getWorldHeight() - glyphLayout.height / 2 );

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
