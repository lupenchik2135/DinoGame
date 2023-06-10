package com.mygdx.game.sprites.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;
import com.mygdx.game.sprites.playable.Player;

import java.util.Objects;

public class StoneWall extends InteractiveTileObject {

    public StoneWall(Level screen, Rectangle bounds) {
        super(screen, bounds);
        fixture.setUserData(this);
        setCategoryFilter(GameLogic.STONE_WALL);
    }

    @Override
    public void onHeadHit(Player player) {
        if(Objects.equals(player.getCurrentForm().getType(), "Triceratops")){
            setCategoryFilter(GameLogic.DESTROYED_BIT);
            getCell().setTile(null);
        }
    }
}
