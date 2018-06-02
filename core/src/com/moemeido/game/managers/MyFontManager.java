package com.moemeido.game.managers;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.moemeido.game.Application;

public class MyFontManager {

    public BitmapFont font10;
    public BitmapFont font20;
    public BitmapFont font30;
    public BitmapFont font40;
    public BitmapFont font100;

    public MyFontManager(Application app) {
        font10 = app.assets.get("size10.ttf", BitmapFont.class);
        font20 = app.assets.get("size20.ttf", BitmapFont.class);
        font30 = app.assets.get("size30.ttf", BitmapFont.class);
        font40 = app.assets.get("size40.ttf", BitmapFont.class);
        font100 = app.assets.get("size100.ttf", BitmapFont.class);
    }

}
