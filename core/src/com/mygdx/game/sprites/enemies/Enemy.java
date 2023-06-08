package com.mygdx.game.sprites.enemies;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.screens.Level;
import com.mygdx.game.screens.LevelOne;

public abstract class Enemy extends Sprite {
    public enum State {RUNNING, HITTING, DEAD}
    protected World world;
    protected Screen screen;
    protected int health;
    public Body b2Body;
    public Vector2 velocity;
    protected State currentState;
    protected float stateTime;
    protected float coolDown;
    protected State previousState;
    protected boolean setToDestroy;
    protected boolean isHitting;
    public Enemy(Level screen, String regionName, float x, float y){
        super(screen.getAtlas().findRegion(regionName));
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineEnemy();
        velocity = new Vector2(1, 0);
        b2Body.setActive(false);
    }

    protected abstract void defineEnemy();
    public abstract void update(float deltaTime);
    public void reverseVelocity(boolean x, boolean y){
        if(x){
            velocity.x = -velocity.x;
        }
        if(y){
            velocity.y = -velocity.y;
        }
    }
    public void hit(){
        if(coolDown == 0){
            isHitting = true;
        }else coolDown -= 1;
    }
    public void getHit(int damage){
        setBounds(b2Body.getPosition().x, b2Body.getPosition().y, getWidth() / 2, getHeight());
        if(health != 0){
            health -= damage;
            setBounds(b2Body.getPosition().x, b2Body.getPosition().y, getWidth() * 2, getHeight());
        }else {
            setToDestroy = true;
        }
    }
}
