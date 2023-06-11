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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.GameLogic;
import com.mygdx.game.scenes.Hud;
import com.mygdx.game.sprites.enemies.Worm;
import com.mygdx.game.sprites.items.Item;
import com.mygdx.game.sprites.objects.ObjectDef;
import com.mygdx.game.sprites.playable.Player;
import com.mygdx.game.sprites.playable.forms.Form;
import com.mygdx.game.sprites.projectiles.Projectile;
import com.mygdx.game.tools.B2WorldCreator;
import com.mygdx.game.tools.WorldContactListener;

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
    protected Worm worm;
    public LinkedBlockingQueue<ObjectDef> objectsToSpawn;
    protected Level(GameLogic game, String level){
        atlas = new TextureAtlas("TexturesForGame.atlas");
        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(GameLogic.V_WIDTH / GameLogic.PPM, GameLogic.V_HEIGHT / GameLogic.PPM, gameCam);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load(level);
        renderer = new OrthogonalTiledMapRenderer(map, 1 / GameLogic.PPM);
        gameCam.position.set(gamePort.getWorldWidth()/2, gamePort.getWorldHeight()/2, 0);
        world = new World(new Vector2(0, GameLogic.GRAVITY), true);
        b2dr = new Box2DDebugRenderer();
        creator = new B2WorldCreator(this);
        player = new Player(this, 16, 32);
        hud = new Hud(game.getBatch(), player);
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

        worm = creator.getWorm();
    }
    public abstract void spawnObject(ObjectDef objectDef);
    public abstract void handleSpawningObjects();

    public abstract TextureAtlas getAtlas();


    public void handleInput(){
        if(!player.getCurrentForm().isChanging() && player.getState() != Form.State.DEAD && player.getState() != Form.State.SWIMMING){
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && player.getIsAbleToJump()) {
                player.b2Body.applyLinearImpulse(new Vector2(0, player.getCurrentForm().getJumpHeight()), player.b2Body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && player.getState() == Form.State.FLYING && player.b2Body.getLinearVelocity().y > -1) {
                player.b2Body.applyLinearImpulse(new Vector2(0, 0.16f), player.b2Body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) && player.b2Body.getLinearVelocity().x <= player.getCurrentForm().getVelocityX()) {
                player.b2Body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2Body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A) && player.b2Body.getLinearVelocity().x >= -player.getCurrentForm().getVelocityX()) {
                player.b2Body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2Body.getWorldCenter(), true);
            }
            if(player.getIsAbleToChange()){
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
                    player.changeInto(0);
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
                    player.changeInto(1);
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
                    player.changeInto(2);
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
                    player.changeInto(3);
                }
                if (hud.getUltimateTimer() == 0 && Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
                    player.changeInto(4);
                }
            }
        }
    }
    public abstract void update(float deltaTime);
    public abstract TiledMap getMap();

    public abstract World getWorld();
    public boolean gameOver(){
        return player.getState() == Form.State.DEAD && player.getStateTimer() > 3;
    }
    public void setNewUltTimer(Integer time){
        hud.setUltimateTimer(time);
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
