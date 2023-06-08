package com.mygdx.game.sprites.playable.forms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;
import com.mygdx.game.sprites.playable.Player;

public class Human extends Form{
    public Human(Level screen, Player player){
        super(screen, "Woodcutter", player);
        this.type = "Human";

        frames.add(new TextureRegion(getTexture(),700, 318, 27, 34));
        frames.add(new TextureRegion(getTexture(),749, 318, 27, 34));
        frames.add(new TextureRegion(getTexture(),800, 318, 27, 34));
        frames.add(new TextureRegion(getTexture(),843, 318, 27, 34));
        frames.add(new TextureRegion(getTexture(),893, 318, 27, 34));
        frames.add(new TextureRegion(getTexture(),944, 318, 27, 34));

        runAnimation = new Animation<>(0.9f, frames);
        frames.clear();

        frames.add(new TextureRegion(getTexture(), 704, 569, 27, 34));
        frames.add(new TextureRegion(getTexture(), 752, 570, 27, 34));
        frames.add(new TextureRegion(getTexture(), 803, 569, 27, 34));
        frames.add(new TextureRegion(getTexture(), 850, 566, 27, 34));
        frames.add(new TextureRegion(getTexture(), 896, 563, 27, 34));
        frames.add(new TextureRegion(getTexture(), 946, 570, 27, 34));

        jumpAnimation = new Animation<>(0.1f, frames);
        frames.clear();
        // change animation
        frames.add(new TextureRegion(getTexture(),705 , 518, 48, 30));
        frames.add(new TextureRegion(getTexture(),705, 133, 59, 30));
        frames.add(new TextureRegion(getTexture(), 705, 137, 65, 28));

        hitAnimation = new Animation<>(0.1f, frames);
        frames.clear();

        standTexture = new TextureRegion(getTexture(), 985, 769, 27, 33);
        velocityX = 250 / GameLogic.PPM;
        jumpHeight = 350 / GameLogic.PPM;

        currentFormHealth = 3;
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
            case RUNNING:
                region = runAnimation.getKeyFrame(stateTimer, true);
                break;
            case HITTING:
                region = hitAnimation.getKeyFrame(stateTimer);
                if(runningRight){
                    FixtureDef fdefRight = new FixtureDef();
                    fdefRight.filter.categoryBits = GameLogic.PLAYER_ATTACK_BIT;
                    EdgeShape head = new EdgeShape();
                    head.set(34 / GameLogic.PPM, -9 / GameLogic.PPM, 34 / GameLogic.PPM, 9 / GameLogic.PPM);
                    fdefRight.shape = head;
                    fdefRight.isSensor = true;
                    player.b2Body.createFixture(fdefRight).setUserData(player);
                }
                else{
                    FixtureDef fdefLeft = new FixtureDef();
                    fdefLeft.filter.categoryBits = GameLogic.PLAYER_ATTACK_BIT;
                    EdgeShape head = new EdgeShape();
                    head.set(-34 / GameLogic.PPM, -9 / GameLogic.PPM, -34 / GameLogic.PPM, 9 / GameLogic.PPM);
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
            case JUMPING:
                region = jumpAnimation.getKeyFrame(stateTimer);
                player.ableToJump(false);
                break;
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
                GameLogic.OBJECT_BIT;

        EdgeShape head = new EdgeShape();
        head.set(8 / GameLogic.PPM, 0 / GameLogic.PPM, 8 / GameLogic.PPM, 9 / GameLogic.PPM);
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
        head.set(-8 / GameLogic.PPM, 0 / GameLogic.PPM, -8 / GameLogic.PPM, 9 / GameLogic.PPM);
        fdefLeft.shape = head;
        fdefLeft.isSensor = true;
        player.b2Body.createFixture(fdefLeft).setUserData(player);
    }
    public State getState(){
        if(runChangeAnimation){
            return State.CHANGING;
        }
        else if (player.b2Body.getLinearVelocity().y > 0 || (player.b2Body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if (player.b2Body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (player.b2Body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else if (Gdx.input.isTouched()){
            return State.HITTING;
        }
        else
            return State.STANDING;
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
        setBounds(player.b2Body.getPosition().x, player.b2Body.getPosition().y, 27 / GameLogic.PPM, 35 / GameLogic.PPM);
        setRegion(standTexture);
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
                GameLogic.WATER_BIT |
                GameLogic.OBJECT_BIT |
                GameLogic.ITEM_BIT;
        fdef.shape = shape;
        if (player.b2Body != null){
            this.player.b2Body.createFixture(fdef).setUserData(player);
        }
        destroyed = false;

    }
}
