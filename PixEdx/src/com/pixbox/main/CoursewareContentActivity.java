package com.pixbox.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.google.gson.Gson;

import com.pixbox.adapter.CoursewareContentListAdapter;
import com.pixbox.beans.CategoryBean;
import com.pixbox.beans.CategoryMediaBean;
import com.pixbox.beans.MediaProfileBean;

import com.pixbox.utils.GlobalVolleyApplication;
import com.videoexpress.pixedx.R;

public class CoursewareContentActivity extends Activity {
	private final String TAG=this.getClass().getSimpleName();
	Bundle bundle;
	Intent intent;
	
	CategoryBean categoryBean;
	String categoryContentsUrl;
	
	//异步请求队列
    private RequestQueue mQueue;  
    private ImageLoader mImageLoader;
   	private ImageListener imageListener;
    
	private CategoryMediaBean cmb=null;
	private MediaProfileBean[] videoBeansArr;
	List<MediaProfileBean> videoBeanList;
	
	ProgressDialog progressBar=null;
	private static int maxRetryTime=3;
	private int retryTimes=0;
	private ListView coursewareListView;
    private CoursewareContentListAdapter coursewareListViewAdapter;
    private ArrayList<CoursewareContent> mArrayList;
	//相关视频

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.courseware_content_activity);
		
		
		//获取上一级数据
		intent = this.getIntent();
		bundle = intent.getExtras();
		String categoryJson =bundle.getString("CategoryBean");
		Log.i(TAG, "get bundle data "+categoryJson);
		Gson gson = new Gson();
		categoryBean = gson.fromJson(categoryJson, CategoryBean.class);
			
		//获取volley对象
		mQueue = ((GlobalVolleyApplication)this.getApplicationContext()).getQueue();
		mImageLoader=((GlobalVolleyApplication)this.getApplicationContext()).getImageLoader();
		
		initListView(categoryBean);
		
		requestJsonObject();
	}
	
	 private void initListView(CategoryBean categoryBean){
		 ImageView ivPic = (ImageView)this.findViewById(R.id.imageViewMediaPic);
		 TextView tvTitle = (TextView)findViewById(R.id.textViewTitle);
//		 TextView tvPublishTime = (TextView)findViewById(R.id.textViewPublishTime);
//		 TextView tvActor = (TextView)findViewById(R.id.textViewActor);
//		 TextView tvAuthor = (TextView)findViewById(R.id.textViewAuthor);
//		 TextView tvDesc = (TextView)findViewById(R.id.textViewDesc);
		 
		 tvTitle.setText(categoryBean.getCategory_name());
		
		 
		imageListener = ImageLoader.getImageListener(ivPic, -1, -1);
		mImageLoader.get(categoryBean.getThumbs().getS().getUrl(), imageListener);
			
			
	   coursewareListView=(ListView) findViewById(R.id.listViewCourseware);
	   mArrayList=new ArrayList<CoursewareContent>();
	   
	   coursewareListViewAdapter=new CoursewareContentListAdapter(mArrayList, CoursewareContentActivity.this);
	   coursewareListView.setAdapter(coursewareListViewAdapter);
   }
	 
////////////////////////从网络下载数据///////////////////////////////////////

//根据IP和端口构造json URL
private void buildJsonUrl()
{
	String IP;
	String port;
	
	IP = ((GlobalVolleyApplication)this.getApplicationContext()).getServerIp();
	port = ((GlobalVolleyApplication)this.getApplicationContext()).getServerPort();
	
	Log.i(TAG, " buildJsonUrl() Ip:"+IP+" port "+port);
	
	if(port.equals("80")){
		categoryContentsUrl="http://"+IP+"/catagory_content_list?";
	}else{
		categoryContentsUrl="http://"+IP+":"+port+"/catagory_content_list?";
	}
	categoryContentsUrl = categoryContentsUrl+"category_id="+categoryBean.getCategory_id()+"&start_index=0";
	
	Log.i(TAG, " buildJsonUrl() url:"+categoryContentsUrl);
}
	
private void requestJsonObject() {
	Log.i(TAG, "requestJsonObject()");
	buildJsonUrl();
	mQueue.cancelAll("get_courseware_medias");
	
	JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
	Method.GET, categoryContentsUrl,null, new ResponseListener(), new ErrorListener());// 也可以在这里携带需要上传的数据
	Log.i(TAG, "Get category medias from Url:"+categoryContentsUrl);
	jsonObjectRequest.setTag("get_courseware_medias");
	mQueue.add(jsonObjectRequest);// 添加请求到队列里
	
	 //设置滚动条
  	progressBar = ProgressDialog.show(this, "", "加载网络数据...",true);
  	retryTimes++;
  
}

private class ResponseListener implements Response.Listener<JSONObject>{
	@Override
	public void onResponse(JSONObject response) {
		Log.i(TAG, "onResponse(JSONObject)");
		int i;
		Gson gson = new Gson();
		String json = response.toString();
		Log.i(TAG,"Category Json Result:"+json);
		
		cmb = gson.fromJson(json,CategoryMediaBean.class );
		videoBeansArr = cmb.getMedias();
		videoBeanList = Arrays.asList(videoBeansArr);

		 CoursewareContent arrayListForGridView = new CoursewareContent(videoBeanList,"第一章第一节");
		 mArrayList.add(arrayListForGridView);
		 coursewareListViewAdapter.refresh(mArrayList);
		 progressBar.dismiss();
		 retryTimes=0;
	}

}

private class ErrorListener implements Response.ErrorListener{
	@Override
	public void onErrorResponse(VolleyError error) {
		Log.i(TAG, "onErrorResponse:"+error.getMessage());

		progressBar.dismiss();
		
		if(retryTimes>=maxRetryTime){
			Toast.makeText(getApplicationContext(), 
					"加载数据失败,请检查网络和服务器设置.", Toast.LENGTH_LONG).show();
			return;
		}
		
		Toast.makeText(getApplicationContext(), 
		"加载数据失败，正在重试...", Toast.LENGTH_LONG).show();
		Log.e(TAG, "Get category medias failed.Try again from url:"+categoryContentsUrl);

		requestJsonObject();
	}
}
	
	
}
