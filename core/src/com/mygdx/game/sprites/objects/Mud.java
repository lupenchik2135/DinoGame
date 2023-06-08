package com.mygdx.game.sprites.objects;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.Level;
import com.mygdx.game.screens.LevelOne;
import com.mygdx.game.sprites.playable.Player;

public class Mud extends InteractiveTileObject{
    public Mud(Level screen, Rectangle bounds) {
        super(screen, bounds);
        fixture.setUserData(this);
        setCategoryFilter(GameLogic.OBJECT_BIT);
    }

    @Override
    public void onHeadHit(Player player) { /* not to use */ }
}
