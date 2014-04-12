package com.pixbox.dao;

public class MyCoursewareBean {
		int id;
		int category_id;
		
		String play_url;
		String img_url;
		String title;
		
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public int getCategory_id() {
			return category_id;
		}
		public void setCategory_id(int category_id) {
			this.category_id = category_id;
		}
		public String getPlay_url() {
			return play_url;
		}
		public void setPlay_url(String play_url) {
			this.play_url = play_url;
		}
		public String getImg_url() {
			return img_url;
		}
		public void setImg_url(String img_url) {
			this.img_url = img_url;
		}
		
	
}
