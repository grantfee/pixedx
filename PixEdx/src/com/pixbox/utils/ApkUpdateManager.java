package com.pixbox.utils;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.pixbox.beans.TopCategoryBean;

import com.videoexpress.pixedx.R;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class ApkUpdateManager {

	private final  String TAG=this.getClass().getSimpleName();
	private Context mContext;
	
	private String packageName = "com.videoexpress.pixbox";
	
	//���ص�������Ϣ
	private RequestQueue mQueue;
	private UpdateInfo updateInfo;
	private String checkUpdateUrl="http://54.201.52.201/static/update/edx_pad/version.json";
	private String apkDownloadUrl;
	
	//������ʾ�Ի���
	private Dialog noticeDialog;
	private Dialog downloadDialog;
	
	 /* ���ذ���װ·�� */
    private static final String savePath = "/sdcard/updatepixbox/";
    private static final String saveFileName = savePath + "PixBoxNew.apk";

    /* ��������֪ͨuiˢ�µ�handler��msg���� */
    private ProgressBar mProgress;
    private TextView tvProgress;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private int progress;
    
    private Thread downLoadThread;
    private boolean interceptFlag = false;
    
    //handler��������progress
    private Handler mHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				tvProgress.setText(progress+"%");
				break;
			case DOWN_OVER:
				installApk();
				break;
			default:
				break;
			}
    	};
    };
    
	public ApkUpdateManager(Context context) {
		this.mContext = context;
	}
	
	//
	public int getSelfVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(
            		packageName, 0).versionCode;
        } catch (NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return verCode;
        
    }
   
    public  String getSelfVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
            		packageName, 0).versionName;
           
        } catch (NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return verName;   
    }
	
    private void buildJsonUrl()
    {
    	String IP = ((GlobalVolleyApplication)mContext.getApplicationContext()).getServerIp();
		String port = ((GlobalVolleyApplication)mContext.getApplicationContext()).getServerPort();
		
		if(port.equals("80")){
			checkUpdateUrl="http://"+IP+"/static/update/edx_pad/version.json";
		}
		else{
			checkUpdateUrl="http://"+IP+":"+port+"/static/update/edx_pad/version.json";
		}
    }
    ///////////////////////////////////////////////////////////
    public void checkUpdateInfoReq()
    {
    	
    	buildJsonUrl();
    	
    	mQueue = ((GlobalVolleyApplication)mContext.getApplicationContext()).getQueue();
    	 //ȡ�����е�ǰ����
        mQueue.cancelAll("get_update_info");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Method.GET, checkUpdateUrl,null, new ResponseListener(), new ErrorListener());// Ҳ����������Я����Ҫ�ϴ�������
        
        jsonObjectRequest.setTag("get_update_info");
        mQueue.add(jsonObjectRequest);// ������󵽶�����
	
    }
  //�����������Ӧ
  	private class ResponseListener implements Response.Listener<JSONObject>{
  		@Override
  		public void onResponse(JSONObject response) {
  		   Log.i(TAG, "onResponse(JSONObject)");
  		   int verCode=0;
  		   String remoteVerCode;
           Gson gson = new Gson();
           String json = response.toString();
      
           updateInfo = gson.fromJson(json,UpdateInfo.class );
           verCode = getSelfVerCode(mContext);
           remoteVerCode = updateInfo.getVerCode();
           apkDownloadUrl = updateInfo.getApkDownloadURL();
           
           Log.i(TAG,"checkUpdateInfoReq Json Result:"+json);   
           Log.i(TAG,"Self verCode:"+verCode+" new verCode:"+remoteVerCode);
           
           if(Integer.parseInt(remoteVerCode) > verCode){
        	   Log.i(TAG,"Need upgrade");
        	   Log.i(TAG,"apk download url:"+apkDownloadUrl);
        	   showNoticeDialog(updateInfo.getApkDesc());
           }else{
        	   Log.i(TAG,"No need upgrade");
           }
           
         
  		}
  		
  	}
  	
  	//��������Ӧ����
  	private class ErrorListener implements Response.ErrorListener{
  		@Override
  		public void onErrorResponse(VolleyError error) {
  			Log.i(TAG, "onErrorResponse:"+error.getMessage());

  		}
  	}
  	//��ʾ������ʾ�Ի���
	private void showNoticeDialog(String updateMsg){
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);   
        View v = layoutInflater.inflate(R.layout.apk_update_notice, null);   
        TextView tvInfo = (TextView)v.findViewById(R.id.textViewVersionInfo);
        tvInfo.setText(updateMsg);
        
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("����汾����");
		builder.setMessage("");
		builder.setView(v);
		builder.setPositiveButton("��������", new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();			
			}
		});
		builder.setNegativeButton("�Ժ���˵", new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();				
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
	}
	//��ʾ���ضԻ���
	private void showDownloadDialog(){
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("�������");
		
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.apk_download_progress, null);
		mProgress = (ProgressBar)v.findViewById(R.id.progress);
		tvProgress = (TextView)v.findViewById(R.id.textViewProgress);
		
		builder.setView(v);
		builder.setNegativeButton("ȡ��", new OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		
		downloadDialog = builder.create();
		downloadDialog.setCanceledOnTouchOutside(false);
		downloadDialog.show();
		
		downloadApk();
	}
	//http�����߳�
	private Runnable mdownApkRunnable = new Runnable() {	
		@Override
		public void run() {
			try {
				URL url = new URL(apkDownloadUrl);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
				
				File file = new File(savePath);
				if(!file.exists()){
					file.mkdir();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);
				
				int count = 0;
				byte buf[] = new byte[1024];
				
				do{   		   		
		    		int numread = is.read(buf);
		    		count += numread;
		    	    progress =(int)(((float)count / length) * 100);
		    	    //���½���
		    	    mHandler.sendEmptyMessage(DOWN_UPDATE);
		    		if(numread <= 0){	
		    			//�������֪ͨ��װ
		    			mHandler.sendEmptyMessage(DOWN_OVER);
		    			break;
		    		}
		    		fos.write(buf,0,numread);
		    	}while(!interceptFlag);//���ȡ����ֹͣ����.
				
				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch(IOException e){
				e.printStackTrace();
			}
			
		}
	};
	
	 /**
     * ��������apk
     * @param url
     */
	
	private void downloadApk(){
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}
	 /**
     * ��װapk
     * @param url
     */
	private void installApk(){
		File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
        	Log.e("installApk","Not found the apk package");
            return;
        }    
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
	
	}
	
//	{
//		"apkname":"PixedXPad.apk",
//		"verName":"1.0.1",
//		"verCode":"2",
//		"apkDownloadURL":"http://54.201.52.201/static/update/edx_pad/PixedXPad.apk",
	//  "apkDesc":"dfjoiwejf"
//	}
	private class UpdateInfo{
		
		String apkname;
		String verName;
		String verCode;
		String apkDownloadURL;
		String apkDesc;
		
		public String getApkname() {
			return apkname;
		}
		public void setApkname(String apkname) {
			this.apkname = apkname;
		}
		public String getVerName() {
			return verName;
		}
		public void setVerName(String verName) {
			this.verName = verName;
		}
		public String getVerCode() {
			return verCode;
		}
		public void setVerCode(String verCode) {
			this.verCode = verCode;
		}
		public String getApkDownloadURL() {
			return apkDownloadURL;
		}
		public void setApkDownloadURL(String apkDownloadURL) {
			this.apkDownloadURL = apkDownloadURL;
		}
		public String getApkDesc() {
			return apkDesc;
		}
		public void setApkDesc(String apkDesc) {
			this.apkDesc = apkDesc;
		}
		
	}

}
