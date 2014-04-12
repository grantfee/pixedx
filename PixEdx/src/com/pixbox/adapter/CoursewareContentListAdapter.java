package com.pixbox.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.pixbox.beans.MediaProfileBean;
import com.pixbox.main.CoursewareContent;
import com.videoexpress.pixedx.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class CoursewareContentListAdapter extends BaseAdapter {
    private List<CoursewareContent> mList;
    private Context mContext;
   
	
	public CoursewareContentListAdapter(ArrayList<CoursewareContent> mList, Context mContext) {
		super();
		this.mList = mList;
		this.mContext = mContext;
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
	
	public void refresh(List<CoursewareContent> videoBeansList) {
		mList = videoBeansList;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();			
			convertView = LayoutInflater.from(this.mContext).inflate(R.layout.courseware_content_list_item, null, false);	
			holder.tvChapter = (TextView)convertView.findViewById(R.id.textViewCharpter);
			holder.imageView = (ImageView) convertView.findViewById(R.id.listview_item_imageview);
			holder.gridView = (GridView) convertView.findViewById(R.id.listview_item_gridview);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		if (this.mList != null) {
			holder.tvChapter.setText(this.mList.get(position).getParentTitle());
			
			if (holder.imageView != null) {
				holder.imageView.setImageDrawable
				(mContext.getResources().getDrawable(R.drawable.launcher_tab_item_focused));
			}
			if (holder.gridView != null) {
				List<MediaProfileBean> arrayListForEveryGridView = this.mList.get(position).getMedias();
				CoursewareContentGridAdapter gridViewAdapter=new CoursewareContentGridAdapter(mContext, arrayListForEveryGridView);
				holder.gridView.setAdapter(gridViewAdapter);
			}

		}

		return convertView;

	}

	
	private class ViewHolder {
		TextView tvChapter;
		ImageView imageView;
		GridView gridView;
	}

}
