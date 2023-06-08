package com.mygdx.game.sprites.playable.forms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;
import com.mygdx.game.sprites.playable.Player;


public class Triceratops extends Form {

    public Triceratops(Level screen, Player player){
        super(screen, "Triceratops", player);
        this.type = "Triceratops";
        for(int i = 0; i < 7; i++){
            if(i <= 3) frames.add(new TextureRegion(getTexture(),228 + i * 60, 127, 58, 48));
            else frames.add(new TextureRegion(getTexture(), 228 + (i - 4) * 60, 177, 58, 48));
        }
        runAnimation = new Animation<>(0.1f, frames);
        frames.clear();
        for (int i = 0; i < 4; i ++){
            frames.add(new TextureRegion(getTexture(), 226 + i * 60, 443, 52, 48));
        }
        jumpAnimation = new Animation<>(0.2f, frames);
        frames.clear();
        for(int i = 0; i < 4; i++){
            frames.add(new TextureRegion(getTexture(),228 + i * 60, 230, 58, 48));
        }
        hitAnimation = new Animation<>(0.2f, frames);
        frames.clear();
        standTexture = new TextureRegion(getTexture(), 228, 386, 58, 46);
        velocityX = 200 / GameLogic.PPM;
        jumpHeight = 300 / GameLogic.PPM;
        currentFormHealth = 4;
        damage = 3;
    }

    public TextureRegion getFrame(float deltaTime){
        currentState = getState();
        TextureRegion region;
        switch (currentState){
            case CHANGING:
                region = changeForm.getKeyFrame(stateTimer);
                if (changeForm.isAnimationFinished(stateTimer)){
                    runChangeAnimation = false;
                }
                break;
            case JUMPING:
                region = jumpAnimation.getKeyFrame(stateTimer);
                player.ableToJump(false);
                break;
            case RUNNING:
                region = runAnimation.getKeyFrame(stateTimer, true);
                break;
            case HITTING:
                region = hitAnimation.getKeyFrame(stateTimer);
                if(runningRight){
                    FixtureDef fdefRight = new FixtureDef();
                    fdefRight.filter.categoryBits = GameLogic.PLAYER_ATTACK_BIT;
                    EdgeShape head = new EdgeShape();
                    head.set(34 / GameLogic.PPM, 0 / GameLogic.PPM, 34 / GameLogic.PPM, 9 / GameLogic.PPM);
                    fdefRight.shape = head;
                    fdefRight.isSensor = true;
                    player.b2Body.createFixture(fdefRight).setUserData(player);
                }
                else{
                    FixtureDef fdefLeft = new FixtureDef();
                    fdefLeft.filter.categoryBits = GameLogic.PLAYER_ATTACK_BIT;
                    EdgeShape head = new EdgeShape();
                    head.set(-34 / GameLogic.PPM, 0 / GameLogic.PPM, -34 / GameLogic.PPM, 9 / GameLogic.PPM);
                    fdefLeft.shape = head;
                    fdefLeft.isSensor = true;
                    player.b2Body.createFixture(fdefLeft).setUserData(player);
                }
                if(hitAnimation.isAnimationFinished(stateTimer) && player.b2Body.getFixtureList().size >= 2){
                   while (player.b2Body.getFixtureList().size > 1) {
                            player.b2Body.destroyFixture(player.b2Body.getFixtureList().get(1));
                   }
                   isHitting = false;
                }
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
    public State getState(){
        if(runChangeAnimation){
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
    public void define(){

        walking = manager.get("audio/Sounds/triceStep.mp3", Sound.class);
        walking.play(0.5f, 10, 10);
        walking.loop();
        if (player.b2Body.getFixtureList().size >= 2){
            while (player.b2Body.getFixtureList().size > 1) {
                player.b2Body.destroyFixture(player.b2Body.getFixtureList().get(1));
            }
        }

        setBounds(player.b2Body.getPosition().x, player.b2Body.getPosition().y, 64 / GameLogic.PPM, 32 / GameLogic.PPM);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(32 / GameLogic.PPM, 12 / GameLogic.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.filter.categoryBits = GameLogic.PLAYER_BIT;
        fdef.filter.maskBits = GameLogic.GROUND_BIT |
                GameLogic.STONE_WALL |
                GameLogic.ENEMY_BIT |
                GameLogic.OBJECT_BIT |
                GameLogic.SMALL_ENEMY_BIT |
                GameLogic.SMALL_ENEMY_HEAD_BIT |
                GameLogic.WATER_BIT |
                GameLogic.ITEM_BIT;
        fdef.shape = shape;
        if (player.b2Body != null){
            this.player.b2Body.createFixture(fdef).setUserData(player);
        }

        destroyed = false;
    }
}
