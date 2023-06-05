package com.mygdx.game.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.GameLogic;
import com.mygdx.game.Sprites.Enemies.Enemy;
import com.mygdx.game.Sprites.InteractiveTileObject;
import com.mygdx.game.Sprites.Playable.Forms.Form;
import com.mygdx.game.Sprites.Playable.Forms.Ichtiozaur;
import com.mygdx.game.Sprites.Playable.Player;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
            if (fixA.getUserData() == "head" || fixB.getUserData() == "head") {
                Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
                Fixture object = head == fixA ? fixB : fixA;
                if (object.getUserData() != null && InteractiveTileObject.class.isAssignableFrom(object.getUserData().getClass())) {
                    ((InteractiveTileObject) object.getUserData()).onHeadHit();
                }
            }
        if (fixA.getUserData() == "mouth" || fixB.getUserData() == "mouth") {
            Fixture head = fixA.getUserData() == "mouth" ? fixA : fixB;
            Fixture object = head == fixA ? fixB : fixA;
            if (object.getUserData() != null && Enemy.class.isAssignableFrom(object.getUserData().getClass())) {
                ((Enemy) object.getUserData()).hitOnHead();
            }
        }
        switch (cDef){
            case GameLogic.ENEMY_HAND_BIT | GameLogic.PLAYER_BIT:
                if(fixA.getFilterData().categoryBits == GameLogic.ENEMY_HAND_BIT)
                    ((Enemy)fixA.getUserData()).hitOnHead();
                else if(fixB.getFilterData().categoryBits == GameLogic.ENEMY_HAND_BIT)
                    ((Enemy)fixB.getUserData()).hitOnHead();

        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
