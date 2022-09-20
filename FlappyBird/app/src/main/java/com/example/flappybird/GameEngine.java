package com.example.flappybird;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class GameEngine extends View
{

    private Bitmap bg;
    private Bitmap fg;
    private Bitmap pipeBot;
    private Bitmap pipeUp;

    //scores properties
    Paint scores = new Paint();

    //For updating animation
    Handler handler;
    Runnable runnable;
    //final int UPDATE_MILISEC = 20;
    final int UPDATE_MILISEC;

    //Display properties
    Display display;
    Point point;
    int dWidth, dHeight;
    Rect bgRect;
    Rect fgRect;

    //Here we store birds animation images
    Bitmap birdImage;
    Bitmap birdSubImage;

    //Flying animation
    int birdPositionX;
    int birdPositionY;

    //Variable for bird images array
    int index = 5;

    //var to control bird img cut
    int imgNum = 0;

    //Fall variable
    int velocity = 0;
    //int gravity = 5; (INITIAL)
    int gravity = 6;

    //Pipe animation coordinates
    int pipeX;
    int pipeY;

    //Gap between pipes
    //final int GAP = 350; (INITIAL)
    final int GAP;

    int scoreCounter;

    //Stops game drawing
    boolean inGame;

    //List which populate and containing pipe objects
    private List<Drawable> drawables;

    public GameEngine(Context context) {
        super(context);
        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };

        inGame = true;

        //Game imgs
        birdImage = BitmapFactory.decodeResource(getResources(),R.drawable.final1);
        birdSubImage = Bitmap.createBitmap(birdImage, imgNum * birdImage.getWidth(), 0, birdImage.getWidth() / 3, birdImage.getHeight());
        bg = BitmapFactory.decodeResource(getResources(),R.drawable.flappy_bird_bg);
        fg = BitmapFactory.decodeResource(getResources(),R.drawable.flappy_bird_fg);
        pipeBot = BitmapFactory.decodeResource(getResources(),R.drawable.flappy_bird_pipebottom);
        pipeUp = BitmapFactory.decodeResource(getResources(),R.drawable.flappy_bird_pipeup);

        scores.setColor(Color.BLACK);
        scores.setTextSize(50);
        scores.setTypeface(Typeface.DEFAULT);
        scores.setAntiAlias(true);

        //Get display size
        display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        point = new Point();
        display.getSize(point);
        dWidth = point.x;
        dHeight = point.y;

        //Bg and Fg rectangles to draw them on screen properly
        bgRect = new Rect(0,0,dWidth,dHeight);
        fgRect = new Rect(0,dHeight - fg.getHeight(),dWidth,dHeight);

        birdPositionX = dWidth / 2 - birdImage.getWidth()/2;
        birdPositionY = dHeight /2 - birdImage.getHeight()/2;

        pipeX = dWidth;
        pipeY = dHeight/-3;

        scoreCounter = 0;

        createFirstObject();

        //Temp
        if(dWidth < 800) {
            GAP = dHeight / 4;
            UPDATE_MILISEC = 30;
        }
        else {
            GAP = 350;
            UPDATE_MILISEC = 20;
        }
        fly();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(inGame) {
            canvas.drawBitmap(bg, null, bgRect, null);
            animation();
            fall();
            checkTouch();
            getScoreCounter();

            //draw bird
            canvas.drawBitmap(birdSubImage, birdPositionX, birdPositionY, null);

            for (Drawable drawable : drawables) {
                //Draw upper pipe
                canvas.drawBitmap(pipeUp, drawable.getX(), drawable.getY(), null);

                //Draw lower pipe
                canvas.drawBitmap(pipeBot, drawable.getX(), drawable.getY() +
                        pipeUp.getHeight() + GAP, null);

            }

            canvas.drawBitmap(fg, null, fgRect, null);
            canvas.drawText("Scores: " + scoreCounter, 80, dHeight - (fg.getHeight()/2), scores);
            handler.postDelayed(runnable, UPDATE_MILISEC);
        }
    }

    //When screen is touched bird fly upwards

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        int touch = event.getAction();
        if(touch == MotionEvent.ACTION_DOWN)
        {
            //If TEMP (USE ELSE AS INITIAL)
            if(dWidth < 800) {
                velocity = -25;
            }else {
                velocity = -30;
            }

        }
        return true;
    }

    //flying animation
    public void fly(){
        //birdSubImage = Bitmap.createBitmap(birdImage, imgNum * birdImage.getWidth(), 0,
                //birdImage.getWidth() / 3, birdImage.getHeight());
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        imgNum++;
                        if (imgNum >= 3) imgNum = 0;
                        birdSubImage = Bitmap.createBitmap(birdImage, imgNum * birdImage.getWidth() / 3,
                                0, birdImage.getWidth() / 3, birdImage.getHeight());
                    }
                },
                200, 200);
    }

    //make bird fall by increasing velocity adding gravity
    public void fall()
    {
        if(birdPositionY < dHeight - birdSubImage.getHeight() ) {
            velocity += gravity;
            birdPositionY = birdPositionY + velocity;
            if(birdPositionY < 0)
            {
                //birdPositionY += 20; (INITIAL)
                birdPositionY += 5;
            }
        }
    }

    //Pipes coordinates decrementing here
    //As soon as pipeX coordinates == screen size/3 ,new pair of pipes are created beyond the screen
    private void animation()
    {
        for(Drawable drawable : new ArrayList<Drawable>(drawables))
        {
            drawable.update();

            //Temp IF (USE ELSE AS INITIAL)
            if(dWidth < 800) {
                if (drawable.getX() == dWidth / 2) {
                    /**
                     *                 drawables.add(new Drawable(pipeX, (int)(Math.random() *
                     *                         (pipeUp.getHeight() -
                     *                                 ((pipeUp.getHeight() * 0.25))) - (pipeUp.getHeight() -
                     *                         (pipeUp.getHeight() * 0.25)))));
                     */
                    drawables.add(new Drawable(pipeX, (int) (Math.random() *
                            (dHeight -
                                    ((dHeight * 0.7))) - (dHeight -
                            (dHeight * 0.55)))));
                }
                if (drawable.getX() <= 0 - pipeBot.getWidth()) {
                    drawables.remove(drawable);
                }
            } else {
                if(drawable.getX() == dWidth/3 + dWidth/3)
                {
                    drawables.add(new Drawable(pipeX, (int)(Math.random() *
                            (pipeUp.getHeight() -
                                    ((pipeUp.getHeight() * 0.25))) - (pipeUp.getHeight() -
                            (pipeUp.getHeight() * 0.25)))));
                }
                if (drawable.getX() <= 0 - pipeBot.getWidth()) {
                    drawables.remove(drawable);
                }
            }
        }
    }

    //when bird touch obstacles inGame var == false and game stops
    private void checkTouch()
    {
        for (Drawable drawable : drawables)
        {
            //foreground touch
            if (birdPositionY + birdSubImage.getHeight() >=
                    dHeight - fg.getHeight()) {
                inGame = false;
                Intent mainIntent = new Intent(getContext(), SplashActivity.class);
                //mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getContext().startActivity(mainIntent);
            }
            //top pipe front touch
            else if (drawable.getX() == birdPositionX + birdSubImage.getWidth() &&
                    birdPositionY <= drawable.getY() +
                            pipeUp.getHeight()) {
                inGame = false;
                Intent mainIntent = new Intent(getContext(), SplashActivity.class);
                //mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getContext().startActivity(mainIntent);
            }
            //bottom pipe front touch
            else if (drawable.getX() == birdPositionX + birdSubImage.getWidth() &&
                    birdPositionY + birdSubImage.getHeight() >= drawable.getY() +
                            pipeUp.getHeight() + GAP) {
                inGame = false;
                Intent mainIntent = new Intent(getContext(), SplashActivity.class);
                //mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getContext().startActivity(mainIntent);
            }
            //top pipe lower touch
            else if ((drawable.getX() <= birdPositionX + birdSubImage.getWidth() &&
                    (drawable.getX() + pipeUp.getWidth() >= birdPositionX)) &&
                    ((drawable.getY() + pipeUp.getHeight()) >=
                            birdPositionY)) {
                inGame = false;
                Intent mainIntent = new Intent(getContext(), SplashActivity.class);
                //mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getContext().startActivity(mainIntent);
            }
            //bottom pipe upper touch
            else if ((drawable.getX() <=  birdPositionX + birdSubImage.getWidth() &&
                    (drawable.getX() + pipeBot.getWidth() > birdPositionX)) &&
                    ((drawable.getY() + pipeUp.getHeight() + GAP) <=
                            (birdPositionY + birdSubImage.getHeight()))) {
                inGame = false;
                Intent mainIntent = new Intent(getContext(), SplashActivity.class);
                //mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getContext().startActivity(mainIntent);
            }
        }
    }

    //We create first constant pair of pipes
    private void createFirstObject()
    {
        drawables = new ArrayList<Drawable>(2);
        drawables.add(new Drawable(pipeX, pipeY));
    }

    //Increment scores when pipeX == birdPositionX
    private void getScoreCounter()
    {
        for(Drawable drawable : drawables)
        {
            if((drawable.getX()+ pipeUp.getWidth()) - 1 == birdPositionX) {
                scoreCounter += 1;
            }
        }
    }

    //Class were pipes creating and their coordinates updating
    public static class Drawable
    {
        private int x;
        private int y;
        Drawable(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
        int getX()
        {
            return x;
        }
        int getY()
        {
            return  y;
        }
        void update() {
            x -= 3;
        }
    }
}


