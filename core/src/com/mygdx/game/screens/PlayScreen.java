package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.GameLogic;
import com.mygdx.game.scenes.Hud;
import com.mygdx.game.sprites.enemies.Enemy;
import com.mygdx.game.sprites.items.Heart;
import com.mygdx.game.sprites.items.Item;
import com.mygdx.game.sprites.objects.ItemDef;
import com.mygdx.game.sprites.playable.forms.Form;
import com.mygdx.game.sprites.playable.Player;
import com.mygdx.game.tools.B2WorldCreator;
import com.mygdx.game.tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {
    private GameLogic game;
    private TextureAtlas atlas;

    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;
    private AssetManager manager;
    private Music music;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private B2WorldCreator creator;
    private World world;
    private Box2DDebugRenderer b2dr;
    private Player player;

    private Array<Item> items;
    public LinkedBlockingQueue<ItemDef> itemsToSpawn;
    public PlayScreen(GameLogic game){
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
        itemsToSpawn = new LinkedBlockingQueue<>();
    }
    public void spawnItem(ItemDef itemDef){
        itemsToSpawn.add(itemDef);
    }
    public void handleSpawningItems(){
        if (!itemsToSpawn.isEmpty()){
            ItemDef itemDef = itemsToSpawn.poll();
            if(itemDef.type == Heart.class){
                items.add(new Heart(this, itemDef.position.x, itemDef.position.y));
            }
        }
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    public void handleInput(float deltaTime){
        if(player.getState() != Form.State.CHANGING){
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                player.b2Body.applyLinearImpulse(new Vector2(0, 4f), player.b2Body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) && player.b2Body.getLinearVelocity().x <= 2) {
                player.b2Body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2Body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A) && player.b2Body.getLinearVelocity().x >= -2) {
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
    public void update(float deltaTime){
        handleInput(deltaTime);
        handleSpawningItems();
        world.step(1/60f, 6, 2);
        player.update(deltaTime);
        for (Enemy enemy : creator.getMillyWarriors()){
            enemy.update(deltaTime);
            if(enemy.getX() < player.getX() + (256 / GameLogic.PPM)){
                enemy.b2Body.setActive(true);
            }
        }
        for (Item item : items){
            item.update(deltaTime);
        }
        gameCam.position.x = player.b2Body.getPosition().x;
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
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for (Enemy enemy : creator.getMillyWarriors()){
            enemy.draw(game.batch);
        }
        for (Item item : items){
            item.draw(game.batch);
        }
        game.batch.end();
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
