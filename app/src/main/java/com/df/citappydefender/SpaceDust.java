package com.df.citappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

/**
 * Created by n0206863 on 10/08/2017.
 */

public class SpaceDust {


    //private Bitmap bitmap;
    private int x, y;
    private int speed = 1;
    private int maxX;
    private int minX;
    private int minY;
    private int maxY;

    public SpaceDust(int screenX, int screenY) {
        //bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);
        maxX = screenX;
        maxY = screenY;
        minX =0;
        minY =0;

        Random generator = new Random();
        speed = generator.nextInt(10);

        x = generator.nextInt(maxX);
        y = generator.nextInt(maxY);
        //x = screenX;
        //y = generator.nextInt(maxY) - bitmap.getHeight();

    }

    //public Bitmap getBitmap() {
//        return bitmap;
//    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void update (int playerSpeed) {
        x -= playerSpeed;
        x -= speed;

        //respawn when off the screen
        if (x < 0) {
            Random generator = new Random();
            speed = generator.nextInt(15);
            x = maxX;
            y = generator.nextInt(maxY);

        }

    }
}
