package com.moemeido.game.entities.workers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.moemeido.game.Application;
import com.moemeido.game.screens.AbstractScreen;
import com.moemeido.game.screens.LoggingScreen;
import com.moemeido.game.screens.WorkshopScreen;
import com.moemeido.game.utils.UITools;

public class Plot {

    private Application app;
    private AbstractScreen screen;
    private Stage stage, dynamicStage;

    private float x, y;
    private float width, height;

    private Rectangle bounds;

    private VisTable table, stageTable;
    private VisTextButton buyButton;
    private VisWindow dialogWindow;

    private Vector2 initialDialogPosition;

    private int cost;

    private PlotType type;

    private UITools uiTools;

    public enum PlotType {
        WORKSHOP,
        LOGGING
    }

    public Plot(final Application app, final AbstractScreen screen, final Stage stage, Stage dynamicStage, float x, float y, final PlotType type) {
        this.app = app;
        this.stage = stage;
        this.screen = screen;
        this.dynamicStage = dynamicStage;
        this.x = x;
        this.y = y;
        this.type = type;

        width = 425;
        height = 200;

        bounds = new Rectangle(x - width / 2, y - height / 2, width, height);

        cost = 100;

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

        uiTools = new UITools(app, stage);
    }

    public void incrementCost(){
        cost *= 2;
    }

    private void setupDialog(){
        stageTable = new VisTable();
        stageTable.setFillParent(true);

        Window.WindowStyle windowStyle = new Window.WindowStyle(app.fonts.font30, Color.WHITE, VisUI.getSkin().getDrawable("window"));
        dialogWindow = new VisWindow("", windowStyle);
        dialogWindow.setVisible(false);
        dialogWindow.setMovable(false);
        dialogWindow.setTouchable(Touchable.disabled);

        Label.LabelStyle labelStyle1 = new Label.LabelStyle(app.fonts.font30, Color.WHITE);
        VisLabel dialogLabel = new VisLabel("Not enough gold!", labelStyle1);
        dialogLabel.setAlignment(Align.center);

        dialogWindow.add(dialogLabel).padBottom(25).center();
        stageTable.top();
        stageTable.add(dialogWindow).width(Application.V_WIDTH);
        stage.addActor(stageTable);
    }

    private void displayDialogWindow() {
        dialogWindow.getActions().clear();

        dialogWindow.setPosition(0, 800);

        dialogWindow.addAction(Actions.parallel(
                Actions.show(),
                Actions.alpha(0),
                Actions.alpha(.9f, .25f, Interpolation.pow5),
                Actions.sequence(
                        Actions.moveBy(0, 75),
                        Actions.moveBy(0, -75, .25f, Interpolation.pow5),
                        Actions.delay(1f),
                        Actions.fadeOut(1f, Interpolation.pow5)
                )
        ));
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

        switch (type) {
            case LOGGING:
                newY = getCenter().y + 325;
                break;
            case WORKSHOP:
                newY = getCenter().y + 250;
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