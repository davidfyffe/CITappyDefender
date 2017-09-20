package com.df.citappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Created by n0206863 on 09/08/2017.
 */

public class PlayerShip {

    private Bitmap bitmap;
    private int x, y;
    private int speed = 0;
    private boolean boosting;
    private final int GRAVITY = -12;
    private int maxY;
    private int minY;
    private int maxX;
    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 20;

    private Rect hitBox;

    private int shieldStrength = 3;

    public PlayerShip(Context context , int screenX, int screenY) {
        x=50;
        y=50;
        speed = 1;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);
        boosting = false;

        //y + the height of the ship
        maxY = screenY + bitmap.getHeight();
        minY = 0;

        hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }


    public boolean isBoosting() {
        return boosting;
    }
    public void stopBoosting() {
        this.boosting = false;
    }
    public void setBoosting() {
        this.boosting = true;
    }

    public void update() {
        if (boosting) {
            speed += 2;
        } else {
            speed -= 5;
        }

        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }

        if(speed < MIN_SPEED) {
            speed = MIN_SPEED;
        }

        y -= speed + GRAVITY;

        //sotp it going of the screen
        if (y < minY) {
            y = minY;
        }

        if (y > maxY) {
            y = maxY;
        }

        //refresh the hitbox location
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getSpeed() {
        return speed;
    }
    public Bitmap getBitmap() {
        return bitmap;
    }
    public Rect getHitBox() { return hitBox; }

    public int getShieldStrength() {
        return shieldStrength;
    }

    public void reduceShieldStrength() {
        shieldStrength --;
    }
}
