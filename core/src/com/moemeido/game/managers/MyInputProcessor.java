package com.moemeido.game.managers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.moemeido.game.Application;

public class MyInputProcessor implements InputProcessor {

    private Application app;

    private boolean isDown;

    public MyInputProcessor(Application app) {
        this.app = app;
    }

    public void delta() {

    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK) {
            app.gsm.setScreen(GameScreenManager.STATE.PLAY);
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        isDown = true;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        isDown = false;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public Boolean isDown() {
        return isDown;
    }

}
