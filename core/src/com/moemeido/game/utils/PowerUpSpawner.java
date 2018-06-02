package com.moemeido.game.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.moemeido.game.Application;
import com.moemeido.game.entities.PowerUp;

import java.util.Locale;

public class PowerUpSpawner {

    private Application app;
    private Stage stage;

    private float spawnTime;
    private float currentTime;

    private float minTime;
    private float maxTime;

    private Vector2 spawnPosition;

    public PowerUpSpawner(Application app, Stage stage) {
        this.app = app;
        this.stage = stage;
        spawnPosition = new Vector2();

        minTime = 5f;
        maxTime = 10f;

        setRandomValues();
    }

    public void update(float delta, Array<PowerUp> array) {
        currentTime += delta;

        if (currentTime >= spawnTime) {
            array.add(newPowerRoll(stage));
            currentTime = 0;
        }

    }

    private PowerUp newPowerRoll(Stage stage) {
        int typeRoll = MathUtils.random(1, PowerUp.POWER.values().length - 1); // Randomizes the type of power-up dropped
        setRandomValues();
        return new PowerUp(app, stage, spawnPosition.x, spawnPosition.y, PowerUp.POWER.values()[typeRoll]);
    }

    private void setRandomValues() {
        spawnTime = MathUtils.random(minTime, maxTime);
        spawnPosition.x = -125;
        spawnPosition.y = MathUtils.random(700f, 900f);
    }

    public String getTimeToSpawn() {
        return String.format(Locale.getDefault(),"%.2f", Math.abs(currentTime - spawnTime));
    }
}
