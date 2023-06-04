package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameLogic;
import com.mygdx.game.Screens.PlayScreen;



public class Triceratops extends Sprite {
    public enum State{FALLING, JUMPING, RUNNING, CHANGING, STANDING, HITTING}
    public State currentState;
    public State previousState;
    public World world;
    public Body b2Body;

    private TextureRegion triceratopsStand;
    private Animation triceratopsRun;
    private Animation triceratopsJump;

    private Animation triceratopsHit;
    private float stateTimer;
    private boolean runningRight;

    public Triceratops(World world, PlayScreen screen){
        super(screen.getAtlas().findRegion("Triceratops"));
        this.world = world;
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i = 0; i < 7; i++){
            if(i <= 3) frames.add(new TextureRegion(getTexture(),525 + i * 60, 50, 58, 48));
            else frames.add(new TextureRegion(getTexture(), 525 + (i - 3) * 60, 100, 58, 48));
        }
        triceratopsRun = new Animation(0.1f, frames);
        frames.clear();

        for (int i = 0; i < 3; i ++){
            frames.add(new TextureRegion(getTexture(), 522 + i * 60, 365, 52, 48));
        }
        triceratopsJump = new Animation(0.1f, frames);
        frames.clear();

        for(int i = 0; i < 7; i++){
            if(i <= 3) frames.add(new TextureRegion(getTexture(),525 + i * 60, 50, 58, 48));
            else frames.add(new TextureRegion(getTexture(), 525 + (i - 3) * 60, 100, 58, 48));
        }
        triceratopsHit = new Animation(0.1f, frames);
        frames.clear();

        triceratopsStand = new TextureRegion(getTexture(), 522, 307, 58, 46);

        defineTriceratops();
        setBounds(0, 0, 64 / GameLogic.PPM, 32 / GameLogic.PPM);
        setRegion(triceratopsStand);
    }

    public void update(float deltaTime){
        setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(deltaTime));
    }
    public TextureRegion getFrame(float deltaTime){
        currentState = getState();
        TextureRegion region;
        switch (currentState){
            case JUMPING:
                region = (TextureRegion) triceratopsJump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = (TextureRegion) triceratopsRun.getKeyFrame(stateTimer, true);
                break;
            case HITTING:
                region = (TextureRegion) triceratopsHit.getKeyFrame(stateTimer);
                break;
            case FALLING:
            case STANDING:
            default:
                region = triceratopsStand;
                break;
        }
        if ((b2Body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            setLeftTriceFixture(b2Body);
            runningRight = false;
        }
        else if (((b2Body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX())){
            region.flip(true, false);
            setRightTriceFixture(b2Body);
            runningRight = true;
        }
        stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
        previousState = currentState;
        return region;
    }
    public State getState(){
        if (b2Body.getLinearVelocity().y > 0 || (b2Body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
                return State.JUMPING;
        else if (b2Body.getLinearVelocity().y < 0)
                return State.FALLING;
        else if (b2Body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }
    public void setRightTriceFixture(Body b2Body){
        ChainShape shapeRight = new ChainShape();
        Vector2[] vectors = new Vector2[11];
        vectors[0] = new Vector2(32 / GameLogic.PPM, 3 / GameLogic.PPM);
        vectors[1] = new Vector2(32 / GameLogic.PPM, 9 / GameLogic.PPM);
        vectors[2] = new Vector2(12 / GameLogic.PPM, 16 / GameLogic.PPM);
        vectors[3] = new Vector2(7 / GameLogic.PPM, 15 / GameLogic.PPM);
        vectors[4] = new Vector2(-12 / GameLogic.PPM, 6 / GameLogic.PPM);
        vectors[5] = new Vector2(-29 / GameLogic.PPM, -2 / GameLogic.PPM);
        vectors[6] = new Vector2(-29 / GameLogic.PPM, -3 / GameLogic.PPM);
        vectors[7] = new Vector2(-24 / GameLogic.PPM, -5 / GameLogic.PPM);
        vectors[8] = new Vector2(-14 / GameLogic.PPM, -12 / GameLogic.PPM);
        vectors[9] = new Vector2(17 / GameLogic.PPM, -12 / GameLogic.PPM);
        vectors[10] = new Vector2(28 / GameLogic.PPM, -4 / GameLogic.PPM);
        shapeRight.createLoop(vectors);


        FixtureDef fdefRight = new FixtureDef();
        fdefRight.filter.categoryBits = GameLogic.PLAYER_BIT;
        fdefRight.filter.maskBits = GameLogic.DEFAULT_BIT | GameLogic.STONE_WALL;
        fdefRight.shape = shapeRight;
        if(b2Body.getFixtureList().size > 2) {
            b2Body.destroyFixture(b2Body.getFixtureList().get(0));
            b2Body.destroyFixture(b2Body.getFixtureList().get(1));
        }
        b2Body.createFixture(fdefRight);

        EdgeShape head = new EdgeShape();
        head.set(32 / GameLogic.PPM, 0 / GameLogic.PPM, 32 / GameLogic.PPM, 9 / GameLogic.PPM);
        fdefRight.shape = head;
        fdefRight.isSensor = true;
        b2Body.createFixture(fdefRight).setUserData("head");


    }
    public void setLeftTriceFixture(Body b2Body){
        ChainShape shapeLeft = new ChainShape();
        Vector2[] vectors = new Vector2[11];
        vectors[0] = new Vector2(-32 / GameLogic.PPM, 3 / GameLogic.PPM);
        vectors[1] = new Vector2(-32 / GameLogic.PPM, 9 / GameLogic.PPM);
        vectors[2] = new Vector2(-12 / GameLogic.PPM, 16 / GameLogic.PPM);
        vectors[3] = new Vector2(-7 / GameLogic.PPM, 15 / GameLogic.PPM);
        vectors[4] = new Vector2(13 / GameLogic.PPM, 6 / GameLogic.PPM);
        vectors[5] = new Vector2(29 / GameLogic.PPM, -2 / GameLogic.PPM);
        vectors[6] = new Vector2(29 / GameLogic.PPM, -3 / GameLogic.PPM);
        vectors[7] = new Vector2(24 / GameLogic.PPM, -7 / GameLogic.PPM);
        vectors[8] = new Vector2(14 / GameLogic.PPM, -12 / GameLogic.PPM);
        vectors[9] = new Vector2(-17 / GameLogic.PPM, -12 / GameLogic.PPM);
        vectors[10] = new Vector2(-28 / GameLogic.PPM, -4 / GameLogic.PPM);
        shapeLeft.createLoop(vectors);
        if(b2Body.getFixtureList().size > 2) {
            b2Body.destroyFixture(b2Body.getFixtureList().get(0));
            b2Body.destroyFixture(b2Body.getFixtureList().get(1));
        }
        FixtureDef fdefLeft = new FixtureDef();
        fdefLeft.filter.categoryBits = GameLogic.PLAYER_BIT;
        fdefLeft.filter.maskBits = GameLogic.DEFAULT_BIT | GameLogic.STONE_WALL;
        fdefLeft.shape = shapeLeft;
        b2Body.createFixture(fdefLeft);

        EdgeShape head = new EdgeShape();
        head.set(-32 / GameLogic.PPM, 0 / GameLogic.PPM, -32 / GameLogic.PPM, 9 / GameLogic.PPM);
        fdefLeft.shape = head;
        fdefLeft.isSensor = true;
        b2Body.createFixture(fdefLeft).setUserData("head");
    }
    public void defineTriceratops(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(64 / GameLogic.PPM,32 / GameLogic.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bdef);

        setRightTriceFixture(b2Body);


    }
}
