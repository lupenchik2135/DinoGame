package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.GameLogic;

public class Worker extends Sprite {
    public World world;
    public Body b2Body;

    public Worker(World world){
        this.world = world;
        defineWorker();
    }

    public void defineWorker(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / GameLogic.PPM,32 / GameLogic.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5 / GameLogic.PPM);

        fdef.shape = shape;
        b2Body.createFixture(fdef);
    }
}
