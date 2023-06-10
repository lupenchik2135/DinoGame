package com.mygdx.game.sprites.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;
import com.mygdx.game.sprites.items.Heart;
import com.mygdx.game.sprites.objects.ObjectDef;

public class MillyEnemy extends Enemy{
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> hitAnimation;

    private boolean runningRight;

    public MillyEnemy(Level screen, float x, float y) {
        super(screen, "Mobs", x, y);
        this.screen = screen;
        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(getTexture(), 4537 + 71 + 82 + 77, 57, 37, 19));
        frames.add(new TextureRegion(getTexture(), 4537 + 71 + 82 + 77 + 57, 57, 38, 19));
        frames.add(new TextureRegion(getTexture(), 4537 + 71 + 82 + 77 + 57 + 58, 56, 38, 20));
        frames.add(new TextureRegion(getTexture(), 4537 + 71 + 82 + 77 + 57 + 58 + 58, 56, 42, 19));
        frames.add(new TextureRegion(getTexture(), 4537 + 71 + 82 + 77 + 57 + 58 + 58 + 62, 56, 37, 20));
        walkAnimation = new Animation<>(0.1f, frames);
        frames.clear();
        frames.add(new TextureRegion(getTexture(), 4537, 63, 51, 14));
        frames.add(new TextureRegion(getTexture(), 4537 + 71, 68, 62, 8));
        frames.add(new TextureRegion(getTexture(), 4537 + 71 + 82, 56, 57, 19));
        hitAnimation = new Animation<>(0.3f, frames);
        frames.clear();
        stateTime = 0;
        setBounds(getX(), getY(), 16 / GameLogic.PPM, 16 / GameLogic.PPM);
        currentState = previousState = State.RUNNING;
        setToDestroy = false;
        destroyed = false;
        health = 10;
    }
    public State getState(){
        if (isHitting){
            return State.HITTING;
        }
        return State.RUNNING;
    }
    public TextureRegion getFrame(float deltaTime){
        currentState = getState();
        TextureRegion region;
        switch (currentState){
            case HITTING:
                if(runningRight){
                    FixtureDef fdefRight = new FixtureDef();
                    fdefRight.filter.categoryBits = GameLogic.ENEMY_ATTACK_BIT;
                    EdgeShape head = new EdgeShape();
                    head.set(9f / GameLogic.PPM, 0 / GameLogic.PPM, 9f / GameLogic.PPM, 9 / GameLogic.PPM);
                    fdefRight.shape = head;
                    fdefRight.isSensor = true;
                    b2Body.createFixture(fdefRight).setUserData(this);
                }else{
                    FixtureDef fdefLeft = new FixtureDef();
                    fdefLeft.filter.categoryBits = GameLogic.ENEMY_ATTACK_BIT;
                    EdgeShape head = new EdgeShape();
                    head.set(-9f / GameLogic.PPM, 0 / GameLogic.PPM, -9f / GameLogic.PPM, 9 / GameLogic.PPM);
                    fdefLeft.shape = head;
                    fdefLeft.isSensor = true;
                    b2Body.createFixture(fdefLeft).setUserData(this);

                }
                region = hitAnimation.getKeyFrame(stateTime);
                if(hitAnimation.isAnimationFinished(stateTime) && b2Body.getFixtureList().size >= 2){
                    Gdx.app.log("meele", "attack  " + stateTime);
                    coolDown = 150;
                    stateTime = 0;
                    while (b2Body.getFixtureList().size > 1) {
                        b2Body.destroyFixture(b2Body.getFixtureList().get(1));
                    }
                    isHitting = false;
                }
                break;
            case RUNNING:
            default:
                region = walkAnimation.getKeyFrame(stateTime, true);
                break;
        }
        if ((b2Body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }
        else if (((b2Body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX())){
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
                GameLogic.PROJECTILE_BIT |
                GameLogic.PLAYER_ATTACK_BIT |
                GameLogic.PLAYER_BIT |
                GameLogic.TYRANNOSAUR_BIT |
                GameLogic.ENEMY_STOPPER;
        fdef.shape = shape;
        b2Body.createFixture(fdef).setUserData(this);

    }
    @Override
    public void draw(Batch batch){
        if(!destroyed||stateTime<1 ){
            super.draw(batch);
        }
    }

    @Override
    public void update(float deltaTime) {
        if(setToDestroy && !destroyed){
            destroyed = true;
            setBounds(getX(), getY(), 16 / GameLogic.PPM, 8 / GameLogic.PPM);
            stateTime = 0;
            screen.spawnObject(new ObjectDef(new Vector2(b2Body.getPosition().x+ 16 / GameLogic.PPM, b2Body.getPosition().y),
                    Heart.class, runningRight));
            world.destroyBody(b2Body);
        }
        else if (!destroyed){
            setRegion(getFrame(deltaTime));
            setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - getHeight() / 2);
            b2Body.setLinearVelocity(velocity);
        }
        else stateTime += deltaTime;
    }


}
