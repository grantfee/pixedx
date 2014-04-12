package com.pixbox.beans;



public class MediaProfileBean{
	Object[] related_tags;
	String subtitle;
	String author;
	String play_url;
	String title;
	int related_tags_count;
	String published_ts;
	int related_medias_count;
	String director;
	String actors;
	Thumbs thumbs;
	String media_type;
	int media_id;
	
	Object[] related_medias;//该字段类型未知!!!
	String desc;
	
	public Object[] getRelated_tags() {
		return related_tags;
	}

	public void setRelated_tags(Object[] related_tags) {
		this.related_tags = related_tags;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPlay_url() {
		return play_url;
	}

	public void setPlay_url(String play_url) {
		this.play_url = play_url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getRelated_tags_count() {
		return related_tags_count;
	}

	public void setRelated_tags_count(int related_tags_count) {
		this.related_tags_count = related_tags_count;
	}

	public String getPublished_ts() {
		return published_ts;
	}

	public void setPublished_ts(String published_ts) {
		this.published_ts = published_ts;
	}

	public int getRelated_medias_count() {
		return related_medias_count;
	}

	public void setRelated_medias_count(int related_medias_count) {
		this.related_medias_count = related_medias_count;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getActors() {
		return actors;
	}

	public void setActors(String actors) {
		this.actors = actors;
	}

	public Thumbs getThumbs() {
		return thumbs;
	}

	public void setThumbs(Thumbs thumbs) {
		this.thumbs = thumbs;
	}

	public String getMedia_type() {
		return media_type;
	}

	public void setMedia_type(String media_type) {
		this.media_type = media_type;
	}

	public int getMedia_id() {
		return media_id;
	}

	public void setMedia_id(int media_id) {
		this.media_id = media_id;
	}

	public Object[] getRelated_medias() {
		return related_medias;
	}

	public void setRelated_medias(Object[] related_medias) {
		this.related_medias = related_medias;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	
	
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
}