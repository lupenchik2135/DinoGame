package com.mygdx.game.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.GameLogic;
import com.mygdx.game.sprites.enemies.Enemy;
import com.mygdx.game.sprites.enemies.SmallEnemy;
import com.mygdx.game.sprites.enemies.Worm;
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
                handlePlayerAttack(fixA, fixB);
                break;
            case GameLogic.CHECK_BIT | GameLogic.GROUND_BIT:
            case GameLogic.CHECK_BIT | GameLogic.WATER_BIT:
            case GameLogic.CHECK_BIT | GameLogic.STONE_WALL:
                handleCheckBit(fixA, fixB);
                break;
                //player stands on enemy
            case GameLogic.SMALL_ENEMY_HEAD_BIT | GameLogic.PLAYER_BIT:
                handleEnemyHead(fixA, fixB);
                break;
            //small enemy touches object
            case GameLogic.ENEMY_BIT | GameLogic.ENEMY_STOPPER:
            case GameLogic.SMALL_ENEMY_BIT | GameLogic.ENEMY_STOPPER:
                handleStopper(fixA, fixB);
                break;
            // player collides with small enemy
            case GameLogic.PLAYER_BIT | GameLogic.SMALL_ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == GameLogic.PLAYER_BIT) {
                    ((Player) fixA.getUserData()).getHitted();
                    ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                }else {
                    ((Player) fixB.getUserData()).getHitted();
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                }
                break;
            //enemy collides with other objects
            case (GameLogic.ENEMY_BIT):
            //small enemy collides with other objects
            case (GameLogic.SMALL_ENEMY_BIT):
            case (GameLogic.SMALL_ENEMY_BIT | GameLogic.ENEMY_BIT):
                ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                break;
            //item collides with player
            case GameLogic.ITEM_BIT | GameLogic.TYRANNOSAUR_BIT:
            case GameLogic.ITEM_BIT | GameLogic.PLAYER_BIT:
                handleItem(fixA, fixB);
                break;
            case GameLogic.PLAYER_BIT | GameLogic.WATER_BIT:
                handleWater(fixA, fixB);
                break;
            // player collides with enemy
            case GameLogic.TYRANNOSAUR_BIT | GameLogic.ENEMY_BIT:
            case GameLogic.PLAYER_ATTACK_BIT | GameLogic.ENEMY_BIT:
                checkEnemy(fixA, fixB);
                break;
            case GameLogic.ENEMY_ATTACK_BIT | GameLogic.PLAYER_BIT:
                handleEnemyAttack(fixA, fixB);
                break;
            case GameLogic.PLAYER_BIT | GameLogic.ENEMY_BIT:
                handlePlayer(fixA, fixB);
                break;
            case GameLogic.PROJECTILE_BIT | GameLogic.ENEMY_BIT:
                handleProjectileAndeEnemy(fixA, fixB);
                break;
            case GameLogic.PROJECTILE_BIT | GameLogic.PLAYER_BIT:
                handleProjectile(fixA, fixB);
                break;
            default:
                break;
        }
    }

    private static void handleProjectileAndeEnemy(Fixture fixA, Fixture fixB) {
        if(fixA.getFilterData().categoryBits == GameLogic.PROJECTILE_BIT) {
            ((Enemy) fixB.getUserData()).getHit(((Projectile) fixA.getUserData()).getDamage());
        }
        else {
            ((Enemy) fixA.getUserData()).getHit(((Projectile) fixB.getUserData()).getDamage());
        }
    }

    private static void handleWater(Fixture fixA, Fixture fixB) {
        if(fixA.getFilterData().categoryBits == GameLogic.PLAYER_BIT)
            ((Player) fixA.getUserData()).swim();

        else
            ((Player) fixB.getUserData()).swim();
    }

    private static void handleItem(Fixture fixA, Fixture fixB) {
        if(fixA.getFilterData().categoryBits == GameLogic.ITEM_BIT)
            ((Item) fixA.getUserData()).use((Player) fixB.getUserData());
        else
            ((Item) fixB.getUserData()).use((Player) fixA.getUserData());
    }

    private static void handleStopper(Fixture fixA, Fixture fixB) {
        if(fixA.getFilterData().categoryBits == GameLogic.ENEMY_BIT || fixA.getFilterData().categoryBits == GameLogic.SMALL_ENEMY_BIT)
            ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
        else
            ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
    }

    private static void handleEnemyHead(Fixture fixA, Fixture fixB) {
        if(fixA.getFilterData().categoryBits == GameLogic.SMALL_ENEMY_HEAD_BIT)
            ((SmallEnemy) fixA.getUserData()).hitOnHead();
        else
            ((SmallEnemy) fixB.getUserData()).hitOnHead();
    }

    private static void handleProjectile(Fixture fixA, Fixture fixB) {
        if(fixA.getFilterData().categoryBits == GameLogic.PROJECTILE_BIT) {
            ((Player) fixB.getUserData()).getHitted();
            ((Projectile) fixA.getUserData()).hitted();
        }
        else {
            ((Player) fixA.getUserData()).getHitted();
            ((Projectile) fixB.getUserData()).hitted();
        }
    }

    private static void handleEnemyAttack(Fixture fixA, Fixture fixB) {
        if(fixA.getFilterData().categoryBits == GameLogic.ENEMY_ATTACK_BIT) {
            ((Player) fixB.getUserData()).getHitted();
        }
        else {
            ((Player) fixA.getUserData()).getHitted();
        }
    }

    private static void handlePlayer(Fixture fixA, Fixture fixB) {
        if(fixA.getFilterData().categoryBits == GameLogic.PLAYER_BIT) {
            if(((Enemy) fixB.getUserData()).getClass().isAssignableFrom(Worm.class)){
                ((Player) fixA.getUserData()).getHitted();
            }else {
                ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
            }
        }
        else {
            if(((Enemy) fixA.getUserData()).getClass().isAssignableFrom(Worm.class)){
                ((Player) fixA.getUserData()).getHitted();
            }else {
                ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
            }
        }
    }

    private static void handlePlayerAttack(Fixture fixA, Fixture fixB) {
        if(fixA.getFilterData().categoryBits == GameLogic.PLAYER_ATTACK_BIT)
            ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Player) fixA.getUserData());
        else
            ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Player) fixB.getUserData());
    }

    private static void handleCheckBit(Fixture fixA, Fixture fixB) {
        if(fixA.getFilterData().categoryBits == GameLogic.CHECK_BIT)
            ((Player) fixA.getUserData()).isAbleToChange(false);
        else
            ((Player) fixB.getUserData()).isAbleToChange(false);
    }

    private static void checkEnemy(Fixture fixA, Fixture fixB) {
        if(fixB.getFilterData().categoryBits == GameLogic.ENEMY_BIT) {
            ((Enemy) fixB.getUserData()).getHit(((Player) fixA.getUserData()).getCurrentForm().getDamage());
        }
        else {
            ((Enemy) fixA.getUserData()).getHit(((Player) fixB.getUserData()).getCurrentForm().getDamage());
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
            if(fixA.getFilterData().categoryBits == GameLogic.PLAYER_BIT && ((Player) fixA.getUserData()).getState() == Form.State.SWIMMING)
                ((Ichtiozaur)((Player) fixA.getUserData()).getCurrentForm()).setSwimming(false);
            else
                ((Ichtiozaur)((Player) fixB.getUserData()).getCurrentForm()).setSwimming(false);
            break;
            case GameLogic.CHECK_BIT | GameLogic.GROUND_BIT:
            case GameLogic.CHECK_BIT | GameLogic.WATER_BIT:
            case GameLogic.CHECK_BIT | GameLogic.STONE_WALL:
                Gdx.app.log("Collision", "checkBit");
                if(fixA.getFilterData().categoryBits == GameLogic.CHECK_BIT)
                    ((Player)fixA.getUserData()).isAbleToChange(true);
                else
                    ((Player)fixB.getUserData()).isAbleToChange(true);
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
