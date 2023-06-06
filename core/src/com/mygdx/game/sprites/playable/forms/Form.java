package com.mygdx.game.sprites.playable.forms;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.screens.PlayScreen;
import com.mygdx.game.sprites.playable.Player;

public abstract class Form extends Sprite {
    protected World world;
    protected Screen screen;
    //states
    public enum State{ CHANGING, STANDING, RUNNING, HITTING, JUMPING, FALLING}
    protected State currentState;
    protected State previousState;
    // animations
    protected TextureRegion standTexture;
    protected Animation<TextureRegion> runAnimation;
    protected Animation<TextureRegion> jumpAnimation;

    protected Animation<TextureRegion> hitAnimation;
    protected Animation<TextureRegion> changeForm;
    protected float stateTimer;
    protected boolean runningRight;
    protected boolean runChangeAnimation;
    protected Array<TextureRegion> frames;
    protected boolean destroyed;
    protected AssetManager manager;
    protected Player player;
    protected Sound walking;
    protected String type;
    protected Form(PlayScreen screen, String regionName, Player player){
        super(screen.getAtlas().findRegion(regionName));
        this.player = player;
        frames = new Array<>();
        this.screen = screen;
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;
        frames.add(new TextureRegion(getTexture(),2, 18, 27, 33));
        frames.add(new TextureRegion(getTexture(), 2, 169, 46, 30));
        frames.add(new TextureRegion(getTexture(), 281, 335, 42, 44));
        frames.add(new TextureRegion(getTexture(), 550, 222, 49, 34));
        changeForm = new Animation<>(0.3f, frames);
        frames.clear();
        destroyed = true;
        manager = new AssetManager();
        manager.load("audio/Sounds/triceStep.mp3", Sound.class);
        manager.finishLoading();
    }
    public void change(){
        runChangeAnimation = true;
    }
    public abstract void setRightFixture();
    public abstract void setLeftFixture();
    public abstract State getState();
    public abstract void define();
    protected abstract TextureRegion getFrame(float deltaTime);
    public void update(float deltaTime){
        if (!destroyed){
            setPosition(player.b2Body.getPosition().x - getWidth() / 2, player.b2Body.getPosition().y - getHeight() / 2);
            if (currentState == State.RUNNING){
                walking.resume();
            }else {
                walking.pause();
                walking.dispose();
            }
            setRegion(getFrame(deltaTime));
        }
    }
    public String getType(){
        return type;
    }
    public void hit(){
        if(player.health > 0){

        }
    }
    public void destroy(){
            destroyed = true;
            while (player.b2Body.getFixtureList().size != 0){
                player.b2Body.destroyFixture(
                        player.b2Body.getFixtureList().get(player.b2Body.getFixtureList().size-1)
                );
            }
            walking.pause();
    }
    public void dispose(){
        walking.dispose();
        manager.dispose();
    }
}
