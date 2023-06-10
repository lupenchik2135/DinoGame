package com.mygdx.game.sprites.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;
import com.mygdx.game.sprites.items.Heart;
import com.mygdx.game.sprites.objects.ObjectDef;


public class SmallEnemy extends Enemy{
    private Animation<TextureRegion> walkAnimation;
    private boolean runningRight;
    public SmallEnemy(Level screen, float x, float y) {
        super(screen,"Mobs", x, y);
        this.screen = screen;
        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(getTexture(), 3757, 62, 20, 14));
        frames.add(new TextureRegion(getTexture(), 3757 + 30, 62, 20, 14));
        frames.add(new TextureRegion(getTexture(), 3757 + 30 + 30, 62, 18, 15));
        frames.add(new TextureRegion(getTexture(), 3757 + 30 + 30 + 28 , 62, 20, 19));
        walkAnimation = new Animation<>(0.1f, frames);
        frames.clear();
        stateTime = 0;
        setBounds(getX(), getY(), 16 / GameLogic.PPM, 16 / GameLogic.PPM);
        setToDestroy = false;
        destroyed = false;
    }

    public void update(float deltaTime){
        stateTime += deltaTime;
        if(setToDestroy && !destroyed){
            b2Body.getFixtureList().get(0).getFilterData().categoryBits = GameLogic.GROUND_BIT;
            setBounds(getX(), getY(), 16 / GameLogic.PPM, 8 / GameLogic.PPM);
            screen.spawnObject(new ObjectDef(new Vector2(b2Body.getPosition().x+ 16 / GameLogic.PPM, b2Body.getPosition().y),
                    Heart.class, runningRight));
            destroyed = true;
            stateTime = 0;
            world.destroyBody(b2Body);
        }
        else if (!destroyed){
            b2Body.setLinearVelocity(velocity);
            setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - getHeight() / 2);
            setRegion(getFrame());
        }
    }
    public TextureRegion getFrame(){
        TextureRegion region;
        region = walkAnimation.getKeyFrame(stateTime, true);
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
        fdef.filter.categoryBits = GameLogic.SMALL_ENEMY_BIT;
        fdef.filter.maskBits = GameLogic.GROUND_BIT |
                GameLogic.STONE_WALL |
                GameLogic.ENEMY_BIT |
                GameLogic.SMALL_ENEMY_BIT |
                GameLogic.PLAYER_BIT |
                GameLogic.TYRANNOSAUR_BIT |
                GameLogic.ENEMY_STOPPER;
        fdef.shape = shape;
        b2Body.createFixture(fdef).setUserData(this);

        PolygonShape hand = new PolygonShape();
        Vector2[] vectrice = new Vector2[4];
        vectrice[0] = new Vector2(-6.5f, 10).scl(1/GameLogic.PPM);
        vectrice[1] = new Vector2(6.5f, 10).scl(1/GameLogic.PPM);
        vectrice[2] = new Vector2(-6.5f, 3).scl(1/GameLogic.PPM);
        vectrice[3] = new Vector2(6.5f, 3).scl(1/GameLogic.PPM);
        hand.set(vectrice);

        fdef.shape = hand;
        fdef.restitution = 0.5f;
        fdef.filter.categoryBits = GameLogic.SMALL_ENEMY_HEAD_BIT;
        fdef.filter.maskBits = GameLogic.GROUND_BIT |
                GameLogic.STONE_WALL |
                GameLogic.ENEMY_BIT |
                GameLogic.SMALL_ENEMY_BIT |
                GameLogic.PLAYER_BIT |
                GameLogic.TYRANNOSAUR_BIT;
        b2Body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void draw(Batch batch){
        if(!destroyed||stateTime<1 ){
            super.draw(batch);
        }
    }
    public void hitOnHead() {
        setToDestroy = true;
    }
}
