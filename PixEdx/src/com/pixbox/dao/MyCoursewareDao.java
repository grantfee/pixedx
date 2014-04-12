package com.pixbox.dao;



import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MyCoursewareDao {
	SQLiteDatabase db=null;
	Context mContext=null;
	static int valIndex=0;
	
	public MyCoursewareDao(Context mContext) {
		super();
		this.mContext = mContext;
	}

	public void createFavoriteDB()
	{
		db = mContext.openOrCreateDatabase("pixedx.db",mContext.MODE_PRIVATE,null);
		//db.execSQL("DROP TABLE IF EXISTS favorite_courseware");
		db.execSQL("CREATE TABLE IF NOT EXISTS favorite_courseware (_id INTEGER PRIMARY KEY AUTOINCREMENT,title VARCHAR,category_id INTEGER,play_url VARCHAR,img_url VARCHAR)");
	}
	
	public  void insertFavoriteRec(MyCoursewareBean mycourse)
	{
	
//		MyCoursewareBean mycourse = new MyCoursewareBean();
//		mycourse.setTitle("shiwangzhengba"+valIndex);
//		mycourse.setCategory_id(valIndex);
//		mycourse.setImg_url("http://www.mycourse.com/chinese/language.png");
//		mycourse.setPlay_url("http://www.mycourse.com/chinese/language.m3u8");
		
		//db.execSQL("INSERT INTO favorite_courseware VALUES(NULL,?,?,?,?)",new Object[]{mycourse.getTitle(),mycourse.getCategory_id(),mycourse.getPlay_url(),mycourse.getImg_url()});
		ContentValues cv =new ContentValues();
		
		cv.put("title", mycourse.getTitle());
		cv.put("category_id", mycourse.getCategory_id());
		cv.put("play_url", mycourse.getPlay_url());
		cv.put("img_url", mycourse.getImg_url());
		
		if(db!=null){
			db.insert("favorite_courseware", null, cv);
		}else{
			Log.e("insert data","faile to insert data");
		}
		
		Log.i("insert data",cv.toString());
		
	}
	
	public List<MyCoursewareBean> queryAllFavoriteRec()
	{
		List<MyCoursewareBean> val= new ArrayList<MyCoursewareBean>();
		MyCoursewareBean mycourse=null;
		int itemCnt=0;
		Cursor c= db.rawQuery("SELECT * FROM favorite_courseware", null);
		
		while(c.moveToNext()){
			mycourse = new MyCoursewareBean();
			String title = c.getString(c.getColumnIndex("title"));
			int category_id = c.getInt(c.getColumnIndex("category_id"));
			String play_url = c.getString(c.getColumnIndex("play_url"));
			String img_url = c.getString(c.getColumnIndex("img_url"));
			
			mycourse.setCategory_id(category_id);
			mycourse.setTitle(title);
			mycourse.setPlay_url(play_url);
			mycourse.setImg_url(img_url);
			
			val.add(mycourse);
			Log.i("query data","item index:"+itemCnt+" "+mycourse.toString());
				
			itemCnt++;
		}
		
		c.close();
		Log.i("query data","item total count:"+itemCnt);
		return val;
	}
	
	public void closeDB()
	{
		db.close();
	}
	public void clearDBTable()
	{
		db.execSQL("DROP TABLE IF EXISTS favorite_courseware");
		
	}
	
}
