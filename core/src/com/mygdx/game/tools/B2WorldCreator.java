package com.mygdx.game.tools;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.PlayScreen;
import com.mygdx.game.sprites.enemies.MillyWarrior;
import com.mygdx.game.sprites.objects.Mud;
import com.mygdx.game.sprites.objects.StoneWall;

public class B2WorldCreator {
    private final Array<MillyWarrior> millyWarriors;
    public B2WorldCreator(PlayScreen screen){
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
        //create mid
        for (RectangleMapObject object : tiledMap.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = object.getRectangle();
            new Mud(screen, rect);
        }
        //create warriors
        millyWarriors = new Array<>();
        for (RectangleMapObject object : tiledMap.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = object.getRectangle();
            millyWarriors.add(new MillyWarrior(screen, rect.getX() / GameLogic.PPM, rect.getY() / GameLogic.PPM));
        }
    }

    public Array<MillyWarrior> getMillyWarriors() {
        return millyWarriors;
    }
}
