package com.pixbox.main;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.google.gson.Gson;
import com.pixbox.beans.CategoryBean;
import com.pixbox.beans.CategoryMediaBean;
import com.pixbox.beans.MediaProfileBean;
import com.pixbox.beans.TopCategoryBean;

import com.pixbox.utils.GlobalVolleyApplication;
import com.videoexpress.pixedx.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//取得课件分类列表
public class CoursewareListActivity extends Activity {
	
	private final  String TAG= this.getClass().getSimpleName();
	Bundle bundle;
	Intent intent;
	CategoryBean categoryBeanObj;
	
	LinearLayout mLinearLayout;
	int screenWidth;// 屏幕宽度
	int screenHeight;// 屏幕高度
	LinearLayout videofilterl;
	ListView videoTpyelist = null;
	int      videotype = 1;
	GridView videGridlist = null;
	ImageView titleImage = null;
	TextView titleName =null;
	ImageView upImageV = null;
	ImageView downImageV = null;
	TextView tvMediaCnt=null;
	ProgressDialog progressBar=null;
	String[] itemlist = { "搜    索", "全    部", "筛    选", "最新更新" };
	
    //异步请求队列
    private RequestQueue mQueue;  
    private ImageLoader mImageLoader;
	private ImageListener imageListener;
	private static int retryCnt=0;//网络请求重试次数
	private final static int MAX_RETRY_TIMES=3;//重试上限
	//json解析后存储对象
	private TopCategoryBean tcb=null;
	private CategoryBean[] cbs=null;
	private CategoryBean[] categoryBeanArr;
	List<CategoryBean> categoryBeanList;
	
	CoursewareListAdapter videoListAdapter;
	MyVideoTypeAdapter videoTypeAdapter;
	
	//数据下载url
	private String CategoryJsonUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.courseware_list_activity_layout);
		
		Log.i(TAG, "get bundle data");
		//获取上一级数据
		intent = this.getIntent();
		bundle = intent.getExtras();
		String categoryBean=bundle.getString("CategoryBean");
		Gson gson =new Gson();
		categoryBeanObj = gson.fromJson(categoryBean, CategoryBean.class);

		//获取volley对象，请求json数据
		mQueue = ((GlobalVolleyApplication)this.getApplicationContext()).getQueue();
		mImageLoader=((GlobalVolleyApplication)this.getApplicationContext()).getImageLoader();
		requestJsonObject();
		
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
		mLinearLayout = (LinearLayout) findViewById(R.id.leftL);
		mLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
				screenWidth / 6, screenHeight));
		videoTpyelist = (ListView) findViewById(R.id.listView1);
		
		//titleImage =(ImageView)findViewById(R.id.imageViewType);
		titleName = (TextView)findViewById(R.id.textViewType);
		upImageV = (ImageView) findViewById(R.id.imageViewUp);
		downImageV = (ImageView) findViewById(R.id.imageViewDown);
		videGridlist = (GridView) findViewById(R.id.gridView1);
		videofilterl = (LinearLayout)findViewById(R.id.videofilter);
		videofilterl.setVisibility(View.INVISIBLE);
		tvMediaCnt=(TextView) findViewById(R.id.textViewMediaCnt);
		
		titleName.setText(categoryBeanObj.getCategory_name());
		//初始化节目分类表
		videoTypeAdapter = new MyVideoTypeAdapter(itemlist,
				this);
		videoTpyelist.setAdapter(videoTypeAdapter);
		videoTpyelist.setDivider(null);
		
		//初始化节目列表为空
		categoryBeanList = new ArrayList<CategoryBean>();
		videoListAdapter =  new CoursewareListAdapter(categoryBeanList, this);
		videGridlist.setAdapter(videoListAdapter);
		videGridlist.setNumColumns(4);

		//
		
		////////////////////设置滚动事件////////////////////////
		videoTpyelist.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch (scrollState) {
				// 当不滚动时
				case OnScrollListener.SCROLL_STATE_IDLE:
					// 判断滚动到底部
					if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
						upImageV.setVisibility(View.VISIBLE);
						downImageV.setVisibility(View.INVISIBLE);
					}
					// 判断滚动到顶部
					if (view.getFirstVisiblePosition() == 0) {
						downImageV.setVisibility(View.VISIBLE);
						upImageV.setVisibility(View.INVISIBLE);
					}
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if (firstVisibleItem + visibleItemCount == totalItemCount) {
					// 底部
					upImageV.setVisibility(View.VISIBLE);
					downImageV.setVisibility(View.INVISIBLE);
				} else {
					downImageV.setVisibility(View.VISIBLE);
					upImageV.setVisibility(View.INVISIBLE);
				}
			}
		});
		
		//////////////////////设置节目分类点击事件////////////////////////
		videoTpyelist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				videoTypeAdapter.setSelectedPosition(position);
				videoTypeAdapter.notifyDataSetInvalidated();
				Log.i("setOnItemClickListener", "+++ postion" + position);
				videotype = position;
				if(position==3)
				{
					//loadLastRefreshData();
					//videoListAdapter.notifyDataSetInvalidated();
				}else if(position==1)
				{
					//videoBeansList.clear();
					//loadData();
					//videoListAdapter.notifyDataSetInvalidated();
				}
			}

		});
		
		
		
		/////////////////////////设置节目列表项点击事件///////////////////////
		videGridlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				 Log.i(TAG, "switch to detail activity,item pos "+position);
				 GridView gridView = (GridView)parent;
				 CategoryBean cb = (CategoryBean)gridView.getItemAtPosition(position);
				 if(cb.isBe_edge())
				 {
					 startCoursewareContentActivity(cb);
				 }
				 else{
					 startCoursewareListActivity(cb);
				 }
			

			}
		});
		
		videGridlist.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if (firstVisibleItem + visibleItemCount == totalItemCount) {
					// 底部
					if(videotype==1 && totalItemCount < 100){
						//loadData();
						//videoListAdapter.notifyDataSetInvalidated();
					}
				} 
			}
		});
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
	intent.setClass(this, CoursewareContentActivity.class);
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
	intent.setClass(this, CoursewareListActivity.class);
	intent.putExtras(bundle);
	
	startActivity(intent);
}
	
//根据IP和端口构造json URL
	private void buildJsonUrl()
	{
		String IP;
		String port;
		
		IP = ((GlobalVolleyApplication)this.getApplicationContext()).getServerIp();
		port = ((GlobalVolleyApplication)this.getApplicationContext()).getServerPort();
		
		Log.i(TAG, " buildJsonUrl() Ip:"+IP+" port "+port);
		
		if(port.equals("80")){
			CategoryJsonUrl = "http://"+IP+"/categories?category_id="+categoryBeanObj.getCategory_id();
			
		}else{
			CategoryJsonUrl = "http://"+IP+":"+port+"/categories?category_id="+categoryBeanObj.getCategory_id();
			
		}
		
		Log.i(TAG, " buildJsonUrl() url:"+CategoryJsonUrl);
	}
////////////////////////从网络下载数据///////////////////////////////////////
	
	private void requestJsonObject() {
        Log.i(TAG, "requestJsonObject()");
        
        buildJsonUrl();
        mQueue.cancelAll("get_courseware_list");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Method.GET, CategoryJsonUrl,null, new ResponseListener(), new ErrorListener());// 也可以在这里携带需要上传的数据
        Log.i(TAG, "Get courseware from Url:"+CategoryJsonUrl);
        jsonObjectRequest.setTag("get_courseware_list");
        mQueue.add(jsonObjectRequest);// 添加请求到队列里
        //设置滚动条
      	progressBar = ProgressDialog.show(CoursewareListActivity.this, "", "加载网络数据...",true);
    }
	
	private class ResponseListener implements Response.Listener<JSONObject>{
		@Override
		public void onResponse(JSONObject response) {
			 Log.i(TAG, "onResponse(JSONObject)");
		
             Gson gson = new Gson();
             String json = response.toString();
             Log.i(TAG,"Category Json Result:"+json);
           
             tcb = gson.fromJson(json,TopCategoryBean.class );
           
             cbs = tcb.getCategories();
             categoryBeanArr = cbs;
             Log.i(TAG, "videobean array size:"+categoryBeanArr.length);
             categoryBeanList = Arrays.asList(categoryBeanArr);
             
             Log.i(TAG, "videobean size:"+categoryBeanList.size());
            
             videoListAdapter.refresh(categoryBeanList);
             tvMediaCnt.setText("总共("+cbs.length+"部)");
 			 progressBar.dismiss();
		}
		
	}
	
	private class ErrorListener implements Response.ErrorListener{
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.i(TAG, "onErrorResponse:"+error.getMessage());
			progressBar.dismiss();
			
			if(retryCnt >= MAX_RETRY_TIMES){
				Toast.makeText(getApplicationContext(), 
	                    "重试3次失败!请检查网络或更改设置.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			Toast.makeText(getApplicationContext(), 
                    "加载数据失败，正在重试...", Toast.LENGTH_LONG).show();
			tcb=null;
			Log.i(TAG, "Get category medias failed.Try again from url:"+CategoryJsonUrl);
			
			requestJsonObject();
			retryCnt++;
		}
	}
	
	private class MyVideoTypeAdapter extends BaseAdapter {
		private List<String> courseString;
		private Context context;
		private int selectedPosition = 1;// 选中的位置

		public MyVideoTypeAdapter(String[] courseStrlist, Context context) {
			super();
			this.courseString = Arrays.asList(courseStrlist);
			this.context = context;
		}

		@Override
		public int getCount() {
			return courseString.size();
		}

		@Override
		public Object getItem(int arg0) {
			return courseString.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater mInflater = (LayoutInflater) ((Activity) context)
					.getLayoutInflater();
			final int index = position;
			TextView tv = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.courseware_list_type_item,
						null);
				tv = (TextView) convertView.findViewById(R.id.ItemVideo);
				// courseBeans[position].textView = tv;
				convertView.setTag(tv);
			} else {
				tv = (TextView) convertView.getTag();
			}
			tv.setText(courseString.get(position));
			// convertView.setPadding(0, 15, 0, 15);
			// Log.i("getView", "+++ postion" + selectedPosition);
			if (selectedPosition == index) {
				tv.setSelected(true);
				tv.setPressed(true);
			} else {
				tv.setSelected(false);
				tv.setPressed(false);
			}

			return convertView;
		}
	}

	private class CoursewareListAdapter extends BaseAdapter {
		private List<CategoryBean> beans;
		private Context context;

		public CoursewareListAdapter(List<CategoryBean> videobeans, Context context) {
			super();
			this.beans = videobeans;
			this.context = context;
		}

		@Override
		public int getCount() {
			return beans.size();
		}

		@Override
		public Object getItem(int arg0) {
			return beans.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		public void refresh(List<CategoryBean> categoryBeanList) {
			beans = categoryBeanList;
			notifyDataSetChanged();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater mInflater = (LayoutInflater) ((Activity) context)
					.getLayoutInflater();
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.courseware_list_grid_item,
						null);
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.videotvbg);
				holder.textView = (TextView) convertView
						.findViewById(R.id.videotvName);
				
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			Log.i("getView", "+++ postion" + position);
			if(beans!=null){
				imageListener = ImageLoader.getImageListener(holder.imageView, R.drawable.program_loading_bg, R.drawable.program_loading_bg);
				mImageLoader.get(beans.get(position).getThumbs().getBig().getUrl(), imageListener);
				holder.textView.setText(beans.get(position).getCategory_name());
				holder.imageView.setTag(beans.get(position));
				
				Log.i("getView switch to detail", "imageurl:" + beans.get(position).getThumbs().getS().getUrl());
				Log.i("getView", "category name:" + beans.get(position).getCategory_name());
				
//				holder.imageView.setOnClickListener(new OnClickListener(){
//
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						Log.i("getView", "item on click");
//						if(v.getTag()!=null);
//							startMediaDetailActivity((MediaProfileBean)v.getTag());
//					}
//					
//				});
			}else{
				//do nothing
			}
			
			
			convertView.setPadding(15, 5, 15, 5);
			return convertView;
		}

		public class ViewHolder {
			public ImageView imageView;
			public TextView textView;
		}
	}
}
