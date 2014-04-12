package com.pixbox.beans;

import java.util.List;


public class CategoryBean {

	int media_count;
	int hierarchy_laier;
	int parent_category_id;
	String category_type;
	
	Thumbs thumbs;
	String parent_category_name;
	int category_id;
	boolean be_edge;
	String category_name;
	
	
	public class Thumbs{
		ImageUrlInfo big;
		ImageUrlInfo m;
		ImageUrlInfo s;
		public ImageUrlInfo getBig() {
			return big;
		}
		public void setBig(ImageUrlInfo big) {
			this.big = big;
		}
		public ImageUrlInfo getM() {
			return m;
		}
		public void setM(ImageUrlInfo m) {
			this.m = m;
		}
		public ImageUrlInfo getS() {
			return s;
		}
		public void setS(ImageUrlInfo s) {
			this.s = s;
		}
		
	}
	
	public class ImageUrlInfo{
		String url;
		int x;
		int y;
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		
	}

	public int getMedia_count() {
		return media_count;
	}

	public void setMedia_count(int media_count) {
		this.media_count = media_count;
	}

	public int getHierarchy_laier() {
		return hierarchy_laier;
	}

	public void setHierarchy_laier(int hierarchy_laier) {
		this.hierarchy_laier = hierarchy_laier;
	}

	public int getParent_category_id() {
		return parent_category_id;
	}

	public void setParent_category_id(int parent_category_id) {
		this.parent_category_id = parent_category_id;
	}

	public String getCategory_type() {
		return category_type;
	}

	public void setCategory_type(String category_type) {
		this.category_type = category_type;
	}

	public Thumbs getThumbs() {
		return thumbs;
	}

	public void setThumbs(Thumbs thumbs) {
		this.thumbs = thumbs;
	}

	public String getParent_category_name() {
		return parent_category_name;
	}

	public void setParent_category_name(String parent_category_name) {
		this.parent_category_name = parent_category_name;
	}

	public int getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}

	public boolean isBe_edge() {
		return be_edge;
	}

	public void setBe_edge(boolean be_edge) {
		this.be_edge = be_edge;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	
	
}
