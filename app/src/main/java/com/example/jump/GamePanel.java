package com.example.jump;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    public static final int WIDTH = 480;
    public static final int HEIGHT = 900;
    public static long playerMov = 0;
    public static boolean jump = false;
    public static boolean falling = true;
    private int score = 0;
    private int best = 0;
    private Player player;
    private MainThread thread;
    private Background bg;
    private ArrayList<Platform> platforms;
    private boolean newGameCreated = false;
    private OrientationData orientationData;
    private long frameTime;
    private Random rand = new Random();

    public GamePanel(Context context) {
        super(context);

        //add the callback to the surfaceHolder to intercept events.
        getHolder().addCallback(this);

        //make gamePanel focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        bg = new Background(WIDTH, HEIGHT);
        player = new Player(60,60);
        platforms = new ArrayList<>();

        orientationData = new OrientationData(getContext());
        orientationData.register();
        frameTime = System.currentTimeMillis();

        thread = new MainThread(getHolder(), this);
        //we can safely start the game loop
        thread.setRunning(true);
        thread.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        boolean retry = true;
        int counter = 0;
        while(retry && counter < 1000) {
            counter++;
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null; // so garbage collector can pick up the object
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void update() {
        if(!newGameCreated) {
            newGame();
        }
        bg.update();
        updateAccelerometer();
        player.update();
        updatePlatforms();

        boolean b = true;
       for(int i = 0; i < platforms.size(); i++) {
            if(platforms.get(i).getY() > player.getY()) {
                b = false;
            }
       }
       if(b) {
           newGame();
       }
        //System.out.println(falling);
        //System.out.println(platforms.size());
    }

    @Override
    public void draw(Canvas canvas) {
        final float scaleFactorX = (float) getWidth() / WIDTH;
        final float scaleFactorY = (float) getHeight() / HEIGHT;
        final int savedState = canvas.save(); //before scaled.


        canvas.scale(scaleFactorX, scaleFactorY);
        bg.draw(canvas);
        player.draw(canvas);

        for (Platform p : platforms) {
            p.draw(canvas);
        }

        drawText(canvas);

        // want to go back to nonscaled state at end so we can rescale everything correctly.
        canvas.restoreToCount(savedState);
        //super.draw(canvas);
    }

    public void newGame() {
        //System.out.println("new game made");
        platforms.clear();
        player.resetXY();
        //orientationData.newGame();
        int indent = 10;
        for(int i = 0; i*100 < WIDTH; i++) {
            //initial platform on the left
            if(i == 0 ) {
                platforms.add(new Platform(i*100 + indent, HEIGHT - 40));
                indent += 10;
            } else { //the rest of the borders.
                platforms.add(new Platform(i*100 + indent, HEIGHT - 40));
                indent +=10;
            }
        }
        //now make starting borders going up.
        for(int i = 0; i*100 < HEIGHT +100 ; i++) {
            platforms.add(new Platform(((int)(rand.nextDouble()*(WIDTH-100))), i*100));
        }

        for(int i = 0; i < platforms.size(); i++) {
            platforms.get(i).setNew(false);
        }

        if(score > best) {
            best = score;
        }


        indent = 10; //reset indent incase new game.
        score = 0;
        newGameCreated = true;
    }

    public void updatePlatforms() {

        platformCollision();

        for(int i=0; i < platforms.size(); i++) {
            platforms.get(i).update();
            addNewPlatforms();
            //System.out.println(platforms.get(i).getY());
            //System.out.println(platforms.size());
            //When the platforms are too far below or high on
            // the screen they disappear
            if(platforms.get(i).getY() > 940 ||
            platforms.get(i).getY() < -1500) {
                //System.out.println(platforms.get(i).getY());
                score++;
                platforms.remove(i);
                //System.out.println("platform deleted.");
            }
        }
        jump = false;
    }

    public void addNewPlatforms() {
        boolean found = false;
        int highestY = HEIGHT;
        for(int i = 0; i < platforms.size(); i++) {
            if(highestY > platforms.get(i).getY()) {
                highestY = platforms.get(i).getY();
            }
            if(platforms.get(i).getY() > 940) {
                found = true;
            }
        }
        if(found) {
            //System.out.println("new platform added");
            platforms.add(new Platform(((int)(rand.nextDouble()*(WIDTH-100))), highestY-50));
        }

    }

    public void platformCollision() {
        for(int i = 0; i < platforms.size(); i++)  {
            if(collision(platforms.get(i),player) && falling) {
                //System.out.println("collision");
                jump  = true;
            }
        }
    }

    public void updateAccelerometer() {
        int elapsedTime = (int) (System.currentTimeMillis() - frameTime);
        frameTime = System.currentTimeMillis();
        if(orientationData.getOrientation() !=null && orientationData.getStartingOrientation() != null) {
            //pitch goes from pi -> - pi
            float pitch = orientationData.getOrientation()[1] - orientationData.getStartingOrientation()[1];
            //roll goes from pi/2 -> -pi/2 we will multiply roll by 2 to account for that.
            float roll =  orientationData.getOrientation()[2] - orientationData.getStartingOrientation()[2];

            //if we were to tilt the phone to the right it will
            // go from one side of the screen to the other in 1 second
            // at 1000f(1000 milliseconds or 1 sec)
            float xSpeed = 2* roll * WIDTH/10000f;
            float ySpeed = pitch * HEIGHT/1000f;

            playerMov += xSpeed * elapsedTime;
            if(Math.abs(xSpeed * elapsedTime) < 1) {
                if(playerMov > 0) {
                    playerMov -= 0.5;
                } else if(playerMov < 0) {
                    playerMov += 0.5;
                }
                if(playerMov < 1 && playerMov > -1) {
                    playerMov = 0;
                }
            }

        }
    }

    public void drawText (Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Score: " + score, 10, 30, paint);
        canvas.drawText("Best: " + best, WIDTH - 100 - (String.valueOf(best).length() * 10) , 30, paint);
    }

    //Hit box detection for 2 rectangles.
    public boolean collision(GameObject a, GameObject b) {
        if(Rect.intersects(a.getRectangle(), b.getRectangle())) {
            return true;
        }
        return false;
    }

}
