package com.pixbox.main;

import java.io.IOException;

import com.google.gson.Gson;
import com.pixbox.beans.MediaProfileBean;
import com.videoexpress.pixedx.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

public class RtspPlayActivity extends Activity{
	static String TAG = "RtspPlayActivity";
	VideoView videoView;
	MediaController mc;
	Intent intent;
	Bundle bundle;
	String rtspUrl;
	ImageButton imageButtonPlay;
	
	MediaPlayer mp;
	
	private ProgressDialog loadingDlg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rtsp_play_layout);
		
		//获取上一级数据
		intent = this.getIntent();
		bundle = intent.getExtras();
		rtspUrl=bundle.getString("RtspUrl");
		Log.i(TAG, "get bundle data "+rtspUrl);
		
		loadingDlg  = new ProgressDialog(this);
	
		loadingDlg.setMessage("加载中...");
		loadingDlg.show();
		
		//mc = new MediaController(this);
		videoView = (VideoView)this.findViewById(R.id.videoViewRtsp);
		imageButtonPlay = (ImageButton)findViewById(R.id.imageButtonPlay);
		
		//mc.setAnchorView(videoView);
        //mc.setMediaPlayer(videoView);
		
     // TODO Auto-generated method stub
		videoView.setMediaController(mc);
		videoView.setVideoURI(Uri.parse(rtspUrl));
		//videoView.setVideoPath(rtspUrl);
		
		videoView.requestFocus();
		
       imageButtonPlay.setOnClickListener(new ImageButtonOnClickListener());
       videoView.setOnPreparedListener(new OnPreparedListener(){

			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				loadingDlg.dismiss();
			    videoView.start();
	 
			}
        	
        });
		
	}
	
	
	private class ImageButtonOnClickListener implements OnClickListener
	{

		@Override
		public void onClick(View arg0) {
			
		}
		
	}
	
}
