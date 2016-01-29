package com.freehui.wechat2;

import android.app.Application;
import android.content.Context;

public class WechatApplication extends Application {
	
	static private Context context;
	static public Context getContext(){
		
		return context;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
		context = getApplicationContext();
	}
}
