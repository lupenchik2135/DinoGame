package com.mygdx.game.sprites.playable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.PlayScreen;
import com.mygdx.game.sprites.playable.forms.*;

public class Player {
    private World world;
    private BodyDef bdef;
    private Form[] forms;
    public final Body b2Body;
    private int currentForm;
    private int previousForm;
    public int health;

    public Player(PlayScreen screen){
        this.world = screen.getWorld();
        this.forms = new Form[4];
        bdef = new BodyDef();
        this.bdef.position.set(16 / GameLogic.PPM, 32 / GameLogic.PPM);
        this.bdef.type = BodyDef.BodyType.DynamicBody;
        this.b2Body = world.createBody(bdef);
        this.currentForm = 0;
        this.previousForm = 0;
        this.health = 3;
        if(b2Body != null) {
            forms[0] = new Human(screen, this);
            forms[1] = new Triceratops(screen, this);
            forms[2] = new Ichtiozaur(screen, this);
            forms[3] = new Archeopteryx(screen, this);
        }
        forms[currentForm].define();

    }

   public void draw(SpriteBatch batch){
        if(currentForm == previousForm){
            forms[currentForm].draw(batch);
        }
   }
    public void update(float deltaTime){
        if (currentForm == previousForm){
            forms[currentForm].update(deltaTime);
        }else {
            forms[currentForm].change();
            forms[previousForm].destroy();
            previousForm = currentForm;
            forms[currentForm].define();
        }
    }
    public void hit(){
        if(health > 0){
            health -= 1;
        }
    }
    public Form.State getState(){
        return forms[currentForm].getState();
    }
    public float getX(){
        return forms[currentForm].getX();
    }
    public float getY(){
        return forms[currentForm].getY();
    }
    public Form getCurrentForm() {
        return forms[currentForm];
    }

    public void setCurrentForm(int currentForm) {
        this.currentForm = currentForm;
    }

    public void dispose(){
        for(Form form : forms){
            form.dispose();
        }
    }

}