package com.pixbox.adapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.pixbox.beans.MediaProfileBean;
import com.pixbox.main.RtspPlayActivity;
import com.pixbox.player.PixBoxMediaPlayerActivity;
import com.videoexpress.pixedx.R;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CoursewareContentGridAdapter extends BaseAdapter{
	private final String TAG = this.getClass().getSimpleName();
	private Context mContext;
	private List<MediaProfileBean> mList;
	
	public CoursewareContentGridAdapter(Context mContext,List<MediaProfileBean> mList) {
		super();
		this.mContext = mContext;
		this.mList = mList;
	}

	@Override
	public int getCount() {
		if (mList == null) {
			return 0;
		} else {
			return this.mList.size();
		}
	}

	@Override
	public Object getItem(int position) {
		if (mList == null) {
			return null;
		} else {
			return this.mList.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();			
			convertView = LayoutInflater.from
			(this.mContext).inflate(R.layout.courseware_content_grid_item, null, false);		
			holder.iv = (ImageView)convertView.findViewById(R.id.imageViewGridView);
			holder.tv = (TextView)convertView.findViewById(R.id.textViewGridView);
			convertView.setTag(holder);
		
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		if (this.mList != null) {
			MediaProfileBean mpb = this.mList.get(position);
			if (holder.iv != null) {
				holder.iv.setTag(mpb);
				holder.iv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						MediaProfileBean mpb = (MediaProfileBean)v.getTag();
						startOriginalPlayer(mpb.getPlay_url());
						//startRtspPlayActivity(mpb.getPlay_url());
						//startPixboxPlayerActivity(mpb);
						Log.i(TAG, "start to play url:"+mpb.getPlay_url());
					}
				});
			}
			
			if(holder.tv!=null){
				holder.tv.setText(mpb.getTitle());
				
			}
		}

		return convertView;

	}
	
	private void startOriginalPlayer(String url)
	{
		Intent it = new Intent(Intent.ACTION_VIEW);
	    Uri uri = Uri.parse(url);
	    it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    it.putExtra("oneshot", 0);
	    it.putExtra("configchange", 0);
	    Log.i(TAG, "Try to play:"+ url);
	    it.setDataAndType(uri , "video/*");
	    mContext.startActivity(it);
	}
	
	
	//启动节目详细页面activity
	private void startRtspPlayActivity(String url)
	{
		Intent intent;
		Bundle bundle;
		
		//bundle.putString("category_all_contents_url", v.getTag().toString());
		if(url!=null){
			Log.i(TAG, "Try to switch to RtspPlayActivity,play url:"+url);
			bundle = new Bundle();
			intent = new Intent();
			bundle.putString("RtspUrl", url);
			intent.setClass(mContext, RtspPlayActivity.class);
			intent.putExtras(bundle);
			
			mContext.startActivity(intent);
		}else{
			Log.i(TAG, "Failed to switch to ContentDetailActivity failed.MediaProfileBean is null");
			
		}
		
	}
	
	private void startPixboxPlayerActivity(MediaProfileBean mpb)
	{
		Intent intent;
		Bundle bundle;
		
		//bundle.putString("category_all_contents_url", v.getTag().toString());
		if(mpb!=null){
			Gson gson =new Gson();
			Log.i(TAG, "Try to switch to RtspPlayActivity,play url:"+mpb.getPlay_url());
			bundle = new Bundle();
			intent = new Intent();
			String json = gson.toJson(mpb);
			bundle.putString("MediaPlayUrl", json);
			intent.setClass(mContext, PixBoxMediaPlayerActivity.class);
			intent.putExtras(bundle);
			
			mContext.startActivity(intent);
		}else{
			Log.i(TAG, "Failed to switch to ContentDetailActivity failed.MediaProfileBean is null");
			
		}
		
	}
	private class ViewHolder {
		ImageView iv;
		TextView tv;
	}

}
