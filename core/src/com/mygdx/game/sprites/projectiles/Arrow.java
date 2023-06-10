package com.mygdx.game.sprites.projectiles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;

public class Arrow extends Projectile{
    public Arrow(Level screen, float x, float y, boolean right) {
        super(screen, x, y, right);
        setRegion(screen.getAtlas().findRegion("Projectiles"), 23, 2, 16, 16);
        setBounds(getX(), getY(), 16 / GameLogic.PPM, 16 / GameLogic.PPM);
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
                GameLogic.PLAYER_BIT;
        body.createFixture(fdef).setUserData(this);
        if(right){
            body.applyLinearImpulse(new Vector2(1.2f, 4f), body.getWorldCenter(), true);
        }else body.applyLinearImpulse(new Vector2(-1.2f, 4f), body.getWorldCenter(), true);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
    }

}
