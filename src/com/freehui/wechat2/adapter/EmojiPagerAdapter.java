package com.freehui.wechat2.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.pdf.PdfDocument.Page;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.example.wechat2.R;

public class EmojiPagerAdapter extends PagerAdapter {

	ViewGroup[] emojiview; 
	onEmojiItemSelected callback;
	
	public EmojiPagerAdapter(Context ctx,int [][]image_resource) {
		super();
		// TODO Auto-generated constructor stub
		
		emojiview = new ViewGroup[4];
		
		LayoutInflater inflater = LayoutInflater.from(ctx);
		emojiview[0] = (ViewGroup) inflater.inflate(R.layout.emoji_page,null);
		emojiview[1] = (ViewGroup) inflater.inflate(R.layout.emoji_page,null);
		emojiview[2] = (ViewGroup) inflater.inflate(R.layout.emoji_page,null);
		emojiview[3] = (ViewGroup) inflater.inflate(R.layout.emoji_page,null);
		
		for (int i = 0;i < emojiview.length;i++){
			
			ArrayList<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
				
	        for (int j = 0;j < image_resource[i].length;j++){
	        		
		        Map<String,Object> map = new HashMap<String, Object>();  
		        map.put("imageView",image_resource[i][j]); 
		        data.add(map);  	        		
	        }
	        
			SimpleAdapter simpleAdapter = 
					 new SimpleAdapter(
							 ctx, data,
							 R.layout.testimage , 
							 new String[]{"imageView"}, 
							 new int[]{R.id.imageView1});  		
			
			GridView view = (GridView)emojiview[i].findViewById(R.id.emoji);
			view.setAdapter(simpleAdapter);
			view.setOnItemClickListener(new EmojiItemOnSelected(i));
		}
	}
	
	public class EmojiItemOnSelected implements GridView.OnItemClickListener{
		
		int Pageindex;
		
		EmojiItemOnSelected(int i){
			
			Pageindex = i;
		}
		
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Log.w("TAG","PAGE-;"+  Pageindex +"EMOJI-"+arg2);
			if (callback != null)
				callback.onSelected(Pageindex, arg2);
		}
	}
	
	public interface onEmojiItemSelected{
		
		void onSelected (int pageindex,int emojiindex);
	}
	
	public void setOnEmojiItemSelected(onEmojiItemSelected e){
		 
		this.callback = e;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return emojiview.length;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}	

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager)container).removeView(emojiview[position]);
	}	
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		((ViewPager)container).addView(emojiview[position]);
		return emojiview[position];
	}
}
