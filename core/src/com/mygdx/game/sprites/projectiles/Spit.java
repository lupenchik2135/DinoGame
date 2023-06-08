package com.mygdx.game.sprites.projectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;
import com.mygdx.game.sprites.playable.Player;

public class Spit extends Projectile {
    public Spit(Level screen, float x, float y) {
        super(screen, x, y);
        setRegion(screen.getAtlas().findRegion("pixelHeart"), 0, 0, 16, 16);
        velocity = new Vector2(0, 0);
        damage = 1;
    }

    @Override
    public void defineProjectile() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(4 / GameLogic.PPM, 4 / GameLogic.PPM);

        fdef.shape = shape;
        fdef.filter.categoryBits = GameLogic.PROJECTILE_BIT;
        fdef.filter.maskBits = GameLogic.GROUND_BIT |
                GameLogic.STONE_WALL |
                GameLogic.ENEMY_BIT |
                GameLogic.OBJECT_BIT;
        body.createFixture(fdef).setUserData(this);
        body.applyLinearImpulse(new Vector2(0.05f, 0), body.getWorldCenter(), true);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

    }

}
