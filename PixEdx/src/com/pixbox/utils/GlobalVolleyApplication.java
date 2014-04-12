package com.pixbox.utils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import android.app.Application;
import android.content.SharedPreferences;
/**
 * CapTech Consulting Blog
 * 
 * Runs at the start of the application and is responsible for initializing
 * the queues, ImageLoader, and image cache.  
 * 
 * @author Clinton Teegarden
 *
 */


public class GlobalVolleyApplication extends Application{
	private ImageLruCache imageCache;
	private RequestQueue queue;
	private ImageLoader imageLoader;
	private String serverIp;
	private String    serverPort;
	
	
	public void onCreate() {
		imageCache = new ImageLruCache(this,this.getPackageCodePath());
		queue =Volley.newRequestQueue(this);
		imageLoader = new ImageLoader(queue, imageCache);
		serverIp = "192.168.0.71";
		serverPort="80";

	}
	/**
	 * Used to return the singleton Image cache
	 * We do this so that if the same image is loaded 
	 * twice on two different activities, the cache still remains
	 * @return ImageLruCach
	 */
	public ImageLruCache getCache() {
		return imageCache;
	}
	/**
	 * Used to return the singleton RequestQueue
	 * @return RequestQueue
	 */
	public RequestQueue getQueue() {
		return queue;
	}
	/**
	 * Used to return the singleton imageloader 
	 * that utilizes the image lru cache. 
	 * @return ImageLoader
	 */
	public ImageLoader getImageLoader(){
		return imageLoader;
	}
	
	public String getServerIp() {
	
		return serverIp;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	public String getServerPort() {
		return serverPort;
	}
	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
	

}
