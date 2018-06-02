package com.moemeido.game.managers;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MyClickListener extends ClickListener {

    @Override
    public void clicked(InputEvent event, float x, float y) {

        super.clicked(event, x, y);

        System.out.println("clicked");
    }

}
