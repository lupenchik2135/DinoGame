package com.mygdx.game.tools;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;
import com.mygdx.game.sprites.enemies.DistantEnemy;
import com.mygdx.game.sprites.enemies.MillyEnemy;
import com.mygdx.game.sprites.enemies.SmallEnemy;
import com.mygdx.game.sprites.objects.Mud;
import com.mygdx.game.sprites.objects.StoneWall;
import com.mygdx.game.sprites.objects.Water;

public class B2WorldCreator {
    private final Array<SmallEnemy> smallEnemies;
    private final Array<MillyEnemy> millyEnemies;
    private final Array<DistantEnemy> distantEnemies;
    public B2WorldCreator(Level screen){
        World world = screen.getWorld();
        TiledMap tiledMap = screen.getMap();
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //create ground bodies/fixtures
        for (RectangleMapObject object : tiledMap.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = object.getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth()/2) / GameLogic.PPM, (rect.getY() + rect.getHeight()/2) / GameLogic.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / GameLogic.PPM, (rect.getHeight() / 2) / GameLogic.PPM);

            fdef.shape = shape;

            body.createFixture(fdef);
        }
        for (RectangleMapObject object : tiledMap.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = object.getRectangle();
            new StoneWall(screen, rect);
        }
        //create mud
        for (RectangleMapObject object : tiledMap.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = object.getRectangle();
            new Mud(screen, rect);
        }
        //create warriors
        smallEnemies = new Array<>();
        for (RectangleMapObject object : tiledMap.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = object.getRectangle();
            smallEnemies.add(new SmallEnemy(screen, rect.getX() / GameLogic.PPM, rect.getY() / GameLogic.PPM));
        }
        millyEnemies = new Array<>();
        for (RectangleMapObject object : tiledMap.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = object.getRectangle();
            millyEnemies.add(new MillyEnemy(screen, rect.getX() / GameLogic.PPM, rect.getY() / GameLogic.PPM));
        }
        distantEnemies = new Array<>();
        for (RectangleMapObject object : tiledMap.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = object.getRectangle();
            distantEnemies.add(new DistantEnemy(screen, rect.getX() / GameLogic.PPM, rect.getY() / GameLogic.PPM));
        }
        //добавить воду
        for (RectangleMapObject object : tiledMap.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = object.getRectangle();
            new Water(screen, rect);
        }
    }

    public Array<SmallEnemy> getSmallEnemies() {
        return smallEnemies;
    }
    public Array<MillyEnemy> getMillyEnemies() {
        return millyEnemies;
    }
    public Array<DistantEnemy> getDistantEnemies() {
        return distantEnemies;
    }
}
