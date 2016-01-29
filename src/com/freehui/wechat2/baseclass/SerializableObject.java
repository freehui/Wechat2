package com.freehui.wechat2.baseclass;

import java.io.Serializable;

public class SerializableObject implements Serializable{
	
	private Object obj;
	
	public SerializableObject(Object obj) {
		// TODO Auto-generated constructor stub
		this.obj = obj;
	}
	
	public Object get(){
		
		return obj;
	}
}
