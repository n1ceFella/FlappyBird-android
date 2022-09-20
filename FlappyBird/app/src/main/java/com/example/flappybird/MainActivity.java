package com.example.flappybird;

        import android.app.Activity;
        import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GameEngine gameEngine = new GameEngine(this);
        setContentView(gameEngine);

    }
}
