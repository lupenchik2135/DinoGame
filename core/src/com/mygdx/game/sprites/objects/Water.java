package com.mygdx.game.sprites.objects;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;
import com.mygdx.game.screens.LevelOne;


public class Water {

    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;
    protected Level screen;
    protected  Fixture fixture;

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

        fixture.setUserData(this);
        Filter filter = new Filter();
        filter.categoryBits = GameLogic.WATER_BIT;
        fixture.setFilterData(filter);
    }
}
