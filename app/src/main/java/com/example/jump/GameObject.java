package com.example.jump;

import android.graphics.Rect;

public abstract class GameObject {
    protected int x;
    protected int y;
    protected double dx; //x acceleration
    protected double dy; //y acceleration
    protected int height;
    protected int width;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    //For hitbox detection.
    public Rect getRectangle() {
        return new Rect( x, y, x+width, y + height);
    }
}
