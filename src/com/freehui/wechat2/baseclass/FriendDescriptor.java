package com.freehui.wechat2.baseclass;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.text.Spanned;

import com.freehui.wechat2.util.ByteUtils;
import com.freehui.wechat2.util.FileUtils;

/*
 * 好友描述符
 */
public class FriendDescriptor implements Serializable{
	
	/*
	 * 好友名，手机号，IP地址，头像
	 */
	private String name; 
	private String phone;
	private String ip;
	private byte[] image;
	private String head;//名字首字母
	private String private_data;//私有数据
	public int unreadmessage_count; 
	
	public FriendDescriptor() {
		// TODO Auto-generated constructor stub
	}
	
	public FriendDescriptor(String name,String phone) {
		// TODO Auto-generated constructor stub
		
		this.name = name;
		this.phone = phone;
	}
	
	public FriendDescriptor(String name,String phone,String ip) {
		// TODO Auto-generated constructor stub
		
		this.name = name;
		this.phone = phone;
		this.ip = ip;
	}	

	public void setprivate(String private_data){
		
		this.private_data = private_data;
	}
	 
	
	public void setname(String name){
		
		this.name = name;
	}
	
	public void setphone(String phone){
		
		this.phone = phone;
	}	
	
	public void setip(String ip){
		
		this.ip = ip;
	}

	public void sethead(String head){
		
		this.head = head;
	}	
	
	public String getname(){
		
		return this.name;
	}
	
	public String getphone(){
		
		return this.phone;
	}
	
	public String getip(){
		
		return this.ip;
	}
	
	public Bitmap getImage(){
		
		if (image == null){
			
			image = ByteUtils.Bitmap2Bytes(FileUtils.getImageFromLocal(this.phone));
		}
		return ByteUtils.Bytes2Bimap(image); 
	}
	
	public void setimage(Bitmap image){
		
		this.image = ByteUtils.Bitmap2Bytes(image);
	}	
	
	public String gethead(){
		
		return head;
	}	
	
	public String getprivate(){
		
		return private_data;
	}	
}





