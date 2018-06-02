package com.moemeido.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.moemeido.game.Application;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = Application.APP_TITLE;
		config.width = Application.APP_WIDTH;
		config.height = Application.APP_HEIGHT;
		config.foregroundFPS = Application.APP_FPS;
		config.backgroundFPS = Application.APP_FPS;
		new LwjglApplication(new Application(), config);
	}
}
