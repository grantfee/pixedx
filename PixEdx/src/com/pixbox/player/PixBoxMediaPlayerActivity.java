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

	private Button playCtrlBtn;  //���ڿ�ʼ����ͣ�İ�ť
	private SurfaceView pView;   //��ͼ�����������ڰ���Ƶ��ʾ����Ļ��
	private String jsonStr;   
	private MediaProfileBean mpb;
	private String url="";//��Ƶ���ŵ�ַ
	private MediaPlayer mediaPlayer;    //�������ؼ�
	private int postSize;    //�����Ѳ���Ƶ��С
	private SeekBar seekbar;   //�������ؼ�
	private boolean playingFlag = true;   //�����ж���Ƶ�Ƿ��ڲ�����
	private boolean finishPlaying=false;
	private LinearLayout controlBarContainer;
	private RelativeLayout titleBarContainer;   
	private boolean displayControlBar;   //���ڱ�־��ǰcontrolbar�Ƿ�������ʾ
	private int controlBarInactive=0;//����ָʾcontrollbar�������
	private Button backButton;   //���ذ�ť
	private View progressBar;   //ProgressBar 
	private TextView tvBufferSize;
	private upDateSeekBar updateProgress;   //���½�������
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
				WindowManager.LayoutParams.FLAG_FULLSCREEN);   //ȫ��
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Ӧ������ʱ��������Ļ������������
		
		setContentView(R.layout.pixbox_player_layout);   //���ز����ļ�
		
		//��ȡ��һ������
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		jsonStr=bundle.getString("MediaPlayUrl");
		if(jsonStr==null){
			Toast.makeText(getApplicationContext(), "����:��ȡԪý������ʧ��", Toast.LENGTH_LONG).show();
		}
		Gson gson =new Gson();
		mpb = gson.fromJson(jsonStr, MediaProfileBean.class);
		if(mpb!=null){
			url = mpb.getPlay_url();
		}
		
		Log.i(TAG, "get bundle data play url:"+url);
				
		initView();  //��ʼ������
		setListener();   //������¼�
	}
	
	private void initView() {
		mediaPlayer = new MediaPlayer();   //����һ������������
		updateProgress = new upDateSeekBar();  //�������½���������
		
		//backButton = (Button) findViewById(R.id.back);  //���ذ�ť
		seekbar = (SeekBar) findViewById(R.id.seekbar);  //������
		playCtrlBtn = (Button) findViewById(R.id.play);
		playCtrlBtn.setEnabled(false); //�ս����������䲻�ɵ��
		pView = (SurfaceView) findViewById(R.id.mSurfaceView);
		pView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);   //������
		pView.getHolder().setKeepScreenOn(true);   //������Ļ����
		pView.getHolder().addCallback(new surFaceView());   //���ü����¼�
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

	class PlayMovie extends Thread {   //������Ƶ���߳�

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
				mediaPlayer.reset();    //�ظ�������Ĭ��
				
				mediaPlayer.setDataSource(url);   //���ò���·��
				mediaPlayer.setDisplay(pView.getHolder());  //����Ƶ��ʾ��SurfaceView��
				mediaPlayer.setOnPreparedListener(new PreparedOkListener(post));  //���ü����¼�
				mediaPlayer.setOnInfoListener(new infoListener());
				mediaPlayer.prepare();  //׼������
			} catch (Exception e) {
				message.what = 2;
				Log.e(TAG, e.toString());
			}

			super.run();
		}
	}
	
	//mediaplayer�¼�����
	class infoListener implements OnInfoListener{

		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			// TODO Auto-generated method stub
			switch(what){
			case MediaPlayer.MEDIA_INFO_BUFFERING_START:
				//progressBar.setVisibility(View.VISIBLE);  //׼����ɺ����ؿؼ�
				//tvBufferSize.setVisibility(View.VISIBLE);
				Log.i(TAG, "buffering start");
				//tvBufferSize.setText(percent+"%");
				break;
			case MediaPlayer.MEDIA_INFO_BUFFERING_END:
				//progressBar.setVisibility(View.GONE);  //׼����ɺ����ؿؼ�
				//tvBufferSize.setVisibility(View.GONE);
				Log.i(TAG, "buffering end");
				break;
			
			}
			return true;
		}
		
	}
	
	public void fixVideoSizeOnScreen()
	{
		  // ���ñ����С��ƥ����Ƶ����ʾ����С��ȡ�����Ǹ���С��С 
	   int  mVideoWidth = mediaPlayer.getVideoWidth(); 
	   int  mVideoHeight = mediaPlayer.getVideoHeight(); 
	   
	   if(mVideoWidth==0||mVideoWidth==0){
		   Log.i(TAG, "video width:" + mVideoWidth+",videoHeight:"+mVideoWidth);
		   return;
	   }
	     
	    int screenWidth = getWindowManager().getDefaultDisplay().getWidth(); 
	    int screenHeight = getWindowManager().getDefaultDisplay().getHeight(); 

	    if (mVideoWidth > screenWidth 
	        || mVideoHeight > screenHeight) {//�����Ƶ��ȴ�����ʾ����С���õ����� 
	        float heightRatio = (float)mVideoHeight / (float)screenHeight; 
	        float widthRatio = (float)mVideoWidth / (float)screenWidth; 
	         
	        if (heightRatio > 1 || widthRatio >1) {//ֻ�����һ�����򳬳����� 
	            
	         if (heightRatio > widthRatio) {//�Ը�Ϊ׼ 
	            mVideoHeight = (int) Math.ceil((float)mVideoHeight / (float)heightRatio); 
	            mVideoWidth = (int) Math.ceil((float)mVideoWidth / (float)heightRatio);    
	         } else {//�Կ�Ϊ׼ 
	            mVideoHeight = (int) Math.ceil((float)mVideoHeight / (float)widthRatio); 
	            mVideoWidth = (int) Math.ceil((float)mVideoWidth / (float)widthRatio); 
	         } 
	            
	        } 
	        
	    }else{//��Ƶ����ĻС����Ŵ�
	    	 	float heightRatio =  (float)screenHeight/(float)mVideoHeight; 
		        float widthRatio =   (float)screenWidth/(float)mVideoWidth; 
		        if(heightRatio > widthRatio){//�Կ����Ϊ׼����
		        	 mVideoHeight = (int) Math.ceil((float)mVideoHeight * (float)widthRatio); 
			         mVideoWidth = (int) Math.ceil((float)mVideoWidth * (float)widthRatio); 
		        }else{
		        	 mVideoHeight = (int) Math.ceil((float)mVideoHeight * (float)heightRatio); 
			         mVideoWidth = (int) Math.ceil((float)mVideoWidth * (float)heightRatio); 
		        }
		        
	    }
	    Log.i(TAG, "after fixed video width:" + mVideoWidth+",videoHeight:"+mVideoHeight);
	    //������ʾ��С---- 
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

			//��Ƶ�����豸��Ļ��С
			fixVideoSizeOnScreen();
			
			int duration = mediaPlayer.getDuration();
			Log.i(TAG, "get duration" + mediaPlayer.getDuration());
			if(duration <= 0){//ȷ��ý������
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
				mediaPlayer.start();  //��ʼ������Ƶ
			} else {
				return;
			}
			if (postSize > 0) {  //˵����;ֹͣ����activity���ù�pase�����������û����ֹͣ��ť��������ֹͣʱ��λ�ÿ�ʼ����
				Log.i(TAG, "seekTo ");
				mediaPlayer.seekTo(postSize);   //����postSize��Сλ�ô����в���
			}
			new Thread(updateProgress).start();   //�����̣߳����½�����
			
		}
	}

	private class surFaceView implements Callback {     //����󶨵ļ������¼�

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {   //������ɺ����
			if (postSize > 0 && url!= null) {    //˵����ֹͣ��activity���ù�pase����������ֹͣλ�ò���
				new PlayMovie(postSize).start();
				playingFlag = true;
				int sMax = seekbar.getMax();
				int mMax = mediaPlayer.getDuration();
				seekbar.setProgress(postSize * sMax / mMax);
				postSize = 0;
				
				hideProgressBar();
			}
			else {
				new PlayMovie(0).start();   //�����ǵ�һ�ο�ʼ����
			}
		}
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) { //activity���ù�pase���������浱ǰ����λ��
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
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { //��Ƶ�������
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
		//���Ż������
		mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
			
			@Override
			public boolean onInfo(MediaPlayer arg0, int what, int extra) {
				// TODO Auto-generated method stub
				switch(what){
				case MediaPlayer.MEDIA_INFO_BUFFERING_START:
					//progressBar.setVisibility(View.VISIBLE);  //׼����ɺ����ؿؼ�
					//tvBufferSize.setVisibility(View.VISIBLE);
					Log.i(TAG, "buffering start");
					//tvBufferSize.setText(percent+"%");
					break;
				case MediaPlayer.MEDIA_INFO_BUFFERING_END:
					//progressBar.setVisibility(View.GONE);  //׼����ɺ����ؿؼ�
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
 * �����Ƶ�ڲ��ţ������mediaPlayer.pause();��ֹͣ������Ƶ����֮��mediaPlayer.start()  ��ͬʱ����ť����
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

				int value = seekbar.getProgress() * mediaPlayer.getDuration()  //�����������Ҫǰ����λ�����ݴ�С
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
 * �����Ļ���л��ؼ�����ʾ����ʾ��Ӧ�أ����أ�����ʾ
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
 * ���½�����
 */
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			if(displayControlBar ){//���controlbar�Ƿ����أ����Ϊ���״̬���Զ�����
				if(controlBarInactive >=4){
					hideControlBar();
					Log.i(TAG, "controlbar hided automatically");
				}else{
					controlBarInactive++;
				}
			}
			
			//��ȡϵͳʱ��
			Time t=new Time();
			t.setToNow();
			int hour = t.hour; // 0-23  
			int minute = t.minute;  
			int second = t.second;  
			tvPlayerCurTime.setText(hour+":"+minute+":"+second);
			
			//���½�����
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

	//ѭ�����½�����
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
	protected void onDestroy() {   //activity���ٺ��ͷ���Դ
		super.onDestroy();
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		System.gc();
	}
	
	
}