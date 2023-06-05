package com.mygdx.game.Sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameLogic;
import com.mygdx.game.Screens.PlayScreen;



public class MillyWarrior extends Enemy{
    private float stateTime;
    private Animation standAnimation;
    private Animation walkAnimation;
    private boolean runningRight;
    private boolean setToDestroy;
    private boolean destroyed;
    public MillyWarrior(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 0; i <= 5; i ++){
            if (i <= 2) frames.add(new TextureRegion(screen.getAtlas().findRegion("Archeopterix"), 4 + (i * 64), 437, 60, 30));
            else frames.add(new TextureRegion(screen.getAtlas().findRegion("Archeopterix"), 4 + ((i - 3) * 64), 472, 60, 30));
        }
        walkAnimation = new Animation(0.1f, frames);
        frames.clear();
        stateTime = 0;
        setBounds(getX(), getY(), 16 / GameLogic.PPM, 16 / GameLogic.PPM);
        setToDestroy = false;
        destroyed = false;
    }

    public void update(float deltaTime){
        stateTime += deltaTime;
        if(setToDestroy && !destroyed){
            world.destroyBody(b2Body);
            destroyed = true;
            setBounds(getX(), getY(), 16 / GameLogic.PPM, 8 / GameLogic.PPM);
        }
        else if (!destroyed){
            setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - getHeight() / 2);
            setRegion(getFrame(deltaTime));
        }
    }
    public TextureRegion getFrame(float deltaTime){
        TextureRegion region;
        region = (TextureRegion) walkAnimation.getKeyFrame(stateTime, true);
        if ((b2Body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }
        else if (((b2Body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX())){
            region.flip(true, false);
            runningRight = true;
        }
        return region;
    }
    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(8 / GameLogic.PPM, 8 / GameLogic.PPM);
        fdef.filter.categoryBits = GameLogic.ENEMY_BIT;
        fdef.filter.maskBits = GameLogic.GROUND_BIT |
                GameLogic.STONE_WALL |
                GameLogic.ENEMY_BIT |
                GameLogic.PLAYER_BIT |
                GameLogic.OBJECT_BIT;
        fdef.shape = shape;
        b2Body.createFixture(fdef).setUserData(this);

//        PolygonShape hand = new PolygonShape();
//        Vector2[] vectrice = new Vector2[4];
//        vectrice[0] = new Vector2(-5, 8).scl(1/GameLogic.PPM);
//        vectrice[1] = new Vector2(5, 8).scl(1/GameLogic.PPM);
//        vectrice[2] = new Vector2(-3, 3).scl(1/GameLogic.PPM);
//        vectrice[3] = new Vector2(3, 3).scl(1/GameLogic.PPM);
//        hand.set(vectrice);
//
//        fdef.shape = hand;
//        fdef.restitution = 0.5f;
//        fdef.filter.categoryBits = GameLogic.ENEMY_HAND_BIT;
//        b2Body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void hitOnHead() {
        setToDestroy = true;
    }
}
