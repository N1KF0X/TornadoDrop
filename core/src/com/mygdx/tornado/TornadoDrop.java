package com.mygdx.tornado;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class TornadoDrop {
    Rectangle rectangle;
    Texture texture;
    int coin;
    public TornadoDrop(Rectangle rectangle, Texture texture, int coin){
        this.rectangle = rectangle;
        this.texture = texture;
        this.coin = coin;
    }
}
