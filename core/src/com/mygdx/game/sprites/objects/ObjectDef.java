package com.mygdx.game.sprites.objects;

import com.badlogic.gdx.math.Vector2;

public class ObjectDef {
    public Vector2 position;
    public Class<?> type;
    public boolean right;
    public ObjectDef(Vector2 position, Class<?> type, boolean right){
        this.position = position;
        this.type = type;
        this.right = right;
    }
}
