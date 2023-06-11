package com.mygdx.game.sprites.projectiles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.screens.Level;

public abstract class Projectile extends Sprite {
    protected Level screen;
    protected World world;
    protected Vector2 velocity;
    protected int damage;
    protected boolean toDestroy;
    protected boolean destroyed;
    protected Body body;
    protected float existTime;
    protected boolean right;

    protected Projectile(Level screen, float x, float y, boolean right){
        this.screen = screen;
        this.world = screen.getWorld();
        this.right = right;
        setPosition(x, y);
        defineProjectile();
        toDestroy = false;
        destroyed = false;
        existTime = 120;
    }
    public abstract void defineProjectile();
    public void update(float deltaTime){
        existTime -= 1;
        if(existTime == 0){
            toDestroy = true;
        }
        if(toDestroy && !destroyed){
            world.destroyBody(body);
            destroyed = true;
        }
    }
    @Override
    public void draw(Batch batch){
        if(!destroyed){
            super.draw(batch);
        }
    }

    public int getDamage() {
        toDestroy = true;
        return damage;
    }
    public void hitted(){
        toDestroy = true;
    }
}
