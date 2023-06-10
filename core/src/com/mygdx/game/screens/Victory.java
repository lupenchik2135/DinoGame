package com.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.GameLogic;

public class Victory implements Screen {
    private Viewport viewport;
    private Stage stage;

    private Game game;
    public Victory(final Game game){
        this.game = game;
        viewport = new FitViewport(GameLogic.V_WIDTH, GameLogic.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((GameLogic) game).getBatch());
        Gdx.input.setInputProcessor(stage);
        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.GREEN);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label gameOverLabel = new Label("GAME WIN !!!!!", font);


        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = new BitmapFont();
        textButtonStyle.fontColor = Color.WHITE;
        TextButton button = new TextButton("Main Menu", textButtonStyle);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // закрытие текущего экрана
                game.setScreen(new MainMenu(game));
                dispose();
            }
        });

        table.add(gameOverLabel).expandX();
        table.row();
        table.add(button).expandX().padTop(10);

        stage.addActor(table);
    }

    @Override
    public void show() {
        /* not to use */
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.3f, 0.1f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        /* not to use */
    }

    @Override
    public void pause() {
        /* not to use */
    }

    @Override
    public void resume() {
        /* not to use */
    }

    @Override
    public void hide() {
        /* not to use */
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
