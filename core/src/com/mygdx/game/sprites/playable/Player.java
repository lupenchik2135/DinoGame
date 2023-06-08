package com.mygdx.game.sprites.playable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;
import com.mygdx.game.sprites.playable.forms.*;

import java.util.Objects;

public class Player {
    private World world;
    private Form[] forms;
    public final Body b2Body;
    private int currentForm;
    private int previousForm;
    private int health;
    private int lostHealth;
    private boolean getHit;
    private boolean getHeal;
    private boolean isPlayerDead;
    private boolean isAbleToJump;

    public Player(Level screen) {
        this.world = screen.getWorld();
        this.forms = new Form[4];
        BodyDef bdef = new BodyDef();
        bdef.position.set(16 / GameLogic.PPM, 32 / GameLogic.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        this.b2Body = world.createBody(bdef);
        this.currentForm = 0;
        this.previousForm = 0;
        this.health = 3;
        if (b2Body != null) {
            forms[0] = new Human(screen, this);
            forms[1] = new Triceratops(screen, this);
            forms[2] = new Ichtiozaur(screen, this);
            forms[3] = new Archeopteryx(screen, this);
        }
        forms[currentForm].define();
        isPlayerDead = false;

    }
    public void swim(){
        if(!Objects.equals(forms[currentForm].getType(), "Ichtiozaur")){
            forms[currentForm].die();
        }else {
            ((Ichtiozaur) forms[currentForm]).setSwimming(true);
        }
    }

    public void draw(SpriteBatch batch) {
        if (currentForm == previousForm ) {
            forms[currentForm].draw(batch);
        }
    }

    public void update(float deltaTime) {
        if (currentForm == previousForm) {
            forms[currentForm].update(deltaTime);
            if (getHit) {
                health -= 1;
                lostHealth += 1;
                if (health > 0) {
                    getHit = false;
                } else {
                    Gdx.app.log("dead", "player");
                    isPlayerDead = forms[currentForm].die();
                    getHit = false;
                    Gdx.app.log("player hp", " " + health);
                }
            }
            if (getHeal) {
                health += 1;
                lostHealth -= 1;
                getHeal = false;
            }
        } else {
            forms[previousForm].destroy();
            previousForm = currentForm;
            forms[currentForm].change();
        }
    }

    public void getHitted() {
        Gdx.app.log("player hp", " " + health);
        getHit = true;
    }

    public void getHeal() {
        if (forms[currentForm].getCurrentFormHealth() > health) {
            getHeal = true;
        }
    }

    public void ableToJump(boolean ability){
        isAbleToJump = ability;
    }

    public boolean getIsAbleToJump(){
        return isAbleToJump;
    }
    public Form.State getState() {
        return forms[currentForm].getState();
    }

    public float getX() {
        return forms[currentForm].getX();
    }

    public float getY() {
        return forms[currentForm].getY();
    }

    public Form getCurrentForm() {
        return forms[currentForm];
    }

    public void setCurrentForm(int currentForm) {
        this.currentForm = currentForm;
    }

    public void dispose() {
        for (Form form : forms) {
            form.dispose();
        }
    }

    public int getHealth() {
        return health;
    }

    public int getLostHealth() {
        return lostHealth;
    }

    public void setHealth(int health) {
        this.health = health;
    }
    public boolean isPlayerDead() {
        return isPlayerDead;
    }

    public float getStateTimer(){
        return forms[currentForm].getStateTimer();
    }

}