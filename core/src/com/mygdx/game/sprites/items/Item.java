package com.mygdx.game.sprites.items;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;
import com.mygdx.game.sprites.playable.Player;

public abstract class Item extends Sprite {
    protected Level screen;
    protected World world;
    protected Vector2 velocity;
    protected boolean toDestroy;
    protected boolean destroyed;
    protected boolean isUsing;
    protected Body body;

    protected Item(Level screen, float x, float y){
        this.screen = screen;
        this.world = screen.getWorld();
        setPosition(x, y);
        setBounds(getX(), getY(), 8 / GameLogic.PPM, 8 / GameLogic.PPM);
        defineItem();
        toDestroy = false;
        destroyed = false;
    }
    public abstract void defineItem();
    public abstract void use(Player player);
    public void update(float deltaTime){
        if(toDestroy && !destroyed && !isUsing){
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
    public void destroy(){
        toDestroy = true;
    }
}
