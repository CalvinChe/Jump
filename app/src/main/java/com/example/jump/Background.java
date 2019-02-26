package com.example.jump;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Background {
    int h;
    int w;
    public Background(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public void update() {

    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(0,0, w, h, paint);
    }
}
