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
            frames.add(new TextureRegion(getTexture(),77 + i * (56+20), 20, 56, 46));
        }
        runAnimation = new Animation<>(0.1f, frames);
        frames.clear();


        frames.add(new TextureRegion(getTexture(), 686, 26, 55, 40));
        frames.add(new TextureRegion(getTexture(), 686 + 75, 24, 51, 42));
        frames.add(new TextureRegion(getTexture(), 686 + 75 + 71, 28, 50, 38));
        frames.add(new TextureRegion(getTexture(), 686 + 75 + 71 + 70, 21, 54, 45));

        jumpAnimation = new Animation<>(0.1f, frames);
        frames.clear();

        for(int i = 0; i < 4; i++){
            frames.add(new TextureRegion(getTexture(),977 + i * (56+20), 20, 56, 46));
        }
        hitAnimation = new Animation<>(0.2f, frames);
        frames.clear();

        frames.add(new TextureRegion(getTexture(),1205 , 20, 55, 46));
        frames.add(new TextureRegion(getTexture(),1205 + 75, 20, 46, 46));
        frames.add(new TextureRegion(getTexture(),1205 + 75 + 66, 24, 49, 42));
        frames.add(new TextureRegion(getTexture(),1205 + 75 + 66 + 69, 26, 48, 40));
        frames.add(new TextureRegion(getTexture(),1205 + 75 + 66 + 69 + 68, 26, 49, 37));
        frames.add(new TextureRegion(getTexture(),1205 + 75 + 66 + 69 + 68 + 69, 29, 44, 32));
        frames.add(new TextureRegion(getTexture(),1205 + 75 + 66 + 69 + 68 + 69 + 64, 35, 43, 28));
        frames.add(new TextureRegion(getTexture(),1205 + 75 + 66 + 69 + 68 + 69 + 64 + 63, 38, 43, 28));
        frames.add(new TextureRegion(getTexture(),1205 + 75 + 66 + 69 + 68 + 69 + 64 + 63 + 63, 39, 44, 27));
        deadAnimation = new Animation<>(0.2f, frames);
        frames.clear();


        standTexture = new TextureRegion(getTexture(), 2, 20, 55, 46);
        velocityX = 150 / GameLogic.PPM;
        jumpHeight = 200 / GameLogic.PPM;
        currentFormHealth = 4;
        damage = 3;
    }
    public void define(){
        walking = manager.get("audio/Sounds/triceStep.mp3", Sound.class);
        walking.play(0.5f, 10, 10);
        walking.loop();
        setBounds(player.b2Body.getPosition().x, player.b2Body.getPosition().y, 64 / GameLogic.PPM, 32 / GameLogic.PPM);
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
        else if (runChangeAnimation){
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
            case JUMPING:
                region = jumpAnimation.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = runAnimation.getKeyFrame(stateTimer, true);
                break;
            case HITTING:
                region = hitAnimation.getKeyFrame(stateTimer);
                checkRunAndAnimation();
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

    private void checkRunAndAnimation() {
        if(runningRight){
            setAttackFixture(34, 9);
        }
        else{
            setAttackFixture(-34, 9);
        }
        if(hitAnimation.isAnimationFinished(stateTimer) && player.b2Body.getFixtureList().size >= 3){
            destroyFixtures(2);
            isHitting = false;
        }
    }
}
