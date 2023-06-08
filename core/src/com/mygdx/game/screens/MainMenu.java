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

public class MainMenu implements Screen {
    private Viewport viewport;
    private Stage stage;

    private Game game;
    public MainMenu(final Game game){
        this.game = game;
        viewport = new FitViewport(GameLogic.V_WIDTH, GameLogic.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((GameLogic) game).batch);
        Gdx.input.setInputProcessor(stage);
        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.BLUE);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label gameName = new Label("DINO GAME", font);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = new BitmapFont();
        textButtonStyle.fontColor = Color.WHITE;
        TextButton levelOneButton = new TextButton("Level One ", textButtonStyle);
        levelOneButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // закрытие текущего экрана
                game.setScreen(new LevelOne((GameLogic) game));
                dispose();
            }
        });
        TextButton levelTwoButton = new TextButton("Level Two ", textButtonStyle);
        levelTwoButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // закрытие текущего экрана
                game.setScreen(new LevelTwo((GameLogic) game));
                dispose();
            }
        });
        TextButton exitButton = new TextButton("Exit", textButtonStyle);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // закрытие текущего экрана
                Gdx.app.exit();
            }
        });
        Label mainMenu = new Label("Main menu", font);

        table.add(gameName).expandX();
        table.row();
        table.add(mainMenu).expandX().padTop(10);
        table.row();
        table.add(levelOneButton).expandX().padTop(10);
        table.row();
        table.add(levelTwoButton).expandX().padTop(10);
        table.row();
        table.add(exitButton).expandX().padTop(10);

        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {

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
        stage.dispose();
    }
}
