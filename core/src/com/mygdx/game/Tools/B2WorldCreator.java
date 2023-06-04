package com.mygdx.game.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.GameLogic;
import com.mygdx.game.Sprites.StoneWall;

public class B2WorldCreator {
    public B2WorldCreator(World world, TiledMap tiledMap){
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //create ground bodies/fixtures
        for (MapObject object : tiledMap.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth()/2) / GameLogic.PPM, (rect.getY() + rect.getHeight()/2) / GameLogic.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / GameLogic.PPM, (rect.getHeight() / 2) / GameLogic.PPM);

            fdef.shape = shape;
            body.createFixture(fdef);
        }
        for (MapObject object : tiledMap.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            new StoneWall(world, tiledMap, rect);
        }
    }
}
