package com.moemeido.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.moemeido.game.Application;
import com.moemeido.game.entities.workers.LoggingTree;
import com.moemeido.game.entities.workers.Workshop;
import com.moemeido.game.screens.LoggingScreen;
import com.moemeido.game.screens.WorkshopScreen;

public abstract class MyGestureListener implements GestureDetector.GestureListener {

    private Application app;
    private OrthographicCamera camera;
    private Stage stage;

    private float flingVelocityX;
    private float flingVelocityY;

    private boolean flingable;
    private float maxScreenLength;

    protected MyGestureListener(Application app, OrthographicCamera camera, Stage stage, float maxScreenLength) {
        this.app = app;
        this.camera = camera;
        this.stage = stage;
        this.maxScreenLength = maxScreenLength;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    public void flingDecelerate(float time) {
        if (flingVelocityX != 0f || flingVelocityY != 0f) {
            float newFlingX = Math.max(0, Math.abs(flingVelocityX) - Math.abs(flingVelocityX) * 5f * time);
            float newFlingY = Math.max(0, Math.abs(flingVelocityY) - Math.abs(flingVelocityY) * 5f * time);

            if (flingVelocityX < 0)
                flingVelocityX = newFlingX * -1;
            else
                flingVelocityX = newFlingX;

            if (flingVelocityY < 0)
                flingVelocityY = newFlingY * -1;
            else
                flingVelocityY = newFlingY;

            if (Math.abs(flingVelocityX) < 10) flingVelocityX = 0f;
            if (Math.abs(flingVelocityY) < 10) flingVelocityY = 0f;
        }
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        flingable = app.gsm.hasVisibleWindow();
        if (Math.abs(velocityX) > 500 || Math.abs(velocityY) > 500 && !flingable) {
            OrthographicCamera stageCam = (OrthographicCamera)stage.getCamera();

            flingVelocityX = velocityX * stageCam.zoom;
            flingVelocityY = velocityY * stageCam.zoom;
        }
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {

        if (flingVelocityX != 0f || flingVelocityY != 0f) {
            flingVelocityX = 0f;
            flingVelocityY = 0f;
        }

        LoggingScreen loggingScreen = (LoggingScreen)app.gsm.getScreen(GameScreenManager.STATE.LOGGING);
        WorkshopScreen workshopScreen = (WorkshopScreen)app.gsm.getScreen(GameScreenManager.STATE.WORKSHOP);

        Array<Boolean> visibleWindows = new Array<Boolean>();

        switch (app.gsm.getCurrentState()) {
            case LOGGING:
                for(int i = 0; i < loggingScreen.getTrees().size; i++) {
                    LoggingTree tree = loggingScreen.getTrees().get(i);
                    visibleWindows.add(tree.getUpgradeWindow().isVisible());
                }
                break;
            case WORKSHOP:
                for(int i = 0; i < workshopScreen.getWorkshops().size; i++) {
                    Workshop workshop = workshopScreen.getWorkshops().get(i);
                    visibleWindows.add(workshop.getUpgradeWindow().isVisible());
                }
                break;
        }

        if(!visibleWindows.contains(true, true) || visibleWindows.size < 0) {
            if (deltaX != 0f || deltaY != 0f) {
                OrthographicCamera gameCam = camera;

                deltaY *= (gameCam.viewportHeight / (float) Gdx.graphics.getHeight());

                gameCam.translate(0, deltaY);
                stage.getCamera().translate(0, deltaY, 0);
                keepCameraInBounds();
            }
        }

        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

    public void keepCameraInBounds() {
        if (camera.position.y > Application.V_HEIGHT * maxScreenLength) {
            camera.position.y = Application.V_HEIGHT * maxScreenLength;
            stage.getCamera().position.y = Application.V_HEIGHT * maxScreenLength;
        }

        if (camera.position.y < Application.V_HEIGHT / 2) {
            camera.position.y = Application.V_HEIGHT / 2;
            stage.getCamera().position.y = Application.V_HEIGHT / 2;
        }
    }

    public float getFlingVelocityY() {
        return flingVelocityY;
    }

}
