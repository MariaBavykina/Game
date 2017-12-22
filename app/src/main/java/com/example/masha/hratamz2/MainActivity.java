package com.example.masha.hratamz2;

import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView scoreLabel;
    private TextView startLabel;
    private ImageView pacman;
    private ImageView blue;
    private ImageView black;
    private ImageView pink;
    private Button pauseBtn;

    //Size
    private int frameHeight;
    private int pacmanSize;
    private int scrWidth;
    private int scrHeight;

    //Position
    private int pacmanY;
    private int blueX;
    private int blueY;
    private int blackX;
    private int blackY;
    private int pinkX;
    private int pinkY;

    //Speed
    private int pacmanSpeed;
    private int blueSpeed;
    private int pinkSpeed;
    private int blackSpeed;

    //Score
    private int score = 0;

    //Initialize Class
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private SoundPlayer sound;

    //Status Check
    private boolean action_flg = false;
    private boolean start_flg = false;
    private boolean pause_flg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sound = new SoundPlayer(this);

        scoreLabel = (TextView)findViewById(R.id.scoreLabel);
        startLabel = (TextView)findViewById(R.id.startLabel);
        pacman = (ImageView)findViewById(R.id.pacman);
        blue = (ImageView)findViewById(R.id.blue);
        black = (ImageView)findViewById(R.id.black);
        pink = (ImageView)findViewById(R.id.pink);

        pauseBtn = (Button) findViewById(R.id.pauseBtn);

        //Get screen size
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        scrWidth = size.x;
        scrHeight = size.y;

        //Speed
        pacmanSpeed = Math.round(scrHeight / 60F);
        blueSpeed = Math.round(scrWidth / 60F);
        pinkSpeed = Math.round(scrWidth / 36F);
        blackSpeed = Math.round(scrWidth / 45F);

        Log.v("SPEED_BOX", pacmanSpeed+"");
        Log.v("SPEED_BLUE", blueSpeed+"");
        Log.v("SPEED_PINK", pinkSpeed+"");
        Log.v("SPEED_BLACK", blueSpeed+"");

        //Move to out of screen
        blue.setX(-(80));
        blue.setY(-(80));
        pink.setX(-(80));
        pink.setY(-(80));
        black.setX(-(80));
        black.setY(-(80));

        scoreLabel.setText("Score: 0");

        View.OnClickListener pausePushed = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pause_flg == false) {

                    pause_flg = true;

                    //Stop the timer
                    timer.cancel();
                    timer = null;

                    //Change Button Text
                    pauseBtn.setText("START");

                } else {

                    pause_flg = false;

                    //Change Button Text
                    pauseBtn.setText("PAUSE");

                    //Create and Start the timer
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    changePos();
                                }
                            });
                        }
                    }, 0, 20);
                }
            }
        };

        pauseBtn.setOnClickListener(pausePushed);
    }

    public void changePos() {

        hitCheck();

        //Blue
        blueX -= blueSpeed;
        if (blueX < 0) {
            blueX = scrWidth + 20;
            blueY = (int)Math.floor(Math.random()*(frameHeight-blue.getHeight()));
        }
        blue.setX(blueX);
        blue.setY(blueY);

        //Black
        blackX -= blackSpeed;
        if (blackX < 0) {
            blackX = scrWidth + 10;
            blackY = (int)Math.floor(Math.random()*(frameHeight-black.getHeight()));
        }
        black.setX(blackX);
        black.setY(blackY);

        //Pink
        pinkX -= pinkSpeed;
        if (pinkX < 0) {
            pinkX = scrWidth + 5000;
            pinkY = (int)Math.floor(Math.random()*(frameHeight-pink.getHeight()));
        }
        pink.setX(pinkX);
        pink.setY(pinkY);

        //Move Pacman
        if (action_flg == true) {
            //Touching
            pacmanY -= pacmanSpeed;
        } else if (action_flg == false) {
            //Releasing
            pacmanY += pacmanSpeed;
        }

        //Check pacman position
        if (pacmanY < 0)
            pacmanY = 0;
        if (pacmanY > frameHeight - pacmanSize)
            pacmanY = frameHeight - pacmanSize;

        pacman.setY(pacmanY);

        scoreLabel.setText("Score: " + score);
    }

    public void hitCheck() {
        //If the center of the ball is in the box, it counts as a hit

        //Blue
        int blueCenterX = blueX + blue.getWidth()/2;
        int blueCenterY = blueY + blue.getHeight()/2;

        // 0 <= blueCenterX <= pacmanWidth
        // pacmanY <= blueCenterY <= pacmanY+pacmanHeight

        if(0 <= blueCenterX && blueCenterX <= pacmanSize &&
                pacmanY <= blueCenterY && blueCenterY <= pacmanY+pacmanSize) {
            score +=10;
            blueX = -10;
            sound.playHitSound();
        }

        //Pink
        int pinkCenterX = pinkX + pink.getWidth()/2;
        int pinkCenterY = pinkY + pink.getHeight()/2;

        if(0 <= pinkCenterX && pinkCenterX <= pacmanSize &&
                pacmanY <= pinkCenterY && pinkCenterY <= pacmanY+pacmanSize) {
            score += 30;
            pinkX = -10;
            sound.playHitSound();
        }

        //Black
        int blackCenterX = blackX + black.getWidth()/2;
        int blackCenterY = blackY + black.getHeight()/2;

        if(0 <= blackCenterX && blackCenterX <= pacmanSize &&
                pacmanY <= blackCenterY && blackCenterY <= pacmanY+pacmanSize) {

            //Stop Timer
            timer.cancel();
            timer = null;

            sound.playOverSound();

            //Show result
            Intent intent = new Intent(getApplicationContext(), Result.class);
            intent.putExtra("Score", score);
            startActivity(intent);
        }
    }

    public boolean onTouchEvent(MotionEvent me) {
        if (start_flg == false) {

            start_flg = true;

            FrameLayout frame = (FrameLayout)findViewById(R.id.frame);
            frameHeight = frame.getHeight();

            pacmanY = (int)pacman.getY();
            pacmanSize = pacman.getHeight();

            startLabel.setVisibility(View.GONE);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }, 0, 20);

        } else {
            if (me.getAction() == MotionEvent.ACTION_DOWN) {
                action_flg = true;
            } else if (me.getAction() == MotionEvent.ACTION_UP) {
                action_flg = false;
            }
        }

        return true;
    }

    //Disable Return button
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }
}
