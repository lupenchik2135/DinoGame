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

public class Archeopteryx extends Form {
    private boolean isFlying;

    public Archeopteryx(Level screen, Player player) {
        super(screen, "Archeopteryx", player);
        this.type = "Archeopteryx";
        walking = manager.get("audio/Sounds/triceStep.mp3", Sound.class);
        frames.add(new TextureRegion(getTexture(), 3840 + 57 + 20, 85, 60, 30));
        frames.add(new TextureRegion(getTexture(), 3840 + 57 + 20 + 60 + 20, 85, 60, 30));
        frames.add(new TextureRegion(getTexture(), 3840 + 57 + 20 + 60 + 20 + 55 + 20, 85, 60, 30));
        frames.add(new TextureRegion(getTexture(), 3840 + 57 + 20 + 60 + 20 + 55 + 20 + 56 + 20, 85, 60, 30));
        frames.add(new TextureRegion(getTexture(), 3840 + 57 + 20 + 60 + 20 + 55 + 20 + 56 + 20 + 60 + 20, 85, 60, 30));
        frames.add(new TextureRegion(getTexture(), 3840 + 57 + 20 + 60 + 20 + 55 + 20 + 56 + 20 + 60 + 20, 85, 60, 30));

        runAnimation = new Animation<>(0.1f, frames);
        frames.clear();


        frames.add(new TextureRegion(getTexture(), 4302, 84, 57, 30));
        frames.add(new TextureRegion(getTexture(), 4378, 87, 53, 24));
        jumpAnimation = new Animation<>(1.3f, frames);
        frames.clear();

        frames.add(new TextureRegion(getTexture(), 4593 - 71, 81, 50, 30));
        frames.add(new TextureRegion(getTexture(), 4593, 78, 51, 33));
        frames.add(new TextureRegion(getTexture(), 4593 + 72, 79, 48, 33));
        hitAnimation = new Animation<>(0.2f, frames);
        frames.clear();

        standTexture = new TextureRegion(getTexture(), 3757, 83, 63, 30);
        frames.clear();

        frames.add(new TextureRegion(getTexture(), 4731, 83, 49, 30));
        frames.add(new TextureRegion(getTexture(), 4731 + 69, 87, 46, 25));
        frames.add(new TextureRegion(getTexture(), 4731 + 69 + 66, 90, 42, 21));
        frames.add(new TextureRegion(getTexture(), 4731 + 69 + 65 + 62, 95, 38, 25));
        frames.add(new TextureRegion(getTexture(), 4731 + 69 + 65 + 62 + 58, 102, 38, 10));
        frames.add(new TextureRegion(getTexture(), 4731 + 69 + 65 + 62 + 58 + 58, 102, 38, 10));
        deadAnimation = new Animation<>(0.5f, frames);
        frames.clear();


        velocityX = 400 / GameLogic.PPM;
        jumpHeight = 450 / GameLogic.PPM;
        damage = 1;
        currentFormHealth = 2;

    }

    public void define() {
        walking.play(0.5f, 10, 10);
        walking.loop();
        setBounds(player.b2Body.getPosition().x, player.b2Body.getPosition().y, 18 / GameLogic.PPM, 16 / GameLogic.PPM);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(6 / GameLogic.PPM, 6 / GameLogic.PPM);
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
        this.player.b2Body.createFixture(fdef).setUserData(player);
        destroyed = false;
    }

    public Form.State getState() {
        if (isDead) {
            return State.DEAD;
        } else if (runChangeAnimation) {
            return State.CHANGING;
        } else if (isHitting || Gdx.input.justTouched()) {
            isHitting = true;
            return State.HITTING;
        } else if (player.b2Body.getLinearVelocity().y > 0)
            return State.JUMPING;
        else if (isFlying) {
            return State.FLYING;
        } else if (player.b2Body.getLinearVelocity().y < 0)
            return Form.State.FALLING;
        else if (player.b2Body.getLinearVelocity().x != 0)
            return Form.State.RUNNING;
        else
            return Form.State.STANDING;
    }

    public void fly() {
        isFlying = true;
    }

    public TextureRegion getFrame(float deltaTime) {
        currentState = getState();
        TextureRegion region;
        switch (currentState) {
            case DEAD:
                region = deadAnimation.getKeyFrame(stateTimer);
                break;
            case CHANGING:
                region = changeForm;
                if (stateTimer > 1) {
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
            case FLYING:
                region = jumpAnimation.getKeyFrame(stateTimer);
                if (player.getIsAbleToJump() || !Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                    isFlying = false;
                }
                break;
            case FALLING:
            case STANDING:
            default:
                region = standTexture;
                break;
        }
        if ((player.b2Body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        } else if (((player.b2Body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX())) {
            region.flip(true, false);
            runningRight = true;
        }
        stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
        previousState = currentState;
        return region;
    }

    private void checkRunAndAnimation() {
        if (runningRight) {
            setAttackFixture(8, 6);
        } else {
            setAttackFixture(-8, 6);
        }
        if (hitAnimation.isAnimationFinished(stateTimer) && player.b2Body.getFixtureList().size >= 3) {
            destroyFixtures(2);
            isHitting = false;
        }
    }
}
