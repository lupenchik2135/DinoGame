package com.mygdx.game.sprites.objects;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.PlayScreen;
import com.mygdx.game.sprites.playable.Player;
import com.mygdx.game.sprites.playable.forms.Form;

public abstract class InteractiveTileObject {
    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;
    protected PlayScreen screen;
    protected  Fixture fixture;
    InteractiveTileObject(PlayScreen screen, Rectangle bounds){
        this.screen = screen;
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.bounds = bounds;

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((bounds.getX() + bounds.getWidth()/2) / GameLogic.PPM, (bounds.getY() + bounds.getHeight()/2) / GameLogic.PPM);

        body = world.createBody(bdef);

        shape.setAsBox((bounds.getWidth() / 2) / GameLogic.PPM, (bounds .getHeight() / 2) / GameLogic.PPM);

        fdef.shape = shape;
        fixture = body.createFixture(fdef);
    }
    public abstract void onHeadHit(Player player);
    public void setCategoryFilter(short filterBit){
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }
    public TiledMapTileLayer.Cell getCell(){
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
        return layer.getCell((int) (body.getPosition().x * GameLogic.PPM / 16),
                (int) ( + body.getPosition().y * GameLogic.PPM / 16));
    }
}
