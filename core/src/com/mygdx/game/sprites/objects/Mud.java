package com.mygdx.game.sprites.objects;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.GameLogic;
import com.mygdx.game.screens.PlayScreen;
import com.mygdx.game.sprites.playable.Player;
import com.mygdx.game.sprites.playable.forms.Form;

public class Mud extends InteractiveTileObject{
    public Mud(PlayScreen screen, Rectangle bounds) {
        super(screen, bounds);
        fixture.setUserData(this);
        setCategoryFilter(GameLogic.OBJECT_BIT);
    }

    @Override
    public void onHeadHit(Player player) { /* not to use */ }
}
