package com.mygdx.game.sprites.playable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;
import com.mygdx.game.sprites.playable.forms.*;

import java.util.Comparator;
import java.util.Objects;
import java.util.Vector;

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
    private boolean isAbleToChange;
    private boolean isAbleToJump;
    private boolean isDead;
    private boolean timeToDefineForm;
    public Player(Level screen, float x, float y) {
        this.world = screen.getWorld();
        this.forms = new Form[5];
        BodyDef bdef = new BodyDef();
        bdef.position.set(x / GameLogic.PPM, y  / GameLogic.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        this.b2Body = world.createBody(bdef);
        PolygonShape shape = new PolygonShape();
        Vector2[] verticies = new Vector2[4];
        verticies[0] = new Vector2(-32 / GameLogic.PPM, 0 / GameLogic.PPM);
        verticies[1] = new Vector2(-32 / GameLogic.PPM, 42 / GameLogic.PPM);
        verticies[2] = new Vector2(32 / GameLogic.PPM, 42 / GameLogic.PPM);
        verticies[3] = new Vector2(32 / GameLogic.PPM, 0 / GameLogic.PPM);
        shape.set(verticies);
        FixtureDef fdef = new FixtureDef();
        fdef.filter.categoryBits = GameLogic.CHECK_BIT;
        fdef.filter.maskBits = GameLogic.STONE_WALL |
                GameLogic.GROUND_BIT|
                GameLogic.WATER_BIT;
        fdef.shape = shape;
        fdef.isSensor = true;
        if (b2Body != null){
            b2Body.createFixture(fdef).setUserData(this);
        }
        this.currentForm = 0;
        this.previousForm = 0;
        this.health = 3;
        if (b2Body != null) {
            forms[0] = new Human(screen, this);
            forms[1] = new Triceratops(screen, this);
            forms[2] = new Ichtiozaur(screen, this);
            forms[3] = new Archeopteryx(screen, this);
            forms[4] = new Tyrannosaur(screen, this);
        }
        forms[currentForm].define();
        isAbleToChange = true;

    }
    public void swim(){
        if(!Objects.equals(forms[currentForm].getType(), "Ichtiozaur")){
            forms[currentForm].die();
        }else {
            ((Ichtiozaur) forms[currentForm]).setSwimming(true);
            world.setGravity(new Vector2(0, GameLogic.WATER_GRAVITY));
        }
    }

    public void draw(SpriteBatch batch) {
        if (currentForm == previousForm ) {
            forms[currentForm].draw(batch);
        }
    }

    public void update(float deltaTime) {
            if (!timeToDefineForm) {
                forms[currentForm].update(deltaTime);
                if(!isDead){
                    ableToJump();
                    if (getHit) {
                        health -= 1;
                        lostHealth += 1;
                        if (health > 0) {
                            getHit = false;
                        } else {
                            isDead = true;
                            forms[currentForm].die();
                        }
                    }
                    if (getHeal) {
                        health += 1;
                        lostHealth -= 1;
                        getHeal = false;
                    }
                }
            } else {
                if (currentForm != previousForm) {
                    forms[previousForm].destroy();
                    previousForm = currentForm;
                    forms[currentForm].change();
                    timeToDefineForm = false;
                }
            }
        }

    public void getHitted() {
        getHit = true;
    }

    public void getHeal() {
        if (forms[currentForm].getCurrentFormHealth() > health) {
            getHeal = true;
        }
    }
    public boolean getIsAbleToChange() {
        return isAbleToChange;
    }
    public void ableToJump(){
        isAbleToJump = !(getState() == Form.State.JUMPING || getState() == Form.State.FLYING || getState() == Form.State.FALLING || getState() == Form.State.SWIMMING);
    }
    public void isAbleToChange(boolean changeState){
        isAbleToChange = changeState;
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

    public void changeInto(int currentForm) {
        if(isAbleToChange) {
            timeToDefineForm = true;
            this.currentForm = currentForm;
        }
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

    public float getStateTimer(){
        return forms[currentForm].getStateTimer();
    }

}