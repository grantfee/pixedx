package com.pixbox.player;

import com.google.gson.Gson;
import com.pixbox.beans.MediaProfileBean;
import com.videoexpress.pixedx.R;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;



public class PixBoxMediaPlayerActivity extends Activity  {
	private final String TAG=this.getClass().getSimpleName();
	private final static int  MEDIA_TYPE_LIVE=0;
	private final static int MEDIA_TYPE_VOD=1;
	private int mediaType=-1;

	private Button playCtrlBtn;  //用于开始和暂停的按钮
	private SurfaceView pView;   //绘图容器对象，用于把视频显示在屏幕上
	private String jsonStr;   
	private MediaProfileBean mpb;
	private String url="";//视频播放地址
	private MediaPlayer mediaPlayer;    //播放器控件
	private int postSize;    //保存已播视频大小
	private SeekBar seekbar;   //进度条控件
	private boolean playingFlag = true;   //用于判断视频是否在播放中
	private boolean finishPlaying=false;
	private LinearLayout controlBarContainer;
	private RelativeLayout titleBarContainer;   
	private boolean displayControlBar;   //用于标志当前controlbar是否正在显示
	private int controlBarInactive=0;//用于指示controllbar不活动次数
	private Button backButton;   //返回按钮
	private View progressBar;   //ProgressBar 
	private TextView tvBufferSize;
	private upDateSeekBar updateProgress;   //更新进度条用
	private TextView tvPlayerTitle;
	private TextView tvPlayerCurTime;
	private TextView tvSeekBarStartTime;
	private TextView tvSeekBarEndTime;
	
	private int videoWidth=0;
	private int videoHeight= 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);   //全屏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 应用运行时，保持屏幕高亮，不锁屏
		
		setContentView(R.layout.pixbox_player_layout);   //加载布局文件
		
		//获取上一级数据
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		jsonStr=bundle.getString("MediaPlayUrl");
		if(jsonStr==null){
			Toast.makeText(getApplicationContext(), "错误:获取元媒体数据失败", Toast.LENGTH_LONG).show();
		}
		Gson gson =new Gson();
		mpb = gson.fromJson(jsonStr, MediaProfileBean.class);
		if(mpb!=null){
			url = mpb.getPlay_url();
		}
		
		Log.i(TAG, "get bundle data play url:"+url);
				
		initView();  //初始化数据
		setListener();   //绑定相关事件
	}
	
	private void initView() {
		mediaPlayer = new MediaPlayer();   //创建一个播放器对象
		updateProgress = new upDateSeekBar();  //创建更新进度条对象
		
		//backButton = (Button) findViewById(R.id.back);  //返回按钮
		seekbar = (SeekBar) findViewById(R.id.seekbar);  //进度条
		playCtrlBtn = (Button) findViewById(R.id.play);
		playCtrlBtn.setEnabled(false); //刚进来，设置其不可点击
		pView = (SurfaceView) findViewById(R.id.mSurfaceView);
		pView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);   //不缓冲
		pView.getHolder().setKeepScreenOn(true);   //保持屏幕高亮
		pView.getHolder().addCallback(new surFaceView());   //设置监听事件
		titleBarContainer = (RelativeLayout) findViewById(R.id.playerTitleBar);
		controlBarContainer = (LinearLayout)findViewById(R.id.controlBarContainer);
		
		progressBar = findViewById(R.id.pb); 
		tvBufferSize = (TextView)findViewById(R.id.textViewBufferStat);
		tvBufferSize.setVisibility(View.GONE);
		
		tvPlayerTitle = (TextView)findViewById(R.id.textViewPlayerTitle);
		tvPlayerCurTime = (TextView)findViewById(R.id.textViewCurTime);
		
		tvSeekBarStartTime = (TextView)findViewById(R.id.textViewStartTime);
		tvSeekBarEndTime = (TextView)findViewById(R.id.textViewEndTime);
		
		if(mpb!=null){
			tvPlayerTitle.setText(mpb.getTitle());
		}	
		
		progressBar.setVisibility(View.VISIBLE);
		playCtrlBtn.setEnabled(false); 
		hideControlBar();
	}
	
	private void showProgressBar()
	{
		progressBar.setVisibility(View.VISIBLE);
	}
	
	private void hideProgressBar()
	{
		progressBar.setVisibility(View.GONE);
	}
	
	private void showControlBar()
	{
	
		titleBarContainer.setVisibility(View.VISIBLE);
		controlBarContainer.setVisibility(View.VISIBLE);
		
		if(mediaType == MEDIA_TYPE_LIVE){
			seekbar.setVisibility(View.GONE);
			tvSeekBarStartTime.setVisibility(View.GONE);
			tvSeekBarEndTime.setVisibility(View.GONE);
		}
		displayControlBar=true;
	}
	private void hideControlBar()
	{
		titleBarContainer.setVisibility(View.GONE);
		controlBarContainer.setVisibility(View.GONE);
		displayControlBar=false;
	}

	class PlayMovie extends Thread {   //播放视频的线程

		int post = 0;

		public PlayMovie(int post) {
			this.post = post;

		}

		@Override
		public void run() {
			Message message = Message.obtain();
			try {
				Log.i(TAG, "runrun  "+url);
				finishPlaying=false;
				mediaPlayer.reset();    //回复播放器默认
				
				mediaPlayer.setDataSource(url);   //设置播放路径
				mediaPlayer.setDisplay(pView.getHolder());  //把视频显示在SurfaceView上
				mediaPlayer.setOnPreparedListener(new PreparedOkListener(post));  //设置监听事件
				mediaPlayer.setOnInfoListener(new infoListener());
				mediaPlayer.prepare();  //准备播放
			} catch (Exception e) {
				message.what = 2;
				Log.e(TAG, e.toString());
			}

			super.run();
		}
	}
	
	//mediaplayer事件监听
	class infoListener implements OnInfoListener{

		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			// TODO Auto-generated method stub
			switch(what){
			case MediaPlayer.MEDIA_INFO_BUFFERING_START:
				//progressBar.setVisibility(View.VISIBLE);  //准备完成后，隐藏控件
				//tvBufferSize.setVisibility(View.VISIBLE);
				Log.i(TAG, "buffering start");
				//tvBufferSize.setText(percent+"%");
				break;
			case MediaPlayer.MEDIA_INFO_BUFFERING_END:
				//progressBar.setVisibility(View.GONE);  //准备完成后，隐藏控件
				//tvBufferSize.setVisibility(View.GONE);
				Log.i(TAG, "buffering end");
				break;
			
			}
			return true;
		}
		
	}
	
	public void fixVideoSizeOnScreen()
	{
		  // 设置表面大小以匹配视频或显示器大小，取决于那个大小较小 
	   int  mVideoWidth = mediaPlayer.getVideoWidth(); 
	   int  mVideoHeight = mediaPlayer.getVideoHeight(); 
	   
	   if(mVideoWidth==0||mVideoWidth==0){
		   Log.i(TAG, "video width:" + mVideoWidth+",videoHeight:"+mVideoWidth);
		   return;
	   }
	     
	    int screenWidth = getWindowManager().getDefaultDisplay().getWidth(); 
	    int screenHeight = getWindowManager().getDefaultDisplay().getHeight(); 

	    if (mVideoWidth > screenWidth 
	        || mVideoHeight > screenHeight) {//如果视频宽度大于显示器大小，得到比率 
	        float heightRatio = (float)mVideoHeight / (float)screenHeight; 
	        float widthRatio = (float)mVideoWidth / (float)screenWidth; 
	         
	        if (heightRatio > 1 || widthRatio >1) {//只需宽或高一个方向超出即可 
	            
	         if (heightRatio > widthRatio) {//以高为准 
	            mVideoHeight = (int) Math.ceil((float)mVideoHeight / (float)heightRatio); 
	            mVideoWidth = (int) Math.ceil((float)mVideoWidth / (float)heightRatio);    
	         } else {//以宽为准 
	            mVideoHeight = (int) Math.ceil((float)mVideoHeight / (float)widthRatio); 
	            mVideoWidth = (int) Math.ceil((float)mVideoWidth / (float)widthRatio); 
	         } 
	            
	        } 
	        
	    }else{//视频比屏幕小，则放大
	    	 	float heightRatio =  (float)screenHeight/(float)mVideoHeight; 
		        float widthRatio =   (float)screenWidth/(float)mVideoWidth; 
		        if(heightRatio > widthRatio){//以宽比例为准计算
		        	 mVideoHeight = (int) Math.ceil((float)mVideoHeight * (float)widthRatio); 
			         mVideoWidth = (int) Math.ceil((float)mVideoWidth * (float)widthRatio); 
		        }else{
		        	 mVideoHeight = (int) Math.ceil((float)mVideoHeight * (float)heightRatio); 
			         mVideoWidth = (int) Math.ceil((float)mVideoWidth * (float)heightRatio); 
		        }
		        
	    }
	    Log.i(TAG, "after fixed video width:" + mVideoWidth+",videoHeight:"+mVideoHeight);
	    //设置显示大小---- 
	    ViewGroup.LayoutParams lp = pView.getLayoutParams();
		lp.height = mVideoHeight;
		lp.width = mVideoWidth;
	    pView.setLayoutParams(lp);
	    pView.getHolder().setFixedSize(mVideoWidth, mVideoHeight);   
	}
	
	class PreparedOkListener implements OnPreparedListener {
		int postSize;

		public PreparedOkListener(int postSize) {
			this.postSize = postSize;
		}

		@Override
		public void onPrepared(MediaPlayer mp) {
			Log.i(TAG, "onPrepared play");
			Log.i(TAG, "post " + postSize);
			hideProgressBar();
			hideControlBar();
			playCtrlBtn.setEnabled(true); 

			//视频适配设备屏幕大小
			fixVideoSizeOnScreen();
			
			int duration = mediaPlayer.getDuration();
			Log.i(TAG, "get duration" + mediaPlayer.getDuration());
			if(duration <= 0){//确定媒体类型
				mediaType = MEDIA_TYPE_LIVE;
			}else{
				duration = duration/1000;
				mediaType = MEDIA_TYPE_VOD;
				int h = duration/3600;
				int m = (duration%3600)/60;
				int s = duration%60;
				
				String hour = String.valueOf(h);
				String min = String.valueOf(m);
				String sec = String.valueOf(s);
				if(h<10){
					hour = "0"+h;
				}
				if(m<10){
					min = "0"+m;
				}
				if(s<10){
					sec = "0"+s;
				}
				
				tvSeekBarEndTime.setText(hour+":"+min+":"+sec);
			}

			if (mediaPlayer != null) { 
				mediaPlayer.start();  //开始播放视频
			} else {
				return;
			}
			if (postSize > 0) {  //说明中途停止过（activity调用过pase方法，不是用户点击停止按钮），跳到停止时候位置开始播放
				Log.i(TAG, "seekTo ");
				mediaPlayer.seekTo(postSize);   //跳到postSize大小位置处进行播放
			}
			new Thread(updateProgress).start();   //启动线程，更新进度条
			
		}
	}

	private class surFaceView implements Callback {     //上面绑定的监听的事件

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {   //创建完成后调用
			if (postSize > 0 && url!= null) {    //说明，停止过activity调用过pase方法，跳到停止位置播放
				new PlayMovie(postSize).start();
				playingFlag = true;
				int sMax = seekbar.getMax();
				int mMax = mediaPlayer.getDuration();
				seekbar.setProgress(postSize * sMax / mMax);
				postSize = 0;
				
				hideProgressBar();
			}
			else {
				new PlayMovie(0).start();   //表明是第一次开始播放
			}
		}
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) { //activity调用过pase方法，保存当前播放位置
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				postSize = mediaPlayer.getCurrentPosition();
				mediaPlayer.stop();
				playingFlag = false;
				
				showProgressBar();
			}
		}
	}

	private void setListener() {
		mediaPlayer
				.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
					@Override
					public void onBufferingUpdate(MediaPlayer mp, int percent) {
						Log.i(TAG, "buffering update :"+percent);
						tvBufferSize.setVisibility(View.VISIBLE);
						tvBufferSize.setText(percent+"%");
					}
				});

		mediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { //视频播放完成
					@Override
					public void onCompletion(MediaPlayer mp) {
						playingFlag = false;
						finishPlaying=true;
						playCtrlBtn.setBackgroundResource(R.drawable.movie_play_bt);
					}
				});

		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {

			}
		});
		//播放缓冲监听
		mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
			
			@Override
			public boolean onInfo(MediaPlayer arg0, int what, int extra) {
				// TODO Auto-generated method stub
				switch(what){
				case MediaPlayer.MEDIA_INFO_BUFFERING_START:
					//progressBar.setVisibility(View.VISIBLE);  //准备完成后，隐藏控件
					//tvBufferSize.setVisibility(View.VISIBLE);
					Log.i(TAG, "buffering start");
					//tvBufferSize.setText(percent+"%");
					break;
				case MediaPlayer.MEDIA_INFO_BUFFERING_END:
					//progressBar.setVisibility(View.GONE);  //准备完成后，隐藏控件
					//tvBufferSize.setVisibility(View.GONE);
					Log.i(TAG, "buffering end");
					break;
				
				}
				return true;
			}
		});
		
		mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				Log.i(TAG, "media player error:"+what);
				return false;
			}
		});
/**
 * 如果视频在播放，则调用mediaPlayer.pause();，停止播放视频，反之，mediaPlayer.start()  ，同时换按钮背景
 */
		playCtrlBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mediaPlayer.isPlaying()) {    
					playCtrlBtn.setBackgroundResource(R.drawable.movie_play_bt);
					mediaPlayer.pause();
					postSize = mediaPlayer.getCurrentPosition();
				} else {
					if (playingFlag == false) {
						playingFlag = true;
						new Thread(updateProgress).start();
					}
					mediaPlayer.start();
					playCtrlBtn.setBackgroundResource(R.drawable.movie_stop_bt);

				}
				
				controlBarInactive=0;
			}
		});
		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				int value = seekbar.getProgress() * mediaPlayer.getDuration()  //计算进度条需要前进的位置数据大小
						/ seekbar.getMax();
				if(finishPlaying&& mediaPlayer!=null){
					
					new PlayMovie(value).start();
					showProgressBar();
					playCtrlBtn.setBackgroundResource(R.drawable.movie_stop_bt);
					playCtrlBtn.setEnabled(true); 
					hideControlBar();
					playingFlag = true;

				}else{
					mediaPlayer.seekTo(value);
				}
				
				controlBarInactive=0;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				controlBarInactive=0;
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(fromUser){
					Log.i(TAG, "seekbar to progress"+progress);
				}

			}
		});
/**
 * 点击屏幕，切换控件的显示，显示则应藏，隐藏，则显示
 */
		pView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (displayControlBar) {
					hideControlBar();
					Log.i(TAG,"controlbar should gone");
				} else {
					pView.setVisibility(View.VISIBLE);
					Log.i(TAG,"controlbar should be visible");
					showControlBar();
				}

				controlBarInactive=0;

			}
		});
//		backButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (mediaPlayer.isPlaying()) {
//					mediaPlayer.stop();
//					mediaPlayer.release();
//				}
//				mediaPlayer = null;
//				PixBoxMediaPlayerActivity.this.finish();
//
//			}
//		});

	}
/**
 * 更新进度条
 */
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			if(displayControlBar ){//检查controlbar是否隐藏，如果为不活动状态则自动隐藏
				if(controlBarInactive >=4){
					hideControlBar();
					Log.i(TAG, "controlbar hided automatically");
				}else{
					controlBarInactive++;
				}
			}
			
			//获取系统时间
			Time t=new Time();
			t.setToNow();
			int hour = t.hour; // 0-23  
			int minute = t.minute;  
			int second = t.second;  
			tvPlayerCurTime.setText(hour+":"+minute+":"+second);
			
			//更新进度条
			if (mediaPlayer == null) {
				playingFlag = false;
			} else if (mediaPlayer.isPlaying()) {
				playingFlag = true;
				if(mediaType==MEDIA_TYPE_VOD){
					int position = mediaPlayer.getCurrentPosition();
					int mMax = mediaPlayer.getDuration();
					if(mMax==0){
						Log.e(TAG, "mediaplayer getDuration "+mMax);
						return;
					}
					int sMax = seekbar.getMax();
					seekbar.setProgress(position * sMax / mMax);
				}
				
			} else {
				return;
			}
		};
	};

	//循环更新进度条
	class upDateSeekBar implements Runnable {

		@Override
		public void run() {
			mHandler.sendMessage(Message.obtain());
			if (playingFlag) {
				mHandler.postDelayed(updateProgress, 1000);
			}	
		}
	}

	@Override
	protected void onDestroy() {   //activity销毁后，释放资源
		super.onDestroy();
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		System.gc();
	}
	
	
}