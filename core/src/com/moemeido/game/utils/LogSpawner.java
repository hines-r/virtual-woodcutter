package com.moemeido.game.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.moemeido.game.Application;
import com.moemeido.game.entities.actors.ActorLogWorkshop;
import com.moemeido.game.entities.workers.Workshop;

public class LogSpawner {

    private Application app;
    private Stage stage;
    private Workshop shop;

    private float logTime;
    private float logsPerSecond;

    private Array<ActorLogWorkshop> logs;

    public LogSpawner(Application app, Stage stage, Workshop shop) {
        this.app = app;
        this.stage = stage;
        this.shop = shop;

        logs = new Array<ActorLogWorkshop>();

        logTime = 1f;
        logsPerSecond = 1f;
    }

    public void update(float delta) {
        logTime += delta;

        for (ActorLogWorkshop log : logs)
            log.update(delta);

        if (logTime >= logsPerSecond && shop.isWorking() && !shop.isReadyToCollect()) {
            Vector2 target = new Vector2(shop.getPosition().x + shop.getWidth() + 50, shop.getPosition().y + 70);
            Vector2 origin = new Vector2(shop.getPosition().x - 75, shop.getPosition().y + 70);

            logs.add(new ActorLogWorkshop(app, stage, target, origin));
            logs.peek().linearMovement();
            logTime = 0f;
        }
    }

}
