package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.GameLogic;
import com.mygdx.game.scenes.Hud;
import com.mygdx.game.sprites.items.Item;
import com.mygdx.game.sprites.objects.ObjectDef;
import com.mygdx.game.sprites.playable.Player;
import com.mygdx.game.sprites.playable.forms.Form;
import com.mygdx.game.sprites.projectiles.Projectile;
import com.mygdx.game.tools.B2WorldCreator;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class Level implements Screen {
    protected GameLogic game;
    protected TextureAtlas atlas;

    protected OrthographicCamera gameCam;
    protected Viewport gamePort;
    protected Hud hud;
    protected AssetManager manager;
    protected Music music;

    protected TmxMapLoader mapLoader;
    protected TiledMap map;
    protected OrthogonalTiledMapRenderer renderer;
    protected B2WorldCreator creator;
    protected World world;
    protected Box2DDebugRenderer b2dr;
    protected Player player;

    protected Array<Item> items;
    protected Array<Projectile> projectiles;
    public LinkedBlockingQueue<ObjectDef> objectsToSpawn;
    public abstract void spawnObject(ObjectDef objectDef);
    public abstract void handleSpawningObjects();

    public abstract TextureAtlas getAtlas();

    public void handleInput(float deltaTime){
        if(!player.getCurrentForm().isChanging() && player.getState() != Form.State.DEAD){
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && player.getIsAbleToJump()) {
                player.b2Body.applyLinearImpulse(new Vector2(0, player.getCurrentForm().getJumpHeight()), player.b2Body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && player.getState() == Form.State.FLYING) {
                player.b2Body.applyLinearImpulse(new Vector2(0, 0.16f), player.b2Body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) && player.b2Body.getLinearVelocity().x <= player.getCurrentForm().getVelocityX()) {
                player.b2Body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2Body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A) && player.b2Body.getLinearVelocity().x >= -player.getCurrentForm().getVelocityX()) {
                player.b2Body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2Body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
                player.setCurrentForm(0);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
                player.setCurrentForm(1);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
                player.setCurrentForm(2);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
                player.setCurrentForm(3);
            }
        }
    }
    public abstract void update(float deltaTime);
    public abstract TiledMap getMap();

    public abstract World getWorld();
    public boolean gameOver(){
        return player.getState() == Form.State.DEAD && player.getStateTimer() > 3;
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
        manager.dispose();
        atlas.dispose();
        player.dispose();
    }
}
