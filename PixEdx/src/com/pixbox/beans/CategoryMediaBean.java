package com.pixbox.beans;
import java.util.List;

import com.pixbox.beans.CategoryBean.ImageUrlInfo;

/*****************json struct**********************/
//{"count": 2, 
//"medias":
//{"related_tags": [], 
//"subtitle": "HLS\u8c03\u8bd5\u89c6\u9891_SubTitle",
//"author": "", 
//"play_url": "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8",
//"title": "HLS\u8c03\u8bd5\u89c6\u9891_Title",
//"related_tags_count": 0,
//"published_ts": "2014-02-23 15:13:41",
//"related_medias_count": 0, 
//"director": "", 
//"actors": "", 
//"thumbs": {
//"big": {"url": "http://54.201.52.201/html/media/01300000345342123676404999645.jpg", "x": 320, "y": 120}, 
//"s": {"url": "http://54.201.52.201/html/media/u%3D1892971729%2C2978434906%26fm%3D21%26gp%3D0.jpg", "x": 100, "y": 40}, 
//"m": {"url": "http://54.201.52.201/html/media/50da81cb39dbb6fdd1d388080924ab18972b3717.jpg", "x": 195, "y": 100}}, 
//"media_type": "1", 
//"media_id": 1, 
//"related_medias": [], 
//"desc": "HLS\u8c03\u8bd5\u89c6\u9891_Description"}

public class CategoryMediaBean {
	int count;
	MediaProfileBean[] medias;
	int start_index;
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public MediaProfileBean[] getMedias() {
		return medias;
	}
	public void setMedias(MediaProfileBean[] medias) {
		this.medias = medias;
	}
	public int getStart_index() {
		return start_index;
	}
	public void setStart_index(int start_index) {
		this.start_index = start_index;
	}
	
	
	
	
}
