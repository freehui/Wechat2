package com.freehui.wechat2.push;

import java.io.Serializable;

public class PushMessage implements Serializable{
	
	public int type;
	public int length;
	public String data;
}