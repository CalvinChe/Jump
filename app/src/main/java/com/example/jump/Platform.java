package com.example.jump;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Platform extends GameObject {
    private boolean isNew;

    public Platform(int x, int y) {
        height = 20;
        width = 100;

        this.x = x;
        this.y = y;
        isNew = true;
        //Everything should move depending on the player's movement
        dy = 5;
    }

    public void update() {
        //update depending on the player.
        if (GamePanel.jump == true) {
            isNew = false;
        }
        if(!isNew) {
        if (GamePanel.jump) {
            y -= dy;
            dy = 10;
        } else {
            dy -= 0.5;
        }

        if (dy <= 0) {
            GamePanel.falling = true;
        } else {
            GamePanel.falling = false;
        }


        y += (int) dy;
    }
    }

    //should be a single rectangle.
    public void draw(Canvas canvas) {

            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);

            canvas.drawRect(x,y+4,x+width, y+height, paint);

    }

    public void setNew(boolean b) {
        isNew = b;
    }

}
