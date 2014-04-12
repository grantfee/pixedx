package com.pixbox.main;

import com.pixbox.utils.GlobalVolleyApplication;
import com.videoexpress.pixedx.R;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ServerSettingFragment extends DialogFragment{
	
	private EditText editTextIp;
	private EditText editTextPort;
	private Button   btnConfirm;
	private Button   btnCancel;
	
	private String IP;
	private String port;
	
	private OnDataSettingChanged mDataChangedListener;
	
	public interface OnDataSettingChanged{
		public void onDataSettingChanged();
	}
	
	private static ServerSettingFragment ssf;
	
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		try{
			mDataChangedListener = (OnDataSettingChanged)activity;
		}catch(ClassCastException e){
			throw new ClassCastException(activity.toString()+"must implement OnDataSettingChanged");
		}
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	public static ServerSettingFragment newInstance(String currentIP,String port) {  
	   // 创建一个新的带有指定参数的Fragment实例  
		ServerSettingFragment fragment = new ServerSettingFragment();  
	   Bundle args = new Bundle();  
	   args.putString("IP", currentIP);  
	   args.putString("port", port);
	   fragment.setArguments(args); 
	   ssf = fragment;
	   return fragment;  
	 }  

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		
		return super.onCreateDialog(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_svr_settings, container, false);
		
		editTextIp = (EditText) v.findViewById(R.id.editTextIp);
		editTextPort= (EditText) v.findViewById(R.id.editTextPort);
		btnConfirm = (Button) v.findViewById(R.id.buttonConfirm);
		btnCancel= (Button) v.findViewById(R.id.buttonCancel);
	
		IP = ((GlobalVolleyApplication)getActivity().getApplicationContext()).getServerIp();
		port= ((GlobalVolleyApplication)getActivity().getApplicationContext()).getServerPort();
		
		Log.i("SystemSettingFragment", "current IP"+IP+" Port:"+port);
		editTextIp.setText(IP);
		editTextPort.setText(port);
		//this.setStyle(STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog);
		btnConfirm.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				String newIP= editTextIp.getText().toString();
				String newPort= editTextPort.getText().toString();
				newIP=newIP.replace(" ", "");
				newPort=newPort.replace(" ", "");
				//newIP.trim();
				//newPort.trim();
				
				if(newIP==null||newPort==null){
					return;
				}
					
				((GlobalVolleyApplication)getActivity().getApplicationContext()).setServerIp(newIP);
				((GlobalVolleyApplication)getActivity().getApplicationContext()).setServerPort(newPort);
				Log.i("SystemSettingFragment", "setOnClickListener new IP"+newIP+" Port:"+newPort);
				
				//配置数据改变 通知主Activity，更新分类fragment，主activity实现该接口
				mDataChangedListener.onDataSettingChanged();
			
				
				ssf.dismiss();
			}
			
		});
		
		btnCancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ssf.dismiss();
			}
			
		});
		
		return v;
		
	}
}
