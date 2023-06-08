package com.mygdx.game.sprites.playable.forms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;
import com.mygdx.game.screens.LevelOne;
import com.mygdx.game.sprites.playable.Player;

public abstract class Form extends Sprite {
    protected World world;
    protected Level screen;

    //states
    public enum State {CHANGING, STANDING, RUNNING, HITTING, JUMPING, FALLING, FLYING, SWIMMING, DEAD}

    protected State currentState;
    protected State previousState;
    // animations
    protected TextureRegion standTexture;
    protected Animation<TextureRegion> runAnimation;
    protected Animation<TextureRegion> jumpAnimation;
    protected Animation<TextureRegion> deadAnimation;

    protected Animation<TextureRegion> hitAnimation;
    protected Animation<TextureRegion> changeForm;
    protected float stateTimer;
    protected boolean runningRight;
    protected boolean runChangeAnimation;
    protected Array<TextureRegion> frames;
    protected boolean destroyed;
    protected AssetManager manager;
    protected Player player;
    protected float velocityX;
    protected float jumpHeight;
    protected Sound walking;
    protected String type;

    protected boolean timeToDefineForm;

    protected int currentFormHealth;
    protected boolean isDead;
    protected boolean isHitting;
    protected int damage;




    protected Form(Level screen, String regionName, Player player) {
        super(screen.getAtlas().findRegion(regionName));
        this.player = player;
        frames = new Array<>();
        this.screen = screen;
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;
        frames.add(new TextureRegion(getTexture(), 62, 82, 17, 16));
        frames.add(new TextureRegion(getTexture(), 62, 82, 12, 10));
        changeForm = new Animation<>(0.3f, frames);
        frames.clear();
        destroyed = true;
        timeToDefineForm = false;
        isHitting = false;
        manager = new AssetManager();
        manager.load("audio/Sounds/triceStep.mp3", Sound.class);
        manager.finishLoading();
    }

    public void change() {
        runChangeAnimation = true;
        timeToDefineForm = true;
        // balance health with each form
        int healthmath = player.getHealth() - player.getLostHealth() >= currentFormHealth ? currentFormHealth : currentFormHealth - player.getLostHealth();
        // check on 0 hp
        player.setHealth(healthmath == 0 ? 1 : healthmath);
    }
    public boolean die(){
        isDead = true;
        Filter filter = new Filter();
        filter.maskBits = GameLogic.GROUND_BIT;
        for (Fixture fixture: player.b2Body.getFixtureList()){
            fixture.setFilterData(filter);
        }
        player.b2Body.applyLinearImpulse(new Vector2(0, 4f), player.b2Body.getWorldCenter(), true);
        return isDead;
    }

    public abstract State getState();

    public abstract void define();

    protected abstract TextureRegion getFrame(float deltaTime);

    public void update(float deltaTime) {
        if (!destroyed) {
            setPosition(player.b2Body.getPosition().x - getWidth() / 2, player.b2Body.getPosition().y - getHeight() / 2);
            if (currentState == State.RUNNING) {
                walking.resume();
            } else {
                walking.pause();
                walking.dispose();
            }
            setRegion(getFrame(deltaTime));
        }
        if (timeToDefineForm) {
            this.define();
            timeToDefineForm = false;
        }
        if(this.getClass().isAssignableFrom(Archeopteryx.class) && Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            ((Archeopteryx) this).fly();
        }
    }

    public String getType() {
        return type;
    }
    public int getDamage() {
        return damage;
    }

    public boolean isChanging() {
        return runChangeAnimation;
    }

    public int getCurrentFormHealth() {
        return currentFormHealth;
    }

    public float getStateTimer() {
        return stateTimer;
    }

    public void destroy() {
        destroyed = true;
        while (player.b2Body.getFixtureList().size != 0) {
            player.b2Body.destroyFixture(
                    player.b2Body.getFixtureList().get(player.b2Body.getFixtureList().size - 1)
            );
        }
        walking.pause();
    }

    public float getVelocityX(){
        return velocityX;
    }
    public float getJumpHeight(){
        return jumpHeight;
    }
    public void dispose() {
        if (walking != null) {
            walking.dispose();
        }
        manager.dispose();
    }
}
