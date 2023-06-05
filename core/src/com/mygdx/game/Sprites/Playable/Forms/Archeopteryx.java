package com.mygdx.game.Sprites.Playable.Forms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameLogic;
import com.mygdx.game.Screens.PlayScreen;

public class Archeopteryx extends Form{
    private TextureRegion archeopteryxStand;
    private Animation archeopteryxRun;
    private Animation archeopteryxJump;

    private Animation archeopteryxHit;
    private AssetManager manager;
    private Sound walking;
    private boolean setToExpose;
    private boolean exposed;

    public Archeopteryx(PlayScreen screen, Body b2Body){
        super(screen, "Archeopterix", b2Body);

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 0; i <= 5; i ++){
            if (i <= 2) frames.add(new TextureRegion(getTexture(), 5 + (i * 64), 437, 60, 30));
            else frames.add(new TextureRegion(getTexture(), 5 + ((i - 3) * 64), 472, 60, 30));
        }
        archeopteryxRun = new Animation(0.1f, frames);
        frames.clear();



        for(int i = 0; i < 7; i++){
            if(i <= 3) frames.add(new TextureRegion(getTexture(),5 + i * 51, 191, 53, 25));
            else frames.add(new TextureRegion(getTexture(), 5 + (i - 3) * 51, 225, 51, 30));
        }
        archeopteryxHit = new Animation(0.1f, frames);
        frames.clear();

        archeopteryxStand = new TextureRegion(getTexture(), 5, 3, 63, 30);
        setToExpose = false;
        exposed = true;

    }

    public void update(float deltaTime){
        if(setToExpose && !exposed){

        }
        else if (!exposed){
            setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - getHeight() / 2);
            if (currentState == Form.State.RUNNING){
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
                region = (TextureRegion) archeopteryxRun.getKeyFrame(stateTimer, true);
                break;
            case HITTING:
                region = (TextureRegion) archeopteryxHit.getKeyFrame(stateTimer);
                break;
            case FALLING:
            case STANDING:
            default:
                region = archeopteryxStand;
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
    public Form.State getState(){
        if (b2Body.getLinearVelocity().y < 0)
            return Form.State.FALLING;
        else if (b2Body.getLinearVelocity().x != 0)
            return Form.State.RUNNING;
        else if (Gdx.input.isTouched()){
            return Form.State.HITTING;
        }
        else
            return Form.State.STANDING;
    }
    public void setRightTriceFixture(){
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(6 / GameLogic.PPM, 6 / GameLogic.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.filter.categoryBits = GameLogic.PLAYER_BIT;
        fdef.filter.maskBits = GameLogic.GROUND_BIT |
                GameLogic.STONE_WALL |
                GameLogic.ENEMY_BIT |
                GameLogic.OBJECT_BIT |
                GameLogic.ENEMY_HAND_BIT;
        fdef.shape = shape;
        if(b2Body.getFixtureList().size > 2) {
            b2Body.destroyFixture(b2Body.getFixtureList().get(0));
            b2Body.destroyFixture(b2Body.getFixtureList().get(1));
        }
        this.b2Body.createFixture(fdef);
//        ChainShape shapeRight = new ChainShape();
//        Vector2[] vectors = new Vector2[11];
//        vectors[0] = new Vector2(32 / GameLogic.PPM, 3 / GameLogic.PPM);
//        vectors[1] = new Vector2(32 / GameLogic.PPM, 9 / GameLogic.PPM);
//        vectors[2] = new Vector2(12 / GameLogic.PPM, 16 / GameLogic.PPM);
//        vectors[3] = new Vector2(7 / GameLogic.PPM, 15 / GameLogic.PPM);
//        vectors[4] = new Vector2(-12 / GameLogic.PPM, 6 / GameLogic.PPM);
//        vectors[5] = new Vector2(-29 / GameLogic.PPM, -2 / GameLogic.PPM);
//        vectors[6] = new Vector2(-29 / GameLogic.PPM, -3 / GameLogic.PPM);
//        vectors[7] = new Vector2(-24 / GameLogic.PPM, -5 / GameLogic.PPM);
//        vectors[8] = new Vector2(-14 / GameLogic.PPM, -12 / GameLogic.PPM);
//        vectors[9] = new Vector2(17 / GameLogic.PPM, -12 / GameLogic.PPM);
//        vectors[10] = new Vector2(28 / GameLogic.PPM, -4 / GameLogic.PPM);
//        shapeRight.createLoop(vectors);
//
//        FixtureDef fdefRight = new FixtureDef();
//        fdefRight.filter.categoryBits = GameLogic.PLAYER_BIT;
//        fdefRight.filter.maskBits = GameLogic.GROUND_BIT |
//                GameLogic.STONE_WALL |
//                GameLogic.ENEMY_BIT |
//                GameLogic.OBJECT_BIT |
//                GameLogic.ENEMY_HAND_BIT;
//        fdefRight.shape = shapeRight;
//        if(b2Body.getFixtureList().size > 2) {
//            b2Body.destroyFixture(b2Body.getFixtureList().get(0));
//            b2Body.destroyFixture(b2Body.getFixtureList().get(1));
//        }
//        b2Body.createFixture(fdefRight);


    }
    public void setLeftTriceFixture(){
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
        if (b2Body != null){
            this.b2Body.createFixture(fdef);
        }
//        ChainShape shapeLeft = new ChainShape();
//        Vector2[] vectors = new Vector2[11];
//        vectors[0] = new Vector2(-32 / GameLogic.PPM, 3 / GameLogic.PPM);
//        vectors[1] = new Vector2(-32 / GameLogic.PPM, 9 / GameLogic.PPM);
//        vectors[2] = new Vector2(-12 / GameLogic.PPM, 16 / GameLogic.PPM);
//        vectors[3] = new Vector2(-7 / GameLogic.PPM, 15 / GameLogic.PPM);
//        vectors[4] = new Vector2(13 / GameLogic.PPM, 6 / GameLogic.PPM);
//        vectors[5] = new Vector2(29 / GameLogic.PPM, -2 / GameLogic.PPM);
//        vectors[6] = new Vector2(29 / GameLogic.PPM, -3 / GameLogic.PPM);
//        vectors[7] = new Vector2(24 / GameLogic.PPM, -7 / GameLogic.PPM);
//        vectors[8] = new Vector2(14 / GameLogic.PPM, -12 / GameLogic.PPM);
//        vectors[9] = new Vector2(-17 / GameLogic.PPM, -12 / GameLogic.PPM);
//        vectors[10] = new Vector2(-28 / GameLogic.PPM, -4 / GameLogic.PPM);
//        shapeLeft.createLoop(vectors);
//        if(b2Body.getFixtureList().size > 2) {
//            b2Body.destroyFixture(b2Body.getFixtureList().get(0));
//            b2Body.destroyFixture(b2Body.getFixtureList().get(1));
//        }
//        FixtureDef fdefLeft = new FixtureDef();
//        fdefLeft.filter.categoryBits = GameLogic.PLAYER_BIT;
//        fdefLeft.filter.maskBits = GameLogic.GROUND_BIT |
//                GameLogic.STONE_WALL |
//                GameLogic.ENEMY_BIT |
//                GameLogic.OBJECT_BIT |
//                GameLogic.ENEMY_HAND_BIT;
//        fdefLeft.shape = shapeLeft;
//        b2Body.createFixture(fdefLeft);

    }
    public void define(){
        manager = new AssetManager();
        manager.load("audio/Sounds/triceStep.mp3", Sound.class);
        manager.finishLoading();
        walking = manager.get("audio/Sounds/triceStep.mp3", Sound.class);
        walking.play(0.5f, 10, 10);
        walking.loop();
        setBounds(0, 0, 16 / GameLogic.PPM, 16 / GameLogic.PPM);
        setRegion(archeopteryxStand);
        setRightTriceFixture();
        setToExpose = false;
        exposed = false;
    }
    public void expose(){
        exposed = true;
        while (b2Body.getFixtureList().size != 0){
            b2Body.destroyFixture(b2Body.getFixtureList().get(b2Body.getFixtureList().size-1));
        }
    }
}
