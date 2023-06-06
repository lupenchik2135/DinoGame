package com.mygdx.game.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.GameLogic;
import com.mygdx.game.sprites.enemies.Enemy;
import com.mygdx.game.sprites.items.Item;
import com.mygdx.game.sprites.objects.InteractiveTileObject;
import com.mygdx.game.sprites.playable.Player;
import com.mygdx.game.sprites.playable.forms.Form;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        // this is how collisions work
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
        switch (cDef){
            //head with stone wall
            case GameLogic.HEAD_BIT | GameLogic.STONE_WALL:
                if(fixA.getFilterData().categoryBits == GameLogic.HEAD_BIT)
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Player) fixA.getUserData());
                else
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Player) fixB.getUserData());
                break;
            //player stands on enemy
            case GameLogic.ENEMY_HEAD_BIT | GameLogic.PLAYER_BIT:
                if(fixA.getFilterData().categoryBits == GameLogic.ENEMY_HEAD_BIT)
                    ((Enemy)fixA.getUserData()).hitOnHead();
                else
                    ((Enemy)fixB.getUserData()).hitOnHead();
                break;
            //enemy touches objext
            case GameLogic.ENEMY_BIT | GameLogic.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == GameLogic.ENEMY_BIT)
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                break;
            case GameLogic.PLAYER_BIT | GameLogic.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == GameLogic.PLAYER_BIT)
                    ((Player) fixA.getUserData()).hit();
                else
                    ((Player) fixB.getUserData()).hit();
                break;
            case GameLogic.ENEMY_BIT:
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                    ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                break;
            case GameLogic.ITEM_BIT | GameLogic.PLAYER_BIT:
                if(fixA.getFilterData().categoryBits == GameLogic.ITEM_BIT)
                    ((Item) fixA.getUserData()).use((Player) fixB.getUserData());
                else
                    ((Item)fixB.getUserData()).use((Player) fixA.getUserData());
                break;
            default:
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
        /* not needed */
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        /* not needed */
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        /* not needed */
    }
}
