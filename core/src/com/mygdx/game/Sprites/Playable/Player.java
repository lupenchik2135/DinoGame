package com.mygdx.game.Sprites.Playable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameLogic;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Sprites.Playable.Forms.Archeopteryx;
import com.mygdx.game.Sprites.Playable.Forms.Form;
import com.mygdx.game.Sprites.Playable.Forms.Ichtiozaur;
import com.mygdx.game.Sprites.Playable.Forms.Triceratops;

public class Player {
    public World world;
    private boolean setToExpose;
    private boolean exposed;
    private BodyDef bdef;
    private Form[] forms;
    public Body b2Body;
    public int currentForm;
    private int previousForm;

    public Player(PlayScreen screen){
        this.world = screen.getWorld();
        this.forms = new Form[3];
        bdef = new BodyDef();
        this.bdef.position.set(0 / GameLogic.PPM, 32 / GameLogic.PPM);
        this.bdef.type = BodyDef.BodyType.DynamicBody;
        this.b2Body = world.createBody(bdef);
        this.currentForm = 0;
        this.previousForm = 0;
        if(b2Body != null) {
            forms[0] = new Triceratops(screen, b2Body);
            forms[1] = new Ichtiozaur(screen, b2Body);
            forms[2] = new Archeopteryx(screen, b2Body);
        }
        forms[currentForm].define();

    }

   public void draw(SpriteBatch batch){
        if(currentForm == previousForm){
            forms[currentForm].draw(batch);
        }
   }
    public void update(float deltaTime){
        System.out.println(currentForm + "   " + previousForm);
        if (currentForm == previousForm){
            forms[currentForm].update(deltaTime);
        }else {
            forms[previousForm].expose();
            previousForm = currentForm;
            forms[currentForm].define();
        }
    }
    public Form.State getState(){
        return forms[currentForm].getState();
    }
}