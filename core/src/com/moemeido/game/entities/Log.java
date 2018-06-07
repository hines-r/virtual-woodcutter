package com.moemeido.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.moemeido.game.Application;

import static com.moemeido.game.utils.B2DVars.GROUND_BIT;
import static com.moemeido.game.utils.B2DVars.LOG_BIT;
import static com.moemeido.game.utils.B2DVars.PPM;

public class Log implements Poolable {

    private Body body;
    private World world;

    private TextureRegion logTex;
    private Tree tree;

    private float logWidth;
    private float logHeight;
    private Vector2 position;

    private int logYield;

    private float life; // Amount of time the log remains on the screen
    private float currentLife;
    private boolean readyToDestroy;

    public Log(Application app, World world, Tree tree) {
        this.world = world;
        this.tree = tree;

        TextureAtlas atlas = app.assets.get("img/sheet.pack", TextureAtlas.class);
        logTex = atlas.findRegion("log1");

        int minYield = 1;
        int maxYield = 10;
        logYield = MathUtils.random(minYield, maxYield);

        life = 3.25f; // How long logs remain on the screen
        currentLife = life;

        float logScale = 2f;
        logWidth = logTex.getRegionWidth() * logScale - logTex.getRegionWidth() / 2;
        logHeight = logTex.getRegionHeight() * logScale - logTex.getRegionHeight() / 2;

        createPositionOnTree();
        defineLog();
    }

    private void defineLog() {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(position);
        body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(logWidth / 2 / PPM, logHeight / 2 / PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.friction = .075f;
        fdef.restitution = .1f;
        fdef.filter.categoryBits = LOG_BIT;
        fdef.filter.maskBits = GROUND_BIT;
        body.createFixture(fdef).setUserData(this);
        shape.dispose();
    }

    private Vector2 createPositionOnTree() {
        position = new Vector2(tree.getBounds().x + (MathUtils.random(-12, 30) / PPM), tree.getBounds().y + (MathUtils.random(-200, 0) / PPM));
        return position;
    }

    public void jumpToTarget(Vector2 target, float jumptHeight) {
        float gravity = -9.81f;

        body.setTransform(createPositionOnTree(), 0);

        float displacementX = target.x - body.getPosition().x;
        float displacementY = target.y - body.getPosition().y;

        Vector2 initialVelocity = new Vector2(
                displacementX / ((float)(Math.sqrt((-2f * jumptHeight) / gravity) + Math.sqrt((2f * (displacementY - jumptHeight)) / gravity))),
                (float) (Math.sqrt(-2f * gravity * jumptHeight)));

        body.applyLinearImpulse(initialVelocity.x, initialVelocity.y, body.getPosition().x, body.getPosition().y,true);
    }

    public void update(float delta) {
        currentLife -= delta;

        if (currentLife <= 0) {
            body.setAwake(false);
            readyToDestroy = true;
        }
    }

    public void render(SpriteBatch batch) {
        float posX = (body.getPosition().x - logWidth / 2 / PPM) * PPM;
        float posY = (body.getPosition().y - logHeight / 2 / PPM) * PPM;

        batch.draw(logTex, posX, posY, logWidth, logHeight);
    }

    @Override
    public void reset() {
        currentLife = life;
        readyToDestroy = false;
        body.setAwake(false);
    }

    public boolean isReadyToDestroy() {
        return readyToDestroy;
    }

    public Body getBody() {
        return body;
    }

    public int getLogYield() {
        return logYield;
    }
}
