package com.mygdx.game.sprites.playable.forms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;
import com.mygdx.game.sprites.objects.ObjectDef;
import com.mygdx.game.sprites.playable.Player;
import com.mygdx.game.sprites.projectiles.Spit;

public class Ichtiozaur extends Form {
    private boolean isSwimming;
    private Animation<TextureRegion> swimAnimation;


    public Ichtiozaur(Level screen, Player player){
        super(screen, "Ichtiosaur", player);
        this.type = "Ichtiozaur";
        for(int i = 0; i < 5; i++){
            if(i <= 2) frames.add(new TextureRegion(getTexture(),77 + i * (57+20), 84, 57, 30));
            else if(i <= 3) frames.add(new TextureRegion(getTexture(), 310 + (i - 2) * (53+20), 84, 53, 30));
            else frames.add(new TextureRegion(getTexture(),455, 84, 57, 30));
        }
        runAnimation = new Animation<>(0.1f, frames);
        frames.clear();


        frames.add(new TextureRegion(getTexture(),2005, 82, 47, 32));
        frames.add(new TextureRegion(getTexture(),2005 + 67, 88, 58, 25));
        hitAnimation = new Animation<>(0.2f, frames);

        frames.clear();


        frames.add(new TextureRegion(getTexture(),532 + 80, 85, 60, 28));
        frames.add(new TextureRegion(getTexture(), 532 + 80 + 80, 85, 60, 27));
        frames.add(new TextureRegion(getTexture(), 532 + 82 + 80 + 80, 79, 61, 33));

        swimAnimation = new Animation<>(0.25f, frames);
        frames.clear();

        frames.add(new TextureRegion(getTexture(),2150, 87, 44, 25));
        frames.add(new TextureRegion(getTexture(), 2150 + 64, 94, 33, 18));
        frames.add(new TextureRegion(getTexture(), 2150 + 64 + 53, 95, 33, 17));
        frames.add(new TextureRegion(getTexture(), 2150 + 64 + 53 + 53, 96, 33, 16));
        deadAnimation = new Animation<>(0.3f, frames);
        frames.clear();


        standTexture = new TextureRegion(getTexture(), 2, 84, 55, 29);
        velocityX = 50 / GameLogic.PPM;
        jumpHeight = 50 / GameLogic.PPM;
        currentFormHealth = 4;
        isSwimming = false;
    }
    public void define(){
        walking = manager.get("audio/Sounds/triceStep.mp3", Sound.class);
        walking.play(0.5f, 10, 10);
        walking.loop();
        setBounds(player.b2Body.getPosition().x, player.b2Body.getPosition().y, 64 / GameLogic.PPM, 32 / GameLogic.PPM);
        setRegion(standTexture);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(32 / GameLogic.PPM, 12 / GameLogic.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.filter.categoryBits = GameLogic.PLAYER_BIT;
        fdef.filter.maskBits = GameLogic.GROUND_BIT |
                GameLogic.STONE_WALL |
                GameLogic.ENEMY_BIT |
                GameLogic.ENEMY_ATTACK_BIT |
                GameLogic.PROJECTILE_BIT |
                GameLogic.SMALL_ENEMY_BIT |
                GameLogic.WATER_BIT |
                GameLogic.ITEM_BIT;
        fdef.shape = shape;
        if (player.b2Body != null){
            this.player.b2Body.createFixture(fdef).setUserData(player);
        }
        destroyed = false;
    }
    public State getState(){
        if(isDead){
            return State.DEAD;
        }
        else if(runChangeAnimation){
            return State.CHANGING;
        }else if(isSwimming){
            return State.SWIMMING;
        }
        else if (player.b2Body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (player.b2Body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else if (Gdx.input.justTouched()){
            return State.HITTING;
        }
        else
            return State.STANDING;
    }
    public TextureRegion getFrame(float deltaTime){
        currentState = getState();
        TextureRegion region;
        switch (currentState){
            case DEAD:
                region = deadAnimation.getKeyFrame(stateTimer);
                break;
            case CHANGING:
                region = changeForm;
                if (stateTimer > 1){
                    runChangeAnimation = false;
                    coolDown = 3;
                }
                break;
            case RUNNING:
                region = runAnimation.getKeyFrame(stateTimer, true);
                break;
            case HITTING:
                region = hitAnimation.getKeyFrame(stateTimer);
                if(runningRight){
                    screen.spawnObject(new ObjectDef(new Vector2(player.b2Body.getPosition().x + 50 / GameLogic.PPM, player.b2Body.getPosition().y),
                            Spit.class, runningRight));
                }
                else {
                    screen.spawnObject(new ObjectDef(new Vector2(player.b2Body.getPosition().x - 50 / GameLogic.PPM, player.b2Body.getPosition().y),
                            Spit.class, runningRight));
                }
                break;
            case SWIMMING:
                player.b2Body.resetMassData();
                velocityX = 10 / GameLogic.PPM;
                jumpHeight = 10 / GameLogic.PPM;
                if (Gdx.input.isKeyPressed(Input.Keys.W) && player.b2Body.getLinearVelocity().y <= player.getCurrentForm().getJumpHeight()) {
                    player.b2Body.setLinearVelocity(0, 0.3f);
                    region = swimAnimation.getKeyFrame(stateTimer, true);
                }
                else if (Gdx.input.isKeyPressed(Input.Keys.S) && player.b2Body.getLinearVelocity().y >= -player.getCurrentForm().getJumpHeight()) {
                    player.b2Body.applyLinearImpulse(new Vector2(0, -0.3f), player.b2Body.getWorldCenter(), true);
                    region = swimAnimation.getKeyFrame(stateTimer, true);
                }
                else if (Gdx.input.isKeyPressed(Input.Keys.D) && player.b2Body.getLinearVelocity().x <= player.getCurrentForm().getVelocityX()) {
                player.b2Body.setLinearVelocity(0.3f, 0);
                region = swimAnimation.getKeyFrame(stateTimer, true);
                    break;
                }
                else if (Gdx.input.isKeyPressed(Input.Keys.A) && player.b2Body.getLinearVelocity().x >= -player.getCurrentForm().getVelocityX())
                {
                    player.b2Body.applyLinearImpulse(new Vector2(-0.3f, 0), player.b2Body.getWorldCenter(), true);
                    region = swimAnimation.getKeyFrame(stateTimer, true);
                    break;
                }
                else region = swimAnimation.getKeyFrame(stateTimer, true);
                    break;

            case FALLING:
            case STANDING:
            default:
                region = standTexture;
                break;
        }
        if ((player.b2Body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }
        else if (((player.b2Body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX())){
            region.flip(true, false);
            runningRight = true;
        }
        stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
        previousState = currentState;
        return region;
    }

    public boolean isSwimming() {
        return isSwimming;
    }

    public void setSwimming(boolean swimming) {
        isSwimming = swimming;
        if(!isSwimming){
            velocityX = 20 / GameLogic.PPM;
            jumpHeight = 14 / GameLogic.PPM;
            world.setGravity(new Vector2(0, GameLogic.GRAVITY));
        }
    }
}
