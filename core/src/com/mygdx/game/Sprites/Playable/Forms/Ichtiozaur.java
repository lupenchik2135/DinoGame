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

public class Ichtiozaur extends Form {
    private TextureRegion ichtiozaurStand;
    private Animation ichtiozaurRun;
    private Animation ichtiozaurJump;

    private Animation ichtiozaurHit;
    private AssetManager manager;
    private Sound walking;
    private boolean exposed;

    public Ichtiozaur(PlayScreen screen, Body b2Body){
        super(screen, "Ichtio", b2Body);

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
        exposed = true;

    }

    public void update(float deltaTime){
        if (!exposed){
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
            runningRight = false;
        }
        else if (((b2Body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX())){
            region.flip(true, false);
            runningRight = true;
        }
        stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
        previousState = currentState;
        return region;
    }
    public void setRightFixture(){
        if (b2Body.getFixtureList().size >= 2){
            while (b2Body.getFixtureList().size > 1) {
                b2Body.destroyFixture(b2Body.getFixtureList().get(1));
            }
        }
        FixtureDef fdefRight = new FixtureDef();
        fdefRight.filter.categoryBits = GameLogic.PLAYER_BIT;
        fdefRight.filter.maskBits = GameLogic.GROUND_BIT |
                GameLogic.STONE_WALL |
                GameLogic.ENEMY_BIT |
                GameLogic.OBJECT_BIT |
                GameLogic.ENEMY_HAND_BIT;

        EdgeShape head = new EdgeShape();
        head.set(32 / GameLogic.PPM, 0 / GameLogic.PPM, 32 / GameLogic.PPM, 9 / GameLogic.PPM);
        fdefRight.shape = head;
        fdefRight.isSensor = true;
        b2Body.createFixture(fdefRight).setUserData("mouth");
    }
    public void setLeftFixture(){
        if (b2Body.getFixtureList().size >= 2){
            while (b2Body.getFixtureList().size > 1) {
                b2Body.destroyFixture(b2Body.getFixtureList().get(1));
            }
        }
        FixtureDef fdefLeft = new FixtureDef();
        fdefLeft.filter.categoryBits = GameLogic.PLAYER_BIT;
        fdefLeft.filter.maskBits = GameLogic.GROUND_BIT |
                GameLogic.STONE_WALL |
                GameLogic.ENEMY_BIT |
                GameLogic.OBJECT_BIT |
                GameLogic.ENEMY_HAND_BIT;
        EdgeShape head = new EdgeShape();
        head.set(-32 / GameLogic.PPM, 0 / GameLogic.PPM, -32 / GameLogic.PPM, 9 / GameLogic.PPM);
        fdefLeft.shape = head;
        fdefLeft.isSensor = true;
        b2Body.createFixture(fdefLeft).setUserData("mouth");
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
    public void define(){
        manager = new AssetManager();
        manager.load("audio/Sounds/triceStep.mp3", Sound.class);
        manager.finishLoading();
        walking = manager.get("audio/Sounds/triceStep.mp3", Sound.class);
        walking.play(0.5f, 10, 10);
        walking.loop();
        setBounds(0, 0, 64 / GameLogic.PPM, 32 / GameLogic.PPM);
        setRegion(ichtiozaurStand);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(32 / GameLogic.PPM, 12 / GameLogic.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.filter.categoryBits = GameLogic.PLAYER_BIT;
        fdef.filter.maskBits = GameLogic.GROUND_BIT |
                GameLogic.STONE_WALL |
                GameLogic.ENEMY_BIT |
                GameLogic.OBJECT_BIT |
                GameLogic.ENEMY_HAND_BIT;
        fdef.shape = shape;
        if (b2Body != null){
            this.b2Body.createFixture(fdef);
        }
        setRightFixture();
        exposed = false;
    }
    public void expose(){
        exposed = true;
        while (b2Body.getFixtureList().size != 0){
            b2Body.destroyFixture(b2Body.getFixtureList().get(b2Body.getFixtureList().size-1));
        }
        walking.pause();
        walking.dispose();
    }
}
