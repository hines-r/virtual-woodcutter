package com.moemeido.game.entities.workers;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.moemeido.game.Application;
import com.moemeido.game.screens.AbstractScreen;
import com.moemeido.game.screens.LoggingScreen;
import com.moemeido.game.screens.WorkshopScreen;
import com.moemeido.game.utils.UITools;

public class Plot {

    private Application app;
    private Stage stage, dynamicStage;
    private Screen screen;

    private float width = 425;
    private float height = 200;
    private float x, y;

    private Rectangle bounds;

    // UI stuff
    private VisTable table;
    private VisTextButton buyButton;
    private PlotType type;
    private UITools uiTools;

    private int cost = 100;

    public enum PlotType {
        WORKSHOP,
        LOGGING
    }

    public Plot(final Application app, AbstractScreen screen, Stage stage, Stage dynamicStage, float x, float y, PlotType type) {
        this.app = app;
        this.screen = screen;
        this.stage = stage;
        this.dynamicStage = dynamicStage;
        this.x = x;
        this.y = y;
        this.type = type;

        initPlotUI();

        bounds = new Rectangle(x - width / 2, y - height / 2, width, height);
    }

    private void initPlotUI(){
        uiTools = new UITools(app, stage);

        table = new VisTable();
        table.setPosition(x, y);
        table.setDebug(false);

        VisTextButton.VisTextButtonStyle buttonStyle1 = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().getDrawable("button"),
                VisUI.getSkin().getDrawable("button-down"),
                VisUI.getSkin().getDrawable("button"),
                app.fonts.font20);

        buyButton = new VisTextButton("Buy\n" + cost + "g", buttonStyle1);

        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                if(app.prefs.getInteger("playerGold") >= cost) {
                    app.gsm.getPlayer().setGoldCount(app.prefs.getInteger("playerGold") - cost);
                    if (type == PlotType.WORKSHOP) {
                        WorkshopScreen wsc = (WorkshopScreen)screen;
                        wsc.addWorkshop();
                    } else {
                        LoggingScreen ls = (LoggingScreen)screen;
                        ls.addLoggingTree();
                    }
                } else {
                    uiTools.displayDialogWindow(stage);
                }
            }
        });

        table.add(buyButton).width(150).height(50);

        dynamicStage.addActor(table);
    }

    public void incrementCost(){
        float incrementMultiplier = 2f;
        cost *= incrementMultiplier;
    }

    public void update() {
        buyButton.setText("Buy\n" + cost + "g");
    }

    public void drawBounds() {
        app.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        app.shapeRenderer.setColor(Color.BLACK);
        app.shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        app.shapeRenderer.end();
    }

    public boolean checkTouch(Vector3 touch) {
        if (bounds.contains(touch.x, touch.y) && !app.gsm.hasVisibleWindow()) {
            if(app.prefs.getInteger("playerGold") >= cost) {
                app.gsm.getPlayer().setGoldCount(app.prefs.getInteger("playerGold") - cost);
                return true;
            } else {
                uiTools.displayDialogWindow(stage);
            }
        }

        return false;
    }

    public void movePosition() {
        float newX = bounds.x;
        float newY = 0f;

        float logYOffset = 325f;
        float workshopYOffset = 250f;

        switch (type) {
            case LOGGING:
                newY = getCenter().y + logYOffset;
                break;
            case WORKSHOP:
                newY = getCenter().y + workshopYOffset;
                break;
        }

        bounds.setPosition(newX, newY);
        table.setPosition(getCenter().x, getCenter().y);
    }

    public Vector2 getCenter() {
        return new Vector2(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
    }

    public Vector2 getPosition() {
        return new Vector2(bounds.x, bounds.y);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

}
