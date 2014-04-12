package com.pixbox.main;

import com.videoexpress.pixedx.R;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class SettingFragment extends Fragment{
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_setting_main, container, false);
		
		ImageView ivSvrSetting = (ImageView)v.findViewById(R.id.imageViewSvrSetting);
		ImageView ivPlaySetting = (ImageView)v.findViewById(R.id.imageViewPlaySetting);
		
		ivSvrSetting.setOnClickListener(new ImageSvrSettingClick());
		return v;
		
	}
	
	private class ImageSvrSettingClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String tag = "…Ë÷√";  
			DialogFragment myFragment = ServerSettingFragment.newInstance("54.201.52.201","80");  
			myFragment.show(getFragmentManager(), tag); 
			myFragment.setCancelable(true);
			
		}	
	}
}
