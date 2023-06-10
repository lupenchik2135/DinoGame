package com.mygdx.game.sprites.objects;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;
import com.mygdx.game.sprites.playable.Player;

public class EnemyStopper extends InteractiveTileObject{
    public EnemyStopper(Level screen, Rectangle bounds) {
        super(screen, bounds);
        fixture.setUserData(this);
        setCategoryFilter(GameLogic.ENEMY_STOPPER);

    }

    @Override
    public void onHeadHit(Player player) { /* not to use */ }
}
