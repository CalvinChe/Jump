package com.example.jump;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Player extends GameObject{
    private int score;
    private double dxa; //x acceleration
    private boolean left;
    private boolean playing;
    private long startTime;

    public Player(int width, int height) {
        this.width = width;
        this.height = height;
        x = (GamePanel.WIDTH/2) - (width/2);
        y =  600 - (height/2);
        score = 0;
        startTime = System.nanoTime();
    }


    public void update() {

        //System.out.println("player updating");
        x += GamePanel.playerMov;
        if(x < - width/2) {
            x = GamePanel.WIDTH - (width/2) - 1;
        } else if( x + width/2 > GamePanel.WIDTH ) {
            x = width/2 + 1;
        }
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(x,y,x+width, y+height, paint);
    }


    public void resetXY() {
        x = (GamePanel.WIDTH/2) - (width/2);
        y =  600 - (height/2);
    }

}
