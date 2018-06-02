package com.moemeido.game.managers;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.moemeido.game.Application;


public class MyAssetManager {

    private final Application app;

    public MyAssetManager(final Application app) {
        this.app = app;
        queueAssets();
    }

    private void queueAssets() {
        FileHandleResolver resolver = new InternalFileHandleResolver();
        app.assets.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        app.assets.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        FreetypeFontLoader.FreeTypeFontLoaderParameter mySmallFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        mySmallFont.fontFileName = "fonts/munro_narrow.ttf";
        mySmallFont.fontParameters.size = 10;
        app.assets.load("size10.ttf", BitmapFont.class, mySmallFont);

        FreetypeFontLoader.FreeTypeFontLoaderParameter myRegularFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        myRegularFont.fontFileName = "fonts/munro_narrow.ttf";
        myRegularFont.fontParameters.size = 20;
        app.assets.load("size20.ttf", BitmapFont.class, myRegularFont);

        FreetypeFontLoader.FreeTypeFontLoaderParameter myLargeFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        myLargeFont.fontFileName = "fonts/munro_narrow.ttf";
        myLargeFont.fontParameters.size = 30;
        app.assets.load("size30.ttf", BitmapFont.class, myLargeFont);

        FreetypeFontLoader.FreeTypeFontLoaderParameter myXLSize = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        myXLSize.fontFileName = "fonts/munro_narrow.ttf";
        myXLSize.fontParameters.size = 40;
        app.assets.load("size40.ttf", BitmapFont.class, myXLSize);

        FreetypeFontLoader.FreeTypeFontLoaderParameter myMassiveFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        myMassiveFont.fontFileName = "fonts/munro_narrow.ttf";
        myMassiveFont.fontParameters.size = 100;
        app.assets.load("size100.ttf", BitmapFont.class, myMassiveFont);

        app.assets.load("ui/uiskin.atlas", TextureAtlas.class);
        app.assets.load("img/sheet.pack", TextureAtlas.class);

        app.assets.finishLoading();
    }

}
