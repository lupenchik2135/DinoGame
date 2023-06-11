package com.mygdx.game.sprites.items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;
import com.mygdx.game.sprites.playable.Player;

public class Heart extends Item {
    private boolean right;

    public Heart(Level screen, float x, float y, boolean right) {
        super(screen, x, y);
        this.right = right;
        setRegion(screen.getAtlas().findRegion("Projectiles"), 2, 2, 16, 16);
        velocity = new Vector2(0, 0);
    }

    @Override
    public void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(4 / GameLogic.PPM, 4 / GameLogic.PPM);

        fdef.shape = shape;
        fdef.filter.categoryBits = GameLogic.ITEM_BIT;
        fdef.filter.maskBits = GameLogic.GROUND_BIT |
                GameLogic.STONE_WALL |
                GameLogic.TYRANNOSAUR_BIT |
                GameLogic.PLAYER_BIT;
        body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void use(Player player) {
        isUsing = true;
        player.getHeal();
        isUsing = false;
        destroy();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        if (right) {
            body.applyLinearImpulse(new Vector2(0.01f, 0), body.getWorldCenter(), true);
        } else {
            body.applyLinearImpulse(new Vector2(-0.01f, 0), body.getWorldCenter(), true);
        }

    }
}
