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

public class GameEngine
{

    GameView gameView;

    GameEngine(GameView gameView){
        this.gameView = gameView;
        createFirstObject();
        fly();
    }


    //List which populate and containing pipe objects
    private List<GameEngine.Drawable> drawables;

    public List<Drawable> getDrawables() {
        return drawables;
    }

    public void resetVelocity(){
        //If TEMP (USE ELSE AS INITIAL)
        if(gameView.getdWidth() < 800) {
            gameView.setVelocity(-25);
        }else {
            gameView.setVelocity(-30);
        }
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
                        gameView.setImgNum(gameView.getImgNum() + 1);
                        if (gameView.getImgNum() >= 3) gameView.setImgNum(0);

                        Bitmap tempImg = Bitmap.createBitmap(gameView.getBirdImage(), gameView.getImgNum() * gameView.getBirdImage().getWidth() / 3,
                                0, gameView.getBirdImage().getWidth() / 3, gameView.getBirdImage().getHeight());
                        gameView.setBirdSubImage(tempImg);
                    }
                },
                200, 200);
    }

    //make bird fall by increasing velocity adding gravity
    public void fall()
    {
        if(gameView.getBirdPositionY() < gameView.getdHeight() - gameView.getBirdSubImage().getHeight()) {
            gameView.setVelocity(gameView.getVelocity() + gameView.getGravity());
            gameView.setBirdPositionY(gameView.getBirdPositionY() + gameView.getVelocity());
            if(gameView.getBirdPositionY() < 0)
            {
                //birdPositionY += 20; (INITIAL)
                //birdPositionY += 5;
                gameView.setBirdPositionY(gameView.getBirdPositionY() + 5);
            }
        }
    }

    //Pipes coordinates decrementing here
    //As soon as pipeX coordinates == screen size/3 ,new pair of pipes are created beyond the screen
    public void animation()
    {
        for(Drawable drawable : new ArrayList<>(drawables))
        {
            drawable.update();

            //Temp IF (USE ELSE AS INITIAL)
            if(gameView.getdWidth() < 800) {
                if (drawable.getX() == gameView.getdWidth() / 2) {
                    /**
                     *                 drawables.add(new Drawable(pipeX, (int)(Math.random() *
                     *                         (pipeUp.getHeight() -
                     *                                 ((pipeUp.getHeight() * 0.25))) - (pipeUp.getHeight() -
                     *                         (pipeUp.getHeight() * 0.25)))));
                     */
                    drawables.add(new Drawable(gameView.getPipeX(), (int) (Math.random() *
                            (gameView.getdHeight() -
                                    ((gameView.getdHeight() * 0.7))) - (gameView.getdHeight() -
                            (gameView.getdHeight() * 0.55)))));
                }
                if (drawable.getX() <= 0 - gameView.getPipeBot().getWidth()) {
                    drawables.remove(drawable);
                }
            } else {
                if(drawable.getX() == gameView.getdWidth()/3 + gameView.getdWidth()/3)
                {
                    drawables.add(new Drawable(gameView.getPipeX(), (int)(Math.random() *
                            (gameView.getPipeUp().getHeight() -
                                    ((gameView.getPipeUp().getHeight() * 0.25))) - (gameView.getPipeUp().getHeight() -
                            (gameView.getPipeUp().getHeight() * 0.25)))));
                }
                if (drawable.getX() <= 0 - gameView.getPipeBot().getWidth()) {
                    drawables.remove(drawable);
                }
            }
        }
    }

    //when bird touch obstacles inGame var == false and game stops
    public void checkTouch()
    {
        for (GameEngine.Drawable drawable : drawables)
        {
            //foreground touch
            if (gameView.getBirdPositionY() + gameView.getBirdSubImage().getHeight() >=
                    gameView.getdHeight() - gameView.getFg().getHeight()) {
                gameView.setInGame(false);
                Intent mainIntent = new Intent(gameView.getContext(), SplashActivity.class);
                //mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                gameView.getContext().startActivity(mainIntent);
            }
            //top pipe front touch
            else if (drawable.getX() == gameView.getBirdPositionX() + gameView.getBirdSubImage().getWidth() &&
                    gameView.getBirdPositionY() <= drawable.getY() +
                            gameView.getPipeUp().getHeight()) {
                gameView.setInGame(false);
                Intent mainIntent = new Intent(gameView.getContext(), SplashActivity.class);
                //mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                gameView.getContext().startActivity(mainIntent);
            }
            //bottom pipe front touch
            else if (drawable.getX() == gameView.getBirdPositionX() + gameView.getBirdSubImage().getWidth() &&
                    gameView.getBirdPositionY() + gameView.getBirdSubImage().getHeight() >= drawable.getY() +
                            gameView.getPipeUp().getHeight() + gameView.getGAP()) {
                gameView.setInGame(false);
                Intent mainIntent = new Intent(gameView.getContext(), SplashActivity.class);
                //mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                gameView.getContext().startActivity(mainIntent);
            }
            //top pipe lower touch
            else if ((drawable.getX() <= gameView.getBirdPositionX() + gameView.getBirdSubImage().getWidth() &&
                    (drawable.getX() + gameView.getPipeUp().getWidth() >= gameView.getBirdPositionX())) &&
                    ((drawable.getY() + gameView.getPipeUp().getHeight()) >=
                            gameView.getBirdPositionY())) {
                gameView.setInGame(false);
                Intent mainIntent = new Intent(gameView.getContext(), SplashActivity.class);
                //mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                gameView.getContext().startActivity(mainIntent);
            }
            //bottom pipe upper touch
            else if ((drawable.getX() <=  gameView.getBirdPositionX() + gameView.getBirdSubImage().getWidth() &&
                    (drawable.getX() + gameView.getPipeBot().getWidth() > gameView.getBirdPositionX())) &&
                    ((drawable.getY() + gameView.getPipeUp().getHeight() + gameView.getGAP()) <=
                            (gameView.getBirdPositionY() + gameView.getBirdSubImage().getHeight()))) {
                gameView.setInGame(false);
                Intent mainIntent = new Intent(gameView.getContext(), SplashActivity.class);
                //mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                gameView.getContext().startActivity(mainIntent);
            }
        }
    }

    //We create first constant pair of pipes
    public void createFirstObject()
    {
        drawables = new ArrayList<GameEngine.Drawable>(2);
        drawables.add(new Drawable(gameView.getPipeX(), gameView.getPipeY()));
    }

    //Increment scores when pipeX == birdPositionX
    public void scoreCounter()
    {
        for(GameEngine.Drawable drawable : drawables)
        {
            if((drawable.getX()+ gameView.getPipeUp().getWidth()) - 1 == gameView.getBirdPositionX()) {
                gameView.setScoreCounter(gameView.getScoreCounter() + 1);
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


