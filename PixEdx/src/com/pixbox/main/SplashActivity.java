package com.pixbox.main;



import com.videoexpress.pixedx.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class SplashActivity extends Activity {

	 private static final long SPLASH_DELAY_MILLIS = 6000;
	 private Animation anim;
	 
	 private Animation myAnimation_Alpha;
	 private Animation myAnimation_Scale;
	 private Animation myAnimation_Translate;
	 private Animation myAnimation_Rotate;
	 ImageView ivIcon;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS); //去标题栏 
		setContentView(R.layout.splash_layout);
		
		ivIcon = (ImageView)findViewById(R.id.imageViewIcon);
		
		anim = AnimationUtils.loadAnimation(this, R.anim.splash_alpha);
		anim.setRepeatCount(Integer.MAX_VALUE);
		anim.setRepeatMode(Animation.REVERSE);
		
	
		
		myAnimation_Alpha=new AlphaAnimation(0.1f, 1.0f);
		myAnimation_Alpha.setDuration(2000);
		myAnimation_Alpha.setRepeatCount(Integer.MAX_VALUE);
		myAnimation_Alpha.setRepeatMode(Animation.REVERSE);

		myAnimation_Scale =new ScaleAnimation(0.0f, 1.4f, 0.0f, 1.4f,
		             Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

		myAnimation_Translate=new TranslateAnimation(30.0f, -80.0f, 30.0f, 300.0f);

		myAnimation_Rotate=new RotateAnimation(0.0f, +350.0f,
		               Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
		
		ivIcon.startAnimation(myAnimation_Alpha);
		
		// 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity 
        new Handler().postDelayed(new Runnable() {
            public void run() {
                goEntry();
            }
        }, SPLASH_DELAY_MILLIS);
	}
	
	 private void goEntry() {
	        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
	        SplashActivity.this.startActivity(intent);
	        SplashActivity.this.finish();
	    }
}
