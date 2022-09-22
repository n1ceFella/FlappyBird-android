package com.example.flappybird;

import android.content.Intent;
import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class GameEngine
{

    private GameView gameView;

    //List which populate and containing pipe objects
    private final List<Drawable> drawables;

    //Flying animation
    private int birdPositionX;
    private int birdPositionY;
    //var to control bird img cut
    private int imgNum = 0;
    //Fall variable
    private int velocity = 0;
    //Pipe animation coordinates
    private int pipeX;
    private int pipeY;
    //Gap between pipes
    //final int GAP = 350; (INITIAL)
    private final int GAP;

    private int scoreCounter;

    private final int UPDATE_MILISEC;

    //Stops game drawing
    private boolean inGame;

    GameEngine(GameView gameView){
        this.gameView = gameView;
        drawables = new ArrayList<>(2);
        inGame = true;
        scoreCounter = 0;

        birdPositionX = gameView.getdWidth() / 2 - gameView.getBirdImage().getWidth() / 2;
        birdPositionY = gameView.getdHeight() /2 - gameView.getBirdImage().getHeight() / 2;

        pipeX = gameView.getdWidth();
        pipeY = gameView.getdHeight()/-3;
        //Temp
        if(gameView.getdWidth() < 800) {
            GAP = gameView.getdHeight() / 4;
            UPDATE_MILISEC = 30;
        }
        else {
            GAP = 350;
            UPDATE_MILISEC = 20;
        }
        createFirstObject();
        fly();
    }

    public int getGAP() {
        return GAP;
    }

    public int getUPDATE_MILISEC() {
        return UPDATE_MILISEC;
    }

    public int getScoreCounter() {
        return scoreCounter;
    }

    public boolean isInGame() {
        return inGame;
    }

    public List<Drawable> getDrawables() {
        return drawables;
    }

    public int getBirdPositionX() {
        return birdPositionX;
    }

    public int getBirdPositionY() {
        return birdPositionY;
    }

    public void resetVelocity(){
        //If TEMP (USE ELSE AS INITIAL)
        if(gameView.getdWidth() < 800) {
            velocity = -25;
        }else {
            velocity = -30;
        }
    }

    //flying animation
    private void fly(){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        imgNum++;
                        if (imgNum >= 3) imgNum = 0;

                        Bitmap tempImg = Bitmap.createBitmap(gameView.getBirdImage(), imgNum * gameView.getBirdImage().getWidth() / 3,
                                0, gameView.getBirdImage().getWidth() / 3, gameView.getBirdImage().getHeight());
                        gameView.setBirdSubImage(tempImg);
                    }
                },
                200, 200);
    }

    //make bird fall by increasing velocity adding gravity
    public void fall()
    {
        if(birdPositionY < gameView.getdHeight() - gameView.getBirdSubImage().getHeight()) {
            //int gravity = 5; (INITIAL)
            int gravity = 6;
            velocity += gravity;
            birdPositionY += velocity;
            if(birdPositionY < 0)
            {
                birdPositionY += 5;
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
                    drawables.add(new Drawable(pipeX, (int) (Math.random() *
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
                    drawables.add(new Drawable(pipeX, (int)(Math.random() *
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
            if (birdPositionY + gameView.getBirdSubImage().getHeight() >=
                    gameView.getdHeight() - gameView.getFg().getHeight()) {
                inGame = false;
                Intent mainIntent = new Intent(gameView.getContext(), SplashActivity.class);
                //mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                gameView.getContext().startActivity(mainIntent);
            }
            //top pipe front touch
            else if (drawable.getX() == birdPositionX + gameView.getBirdSubImage().getWidth() &&
                    birdPositionY <= drawable.getY() +
                            gameView.getPipeUp().getHeight()) {
                inGame = false;
                Intent mainIntent = new Intent(gameView.getContext(), SplashActivity.class);
                //mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                gameView.getContext().startActivity(mainIntent);
            }
            //bottom pipe front touch
            else if (drawable.getX() == birdPositionX + gameView.getBirdSubImage().getWidth() &&
                    birdPositionY + gameView.getBirdSubImage().getHeight() >= drawable.getY() +
                            gameView.getPipeUp().getHeight() + GAP) {
                inGame = false;
                Intent mainIntent = new Intent(gameView.getContext(), SplashActivity.class);
                //mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                gameView.getContext().startActivity(mainIntent);
            }
            //top pipe lower touch
            else if ((drawable.getX() <= birdPositionX + gameView.getBirdSubImage().getWidth() &&
                    (drawable.getX() + gameView.getPipeUp().getWidth() >= birdPositionX)) &&
                    ((drawable.getY() + gameView.getPipeUp().getHeight()) >=
                            birdPositionY)) {
                inGame = false;
                Intent mainIntent = new Intent(gameView.getContext(), SplashActivity.class);
                //mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                gameView.getContext().startActivity(mainIntent);
            }
            //bottom pipe upper touch
            else if ((drawable.getX() <=  birdPositionX + gameView.getBirdSubImage().getWidth() &&
                    (drawable.getX() + gameView.getPipeBot().getWidth() > birdPositionX)) &&
                    ((drawable.getY() + gameView.getPipeUp().getHeight() + GAP) <=
                            (birdPositionY + gameView.getBirdSubImage().getHeight()))) {
                inGame = false;
                Intent mainIntent = new Intent(gameView.getContext(), SplashActivity.class);
                //mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                gameView.getContext().startActivity(mainIntent);
            }
        }
    }

    //We create first constant pair of pipes
    public void createFirstObject()
    {
        drawables.add(new Drawable(pipeX, pipeY));
    }

    //Increment scores when pipeX == birdPositionX
    public void scoreCounter()
    {
        for(GameEngine.Drawable drawable : drawables)
        {
            if((drawable.getX()+ gameView.getPipeUp().getWidth()) - 1 == birdPositionX) {
                scoreCounter++;
            }
        }
    }

    //Class were pipes creating and their coordinates updating
    public class Drawable
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


