package com.mygdx.game.sprites.projectiles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;

public abstract class Projectile extends Sprite {
    protected Level screen;
    protected World world;
    protected Vector2 velocity;
    protected int damage;
    protected boolean toDestroy;
    protected boolean destroyed;
    protected Body body;

    public Projectile(Level screen, float x, float y){
        this.screen = screen;
        this.world = screen.getWorld();
        setPosition(x, y);
        setBounds(getX(), getY(), 4 / GameLogic.PPM, 4 / GameLogic.PPM);
        defineProjectile();
        toDestroy = false;
        destroyed = false;
    }
    public abstract void defineProjectile();
    public void update(float deltaTime){
        if(toDestroy && !destroyed){
            world.destroyBody(body);
            destroyed = true;
        }
    }
    public void draw(Batch batch){
        if(!destroyed){
            super.draw(batch);
        }
    }

    public int getDamage() {
        toDestroy = true;
        return damage;
    }
}
