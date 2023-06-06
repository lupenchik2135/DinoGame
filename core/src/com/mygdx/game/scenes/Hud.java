package com.mygdx.game.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.GameLogic;
import com.mygdx.game.sprites.playable.Player;


public class Hud implements Disposable {
    public Stage stage;
    public Viewport viewport;

    private Integer worldTimer;
    private Player player;
    private float timeCount;
    private static Integer score;

    Label countdownLabel;
    static Label scoreLabel;
    Label levelLabel;
    Label timeLabel;
    Label worldLabel;
    Label playerLabel;
    Label playerHealthLabel;

    public Hud(SpriteBatch spriteBatch, Player player){
        this.player = player;
        worldTimer = 300;
        timeCount = 0;
        score = 0;
        viewport = new FitViewport(GameLogic.V_WIDTH, GameLogic.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        countdownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("1-1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label("WORLD", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        playerLabel = new Label("PLAYER", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        playerHealthLabel = new Label(String.format("%03d", player.health), new Label.LabelStyle(new BitmapFont(), Color.RED));

        table.add(playerLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countdownLabel).expandX();
        table.row();
        table.add(playerHealthLabel).expandX();

        stage.addActor(table);
    }

    public void update(float deltaTime){
        timeCount += deltaTime;
        if (timeCount >= 1){
            worldTimer--;
            countdownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
        playerHealthLabel.setText(String.format("%03d", player.health));
    }
    public static void addScore (int value){
        score += value;
        scoreLabel.setText(String.format("%06d", score));
    }
    @Override
    public void dispose() {
        stage.dispose();
    }
}
