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
    public final Stage stage;
    public final Viewport viewport;

    private Integer ultimateTimer;
    private Player player;
    private float timeCount;

    Label countdownLabel;
    Label levelLabel;
    Label timeLabel;
    Label worldLabel;
    Label playerLabel;
    Label playerHealthLabel;

    public Hud(SpriteBatch spriteBatch, Player player){
        this.player = player;
        ultimateTimer = 3;
        timeCount = 0;
        viewport = new FitViewport(GameLogic.V_WIDTH, GameLogic.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        countdownLabel = new Label(String.format("%03d", ultimateTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME TO ULT", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("1-1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label("WORLD", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        playerLabel = new Label("PLAYER", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        playerHealthLabel = new Label(String.format("%03d", player.getHealth()), new Label.LabelStyle(new BitmapFont(), Color.RED));

        table.add(playerLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(playerHealthLabel).expandX();
        table.add(countdownLabel).expandX();
        stage.addActor(table);
    }

    public void update(float deltaTime){
        timeCount += deltaTime;
        if (timeCount >= 1 && ultimateTimer > 0){
            ultimateTimer--;
            countdownLabel.setText(String.format("%03d", ultimateTimer));
            timeCount = 0;
        }
        playerHealthLabel.setText(String.format("%01d", player.getHealth()));
    }
    public Integer getUltimateTimer(){
        return ultimateTimer;
    }
    public void setUltimateTimer(Integer timer){
        ultimateTimer = timer;
    }
    @Override
    public void dispose() {
        stage.dispose();
    }
}
