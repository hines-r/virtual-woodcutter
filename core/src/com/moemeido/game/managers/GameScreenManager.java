package com.moemeido.game.managers;

import com.moemeido.game.Application;
import com.moemeido.game.entities.Player;
import com.moemeido.game.screens.*;

import java.util.HashMap;

public class GameScreenManager {

    private final Application app;

    private HashMap<STATE, AbstractScreen> gameScreens;
    private STATE currentState;

    private LoadingScreen loadingScreen;
    private MainMenuScreen mainMenuScreen;
    private PlayScreen playScreen;
    private ShopScreen shopScreen;
    private ShopScreen2 shopScreen2;
    private WorkerScreen workerScreen;
    private LoggingScreen loggingScreen;
    private WorkshopScreen workshopScreen;

    public enum STATE {
        LOADING,
        MAIN_MENU,
        PLAY,
        SHOP,
        SHOP2,
        LOGGING,
        WORKSHOP,
        WORKERS
    }

    public GameScreenManager(final Application app) {
        this.app = app;

        initGameScreens();
        setScreen(STATE.LOADING);
    }

    private void initGameScreens() {
        this.gameScreens = new HashMap<STATE, AbstractScreen>();

        loadingScreen = new LoadingScreen(app);
        playScreen = new PlayScreen(app);
        mainMenuScreen = new MainMenuScreen(app);
        shopScreen = new ShopScreen(app);
        shopScreen2 = new ShopScreen2(app);
        workerScreen = new WorkerScreen(app);
        loggingScreen = new LoggingScreen(app);
        workshopScreen = new WorkshopScreen(app);

        gameScreens.put(STATE.LOADING, loadingScreen);
        gameScreens.put(STATE.PLAY, playScreen);
        gameScreens.put(STATE.MAIN_MENU, mainMenuScreen);
        gameScreens.put(STATE.SHOP, shopScreen);
        gameScreens.put(STATE.SHOP2, shopScreen2);
        gameScreens.put(STATE.WORKERS, workerScreen);
        gameScreens.put(STATE.LOGGING, loggingScreen);
        gameScreens.put(STATE.WORKSHOP, workshopScreen);
    }

    public void setScreen(STATE nextScreen) {
        currentState = nextScreen;
        app.setScreen(gameScreens.get(nextScreen));
    }

    public void dispose() {
        for (AbstractScreen screen : gameScreens.values()) {
            if (screen != null) {
                screen.dispose();
            }
        }
    }

    public void globalUpdate(float delta) {
        loggingScreen.updateLoggingTrees(delta);
        workshopScreen.updateWorkshops(delta);
        workerScreen.updateWorkers(delta);
    }

    public void pause() {
        for (AbstractScreen screen : gameScreens.values()) {
            if (screen != null) {
                screen.pause();
            }
        }
    }

    public STATE getCurrentState() {
        return currentState;
    }

    public AbstractScreen getScreen(STATE state) {
        return gameScreens.get(state);
    }

    public Player getPlayer() {
        return playScreen.getPlayer();
    }

    public boolean hasVisibleWindow() {
        return loggingScreen.containsVisibleWindow() || workshopScreen.containsVisibleWindow();
    }

}
