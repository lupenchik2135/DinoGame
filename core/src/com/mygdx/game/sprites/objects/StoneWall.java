package com.mygdx.game.sprites.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.GameLogic;
import com.mygdx.game.scenes.Hud;
import com.mygdx.game.screens.Level;
import com.mygdx.game.screens.LevelOne;
import com.mygdx.game.sprites.playable.Player;

public class StoneWall extends InteractiveTileObject {

    public StoneWall(Level screen, Rectangle bounds) {
        super(screen, bounds);
        fixture.setUserData(this);
        setCategoryFilter(GameLogic.STONE_WALL);
    }

    @Override
    public void onHeadHit(Player player) {
        if(player.getCurrentForm().getType() == "Triceratops"){
            Gdx.app.log("stone", "collusion");
            setCategoryFilter(GameLogic.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(100);
        }
    }
}
