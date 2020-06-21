package com.saravana.befit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    private final int SCREEN_TIMEOUT=3000;
    private Animation topAnim,bottomAnim;
    private ImageView logoImg;
    private TextView titleText,mottoText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        initWidgets();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                goToMainActivity();
            }
        },SCREEN_TIMEOUT);

    }

    private void initWidgets() {
        logoImg = findViewById(R.id.splash_screen_img);
        titleText = findViewById(R.id.splash_screen_title);
        mottoText = findViewById(R.id.splash_screen_motto);

        topAnim = AnimationUtils.loadAnimation(SplashScreen.this,R.anim.splash_top_anim);
        bottomAnim = AnimationUtils.loadAnimation(SplashScreen.this,R.anim.splash_bottom_anim);

        logoImg.setAnimation(topAnim);
        titleText.setAnimation(bottomAnim);
        mottoText.setAnimation(bottomAnim);
    }

    private void goToMainActivity() {
        Intent mainIntent = new Intent(SplashScreen.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
