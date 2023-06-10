package com.mygdx.game.sprites.playable.forms;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;
import com.mygdx.game.sprites.playable.Player;

public class Tyrannosaur extends Form{
    private float timeToExist;
    private final TextureRegion deadTexture;
    public Tyrannosaur(Level screen, Player player){
        super(screen, "Tyrannosaur", player);
        this.type = "Tyrannosaur";
        frames.add(new TextureRegion(getTexture(),2546, 24, 164, 88));
        frames.add(new TextureRegion(getTexture(),2546 + 184, 24, 169, 88));
        frames.add(new TextureRegion(getTexture(),2546 + 184 + 189, 20, 168, 88));
        frames.add(new TextureRegion(getTexture(),2546 + 184 + 189 + 188, 22, 164, 90));
        frames.add(new TextureRegion(getTexture(),2546 + 184 + 189 + 188 + 184, 17, 169, 95));
        frames.add(new TextureRegion(getTexture(),2546 + 184 + 189 + 188 + 184, 18, 169, 94));
        runAnimation = new Animation<>(0.1f, frames);
        frames.clear();
        deadTexture = new TextureRegion(getTexture(), 2355, 67, 171, 34);
        standTexture = new TextureRegion(getTexture(),3670, 49, 86, 64);
        velocityX = 200 / GameLogic.PPM;
        jumpHeight = 300 / GameLogic.PPM;
        currentFormHealth = 100;
        timeToExist = 30;
        damage = 200;
    }
    public void define(){
        walking = manager.get("audio/Sounds/TRexStep.mp3", Sound.class);
        walking.play(0.5f, 10, 10);
        walking.loop();
        timeToExist = 1200;
        setBounds(player.b2Body.getPosition().x, player.b2Body.getPosition().y, 82 / GameLogic.PPM, 42 / GameLogic.PPM);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(41 / GameLogic.PPM, 21 / GameLogic.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.filter.categoryBits = GameLogic.TYRANNOSAUR_BIT;
        fdef.filter.maskBits = GameLogic.GROUND_BIT |
                GameLogic.STONE_WALL |
                GameLogic.ENEMY_BIT |
                GameLogic.ENEMY_ATTACK_BIT |
                GameLogic.SMALL_ENEMY_HEAD_BIT |
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
        }
        else if (player.b2Body.getLinearVelocity().y > 0 || (player.b2Body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if (player.b2Body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (player.b2Body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }
    public TextureRegion getFrame(float deltaTime){
        currentState = getState();
        TextureRegion region;
        switch (currentState){
            case DEAD:
                region = deadTexture;
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
    public void countTime(){
        timeToExist -= 1;
        if(timeToExist == 0){
            player.changeInto(0);
            this.destroy();
            screen.setNewUltTimer(50);
        }
    }
}
