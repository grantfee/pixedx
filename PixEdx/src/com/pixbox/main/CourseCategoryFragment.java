package com.pixbox.main;




import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.NetworkImageView;

import com.google.gson.Gson;
import com.pixbox.beans.CategoryBean;
import com.pixbox.beans.TopCategoryBean;
import com.pixbox.utils.GlobalVolleyApplication;
import com.pixbox.utils.ScaleAnimEffect;

import com.videoexpress.pixedx.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CourseCategoryFragment extends Fragment{

	private final  String TAG=this.getClass().getSimpleName();
	private String topCategoryJsonUrl = "http://54.201.52.201/categories?category_id=1";//取顶级分类列表api
	
	//异步请求队列
    private RequestQueue mQueue;  
    private ImageLoader mImageLoader;
	private ImageListener imageListener;
	private static int retryCnt=0;//网络请求重试次数
	private final static int MAX_RETRY_TIMES=3;//重试上限
	
	//json解析后存储对象
    private TopCategoryBean tcb=null;
    private CategoryBean[] cbs=null;
    
	private Activity context;
	private static int CATEGORY_ITEM_SIZE=9;
	GridLayout gridlayout;
	private ImageView[] ivs = new ImageView[CATEGORY_ITEM_SIZE];
	private TextView[] tvs = new TextView[CATEGORY_ITEM_SIZE];
	private ImageView[] whiteBorder = new ImageView[CATEGORY_ITEM_SIZE];

	Context mContext;
	Dialog dialog; 
	
	private ScaleAnimEffect animEffect;
	private ProgressDialog progressBar;
	private HorizontalScrollView hsv;
	
	private static final int imageViewIdArray[]={R.id.imageViewCat0,R.id.imageViewCat1,R.id.imageViewCat2,
								R.id.imageViewCat3,R.id.imageViewCat4,R.id.imageViewCat5,
									R.id.imageViewCat6,R.id.imageViewCat7,R.id.imageViewCat8};
	private static final int textViewIdArray[]={R.id.textViewCat0,R.id.textViewCat1,R.id.textViewCat2,R.id.textViewCat3,
							R.id.textViewCat4,R.id.textViewCat5,R.id.textViewCat6,
								R.id.textViewCat7,R.id.textViewCat8};
	private final int whiteBorderIdArray[]={R.id.white_boder0,R.id.white_boder1,R.id.white_boder2,R.id.white_boder3,
			R.id.white_boder4,R.id.white_boder5,R.id.white_boder6,R.id.white_boder7,R.id.white_boder8};
	
	//数据下载标志
	private boolean bDownloadFinish=false;
	
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		Log.i("Fragment","onAttach");
		//创建volley异步请求队列
		mQueue = ((GlobalVolleyApplication)getActivity().getApplicationContext()).getQueue();
		mImageLoader=((GlobalVolleyApplication)getActivity().getApplicationContext()).getImageLoader();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(TAG, "onResume");

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i(TAG, "onPause");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		Log.i(TAG,"onActivityCreated");
	
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		int i;
		View v = inflater.inflate(R.layout.fragment_category, container, false);
		
		animEffect = new ScaleAnimEffect();
		gridlayout = (GridLayout)v.findViewById(R.id.fragment_category_gridlayout);
		for(i=0;i<CATEGORY_ITEM_SIZE;i++){
			ivs[i] = (ImageView) v.findViewById(imageViewIdArray[i]);
			whiteBorder[i] = (ImageView)v.findViewById(whiteBorderIdArray[i]);
			tvs[i] = (TextView) v.findViewById(textViewIdArray[i]);
			
		}
		ivs[0].setFocusable(true);
		ivs[0].requestFocus();
		
		Log.i(TAG,"onCreateView");
		if(tcb == null){//触发网络数据请求
			requestJsonObject();
		}else{
			cbs = tcb.getCategories();
			updateCategoryUI(cbs);
		}

		return v;
	}

	/*某分类图片被按下事件处理*/
	private class CategoryImageClick implements OnClickListener{
		CategoryBean cb;
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			//如果数据下载未完成则不响应
			if(!bDownloadFinish)
				return;
			cb = (CategoryBean)v.getTag();
			if(cb!=null){
				if(cb.isBe_edge()){
					startCoursewareContentActivity(cb);
				}else{
					startCoursewareListActivity(cb);
				}
			}
		}
	}
	
	//启动某分类下节目列表页面
	private void startCoursewareContentActivity(CategoryBean cb)
	{
			Intent intent;
			Bundle bundle;
			bundle = new Bundle();
			
			String json;
			Gson gson;
			
			
			gson = new Gson();
			json = gson.toJson(cb);
			Log.i(TAG, "switch to CatMediaListActivity,bundle:"+json);
			bundle.putString("CategoryBean", json);
			
			intent = new Intent();
			intent.setClass(getActivity(), CoursewareContentActivity.class);
			intent.putExtras(bundle);
			
			startActivity(intent);
	}
		
	private void startCoursewareListActivity(CategoryBean cb)
		{
			Intent intent;
			Bundle bundle;
			bundle = new Bundle();
			
			String json;
			Gson gson;
			
			
			gson = new Gson();
			json = gson.toJson(cb);
			Log.i(TAG, "switch to CatMediaListActivity,bundle:"+json);
			bundle.putString("CategoryBean", json);
			
			intent = new Intent();
			intent.setClass(getActivity(), CoursewareListActivity.class);
			intent.putExtras(bundle);
			
			startActivity(intent);
		}
		
	/*图片焦点发生变化,更新获得焦点的图片动画*/
	private class ImageFocusChange implements OnFocusChangeListener{
		ImageView iv;
		ImageView boarder;
		public ImageFocusChange(ImageView iv,ImageView boarder) {
			super();
			this.iv = iv;
			this.boarder = boarder;
			
		}

		@Override
		public void onFocusChange(View arg0, boolean bFocus) {
			// TODO Auto-generated method stub
			if(!bFocus){
				showLooseFocusAinimation(iv,boarder);
				Log.i("onFocusChange", "ImageView loose focus.index:");
			}else{
				showOnFocusAnimation(iv,boarder);
				Log.i("onFocusChange", "ImageView get focus.index:");
			}
		}
		
	}
	/**
	 * 失去焦点的的动画动作
	 * 
	 * @param paramInt
	 *                失去焦点的item
	 * */
	private void showLooseFocusAinimation(ImageView iv,ImageView border) {
		//this.animEffect.setAttributs(1.105F, 1.0F, 1.105F, 1.0F, 100L);
		this.animEffect.setAttributs(1.2F, 1.0F, 1.2F, 1.0F, 100L);
		iv.startAnimation(animEffect.createAnimation());
		//shadowIv.setVisibility(View.GONE);
		border.setVisibility(View.GONE);
	}

	/**
	 * 获得焦点的item的动画动作
	 * 
	 * @param paramInt
	 *                获得焦点的item
	 * */
	private void showOnFocusAnimation(ImageView iv,ImageView border) {
		//framLayout.bringToFront();
		final ImageView whiteborder = border;
		final ImageView image = iv;
		animEffect.setAttributs(1.0F, 1.2F, 1.0F, 1.2F, 100L);
		Animation localAnimation = animEffect.createAnimation();
		localAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				//shadowIv.startAnimation(animEffect.alphaAnimation(0.0F, 1.0F, 150L, 0L));
				//shadowIv.setVisibility(View.VISIBLE);
				whiteborder.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}
		});
		image.startAnimation(localAnimation);
	}
	
	//根据IP和端口构造json URL
	private void buildJsonUrl()
	{
		String IP;
		String port;
		IP = ((GlobalVolleyApplication)getActivity().getApplicationContext()).getServerIp();
		port = ((GlobalVolleyApplication)getActivity().getApplicationContext()).getServerPort();	
		Log.i(TAG, " buildJsonUrl() Ip:"+IP+" port "+port);
		
		if(port.equals("80")){
			topCategoryJsonUrl = "http://"+IP+"/categories?category_id=1";	
		}else{
			topCategoryJsonUrl = "http://"+IP+":"+port+"/categories?category_id=1";	
		}
		Log.i(TAG, " buildJsonUrl() url:"+topCategoryJsonUrl);
	}
	
	/*
	 * 根据网络数据更新ui
	 * ***/
	public void updateCategoryUI(CategoryBean[] cbs)
	{
		 int i;

		 if(cbs==null){
			 Log.e(TAG,"category beans is null");
			 return;
		 }
			 
		 int original_size=cbs.length <=CATEGORY_ITEM_SIZE?cbs.length:CATEGORY_ITEM_SIZE;
         int dynimic_size = cbs.length >CATEGORY_ITEM_SIZE?(cbs.length-CATEGORY_ITEM_SIZE):0;
         Log.i(TAG,"category original size:"+original_size+" dynimic size:"+dynimic_size);
         
         //设置9个固定分类项
         for(i=0;i<original_size;i++){
				tvs[i].setText(cbs[i].getCategory_name());
				ivs[i].setTag(cbs[i]);
				ivs[i].setOnClickListener(new CategoryImageClick());
				ivs[i].setOnFocusChangeListener(new ImageFocusChange(ivs[i],whiteBorder[i]));
				
				Log.i(TAG,"image url:"+cbs[i].getThumbs().getS().getUrl());
				Log.i(TAG,"category name:"+cbs[i].getCategory_name());
				imageListener = ImageLoader.getImageListener(ivs[i], -1, -1);
				mImageLoader.get(cbs[i].getThumbs().getS().getUrl(), imageListener);
				
			}
        //动态增加分类项
         for(i =0;i<dynimic_size;i++){
			 View framelayout = LayoutInflater.from(getActivity()).inflate(R.layout.category_framelayout_item, null, false);
			 TextView tv = (TextView)framelayout.findViewById(R.id.textViewCat);
			 tv.setTag(cbs[i]);
			 
			 ImageView iv= (ImageView)framelayout.findViewById(R.id.imageViewCat);
			 ImageView border = (ImageView)framelayout.findViewById(R.id.white_boder);
			 iv.setOnClickListener(new CategoryImageClick());
			 iv.setOnFocusChangeListener(new ImageFocusChange(iv,border));
			 imageListener = ImageLoader.getImageListener(iv, R.drawable.program_loading_bg, R.drawable.program_loading_bg);
			 mImageLoader.get(cbs[i].getThumbs().getS().getUrl(), imageListener);
			 gridlayout.addView(framelayout);
		 }

	}
	//向服务器发起请求
	public void requestJsonObject() {
        Log.i(TAG, "requestJsonObject()");
        buildJsonUrl();
        //取消所有当前请求
        mQueue.cancelAll("get_category");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Method.GET, topCategoryJsonUrl,null, new ResponseListener(), new ErrorListener());// 也可以在这里携带需要上传的数据
        
        jsonObjectRequest.setTag("get_category");
        mQueue.add(jsonObjectRequest);// 添加请求到队列里 
        //设置滚动条
        progressBar = ProgressDialog.show(getActivity(), "", "加载数据...",true);
    }
	
	//处理服务器响应
	private class ResponseListener implements Response.Listener<JSONObject>{
		@Override
		public void onResponse(JSONObject response) {
			 Log.i(TAG, "onResponse(JSONObject)");
             Gson gson = new Gson();
             String json = response.toString();
             Log.i(TAG,"Category Json Result:"+json);
             tcb = gson.fromJson(json,TopCategoryBean.class );
             cbs = tcb.getCategories();
             updateCategoryUI(cbs);
 			 progressBar.dismiss();
 			 bDownloadFinish=true;
		}
		
	}
	
	//服务器响应错误
	private class ErrorListener implements Response.ErrorListener{
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.i(TAG, "onErrorResponse:"+error.getMessage());
			progressBar.dismiss();
			tcb=null;
			
			if(retryCnt >= MAX_RETRY_TIMES){
				Toast.makeText(getActivity().getApplicationContext(), 
	                    "重试3次失败!请检查网络或更改设置.", Toast.LENGTH_SHORT).show();
				return;
			}
			Toast.makeText(getActivity().getApplicationContext(), 
                    "加载数据失败!正在重试...", Toast.LENGTH_SHORT).show();
			//重试
			requestJsonObject();
			retryCnt++;

		}
	}
}
