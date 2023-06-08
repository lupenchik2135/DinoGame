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
import com.mygdx.game.sprites.items.Heart;
import com.mygdx.game.sprites.objects.ObjectDef;
import com.mygdx.game.sprites.playable.Player;
import com.mygdx.game.sprites.projectiles.Spit;

public class Ichtiozaur extends Form {
    private boolean isSwimming;
    private Animation<TextureRegion> swimUpAnimation;

    private Animation<TextureRegion> swimDownAnimation;
    private Animation<TextureRegion> swimAnimation;


    public Ichtiozaur(Level screen, Player player){
        super(screen, "Ichtio", player);
        this.type = "Ichtiozaur";
        for(int i = 0; i < 5; i++){
            if(i <= 1) frames.add(new TextureRegion(getTexture(),2 + i * 61, 208, 58, 30));
            else if(i <= 3) frames.add(new TextureRegion(getTexture(), 2 + (i - 2) * 56, 244, 53, 32));
            else frames.add(new TextureRegion(getTexture(),113, 242, 58, 32));
        }
        runAnimation = new Animation<>(0.1f, frames);
        frames.clear();
// change animation
        frames.add(new TextureRegion(getTexture(),2 , 125, 48, 30));
        frames.add(new TextureRegion(getTexture(),58, 133, 59, 30));
        frames.add(new TextureRegion(getTexture(), 122, 137, 65, 28));

        hitAnimation = new Animation<>(0.1f, frames);

        frames.clear();
        frames.add(new TextureRegion(getTexture(),2 , 125, 48, 30));
        frames.add(new TextureRegion(getTexture(),58, 133, 59, 30));
        frames.add(new TextureRegion(getTexture(), 122, 137, 65, 28));

        swimUpAnimation = new Animation<>(0.1f, frames);

        frames.clear();
        frames.add(new TextureRegion(getTexture(),2 , 125, 48, 30));
        frames.add(new TextureRegion(getTexture(),58, 133, 59, 30));
        frames.add(new TextureRegion(getTexture(), 122, 137, 65, 28));

        swimDownAnimation = new Animation<>(0.1f, frames);

        frames.clear();frames.clear();
        frames.add(new TextureRegion(getTexture(),2 , 125, 48, 30));
        frames.add(new TextureRegion(getTexture(),58, 133, 59, 30));
        frames.add(new TextureRegion(getTexture(), 122, 137, 65, 28));

        swimAnimation = new Animation<>(0.1f, frames);
        frames.clear();



        standTexture = new TextureRegion(getTexture(), 2, 53, 55, 30);
        velocityX = 50 / GameLogic.PPM;
        jumpHeight = 0 / GameLogic.PPM;
        currentFormHealth = 4;
        isSwimming = false;
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
                    screen.spawnObject(new ObjectDef(new Vector2(player.b2Body.getPosition().x + 50 / GameLogic.PPM, player.b2Body.getPosition().y),
                            Spit.class));
                }
                else {
                    screen.spawnObject(new ObjectDef(new Vector2(player.b2Body.getPosition().x - 50 / GameLogic.PPM, player.b2Body.getPosition().y),
                            Spit.class));
                }

                break;
            case SWIMMING:
                velocityX = 200 / GameLogic.PPM;
                jumpHeight = 200 / GameLogic.PPM;
                if (Gdx.input.isKeyPressed(Input.Keys.W) && player.b2Body.getLinearVelocity().y <= player.getCurrentForm().getJumpHeight()) {
                    player.b2Body.applyLinearImpulse(new Vector2(0, 0.3f), player.b2Body.getWorldCenter(), true);
                    region = swimUpAnimation.getKeyFrame(stateTimer);
                }
                else if (Gdx.input.isKeyPressed(Input.Keys.S) && player.b2Body.getLinearVelocity().y >= -player.getCurrentForm().getJumpHeight()) {
                    player.b2Body.applyLinearImpulse(new Vector2(0, -0.3f), player.b2Body.getWorldCenter(), true);
                    region = swimDownAnimation.getKeyFrame(stateTimer);
                } else
                    region = swimAnimation.getKeyFrame(stateTimer, true);
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
    public void define(){
        if (player.b2Body.getFixtureList().size >= 2){
            while (player.b2Body.getFixtureList().size > 1) {
                player.b2Body.destroyFixture(player.b2Body.getFixtureList().get(1));
            }
        }
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
                GameLogic.OBJECT_BIT |
                GameLogic.SMALL_ENEMY_BIT |
                GameLogic.WATER_BIT |
                GameLogic.ITEM_BIT;
        fdef.shape = shape;
        if (player.b2Body != null){
            this.player.b2Body.createFixture(fdef).setUserData(player);
        }
        destroyed = false;
    }
    public boolean isSwimming() {
        return isSwimming;
    }

    public void setSwimming(boolean swimming) {
        isSwimming = swimming;
    }
}
