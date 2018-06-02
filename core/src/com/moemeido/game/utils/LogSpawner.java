package com.moemeido.game.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.moemeido.game.Application;
import com.moemeido.game.entities.workers.Workshop;
import com.moemeido.game.entities.actors.ActorLog;

public class LogSpawner {

    private Application app;
    private Stage stage;
    private Workshop shop;

    private float logTime;
    private float logsPerSecond;

    private Array<ActorLog> logs;

    public LogSpawner(Application app, Stage stage, Workshop shop) {
        this.app = app;
        this.stage = stage;
        this.shop = shop;

        logs = new Array<ActorLog>();

        logTime = 1f;
        logsPerSecond = 1f;
    }

    public void update(float delta) {
        logTime += delta;

        for (ActorLog log : logs)
            log.update(delta);

        if (logTime >= logsPerSecond && shop.isWorking() && !shop.isReadyToCollect()) {
            logs.add(new ActorLog(app, stage,
                    new Vector2(shop.getPosition().x - 75, shop.getPosition().y + 70),
                    new Vector2(shop.getPosition().x + shop.getWidth() + 50, shop.getPosition().y + 70)));
            logs.peek().moveToTarget();
            logTime = 0f;
        }
    }

}
