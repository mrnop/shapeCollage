package com.isarainc.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.isarainc.shapecollage.R;

public class SplashActivity extends Activity {

	private Animation animation;
	private ImageView logo1;
	private ImageView logo2;
	private ImageView logo3;
	private ImageView logo4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Fabric.with(this, new Crashlytics());
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.ac_splash);

		logo1 = (ImageView) findViewById(R.id.logo1);
		logo2 = (ImageView) findViewById(R.id.logo2);
		logo3 = (ImageView) findViewById(R.id.logo3);
		logo4 = (ImageView) findViewById(R.id.logo4);

		if (savedInstanceState == null) {
			flyIn();
		}

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				endSplash();
			}
		}, 3000);
	}

	private void flyIn() {
		animation = AnimationUtils.loadAnimation(this, R.anim.holder_top_left);
		logo1.startAnimation(animation);
		animation = AnimationUtils.loadAnimation(this, R.anim.holder_top_right);
		logo2.startAnimation(animation);
		animation = AnimationUtils.loadAnimation(this, R.anim.holder_bottom_left);
		logo3.startAnimation(animation);
		animation = AnimationUtils.loadAnimation(this, R.anim.holder_bottom_right);
		logo4.startAnimation(animation);
	}

	private void endSplash() {

		animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation_back);
		logo1.startAnimation(animation);
		animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation_back);
		logo2.startAnimation(animation);
		animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation_back);
		logo3.startAnimation(animation);
		animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation_back);
		logo4.startAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationStart(Animation arg0) {
			}
		});

	}

	@Override
	public void onBackPressed() {
		// Do nothing
	}

}
