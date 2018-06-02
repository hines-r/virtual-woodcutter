package com.moemeido.game.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.moemeido.game.Application;

public class UITools {

    private Application app;
    private Stage stage;

    private Window.WindowStyle windowStyle1;

    private VisTable stageTable;
    private VisWindow dialogWindow;

    public UITools(Application app, Stage stage) {
        this.app = app;
        this.stage = stage;
        windowStyle1 = new Window.WindowStyle(app.fonts.font30, Color.WHITE, VisUI.getSkin().getDrawable("window"));

        setupDialogWindow();
    }

    private void setupDialogWindow() {
        stageTable = new VisTable();
        stageTable.setFillParent(true);

        dialogWindow = new VisWindow("", windowStyle1);
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

    private void setDialogWindowPosition() {
        int dialogPositionX = 0;
        int dialogPositionY = 800;

        dialogWindow.setPosition(dialogPositionX, dialogPositionY);
    }

    public void displayDialogWindow(Stage stage) {
        stage.addActor(stageTable);

        dialogWindow.getActions().clear();

        setDialogWindowPosition();

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

}
