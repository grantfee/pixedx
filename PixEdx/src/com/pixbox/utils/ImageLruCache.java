package com.pixbox.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader.ImageCache;
/**
 * CapTech Consulting Blog
 * 
 * ImageLruCache used to return previously loaded images from the cache. 
 * 
 * 
 * @author Clinton Teegarden
 *
 */
public class ImageLruCache extends LruCache<String, Bitmap> implements ImageCache {
	/*
	 * Used to calculate the cache size we would like to have
	 * Makes sure we do not get an OOM exception. 
	 */
	//private BitmapSoftRefCache softRefCache;
	private static int DISK_IMAGECACHE_SIZE = 1024*1024*20;
	private static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.PNG;
	private static int DISK_IMAGECACHE_QUALITY = 100; 
	
	private DiskLruImageCache diskCache;
	
	public static int getDefaultLruCacheSize() {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;
		return cacheSize;
	}
	/**
	 * Constructor methods below.  First one is called
	 * and gets the cache size from the custom method and 
	 * builds a cache of that size. 
	 */
	public ImageLruCache(Context context,String uniqueName) {
		this(getDefaultLruCacheSize());
		 
		diskCache = new DiskLruImageCache(context, uniqueName, DISK_IMAGECACHE_SIZE,
				DISK_IMAGECACHE_COMPRESS_FORMAT, 70);
	}
	public ImageLruCache(int size){
		super(size);
		//softRefCache = new BitmapSoftRefCache();  
	}
	/**
	 * Used to return the size of an image
	 */
	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight() / 1024;
	}
	
	@Override
	protected void entryRemoved(boolean evicted, String key, Bitmap oldValue,
			Bitmap newValue) {
		// TODO Auto-generated method stub
		 if (evicted) {  
	            //Log.i("ImageLruCache", "空间已满，缓存图片被挤出:" + key);  
	            // 将被挤出的bitmap对象，添加至软引用BitmapSoftRefCache  
	            //softRefCache.putBitmap(key, oldValue);  
	            
	      }  
		 super.entryRemoved(evicted, key, oldValue, newValue);
	}
	/**
	 * returns the bitmap from the cache by passing the url
	 */
	@Override
	public Bitmap getBitmap(String url) {
		String key = createKey(url);
		 Bitmap bitmap = get(key);  
	        // 如果bitmap为null，尝试从磁盘缓存中查找  
	        if (bitmap == null) {  
	            //bitmap = softRefCache.getBitmap(url);  
	            Log.i("ImageLruCache", "LruCache not cached image,get from disk.key: " + key);
	           
	             bitmap = diskCache.getBitmap(key);
	            
	            if(bitmap!=null){
	            	putBitmap(key,bitmap);
	            }
	        } else {  
	            Log.i("ImageLruCache", "LruCache get cached image" + url);  
	        }  
	        return bitmap;  
	}
	/**
	 * sets the image to the cache using the url as a primary key.
	 */
	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		
		String key = createKey(url);
		put(key, bitmap);
	
		//检查是否需要缓存至磁盘
		 if(diskCache!=null && ! diskCache.containsKey(key)){
			 Log.i("ImageLruCache", "cache bitmap to disk key:" + key);
			 diskCache.putBitmap(key, bitmap); 
		 }
				 
	}
	
	/**
	 * Creates a unique cache key based on a url value
	 * @param url
	 * 		url to be used in key creation
	 * @return
	 * 		cache key value
	 */
	private String createKey(String url){
		return String.valueOf(url.hashCode());
	}

}
