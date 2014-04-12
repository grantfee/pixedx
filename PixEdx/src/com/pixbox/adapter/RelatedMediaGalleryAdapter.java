package com.pixbox.adapter;

import java.util.List;

import com.videoexpress.pixedx.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class RelatedMediaGalleryAdapter extends BaseAdapter{
	private int[] imageIds = new int[]{  
            R.drawable.movie1, R.drawable.movie2,  
            R.drawable.movie3, R.drawable.movie4,  
            R.drawable.movie5, R.drawable.movie6, R.drawable.movie7, 
    };  
	
	Context context;
	
	public RelatedMediaGalleryAdapter(List<String> imgUrls,Context context) {
		super();
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return imageIds.length;
	}

	@Override
	public Object getItem(int item) {
		// TODO Auto-generated method stub
		return item;
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ImageView imageView = new ImageView(context);  
        imageView.setImageResource(imageIds[position]);  
        // 设置ImageView的缩放类型  
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);  
        // 为imageView设置布局参数  
        imageView.setLayoutParams(new Gallery.LayoutParams(75, 100));  
     
        return imageView;  
	}

}
