package com.mygdx.game.Sprites.Playable;

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

public class Player extends Sprite {
    public enum State{FALLING, RUNNING, CHANGING, STANDING, HITTING}
    public com.mygdx.game.Sprites.Playable.Forms.Ichtiozaur.State currentState;
    public com.mygdx.game.Sprites.Playable.Forms.Ichtiozaur.State previousState;
    public World world;
    public Body b2Body;
    private boolean setToExpose;
    private boolean exposed;

    public Player(PlayScreen screen){
        super(screen.getAtlas().findRegion("Ichtio"));
        this.world = screen.getWorld();

    }

    public void update(float deltaTime){

    }

}