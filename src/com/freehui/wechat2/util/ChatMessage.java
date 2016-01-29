package com.freehui.wechat2.util;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;

import com.freehui.wechat2.WechatApplication;

/*
 * 一个消息
 */
public class ChatMessage {

	public String phone;
	public String date; 
	//经过 ImageGetter 转换后的消息内容
	private CharSequence text;
	public boolean recv;

	ImageGetter imgGetter = new ImageGetter() {
		
		public Drawable getDrawable(String source) {
			
			//这里的 ID 就是 R.drawable.imageID
			int id = Integer.parseInt(source);
			
			//得到图像
			Drawable d = WechatApplication.getContext().getResources().getDrawable(id);
			
			//按原大小显示
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			
			return d;
		}
	}; 	
	
	public void settext(String srctext){
		 
		text = Html.fromHtml(srctext,imgGetter,null);
	}
	
	public String getsrctext(){
		
		String t = Html.toHtml((Spanned) text);
		//toHtml 后，文字外围会带上一个 <p> 标签，这里跳过这个标签
		t = t.substring(13, t.length()-5);
		//把双引号换成单引号，不换的话，无论是服务器还是客户端，都会解析出错
		t = t.replace("\"", "\'");
		return t;
	}	
	
	public CharSequence gettext(){
		
		return text;
	}
	
	public ChatMessage(String text, boolean recv) {

		this.settext(text);
		this.recv = recv;
	}
	
	public ChatMessage(String text) {
		
		this.settext(text);
	}		
	
	public ChatMessage() {
		
	}			
}