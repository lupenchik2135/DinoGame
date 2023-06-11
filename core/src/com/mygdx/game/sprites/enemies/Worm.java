package com.mygdx.game.sprites.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;

public class Worm extends Enemy {
    private final Animation<TextureRegion> crawlAnimation;
    float yPosition;

    public Worm(Level screen, float x, float y) {
        super(screen, "Mobs", x, y);
        this.screen = screen;
        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(getTexture(), 3999, 42, 83, 34));
        frames.add(new TextureRegion(getTexture(), 3999 + 103, 41, 84, 33));
        frames.add(new TextureRegion(getTexture(), 3999 + 103 + 104, 43, 84, 30));
        frames.add(new TextureRegion(getTexture(), 3999 + 103 + 104 + 104, 42, 83, 34));
        crawlAnimation = new Animation<>(0.15f, frames);
        frames.clear();
        stateTime = 0;
        setBounds(getX(), getY(), 400 / GameLogic.PPM, 312 / GameLogic.PPM);
        currentState = previousState = State.RUNNING;
        setToDestroy = false;
        destroyed = false;
        health = 100;
        yPosition = getY();

    }

    @Override
    public void draw(Batch batch) {
        if (!destroyed || stateTime < 1) {
            super.draw(batch);
        }
    }

    @Override
    protected void defineEnemy() {

        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(200 / GameLogic.PPM, 156 / GameLogic.PPM);
        fdef.filter.categoryBits = GameLogic.ENEMY_BIT;
        fdef.filter.maskBits = GameLogic.PLAYER_BIT |
                GameLogic.TYRANNOSAUR_BIT;
        fdef.shape = shape;
        b2Body.createFixture(fdef).setUserData(this);
        b2Body.setGravityScale(0);
    }

    @Override
    public void update(float deltaTime) {
        if (setToDestroy && !destroyed) {
            destroyed = true;
            setBounds(getX(), getY(), 400 / GameLogic.PPM, 312 / GameLogic.PPM);
            stateTime = 0;
            world.destroyBody(b2Body);
        } else if (!destroyed) {
            setRegion(crawlAnimation.getKeyFrame(stateTime));
            stateTime += deltaTime;
            if (crawlAnimation.isAnimationFinished(stateTime)) {
                stateTime = 0;
            }
            setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - getHeight() / 2);
            if (b2Body.isActive() && b2Body.getLinearVelocity().x > -0.5f) {
                b2Body.applyLinearImpulse(new Vector2(-0.5f, 0), b2Body.getWorldCenter(), true);
            }
            if (b2Body.getPosition().y > yPosition) {
                b2Body.applyLinearImpulse(new Vector2(0, -1.1f), b2Body.getWorldCenter(), true);
            } else if (b2Body.getPosition().y < yPosition) {
                b2Body.applyLinearImpulse(new Vector2(0, 1.1f), b2Body.getWorldCenter(), true);
            }


        }
    }

    public boolean isWormDead() {
        return destroyed;
    }
}
