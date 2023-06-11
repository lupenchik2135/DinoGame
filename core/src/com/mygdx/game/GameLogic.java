package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.screens.MainMenu;

public class GameLogic extends Game {
	private SpriteBatch batch;
	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT = 208;
	public static final float PPM = 100;

	public static final short GROUND_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short PLAYER_ATTACK_BIT = 4;
	public static final short STONE_WALL = 8;
	public static final short DESTROYED_BIT = 16;
	public static final short ENEMY_STOPPER = 32;
	public static final short ENEMY_BIT = 64;
	public static final short ENEMY_ATTACK_BIT = 128;
	public static final short SMALL_ENEMY_BIT = 256;
	public static final short SMALL_ENEMY_HEAD_BIT = 512;
	public static final short ITEM_BIT = 1024;
	public static final short PROJECTILE_BIT = 2048;
	public static final short WATER_BIT = 4096;
	public static final short CHECK_BIT = 8192;
	public static final short TYRANNOSAUR_BIT = 16384;
	public static final float GRAVITY = -9.8f;
	public static final float WATER_GRAVITY = 0f;


	@Override
	public void create() {
		batch = new SpriteBatch();

		setScreen(new MainMenu(this));
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}
