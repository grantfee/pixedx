package com.pixbox.main;
import java.util.HashMap;
import java.util.List;

import com.pixbox.beans.MediaProfileBean;

public class CoursewareContent{
	List<MediaProfileBean> medias; //¿Î¼þÊÓÆµ°üº¬img url£¬play url£¬title
	String parentTitle;
	
	public CoursewareContent(List<MediaProfileBean> medias,
			String parentTitle) {
		
		this.medias = medias;
		this.parentTitle = parentTitle;
	}
	public List<MediaProfileBean> getMedias() {
		return medias;
	}
	public void setMedias(List<MediaProfileBean> medias) {
		this.medias = medias;
	}
	public String getParentTitle() {
		return parentTitle;
	}
	public void setParentTitle(String parentTitle) {
		this.parentTitle = parentTitle;
	}
	
}
