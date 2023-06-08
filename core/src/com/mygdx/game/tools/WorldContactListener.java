package com.mygdx.game.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.GameLogic;
import com.mygdx.game.sprites.enemies.Enemy;
import com.mygdx.game.sprites.enemies.SmallEnemy;
import com.mygdx.game.sprites.items.Item;
import com.mygdx.game.sprites.objects.InteractiveTileObject;
import com.mygdx.game.sprites.playable.Player;
import com.mygdx.game.sprites.playable.forms.Form;
import com.mygdx.game.sprites.playable.forms.Ichtiozaur;
import com.mygdx.game.sprites.projectiles.Projectile;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        // this is how collisions work
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
        switch (cDef){
            //head with stone wall
            case GameLogic.PLAYER_ATTACK_BIT | GameLogic.STONE_WALL:
                if(fixA.getFilterData().categoryBits == GameLogic.PLAYER_ATTACK_BIT)
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Player) fixA.getUserData());
                else
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Player) fixB.getUserData());
                break;
            //player stands on enemy
            case GameLogic.SMALL_ENEMY_HEAD_BIT | GameLogic.PLAYER_BIT:
                if(fixA.getFilterData().categoryBits == GameLogic.SMALL_ENEMY_HEAD_BIT)
                    ((SmallEnemy)fixA.getUserData()).hitOnHead();
                else
                    ((SmallEnemy)fixB.getUserData()).hitOnHead();
                break;
            //small enemy touches object
            case GameLogic.ENEMY_BIT | GameLogic.OBJECT_BIT:
            case GameLogic.SMALL_ENEMY_BIT | GameLogic.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == GameLogic.ENEMY_BIT)
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                break;
            // player collides with small enemy
            case GameLogic.PLAYER_BIT | GameLogic.SMALL_ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == GameLogic.PLAYER_BIT)
                    ((Player) fixA.getUserData()).getHitted();
                else
                    ((Player) fixB.getUserData()).getHitted();
                break;
            //enemy collides with other objects
            case (GameLogic.ENEMY_BIT):
            //small enemy collides with other objects
            case (GameLogic.SMALL_ENEMY_BIT):
                ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                break;
            //item collides with player
            case GameLogic.ITEM_BIT | GameLogic.PLAYER_BIT:
                if(fixA.getFilterData().categoryBits == GameLogic.ITEM_BIT)
                    ((Item) fixA.getUserData()).use((Player) fixB.getUserData());
                else
                    ((Item)fixB.getUserData()).use((Player) fixA.getUserData());
                break;
            case GameLogic.PLAYER_BIT | GameLogic.GROUND_BIT:
                if(fixA.getFilterData().categoryBits == GameLogic.PLAYER_BIT)
                    ((Player) fixA.getUserData()).ableToJump(true);
                else
                    ((Player) fixB.getUserData()).ableToJump(true);
                break;
            case GameLogic.PLAYER_BIT | GameLogic.WATER_BIT:
                Gdx.app.log("water", "collision");
                if(fixA.getFilterData().categoryBits == GameLogic.PLAYER_BIT)
                    ((Player) fixA.getUserData()).swim();
                else
                    ((Player) fixB.getUserData()).swim();
                break;
            // player collides with enemy
            case GameLogic.PLAYER_ATTACK_BIT | GameLogic.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == GameLogic.PLAYER_ATTACK_BIT) {
                    ((Enemy) fixB.getUserData()).getHit(((Player) fixA.getUserData()).getCurrentForm().getDamage());
                }
                else {
                    Gdx.app.log("Player attack with enemy", "collision");
                    ((Enemy) fixA.getUserData()).getHit(((Player) fixB.getUserData()).getCurrentForm().getDamage());
                }
                break;
            case GameLogic.ENEMY_ATTACK_BIT | GameLogic.PLAYER_BIT:
                if(fixA.getFilterData().categoryBits == GameLogic.ENEMY_ATTACK_BIT) {
                    ((Player) fixB.getUserData()).getHitted();
                }
                else {
                    ((Player) fixA.getUserData()).getHitted();
                }
                break;
            case GameLogic.PROJECTILE_BIT | GameLogic.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == GameLogic.PROJECTILE_BIT) {
                    ((Enemy) fixB.getUserData()).getHit(((Projectile) fixA.getUserData()).getDamage());
                }
                else {
                    ((Enemy) fixA.getUserData()).getHit(((Projectile) fixB.getUserData()).getDamage());
                }
                break;
            case GameLogic.PROJECTILE_BIT | GameLogic.PLAYER_BIT:
                Gdx.app.log("enemy attack with player", "collision");
                if(fixA.getFilterData().categoryBits == GameLogic.PROJECTILE_BIT) {
                    ((Player) fixB.getUserData()).getHitted();
                }
                else {
                    ((Player) fixA.getUserData()).getHitted();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        // this is how collisions end
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
        switch (cDef){
            case GameLogic.PLAYER_BIT | GameLogic.WATER_BIT:
            Gdx.app.log("water", "end collision");
            if(fixA.getFilterData().categoryBits == GameLogic.PLAYER_BIT && ((Player) fixA.getUserData()).getState() == Form.State.SWIMMING)
                ((Ichtiozaur)((Player) fixA.getUserData()).getCurrentForm()).setSwimming(false);
            else
                ((Ichtiozaur)((Player) fixB.getUserData()).getCurrentForm()).setSwimming(false);
            break;
            default:
                break;
        }
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
