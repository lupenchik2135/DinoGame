package com.mygdx.game.sprites.objects;

import com.badlogic.gdx.math.Vector2;

public class ObjectDef {
    public Vector2 position;
    public Class<?> type;
    public ObjectDef(Vector2 position, Class<?> type){
        this.position = position;
        this.type = type;
    }
}
