package com.pixbox.main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.android.volley.RequestQueue;
import com.pixbox.adapter.MyFragmentPageAdapter;
import com.pixbox.utils.GlobalVolleyApplication;
import com.pixbox.utils.ApkUpdateManager;
import com.videoexpress.pixedx.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.HorizontalScrollView;
import android.widget.Toast;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MainActivity extends FragmentActivity implements TabFrameLayout.TabPageChangeListener,ServerSettingFragment.OnDataSettingChanged {
	private final String TAG = getClass().getSimpleName();
	private ViewPager advPager = null;
	private MyFragmentPageAdapter fragAdapter;
	private List<Fragment> fragments;
	private AtomicInteger what = new AtomicInteger(0);
 
	private static String packageName = "com.videoexpress.pixbox";
	private SharedPreferences setting;
	private long exitTime = 0;
	
	float picHeightScale=(float) 0.6;
	int  picHeightDp=400;

    private String[] tilteTabtext = {
    	"�γ̷���",
    	"�ҵĿγ�",
    	"ϵͳ����"
    };
    
    //framlayout�˵�
    TabFrameLayout Tabfl;
    HorizontalScrollView videtypeTabpage;
    
    //������¹�����
    private ApkUpdateManager mUpdateManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//��ȡ���ò���
		getPreferencesSettings();
		
		//mQueue = ((GlobalVolleyApplication)this.getApplicationContext()).getQueue();
		
		//��ʼ�������˵�
		Tabfl =  (TabFrameLayout) findViewById(R.id.tabutton);
		Tabfl.CreateTabButton(tilteTabtext);
		Tabfl.setPageTabControl(this);
		
		//��ʼ����ҳ��
		initViewPager();
		calculatedDispdpi();
		
		//���������汾�Ƿ���Ҫ����  
        mUpdateManager = new ApkUpdateManager(this);  
	}
	
	private void getPreferencesSettings()
	{
		setting = getSharedPreferences(packageName,0);
		String ip = setting.getString("server_ip", "54.201.52.201");
		String port = setting.getString("server_port","80");
		((GlobalVolleyApplication)this.getApplicationContext()).setServerIp(ip);
		((GlobalVolleyApplication)this.getApplicationContext()).setServerPort(port);
		
		Log.i(TAG, "server ip"+ip+" port:"+port);
	}
	
	private void savePreferencesSettings()
	{
		if(setting!=null){
			String ip = ((GlobalVolleyApplication)this.getApplicationContext()).getServerIp();
			String port = ((GlobalVolleyApplication)this.getApplicationContext()).getServerPort();
			SharedPreferences.Editor editor = setting.edit();
			editor.putString("server_ip", ip);
			editor.putString("server_port", port);
			editor.commit();
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mUpdateManager.checkUpdateInfoReq(); 
	}


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		
		Log.i(TAG, "onPause");
		super.onPause();
		
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onStop");
		
		super.onStop();
	}
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		exitMainEntry();
		//super.onBackPressed();
	}

	private void finishThis()
	{
		this.finish();
	}
	public void exitMainEntry()
    {
            if ((System.currentTimeMillis() - exitTime) > 2000)
            {
                    Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
            } else
            {
            	Dialog alertDialog = new AlertDialog.Builder(this).   
                        setTitle("��ʾ").   
                        setMessage("�Ƿ�ȷ���˳�����?��ȷ���˳���").   
                        setIcon(R.drawable.ic_launcher).
                        setPositiveButton("ȷ ��", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								finishThis();
							}
						}).setNegativeButton("ȡ��",new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.cancel();
							}})
						.create();   
            
            	alertDialog.show();   

            }

    }

	public void putFocusOnTab()
	{
		Tabfl.FocusOnTab();
	}
	
	private void calculatedDispdpi(){
		String str = ""; 
        DisplayMetrics dm = new DisplayMetrics();
        dm = this.getApplicationContext().getResources().getDisplayMetrics(); 
        int screenWidth = dm.widthPixels; 
        int screenHeight = dm.heightPixels; 
        float density = dm.density; 
        str += "��Ļ�ֱ���Ϊ:" + dm.widthPixels + " * " + dm.heightPixels + "\n"; 
        str += "���Կ��:" + String.valueOf(screenWidth) + "pixels\n"; 
        str += "���Ը߶�:" + String.valueOf(screenHeight) 
                + "pixels\n"; 
        str += "�߼��ܶ�:" + String.valueOf(density) 
                + "\n"; 
        screenWidth = (int) (dm.widthPixels/density);
        screenHeight =(int) (dm.heightPixels/density); 
        picHeightDp = (int)(screenHeight*picHeightScale);
        str += "��Ļ�� :" + String.valueOf(dm.widthPixels/density) + "dp\n"; 
        str += "��Ļ��:" + String.valueOf(dm.heightPixels/density) + "dp\n";
        str += "ͼƬ�ܸ߶�:" +String.valueOf(picHeightDp);
        Log.i("calculatedDispdpi", str); 
	}
	
	private void initViewPager() {
		advPager = (ViewPager) findViewById(R.id.adv_pager);
		fragments = new ArrayList<Fragment>();
		
		fragments.add(new CourseCategoryFragment());
		fragments.add(new MyCategoryFragment());
		//fragments.add(new SearchFragment());
		//fragments.add(new SettingFragment());
		fragments.add(new SettingFragment());
		
		FragmentManager fm = getSupportFragmentManager();
		fragAdapter = new MyFragmentPageAdapter(fm,fragments);
		
		advPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.pager_margin));
		advPager.setAdapter(fragAdapter);
		advPager.setOnPageChangeListener(new GuidePageChangeListener());
	}
	/**
	 * ����ҳ��ָʾ��
	 * 
	 */
	private final class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int arg0) {
			what.getAndSet(arg0);
			Tabfl.SelectAniTab(arg0);
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

	}
	
	@Override
	public void SelPageTab(int index) {
		// TODO Auto-generated method stub
		if(index!=advPager.getCurrentItem())
		{
			advPager.setCurrentItem(index);
		}	
	}

	//��������IP�Ͷ˿��Ƿ�ı䣬�ӿ�������SystemSettingFragment
	@Override
	public void onDataSettingChanged() {
		// TODO Auto-generated method stub
		
		savePreferencesSettings();
		//���´ӷ�������������,���õ���һ����
		CourseCategoryFragment fragment = (CourseCategoryFragment)fragments.get(0);
		fragment.requestJsonObject();
		mUpdateManager.checkUpdateInfoReq(); 
	}
}
