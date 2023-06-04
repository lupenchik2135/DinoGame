package com.mygdx.game.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.GameLogic;

public class StoneWall extends InteractiveTileObject{

    public StoneWall(World world, TiledMap tiledMap, Rectangle bounds) {
        super(world, tiledMap, bounds);
        fixture.setUserData(this);
        setCategoryFilter(GameLogic.STONE_WALL);
    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("stone", "collusion");
        setCategoryFilter(GameLogic.DESTROYED_BIT);
        getCell().setTile(null);
    }
}
