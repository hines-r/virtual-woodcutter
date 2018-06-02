package com.moemeido.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class MyPreferenceManager {

    public Preferences prefs;

    public MyPreferenceManager() {
        prefs = Gdx.app.getPreferences("My Preferences");
    }

}
