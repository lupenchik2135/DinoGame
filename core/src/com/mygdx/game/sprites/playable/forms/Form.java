package com.mygdx.game.sprites.playable.forms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;
import com.mygdx.game.sprites.playable.Player;

import java.util.Objects;

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
    protected TextureRegion changeForm;
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
    protected int coolDown;
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
        changeForm = new TextureRegion(getTexture(), 23, 2, 16, 16);
        frames.clear();
        destroyed = true;
        timeToDefineForm = false;
        isHitting = false;
        manager = new AssetManager();
        manager.load("audio/Sounds/triceStep.mp3", Sound.class);
        manager.load("audio/Sounds/TRexStep.mp3", Sound.class);
        manager.finishLoading();
    }

    public void change() {
        runChangeAnimation = true;
        timeToDefineForm = true;
        // balance health with each form
        int healthmath = player.getHealth() - player.getLostHealth() >= currentFormHealth ? currentFormHealth : currentFormHealth - player.getLostHealth();
        // check on 0 hp
        player.setHealth(healthmath <= 0 ? 1 : healthmath);
    }

    public boolean die() {
        isDead = true;
        Filter filter = new Filter();
        filter.maskBits = GameLogic.GROUND_BIT;
        for (Fixture fixture : player.b2Body.getFixtureList()) {
            fixture.setFilterData(filter);
        }
        player.b2Body.applyLinearImpulse(new Vector2(0, 4f), player.b2Body.getWorldCenter(), true);
        return isDead;
    }

    public abstract State getState();

    public abstract void define();

    protected abstract TextureRegion getFrame(float deltaTime);

    protected void setAttackFixture(float x, float y) {
        FixtureDef fdef = new FixtureDef();
        fdef.filter.categoryBits = GameLogic.PLAYER_ATTACK_BIT;
        EdgeShape head = new EdgeShape();
        head.set(x / GameLogic.PPM, y / GameLogic.PPM, x / GameLogic.PPM, -y / GameLogic.PPM);
        fdef.shape = head;
        fdef.isSensor = true;
        player.b2Body.createFixture(fdef).setUserData(player);
    }

    public void update(float deltaTime) {
        if (!destroyed) {
            setPosition(player.b2Body.getPosition().x - getWidth() / 2, player.b2Body.getPosition().y - getHeight() / 2);
            if (walking != null) {
                if (currentState == State.RUNNING) {
                    walking.resume();
                } else {
                    walking.pause();
                    walking.dispose();
                }
            }
            setRegion(getFrame(deltaTime));
        }
        if (timeToDefineForm) {
            this.define();
            timeToDefineForm = false;
        }
        if (coolDown > 0) {
            coolDown -= 1;
        }
        if (Objects.equals(this.type, "Tyrannosaur")) {
            ((Tyrannosaur) this).countTime();
        }
        if (Objects.equals(this.type, "Archeopteryx") && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
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
        destroyFixtures(1);
        if (walking != null) {
            walking.pause();
        }
    }

    protected void destroyFixtures(int cuantityToSave) {
        while (player.b2Body.getFixtureList().size != cuantityToSave) {
            player.b2Body.destroyFixture(
                    player.b2Body.getFixtureList().get(player.b2Body.getFixtureList().size - 1)
            );
        }
    }

    public float getVelocityX() {
        return velocityX;
    }

    public float getJumpHeight() {
        return jumpHeight;
    }

    public void dispose() {
        if (walking != null) {
            walking.dispose();
        }
        manager.dispose();
    }
}
