package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.GameLogic;
import com.mygdx.game.scenes.Hud;
import com.mygdx.game.sprites.enemies.Enemy;
import com.mygdx.game.sprites.items.Heart;
import com.mygdx.game.sprites.items.Item;
import com.mygdx.game.sprites.objects.ObjectDef;
import com.mygdx.game.sprites.playable.forms.Form;
import com.mygdx.game.sprites.playable.Player;
import com.mygdx.game.sprites.projectiles.Arrow;
import com.mygdx.game.sprites.projectiles.Projectile;
import com.mygdx.game.sprites.projectiles.Spit;
import com.mygdx.game.tools.B2WorldCreator;
import com.mygdx.game.tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

public class LevelTwo  extends Level{
    public LevelTwo(GameLogic game){
        atlas = new TextureAtlas("PlayableDinos.atlas");
        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(GameLogic.V_WIDTH / GameLogic.PPM, GameLogic.V_HEIGHT / GameLogic.PPM, gameCam);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("SwampMap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / GameLogic.PPM);
        gameCam.position.set(gamePort.getWorldWidth()/2, gamePort.getWorldHeight()/2, 0);
        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();
        creator = new B2WorldCreator(this);
        player = new Player(this);
        hud = new Hud(game.batch, player);
        manager = new AssetManager();
        manager.load("audio/Music/KimMusic.mp3", Music.class);
        manager.finishLoading();
        music = manager.get("audio/Music/KimMusic.mp3", Music.class);
        music.setLooping(true);
        music.setVolume(0.1f);
        music.play();
        world.setContactListener(new WorldContactListener());

        items = new Array<Item>();
        projectiles = new Array<Projectile>();
        objectsToSpawn = new LinkedBlockingQueue<>();
    }
    public void spawnObject(ObjectDef objectDef){
        objectsToSpawn.add(objectDef);
    }
    public void handleSpawningObjects(){
        if (!objectsToSpawn.isEmpty()){
            ObjectDef objectDef = objectsToSpawn.poll();
            if(objectDef.type == Heart.class){
                items.add(new Heart(this, objectDef.position.x, objectDef.position.y));
            }else if(objectDef.type == Spit.class){
                projectiles.add(new Spit(this, objectDef.position.x, objectDef.position.y));
            }else if(objectDef.type == Arrow.class){
                projectiles.add(new Arrow(this, objectDef.position.x, objectDef.position.y));
            }
        }
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    public void update(float deltaTime){
        handleInput(deltaTime);
        handleSpawningObjects();
        world.step(1/60f, 6, 2);
        player.update(deltaTime);
        for (Enemy enemy : creator.getSmallEnemies()){
            enemy.update(deltaTime);
            if(enemy.getX() < player.getX() + (256 / GameLogic.PPM)){
                enemy.b2Body.setActive(true);
            }
        }
        for (Enemy enemy : creator.getMillyEnemies()){
            enemy.update(deltaTime);
            if(enemy.getX() < player.getX() + (256 / GameLogic.PPM)){
                enemy.b2Body.setActive(true);
            }
            if(enemy.getX() < player.getX() + (16 / GameLogic.PPM) || enemy.getX() > player.getX() - (16 / GameLogic.PPM)){
                enemy.hit();
            }
        }
        for (Enemy enemy : creator.getDistantEnemies()){
            enemy.update(deltaTime);
            if(enemy.getX() < player.getX() + (256 / GameLogic.PPM)){
                enemy.b2Body.setActive(true);
            }
            if(enemy.getX() < player.getX() + (32 / GameLogic.PPM)|| enemy.getX() > player.getX() - (32 / GameLogic.PPM)){
                enemy.hit();
            }
        }
        for (Item item : items){
            item.update(deltaTime);
        }
        for (Projectile projectile : projectiles){
            projectile.update(deltaTime);
        }
        if(player.b2Body.getPosition().x > GameLogic.V_WIDTH / GameLogic.PPM - player.b2Body.getPosition().x && player.b2Body.getPosition().x <= 36.4f && player.getState() != Form.State.DEAD) {
            gameCam.position.x = player.b2Body.getPosition().x;
        }
        gameCam.update();
        hud.update(deltaTime);
        renderer.setView(gameCam);
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        b2dr.render(world, gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for (Enemy enemy : creator.getSmallEnemies()){
            enemy.draw(game.batch);
        }
        for (Enemy enemy : creator.getMillyEnemies()){
            enemy.draw(game.batch);
        }
        for (Enemy enemy : creator.getDistantEnemies()){
            enemy.draw(game.batch);
        }
        for (Item item : items){
            item.draw(game.batch);
        }
        for (Projectile projectile : projectiles){
            projectile.draw(game.batch);
        }
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(gameOver()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld(){
        return world;
    }
    public boolean gameOver(){
        if(player.getState() == Form.State.DEAD && player.getStateTimer() > 3){
            return true;
        }
        return false;
    }
    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

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
