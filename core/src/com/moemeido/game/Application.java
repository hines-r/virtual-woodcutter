package com.moemeido.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.TimeUtils;
import com.kotcrab.vis.ui.VisUI;
import com.moemeido.game.managers.MyAssetManager;
import com.moemeido.game.managers.GameScreenManager;
import com.moemeido.game.managers.MyFontManager;
import com.moemeido.game.utils.UITools;

import java.util.concurrent.TimeUnit;


public class Application extends Game {

	// Application variables for the desktop window etc
	public static final String APP_TITLE = "Wood Game!";
	public static final int APP_WIDTH = 576;
	public static final int APP_HEIGHT = APP_WIDTH / 9 * 16;
	public static final int APP_FPS = 60;

	// Game resolution
	public static final int V_WIDTH = 576;
	public static final int V_HEIGHT = V_WIDTH / 9 * 16;

	public SpriteBatch batch;
	public ShapeRenderer shapeRenderer;
	public GameScreenManager gsm;
	public MyAssetManager manager;
	public AssetManager assets;
	public MyFontManager fonts;
	public Preferences prefs;
	public UITools ui;

	private long currentTime;

	@Override
	public void create () {
		currentTime = TimeUnit.SECONDS.convert(TimeUtils.nanoTime(), TimeUnit.NANOSECONDS);
		prefs = Gdx.app.getPreferences("My Preferences");

		if (prefs.getLong("lastTime") != 0) {
			prefs.putLong("timePassed", currentTime - prefs.getLong("lastTime"));
			prefs.flush();
		} else {
			prefs.putLong("timePassed", 0);
			prefs.flush();
		}

		System.out.println("Last time: " + prefs.getLong("lastTime"));
		System.out.println("Current time: " + currentTime);
		System.out.println("Time passed in seconds: " + prefs.getLong("timePassed") + " seconds");
		System.out.println("Time passed in minutes: " + (prefs.getLong("timePassed") / 60) + " minutes");

		VisUI.load();

		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		assets = new AssetManager();
		manager = new MyAssetManager(this);
		fonts = new MyFontManager(this);
		gsm = new GameScreenManager(this);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
		assets.dispose();
		batch.dispose();
		gsm.dispose();
	}

	@Override
	public void pause() {
		super.pause();

		gsm.pause();

		currentTime = TimeUnit.SECONDS.convert(TimeUtils.nanoTime(), TimeUnit.NANOSECONDS);
		prefs.putLong("lastTime", currentTime);
		prefs.flush();
	}
}
