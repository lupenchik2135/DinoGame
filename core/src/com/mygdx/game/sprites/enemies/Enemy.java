package com.mygdx.game.sprites.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.screens.Level;

public abstract class Enemy extends Sprite {
    public enum State {RUNNING, HITTING}

    protected World world;
    protected Level screen;
    protected int health;

    protected Body b2Body;
    protected Vector2 velocity;
    protected State currentState;
    protected float stateTime;
    protected float coolDown;
    protected State previousState;
    protected boolean setToDestroy;
    protected boolean isHitting;
    protected boolean destroyed;

    protected Enemy(Level screen, String regionName, float x, float y) {
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

    public void reverseVelocity(boolean x, boolean y) {
        if (x) {
            velocity.x = -velocity.x;
        }
        if (y) {
            velocity.y = -velocity.y;
        }
    }

    public void hit() {
        if (!this.getClass().isAssignableFrom(SmallEnemy.class)) {
            if (coolDown == 0) {
                isHitting = true;
            } else coolDown -= 1;
        }
    }

    public void getHit(int damage) {
        if (health > 0) {
            health -= damage;
        }
        if (health <= 0) {
            setToDestroy = true;
        }
    }

    public Body getB2Body() {
        return b2Body;
    }
}
