package com.mygdx.game.Sprites.Playable.Forms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameLogic;
import com.mygdx.game.Screens.PlayScreen;

public class Ichtiozaur extends Sprite {
    public enum State{FALLING, RUNNING, CHANGING, STANDING, HITTING}
    public State currentState;
    public State previousState;
    public World world;
    public Body b2Body;

    private TextureRegion ichtiozaurStand;
    private Animation ichtiozaurRun;
    private Animation ichtiozaurJump;

    private Animation ichtiozaurHit;
    private float stateTimer;
    private boolean runningRight;
    private AssetManager manager;
    private Sound walking;
    private boolean setToExpose;
    private boolean exposed;

    public Ichtiozaur(PlayScreen screen){
        super(screen.getAtlas().findRegion("Ichtio"));
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i = 0; i < 5; i++){
            if(i <= 2) frames.add(new TextureRegion(getTexture(),265 + i * 61, 157, 58, 30));
            else if(i <= 4) frames.add(new TextureRegion(getTexture(), 265 + (i - 3) * 56, 192, 52, 30));
            else frames.add(new TextureRegion(getTexture(),375, 192, 58, 30));
        }
        ichtiozaurRun = new Animation(0.1f, frames);
        frames.clear();



        for(int i = 0; i < 7; i++){
            if(i <= 3) frames.add(new TextureRegion(getTexture(),260 + i * 60, 50, 58, 30));
            else frames.add(new TextureRegion(getTexture(), 260 + (i - 3) * 60, 100, 58, 30));
        }
        ichtiozaurHit = new Animation(0.1f, frames);
        frames.clear();

        ichtiozaurStand = new TextureRegion(getTexture(), 267, 3, 56, 29);

        defineTriceratops();
        setBounds(0, 0, 64 / GameLogic.PPM, 32 / GameLogic.PPM);
        setRegion(ichtiozaurStand);

        setToExpose = false;
        exposed = false;

    }

    public void update(float deltaTime){
        if(setToExpose && !exposed){
            world.destroyBody(b2Body);
            exposed = true;
            setBounds(getX(), getY(), 16 / GameLogic.PPM, 8 / GameLogic.PPM);
            walking.dispose();
            manager.dispose();

        }
        else if (!exposed){
            setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - getHeight() / 2);
            if (currentState == State.RUNNING){
                walking.resume();
            }else walking.pause();
            setRegion(getFrame(deltaTime));
        }
    }
    public TextureRegion getFrame(float deltaTime){
        currentState = getState();
        TextureRegion region;
        switch (currentState){
            case RUNNING:
                region = (TextureRegion) ichtiozaurRun.getKeyFrame(stateTimer, true);
                break;
            case HITTING:
                region = (TextureRegion) ichtiozaurHit.getKeyFrame(stateTimer);
                break;
            case FALLING:
            case STANDING:
            default:
                region = ichtiozaurStand;
                break;
        }
        if ((b2Body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
//            setLeftTriceFixture(b2Body);
            runningRight = false;
        }
        else if (((b2Body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX())){
            region.flip(true, false);
//            setRightTriceFixture(b2Body);
            runningRight = true;
        }
        stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
        previousState = currentState;
        return region;
    }
    public State getState(){
        if (b2Body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (b2Body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else if (Gdx.input.isTouched()){
            return State.HITTING;
        }
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
        fdefRight.filter.maskBits = GameLogic.GROUND_BIT |
                GameLogic.STONE_WALL |
                GameLogic.ENEMY_BIT |
                GameLogic.OBJECT_BIT |
                GameLogic.ENEMY_HAND_BIT;
        fdefRight.shape = shapeRight;
        if(b2Body.getFixtureList().size > 2) {
            b2Body.destroyFixture(b2Body.getFixtureList().get(0));
            b2Body.destroyFixture(b2Body.getFixtureList().get(1));
        }
        b2Body.createFixture(fdefRight);


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
        fdefLeft.filter.maskBits = GameLogic.GROUND_BIT |
                GameLogic.STONE_WALL |
                GameLogic.ENEMY_BIT |
                GameLogic.OBJECT_BIT |
                GameLogic.ENEMY_HAND_BIT;
        fdefLeft.shape = shapeLeft;
        b2Body.createFixture(fdefLeft);

    }
    public void defineTriceratops(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(0 / GameLogic.PPM, 32 / GameLogic.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bdef);
        manager = new AssetManager();
        manager.load("audio/Sounds/triceStep.mp3", Sound.class);
        manager.finishLoading();
        walking = manager.get("audio/Sounds/triceStep.mp3", Sound.class);
        walking.play(0.5f, 10, 10);
        walking.loop();
//        setRightTriceFixture(b2Body);
        CircleShape shape = new CircleShape();
        shape.setRadius(7 / GameLogic.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.filter.categoryBits = GameLogic.PLAYER_BIT;
        fdef.filter.maskBits = GameLogic.GROUND_BIT |
                GameLogic.STONE_WALL |
                GameLogic.ENEMY_BIT |
                GameLogic.OBJECT_BIT |
                GameLogic.ENEMY_HAND_BIT;
        fdef.shape = shape;
        b2Body.createFixture(fdef);
    }
    public void expose(){
        setToExpose = true;
    }
}
