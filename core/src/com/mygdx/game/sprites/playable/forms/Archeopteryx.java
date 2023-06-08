package com.mygdx.game.sprites.playable.forms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;
import com.mygdx.game.sprites.playable.Player;

public class Archeopteryx extends Form{
    private boolean isFlying;

    public Archeopteryx(Level screen, Player player){
        super(screen, "Archeopteryx", player);
        this.type = "Archeopteryx";
        for (int i = 0; i <= 5; i ++){
            if (i <= 2) frames.add(new TextureRegion(getTexture(), 479 + (i * 64), 225 + 431, 60, 30));
            else frames.add(new TextureRegion(getTexture(), 479 + ((i - 3) * 64), 225 + 466, 60, 30));
        }
        runAnimation = new Animation<>(0.1f, frames);
        frames.clear();


        frames.add(new TextureRegion(getTexture(), 479, 225 + 290, 57, 30));
        frames.add(new TextureRegion(getTexture(), 479 + 65, 225 + 293, 53, 24));

        jumpAnimation = new Animation<>(1.3f, frames);
        frames.clear();
// change animation
        for(int i = 0; i < 7; i++){
            if(i <= 3) frames.add(new TextureRegion(getTexture(),479  + i * 51, 225 + 191, 53, 25));
            else frames.add(new TextureRegion(getTexture(), 479 + (i - 3) * 51, 225 + 225, 51, 30));
        }
        hitAnimation = new Animation<>(0.1f, frames);
        frames.clear();

        standTexture = new TextureRegion(getTexture(), 479, 225, 63, 30);

        for(int i = 0; i < 5; i++){
            if(i <= 2) frames.add(new TextureRegion(getTexture(),478  + i *55 , 593, 49, 30));
            else frames.add(new TextureRegion(getTexture(),478  + (i-2) *42, 630, 49, 30));
        }
        deadAnimation = new Animation<>(0.5f, frames);
        frames.clear();
        velocityX = 300 / GameLogic.PPM;
        jumpHeight = 450 / GameLogic.PPM;
        currentFormHealth = 2;
    }


    public TextureRegion getFrame(float deltaTime){
        currentState = getState();
        TextureRegion region;
        switch (currentState){
            case DEAD:
                region = deadAnimation.getKeyFrame(stateTimer);
                break;
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
                break;
            case FLYING:
                region = jumpAnimation.getKeyFrame(stateTimer);
                if(player.getIsAbleToJump() || !Gdx.input.isKeyPressed(Input.Keys.SPACE)){
                    isFlying = false;
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

    public void setRightFixture(){
        if (player.b2Body.getFixtureList().size >= 2){
            while (player.b2Body.getFixtureList().size > 1) {
                player.b2Body.destroyFixture(player.b2Body.getFixtureList().get(1));
            }
        }
        FixtureDef fdefRight = new FixtureDef();
        fdefRight.filter.categoryBits = GameLogic.PLAYER_ATTACK_BIT;
        fdefRight.filter.maskBits = GameLogic.GROUND_BIT |
                GameLogic.STONE_WALL |
                GameLogic.ENEMY_BIT |
                GameLogic.SMALL_ENEMY_BIT |
                GameLogic.OBJECT_BIT |
                GameLogic.SMALL_ENEMY_HEAD_BIT;

        EdgeShape head = new EdgeShape();
        head.set(32 / GameLogic.PPM, 0 / GameLogic.PPM, 32 / GameLogic.PPM, 9 / GameLogic.PPM);
        fdefRight.shape = head;
        fdefRight.isSensor = true;
        player.b2Body.createFixture(fdefRight).setUserData(player);
    }
    public void setLeftFixture(){
        if (player.b2Body.getFixtureList().size >= 2){
            while (player.b2Body.getFixtureList().size > 1) {
                player.b2Body.destroyFixture(player.b2Body.getFixtureList().get(1));
            }
        }
        FixtureDef fdefLeft = new FixtureDef();
        fdefLeft.filter.categoryBits = GameLogic.PLAYER_ATTACK_BIT;
        fdefLeft.filter.maskBits = GameLogic.GROUND_BIT |
                GameLogic.STONE_WALL |
                GameLogic.ENEMY_BIT |
                GameLogic.OBJECT_BIT;
        EdgeShape head = new EdgeShape();
        head.set(-32 / GameLogic.PPM, 0 / GameLogic.PPM, -32 / GameLogic.PPM, 9 / GameLogic.PPM);
        fdefLeft.shape = head;
        fdefLeft.isSensor = true;
        player.b2Body.createFixture(fdefLeft).setUserData(player);
    }
    public Form.State getState(){
        if(isDead){
            return State.DEAD;
        }
        else if(runChangeAnimation){
            return State.CHANGING;
        }
        else if (player.b2Body.getLinearVelocity().y > 0)
            return State.JUMPING;
        else if (isFlying){
            return State.FLYING;
        }
        else if (player.b2Body.getLinearVelocity().y < 0)
            return Form.State.FALLING;
        else if (player.b2Body.getLinearVelocity().x != 0)
            return Form.State.RUNNING;
        else if (Gdx.input.isTouched()){
            return Form.State.HITTING;
        }
        else
            return Form.State.STANDING;
    }
    public void fly(){
        isFlying = true;
    }
    public void setRightTriceFixture(){
        /* add head later*/
    }
    public void setLeftTriceFixture(){
        /* add head later*/
    }
    public void define(){
        if (player.b2Body.getFixtureList().size >= 2){
            while (player.b2Body.getFixtureList().size > 1) {
                player.b2Body.destroyFixture(player.b2Body.getFixtureList().get(1));
            }
        }
        walking = manager.get("audio/Sounds/triceStep.mp3", Sound.class);
        walking.play(0.5f, 10, 10);
        walking.loop();
        setBounds(player.b2Body.getPosition().x, player.b2Body.getPosition().y, 18 / GameLogic.PPM, 16 / GameLogic.PPM);
        setRegion(standTexture);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(6 / GameLogic.PPM, 6 / GameLogic.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.filter.categoryBits = GameLogic.PLAYER_BIT;
        fdef.filter.maskBits = GameLogic.GROUND_BIT |
                GameLogic.STONE_WALL |
                GameLogic.SMALL_ENEMY_BIT |
                GameLogic.ENEMY_BIT |
                GameLogic.OBJECT_BIT |
                GameLogic.PROJECTILE_BIT |
                GameLogic.WATER_BIT |
                GameLogic.ITEM_BIT;
        fdef.shape = shape;
        this.player.b2Body.createFixture(fdef).setUserData(player);
        destroyed = false;
    }
}
