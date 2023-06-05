package com.mygdx.game.Sprites.Playable.Forms;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Sprites.Playable.Player;

public abstract class Form extends Sprite {
    protected World world;
    protected Screen screen;
    public enum State{FALLING, RUNNING, CHANGING, STANDING, HITTING, JUMPING}
    public State currentState;
    public State previousState;
    protected float stateTimer;
    Body b2Body;
    protected boolean runningRight;
    public Form(PlayScreen screen, String regionName, Body b2Body){
        super(screen.getAtlas().findRegion(regionName));
        this.screen = screen;
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;
        this.b2Body = b2Body;
    }
    public abstract void setRightTriceFixture();
    public abstract void setLeftTriceFixture();
    public abstract State getState();
    public abstract void define();
    public abstract void update(float deltaTime);

    public abstract void expose();
}
