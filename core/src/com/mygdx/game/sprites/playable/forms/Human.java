package com.mygdx.game.sprites.playable.forms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;
import com.mygdx.game.sprites.playable.Player;

public class Human extends Form{
    public Human(Level screen, Player player){
        super(screen, "Human", player);
        this.type = "Human";

        frames.add(new TextureRegion(getTexture(),5126, 80, 27, 34));
        frames.add(new TextureRegion(getTexture(),5163, 80, 27, 34));
        frames.add(new TextureRegion(getTexture(),5198, 78, 27, 34));
        frames.add(new TextureRegion(getTexture(),5232, 80, 27, 34));
        frames.add(new TextureRegion(getTexture(),5278, 80, 27, 34));
        frames.add(new TextureRegion(getTexture(),5314, 78, 27, 34));

        runAnimation = new Animation<>(0.2f, frames);
        frames.clear();

        frames.add(new TextureRegion(getTexture(), 5347, 80, 27, 34));
        frames.add(new TextureRegion(getTexture(), 5389, 80, 27, 34));
        frames.add(new TextureRegion(getTexture(), 5435, 80, 27, 34));
        frames.add(new TextureRegion(getTexture(), 5480, 80, 27, 34));
        frames.add(new TextureRegion(getTexture(), 5527, 80, 27, 34));
        frames.add(new TextureRegion(getTexture(), 5573, 80, 27, 34));
        frames.add(new TextureRegion(getTexture(), 5619, 80, 27, 34));

        jumpAnimation = new Animation<>(0.1f, frames);
        frames.clear();
        // change animation
        frames.add(new TextureRegion(getTexture(),5665 , 80, 27, 34));
        frames.add(new TextureRegion(getTexture(),5711, 80, 27, 34));
        frames.add(new TextureRegion(getTexture(), 5757, 80, 27, 34));
        frames.add(new TextureRegion(getTexture(), 5801, 80, 27, 34));
        frames.add(new TextureRegion(getTexture(), 5841, 80, 27, 34));

        hitAnimation = new Animation<>(0.1f, frames);
        frames.clear();
        frames.add(new TextureRegion(getTexture(), 5898, 80, 27, 34));
        frames.add(new TextureRegion(getTexture(), 5944, 80, 27, 34));
        frames.add(new TextureRegion(getTexture(), 5990, 80, 27, 34));
        frames.add(new TextureRegion(getTexture(), 6036, 80, 27, 34));
        frames.add(new TextureRegion(getTexture(), 6083, 80, 27, 34));
        frames.add(new TextureRegion(getTexture(), 6130, 80, 27, 34));
        deadAnimation = new Animation<TextureRegion>(0.3f, frames);
        frames.clear();

        standTexture = new TextureRegion(getTexture(), 5080, 80, 27, 32);
        velocityX = 250 / GameLogic.PPM;
        jumpHeight = 350 / GameLogic.PPM;
        damage = 2;
        currentFormHealth = 3;
    }
    public void define(){
        walking = manager.get("audio/Sounds/triceStep.mp3", Sound.class);
        walking.play(0.5f, 10, 10);
        walking.loop();
        setBounds(player.b2Body.getPosition().x, player.b2Body.getPosition().y, 27 / GameLogic.PPM, 35 / GameLogic.PPM);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(10 / GameLogic.PPM, 14 / GameLogic.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.filter.categoryBits = GameLogic.PLAYER_BIT;
        fdef.filter.maskBits = GameLogic.GROUND_BIT |
                GameLogic.STONE_WALL |
                GameLogic.ENEMY_BIT |
                GameLogic.SMALL_ENEMY_BIT |
                GameLogic.ENEMY_ATTACK_BIT |
                GameLogic.PROJECTILE_BIT |
                GameLogic.SMALL_ENEMY_HEAD_BIT |
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
        else if (isHitting || Gdx.input.justTouched()){
            isHitting = true;
            return State.HITTING;
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
                    setAttackFixture(14, 9);
                }
                else{
                    setAttackFixture(-14, 9);
                }
                if(hitAnimation.isAnimationFinished(stateTimer) && player.b2Body.getFixtureList().size >= 2){
                    destroyFixtures(2);
                    isHitting = false;
                }
                break;
            case JUMPING:
                region = jumpAnimation.getKeyFrame(stateTimer);
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
}
