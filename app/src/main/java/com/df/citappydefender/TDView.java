package com.df.citappydefender;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.icu.text.StringPrepParseException;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by n0206863 on 09/08/2017.
 */

public class TDView extends SurfaceView implements Runnable {

    private Context context;

    volatile boolean playing;
    Thread gameThread = null;

    //game objects
    private PlayerShip player;
    public EnemyShip enemy1;
    public EnemyShip enemy2;
    public EnemyShip enemy3;
    public EnemyShip enemy4;
    public EnemyShip enemy5;


    //drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    //space dust
    public ArrayList<SpaceDust> dustList = new ArrayList<SpaceDust>();

    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;

    private int screenX, screenY;

    private Boolean gameEnded;

    private SoundPool soundPool;
    int start = -1;
    int bump = -1;
    int destroyed = -1;
    int win = -1;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public TDView(Context context, int x, int y) {
        super(context);
        this.context = context;

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {

            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("start.ogg");
            start = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("win.ogg");
            win = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("bump.ogg");
            bump = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("destroyed.ogg");
            destroyed = soundPool.load(descriptor, 0);
        } catch (IOException ex) {
            Log.e("error", "failed to load sound files");
        }

        screenX = x;
        screenY = y;

        ourHolder = getHolder();
        paint = new Paint();

//        player = new PlayerShip(context, x, y);
//        enemy1 = new EnemyShip(context, x, y);
//        enemy2 = new EnemyShip(context, x, y);
//        enemy3 = new EnemyShip(context, x, y);
//
//        int numSpecs = 40;
//        for (int i=0;  i<=numSpecs; i++) {
//            SpaceDust spaceDust = new SpaceDust(x, y);
//            dustList.add(spaceDust);
//        };

        preferences = context.getSharedPreferences("HiScores", context.MODE_PRIVATE);
        editor = preferences.edit();

        fastestTime = preferences.getLong("fastestTime", 1000000);

        startGame();
    }

    private void startGame() {
        player = new PlayerShip(context, screenX, screenY);
        enemy1 = new EnemyShip(context, screenX, screenY);
        enemy2 = new EnemyShip(context, screenX, screenY);
        enemy3 = new EnemyShip(context, screenX, screenY);

        if(screenX >1000) {
            enemy4 = new EnemyShip(context, screenX, screenY);
        }

        if(screenX >1200) {
            enemy5 = new EnemyShip(context, screenX, screenY);
        }

        int numSpecs = 40;
        for (int i=0;  i<=numSpecs; i++) {
            SpaceDust spaceDust = new SpaceDust(screenX, screenY);
            dustList.add(spaceDust);
        }

        //reset time and distance
        distanceRemaining = 10000; //10km
        timeTaken = 0;

        //get start time
        timeStarted = System.currentTimeMillis();

        gameEnded = false;

        soundPool.play(start, 1, 1, 0, 0, 1);

    }

    @Override
    public void run() {

        while(playing) {
            update();
            draw();
            control();

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            //finger lifted up
            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;

            case MotionEvent.ACTION_DOWN:
                player.setBoosting();

                //if we on the pause screen the restart
                if(gameEnded) {
                    startGame();
                }
                break;
        }
        return true;
    }

    private void update() {

        //collecion desction on new positions.
        //before move becacuse we are testing last frames position which has just been drawn
        boolean hitDetected = false;
        if (Rect.intersects(player.getHitBox(), enemy1.getHitBox())) {
            //shoot it of the screen. The redraw will take care of everything else.
            hitDetected = true;
            enemy1.setX(-100 - enemy1.getBitmap().getWidth());
        }
        if (Rect.intersects(player.getHitBox(), enemy2.getHitBox())) {
            //shoot it of the screen. The redraw will take care of everything else.
            hitDetected = true;
            enemy2.setX(-100 - enemy2.getBitmap().getWidth());
        }
        if (Rect.intersects(player.getHitBox(), enemy3.getHitBox())) {
            //shoot it of the screen. The redraw will take care of everything else.
            hitDetected = true;
            enemy3.setX(-100 - enemy3.getBitmap().getWidth());
        }
        if (enemy4 != null && Rect.intersects(player.getHitBox(), enemy4.getHitBox())) {
            //shoot it of the screen. The redraw will take care of everything else.
            hitDetected = true;
            enemy4.setX(-100 - enemy4.getBitmap().getWidth());
        }
        if (enemy5 != null && Rect.intersects(player.getHitBox(), enemy5.getHitBox())) {
            //shoot it of the screen. The redraw will take care of everything else.
            hitDetected = true;
            enemy5.setX(-100 - enemy5.getBitmap().getWidth());
        }

        if (hitDetected) {
            soundPool.play(bump, 1, 1, 0, 0, 1);

            player.reduceShieldStrength();
            if (player.getShieldStrength() < 0 ) {
                //game over fella. Do something
                soundPool.play(destroyed, 1, 1, 0, 0, 1);
                gameEnded = true;
            }
        }

        player.update();

        //enemies
        enemy1.update(player.getSpeed());
        enemy2.update(player.getSpeed());
        enemy3.update(player.getSpeed());

        if(enemy4 != null) {
            enemy4.update(player.getSpeed());
        }

        if(enemy5 != null) {
            enemy5.update(player.getSpeed());
        }

        for(SpaceDust spaceDust : dustList) {
            spaceDust.update(player.getSpeed());
        }

        if(!gameEnded) {
            //substract distance to home planet based on current speed
            distanceRemaining -= player.getSpeed();

            //how long has the player been flying
            timeTaken = System.currentTimeMillis() - timeStarted;
        }

        if(distanceRemaining < 0) {

            soundPool.play(win, 1, 1, 0, 0, 1);

            if(timeTaken < fastestTime) {

                //save high score
                editor.putLong("fastestTime", timeTaken);
                editor.commit();

                fastestTime = timeTaken;
            }

            distanceRemaining = 0;

            gameEnded = true;
        }
    }


    private void draw() {

        if(ourHolder.getSurface().isValid()) {
            //lock the area of mem we will draw to
            canvas = ourHolder.lockCanvas();
            //rub out the last frame
            canvas.drawColor(Color.argb(255, 0, 0, 0));


            paint.setColor(Color.argb(255, 255, 255, 255));
            // for debugging
            // draw hit boxes
            canvas.drawRect(player.getHitBox().left, player.getHitBox().top, player.getHitBox().right, player.getHitBox().bottom, paint);
            canvas.drawRect(enemy1.getHitBox().left, enemy1.getHitBox().top, enemy1.getHitBox().right, enemy1.getHitBox().bottom, paint);
            canvas.drawRect(enemy2.getHitBox().left, enemy2.getHitBox().top, enemy2.getHitBox().right, enemy2.getHitBox().bottom, paint);
            canvas.drawRect(enemy3.getHitBox().left, enemy3.getHitBox().top, enemy3.getHitBox().right, enemy3.getHitBox().bottom, paint);
            if (enemy4 != null) {
                canvas.drawRect(enemy4.getHitBox().left, enemy4.getHitBox().top, enemy4.getHitBox().right, enemy4.getHitBox().bottom, paint);
            }
            if (enemy5 != null) {
                canvas.drawRect(enemy5.getHitBox().left, enemy5.getHitBox().top, enemy5.getHitBox().right, enemy5.getHitBox().bottom, paint);
            }



            //white specs of dust
            //paint.setColor(Color.argb(255,255,255,255));

            for(SpaceDust spaceDust : dustList) {
                canvas.drawCircle(spaceDust.getX(), spaceDust.getY(), 2, paint);
            }


            //draw the player
            canvas.drawBitmap(player.getBitmap(), player.getX(), player.getY(), paint);

            canvas.drawBitmap(enemy1.getBitmap(), enemy1.getX(), enemy1.getY(), paint);
            canvas.drawBitmap(enemy2.getBitmap(), enemy2.getX(), enemy2.getY(), paint);
            canvas.drawBitmap(enemy3.getBitmap(), enemy3.getX(), enemy3.getY(), paint);
            if(enemy4 != null) {
                canvas.drawBitmap(enemy4.getBitmap(), enemy4.getX(), enemy4.getY(), paint);
            }
            if(enemy5 != null) {
                canvas.drawBitmap(enemy5.getBitmap(), enemy5.getX(), enemy5.getY(), paint);
            }

            if(!gameEnded) {
                //draw the HUD
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(25);
                canvas.drawText("Fastest:" + formatTime(fastestTime) + "s", 10, 20, paint);
                canvas.drawText("Time:" + formatTime(timeTaken) + "s", screenX / 2, 20, paint);
                canvas.drawText("Distance:" + distanceRemaining / 1000 + " KM", screenX / 3, screenY - 20, paint);
                canvas.drawText("Shield:" + player.getShieldStrength(), 10, screenY - 20, paint);
                canvas.drawText("Speed:" + player.getSpeed() * 60 + " MPS", (screenX / 3) * 2, screenY - 20, paint);

            } else {
                //this happens when the game ends.

                paint.setTextSize(80);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Game Over", screenX/2, 100, paint);
                paint.setTextSize(25);
                canvas.drawText("Fastest:" + formatTime(fastestTime) + "s", screenX / 2, 160, paint);
                canvas.drawText("Time:" + formatTime(timeTaken) + "s", screenX / 2, 200, paint);
                canvas.drawText("Distance:" + distanceRemaining / 1000 + " KM", screenX / 2, 240, paint);

                paint.setTextSize(80);
                canvas.drawText("Tap to replay!", screenX / 2, 350, paint);
            }

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {

        //slow it down a bit.
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {

        }
    }

    public void pause() {

        playing = false;
        try {
            gameThread.join();

        } catch (InterruptedException e) {

        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private String formatTime(long time){
        long seconds = (time) / 1000;
        long thousandths = (time) - (seconds * 1000);
        String strThousandths = "" + thousandths;
        if (thousandths < 100){strThousandths = "0" + thousandths;}
        if (thousandths < 10){strThousandths = "0" + strThousandths;}
        String stringTime = "" + seconds + "." + strThousandths;
        return stringTime;
    }

}
