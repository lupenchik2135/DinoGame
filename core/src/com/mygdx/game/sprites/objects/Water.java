package com.mygdx.game.sprites.objects;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;


public class Water {

    protected World world;
    protected TiledMap map;
    protected Rectangle bounds;
    protected Body body;
    protected Level screen;
    protected Fixture fixture;

    public Water(Level screen, Rectangle bounds) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.bounds = bounds;

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((bounds.getX() + bounds.getWidth() / 2) / GameLogic.PPM, (bounds.getY() + bounds.getHeight() / 2) / GameLogic.PPM);

        body = world.createBody(bdef);

        shape.setAsBox((bounds.getWidth() / 2) / GameLogic.PPM, (bounds.getHeight() / 2) / GameLogic.PPM);

        fdef.shape = shape;
        fdef.isSensor = true;
        fixture = body.createFixture(fdef);
        fixture.setDensity(0.1f);
        fixture.setUserData(this);
        Filter filter = new Filter();
        filter.categoryBits = GameLogic.WATER_BIT;
        filter.maskBits = GameLogic.PLAYER_BIT;
        fixture.setFilterData(filter);
    }
}
