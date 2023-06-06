package com.mygdx.game.sprites.enemies;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.screens.PlayScreen;

public abstract class Enemy extends Sprite {
    protected World world;
    protected Screen screen;
    public Body b2Body;
    public Vector2 velocity;
    public Enemy(PlayScreen screen, String regionName, float x, float y){
        super(screen.getAtlas().findRegion(regionName));
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineEnemy();
        velocity = new Vector2(1, 0);
        b2Body.setActive(false);
    }

    protected abstract void defineEnemy();
    public abstract void hitOnHead();
    public abstract void update(float deltaTime);
    public void reverseVelocity(boolean x, boolean y){
        if(x){
            velocity.x = -velocity.x;
        }
        if(y){
            velocity.y = -velocity.y;
        }
    }
}
