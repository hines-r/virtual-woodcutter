package com.moemeido.game.screens.huds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.moemeido.game.Application;
import com.moemeido.game.entities.Player;

public class ShopHUD {

    private Application app;
    private Stage stage;
    private Player player;

    private Table table;
    private Array<Label> labels;
    private Array<String> items;
    private Array<Table> itemTables;

    private Skin skin;

    private TextureRegion hatchet;
    private Array<Image> images;

    public ShopHUD(Application app, Stage stage) {
        this.app = app;
        this.stage = stage;

        table = new Table();
        table.setFillParent(true);
        table.setDebug(true);
        stage.addActor(table);

        skin = new Skin();
        skin.addRegions(app.assets.get("ui/uiskin.atlas", TextureAtlas.class));
        skin.add("default-font", app.fonts.font20);
        skin.load(Gdx.files.internal("ui/uiskin.json"));

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);

        hatchet = atlas.findRegion("hatchet2");

        images = new Array<Image>();
        for(int i = 0; i < 9; i++) {
            Image image = new Image(hatchet);
            image.setScaling(Scaling.fit);
            images.add(image);
        }

        items = new Array<String>();
        for(int i = 0; i < 9; i++) {
            items.add("Item " + i + "\n" + MathUtils.random(10, 100) + " g");
        }

        labels = new Array<Label>();
        for(int i = 0; i < items.size; i++) {
            Label.LabelStyle style = new Label.LabelStyle();
            style.font = app.fonts.font20;
            labels.add(new Label(items.get(i), style));
            labels.get(i).setAlignment(Align.center);
        }

        table.top().padTop(64);
        table.setSize(Application.V_WIDTH, 350);
        createGrid(table,3, 3);
    }

    private void createGrid(final Table table, int rows, int columns) {

        itemTables = new Array<Table>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {

                int index = ((r * columns) + c); // specifies the index of the grid left to right top down
                Table t = new Table();
                t.setDebug(true);
                itemTables.add(t);

                // adds the item picture
                itemTables.get(index)
                        .add(images.get((r * columns) + c))
                        .width(150)
                        .height(150)
                        .expand();

                itemTables.get(index).row();

                // adds the item text label
                itemTables.get(index)
                        .add(labels.get((r * columns) + c))
                        .fill();

                itemTables.get(index).setName("Item" + index); // gives the item table a name

                // add a click listener to each item table
                itemTables.get(index).addListener(new ClickListener() {
                    @Override
                    public void clicked (InputEvent event, float x, float y) {

                        Player player = app.gsm.getPlayer();
                        //int playerGold = player.getGoldCount();

                        for(int i = 0; i < itemTables.size; i++) {
                            Table t = itemTables.get(i);
                            if (event.getListenerActor().getName() == t.getName()) {

                                player.buyItem(10);

                            }
                        }
                    }
                });

                itemTables.get(index).addAction(Actions.fadeIn(2f));
                table.add(itemTables.get(index)).pad(3);
            }
            table.row();
        }
    }

}
