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
import com.mygdx.game.sprites.items.Heart;
import com.mygdx.game.sprites.objects.ObjectDef;
import com.mygdx.game.sprites.projectiles.Arrow;

public class DistantEnemy extends Enemy {
    private Animation<TextureRegion> walkAnimation;

    private boolean runningRight;

    public DistantEnemy(Level screen, float x, float y) {
        super(screen, "Mobs", x, y);
        this.screen = screen;
        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(getTexture(), 3757 + 30 + 30 + 28 + 30, 62, 20, 14));
        frames.add(new TextureRegion(getTexture(), 3757 + 30 + 30 + 28 + 30 + 30, 62, 20, 14));
        frames.add(new TextureRegion(getTexture(), 3757 + 30 + 30 + 28 + 30 + 30 + 30, 62, 18, 15));
        frames.add(new TextureRegion(getTexture(), 3757 + 30 + 30 + 28 + 30 + 30 + 30 + 28, 62, 20, 19));
        walkAnimation = new Animation<>(0.1f, frames);
        frames.clear();
        stateTime = 0;
        setBounds(getX(), getY(), 16 / GameLogic.PPM, 16 / GameLogic.PPM);
        currentState = previousState = State.RUNNING;
        setToDestroy = false;
        destroyed = false;
        health = 10;
    }

    public State getState() {
        if (isHitting) {
            return State.HITTING;
        }
        return State.RUNNING;
    }

    public TextureRegion getFrame(float deltaTime) {
        currentState = getState();
        TextureRegion region;
        switch (currentState) {
            case HITTING:
                region = walkAnimation.getKeyFrame(stateTime, true);
                if (runningRight) {
                    screen.spawnObject(new ObjectDef(new Vector2(b2Body.getPosition().x + 16 / GameLogic.PPM, b2Body.getPosition().y),
                            Arrow.class, runningRight));
                    coolDown = 150;
                    isHitting = false;
                } else {
                    screen.spawnObject(new ObjectDef(new Vector2(b2Body.getPosition().x - 16 / GameLogic.PPM, b2Body.getPosition().y),
                            Arrow.class, runningRight));
                    coolDown = 150;
                    isHitting = false;
                }
                break;
            case RUNNING:
            default:
                region = walkAnimation.getKeyFrame(stateTime, true);
                break;
        }
        if ((b2Body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        } else if (((b2Body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX())) {
            region.flip(true, false);
            runningRight = true;
        }
        stateTime = currentState == previousState ? stateTime + deltaTime : 0;
        previousState = currentState;
        return region;
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(8 / GameLogic.PPM, 8 / GameLogic.PPM);
        fdef.filter.categoryBits = GameLogic.ENEMY_BIT;
        fdef.filter.maskBits = GameLogic.GROUND_BIT |
                GameLogic.STONE_WALL |
                GameLogic.ENEMY_BIT |
                GameLogic.SMALL_ENEMY_BIT |
                GameLogic.PLAYER_ATTACK_BIT |
                GameLogic.PLAYER_BIT |
                GameLogic.TYRANNOSAUR_BIT |
                GameLogic.PROJECTILE_BIT |
                GameLogic.ENEMY_STOPPER;
        fdef.shape = shape;
        b2Body.createFixture(fdef).setUserData(this);

    }

    @Override
    public void draw(Batch batch) {
        if (!destroyed || stateTime < 1) {
            super.draw(batch);
        }
    }

    @Override
    public void update(float deltaTime) {
        if (setToDestroy && !destroyed) {
            destroyed = true;
            setBounds(getX(), getY(), 16 / GameLogic.PPM, 8 / GameLogic.PPM);
            stateTime = 0;
            screen.spawnObject(new ObjectDef(new Vector2(b2Body.getPosition().x + 16 / GameLogic.PPM, b2Body.getPosition().y),
                    Heart.class, runningRight));
            world.destroyBody(b2Body);
        } else if (!destroyed) {
            setRegion(getFrame(deltaTime));
            setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - getHeight() / 2);
            b2Body.setLinearVelocity(velocity);
        } else stateTime += deltaTime;
    }


}
